package com.uni.share.iota.helper;

import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.iota.boundary.IotaOrderBF;
import com.uni.share.iota.types.IotaAddressBE;
import com.uni.share.iota.types.IotaOrderBE;
import com.uni.share.websocket.WebsocketEndpoint;
import jota.IotaAPI;
import jota.dto.response.SendTransferResponse;
import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.model.Input;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.Checksum;
import jota.utils.StopWatch;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class IotaClient {

    private IotaAPI api;

    @Inject
    private IotaOrderBF iotaOrderBF;


    @PostConstruct
    public void init() {
//        api = new IotaAPI.Builder()
//                .protocol("https")
//                .host("nodes.thetangle.org")
//                .port("443")
//                .build();
        api = new IotaAPI.Builder()
                .protocol("https")
                .host("nodes.devnet.thetangle.org")
                .port("443")
                .build();
    }


    /**
     * gets the balances for all provided addresses
     */
    public long[] getBalanceFromAddresses(List<String> _addresses) {
        long[] balances;
        try {
            //TODO threshold 0 wirft hier Fehler, ist threshold 1 ok? Hat auch balances = 0 angezeigt.
            String[] resp = api.getBalances(1, _addresses).getBalances();
            balances = new long[resp.length];
            for (int i = 0; i < resp.length; i++) {
                balances[i] = Long.valueOf(resp[i]);
            }
            return balances;
        } catch (Exception e) {
            reInitApi();
            System.out.println("Caught exception in getBalanceFromAddresses");
            WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_SERVER_STATUS,"down",true);
            return new long[0];
        } catch (Error e){
            reInitApi();
            System.out.println("Caught error in getBalanceFromAddresses");
            return new long[0];
        }
    }


    /**
     * gets the first bundle for the provided address, retrieves the tail transaction and reattaches it to the tangle
     */
    public void reattachTransaction(String... _address) {
        try {
            Bundle[] bundles = api.bundlesFromAddresses(_address, false);
            if (bundles.length == 0) {
                return;
            }
            //Get tail transacton of first bundle
            //all other bundles besides first are considered being reattachments
            Transaction tailTransaction = null;
            for (Transaction t : bundles[0].getTransactions()) {
                if (t.isTailTransaction()) {
                    tailTransaction = t;
                }
            }
            api.replayBundle(tailTransaction.getHash(), 1, 14, null);
        } catch (Exception e) {
            reInitApi();
            System.out.println("Caught exception in reattachTransaction");
        }catch (Error e) {
            reInitApi();
            System.out.println("Caught error in reattachTransaction");
        }
    }


    /**
     * generates a new address for the seed at index
     */
    public String generateAddress(String _seed, int _index) {
        try {
            return api.getNewAddress(_seed, 2, _index, false, 1, false).getAddresses().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BusinessValidationException(CryptShareErrors.IOTA_NEW_ADDRESS_FAILED, Response.Status.INTERNAL_SERVER_ERROR);
    }


    /**
     * Gets the balance for the address from the tangle
     */
    public long getBalanceFromAddress(String _address) {
        List<String> list = new ArrayList<String>();
        list.add(_address);
        try {
            return api.getBalanceAndFormat(list, null, 0, 0, new StopWatch(), 2).getTotalBalance();
        } catch (Exception e) {
            reInitApi();
            System.out.println("Caught exception in getBalanceFromAddress");
            return -1L;
        }catch(Error e){
            reInitApi();
            System.out.println("Caught error in getBalanceFromAddress");
            return -1L;
        }
    }


    /**
     * Gets the amount of bundles for the given address
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int getAmountBundlesForAddress(String... _address) {
        Bundle[] bundles = null;
        try {
            bundles = api.bundlesFromAddresses(_address, false);
            return bundles.length;
        } catch (Exception e) {
            reInitApi();
            System.out.println("Caught exception in getAmountBundlesForAddress");
            e.printStackTrace();
        }catch (Error e){
            reInitApi();
            System.out.println("Caught error in getAmountBundlesForAddress");
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * adds a checksum to the address
     */
    public String addChecksum(String _address) {
        try {
            return Checksum.addChecksum(_address);
        } catch (Exception e) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_ADD_CHECKSUM_FAILED,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    public void sendTransfer(String _seed, List<IotaAddressBE> _senderAddresses, String _remainderAddr,
                             String _recieverAddress, long _amount, long _orderId) {
        List<Transfer> transfers = new ArrayList<>();
        transfers.add(new Transfer(removeChecksumFromAddress(_recieverAddress), _amount));

        List<Input> inputs = new ArrayList<>();
        for (IotaAddressBE senderAddr : _senderAddresses) {
            inputs.add(
                    new Input(senderAddr.getAddress(), senderAddr.getBalanceBeforeOrder(), senderAddr.getIndex(), 2));
        }

        String remainderAddress = _remainderAddr;

        try {
            SendTransferResponse resp = api.sendTransfer(_seed, 2, 1, 14, transfers, inputs, remainderAddress, false,
                    false, null);
            //here we know transfer was sent to tangle so we can remove order
            System.out.println("We sent the order  ReceiverAddr:("+_recieverAddress+"), can delete the order right now");
            iotaOrderBF.deleteOrder(_orderId);
            WebsocketEndpoint.sendMessage(WebsocketEndpoint.ORDER_COMPLETE, _orderId, false,
                    _senderAddresses.get(0).getUserBE().getId());
        } catch (Exception e) {
            reInitApi();
            System.out.println("Did not manage to send order ReceiverAddr:("+_recieverAddress+") in this round, set currently processed to false");
            e.printStackTrace();
            //Did not manage to send order in this round
            IotaOrderBE orderBE = iotaOrderBF.getOrderByID(_orderId);
            orderBE.setCurrentlyProcessed(false);
            iotaOrderBF.mergeIotaOrderBE(orderBE);
        }catch(Error e){
            reInitApi();
            System.out.println("Caught error in sendTransfer");
            e.printStackTrace();
            IotaOrderBE orderBE = iotaOrderBF.getOrderByID(_orderId);
            orderBE.setCurrentlyProcessed(false);
            iotaOrderBF.mergeIotaOrderBE(orderBE);
        }
    }

    private void reInitApi(){
        System.out.println("ReInit API");
        api = new IotaAPI.Builder()
                .protocol("https")
                .host("nodes.devnet.thetangle.org")
                .port("443")
                .build();

//        api = new IotaAPI.Builder()
//                .protocol("https")
//                .host("nodes.thetangle.org")
//                .port("443")
//                .build();
    }


//    //Todo was wenn account noch balance 0 hat? wirklich so viele schritte durchgehen?
//    public int getLowestIndexWithBalanceFromTangle(String _seed, int _startIndex){
//        try{
//            for(int i=_startIndex;i<_startIndex+10000;i++){
//                //only deprecated function does what its supposed to do so use it here
//                String address = api.getNewAddress(_seed,2,i,false,1,false).getAddresses().get(0);
//                if(getBalanceFromAddress(address) > 0){
//                    return i;
//                }
//            }
//        } catch (ArgumentException e) {
//            e.printStackTrace();
//        }
//        throw new BusinessValidationException("Can´t get lowest index with balance", Response.Status.INTERNAL_SERVER_ERROR);
//    }
//
//
//

//    /**
//     * Gets the balance for a given address
//     * @param _address
//     * @param _timesTried
//     * @return
//     */


//

//
//    public List<IotaAddressBE> getAllAddressesWithBalance(String _seed, int _lowestIndexWithBalance){
//        List<IotaAddressBE> allAddressesWithBalance = new ArrayList<IotaAddressBE>();
//        int indexCounter = _lowestIndexWithBalance;
//        outer:while(true){
//            String addressToCheck = generateAddress(_seed,indexCounter);
//            long balance = getBalanceFromAddress(addressToCheck);
//            if(balance > 0){
//                allAddressesWithBalance.add(new IotaAddressBE(addressToCheck,indexCounter,balance));
//            }else{
//                break outer;
//            }
//            indexCounter++;
//        }
//        return allAddressesWithBalance;
//    }
//
//
//    public int getTotalBalance(String _seed){
//        //TODO startIndex anpassen mit lokalem Wert
//        int lowestIndexWithBalance = getLowestIndexWithBalanceFromTangle(_seed,0);
//        List<AddressBE> allAddressesWithBalance = getAllAddressesWithBalance(_seed,lowestIndexWithBalance);
//        int totalAmount = 0;
//        for(AddressBE addr:allAddressesWithBalance){
//            totalAmount += addr.getBalance();
//        }
//        return totalAmount;
//    }


    public String removeChecksumFromAddress(String _address) {
        try {
            return Checksum.removeChecksum(_address);
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        throw new BusinessValidationException(CryptShareErrors.IOTA_REMOVE_CHECKSUM_FAILED,
                Response.Status.INTERNAL_SERVER_ERROR);
    }
}
