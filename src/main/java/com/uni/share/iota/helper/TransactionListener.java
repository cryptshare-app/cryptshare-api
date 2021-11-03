//package com.uni.share.iota.helper;
//
//import com.uni.share.actions.boundary.ActionsBF;
//import com.uni.share.actions.entity.ActionsEM;
//import com.uni.share.iota.boundary.IotaAddressesBF;
//import com.uni.share.iota.boundary.IotaBF;
//import com.uni.share.iota.boundary.IotaOrderBF;
//import com.uni.share.iota.boundary.IotaTransactionsBF;
//import com.uni.share.iota.entity.IotaTransactionsEM;
//import com.uni.share.iota.types.*;
//import com.uni.share.user.control.UserBA;
//import com.uni.share.websocket.WebsocketEndpoint;
//
//import javax.annotation.PostConstruct;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Startup;
//import javax.ejb.Stateless;
//import javax.inject.Inject;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Stateless
//public class TransactionListener {
//
//    @Inject
//    IotaTransactionsBF iotaTransactionsBF;
//
//    @Inject
//    IotaClient iotaClient;
//
//    @Inject
//    IotaTransactionsEM iotaTransactionsEM;
//
//    @Inject
//    IotaAddressesBF iotaAddressesBF;
//
//    @Inject
//    IotaBF iotaBF;
//
//    @Inject
//    IotaOrderBF iotaOrderBF;
//
//    @Inject
//    IotaTransactionSender iotaTransactionSender;
//
//    @Inject
//    ActionsBF actionsBF;
//
//    @Inject
//    UserBA userBA;
//
//
//    @PostConstruct
//    public void init() {
//        System.out.println("Transaction Listener started");
//    }
//
//
//    /**
//     * Looks every 10 seconds for all currently stored pending transactions if they are confirmed meaning if they got a balance
//     * then updates the status to confirmed and updates the addressBE with the correct balance
//     * Reattaches a pending transaction if it was not confirmed during 3 time intervals
//     */
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    private void startCheckingTransactions() {
//        try {
//            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Transactions", true);
//            List<IotaTransactionsBE> pendingTransactions = iotaTransactionsBF.getAllPendingTransactions();
//            if (pendingTransactions.size() == 0) {
//                return;
//            }
//            List<String> addresses = new ArrayList<>();
//            for (int i = 0; i < pendingTransactions.size(); i++) {
//                addresses.add(pendingTransactions.get(i).getReceiverAddress());
//            }
//            long[] balances = iotaClient.getBalanceFromAddresses(addresses);
//            //if we did not get a response from the tangle we received an empty balances[] and are finished
//            for (int i = 0; i < balances.length; i++) {
//                if (balances[i] > 0) {
//                    setConfirmedAndUpdateAddress(pendingTransactions.get(i), balances[i]);
//                } else {
//                    //Every 6 time periods we reattach the transaction to make sure it will eventually be confirmed
//                    //TODO falls perfomance issues threaded machen
//                    if (pendingTransactions.get(i).getTimesChecked() % 6 == 5) {
//                        iotaClient.reattachTransaction(pendingTransactions.get(i).getReceiverAddress());
//                    }
//                    increaseTimesChecked(pendingTransactions.get(i));
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Caught Exception");
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    private void startCheckingWaitingTransactions() {
//        try {
//            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Waiting Transactions", true);
//            List<IotaTransactionsBE> waitingTransactions = iotaTransactionsBF.getAllWaitingTransactions();
//            if (waitingTransactions.size() == 0) {
//                return;
//            }
//            for (IotaTransactionsBE waitingTx : waitingTransactions) {
//                int amountBundles = iotaClient.getAmountBundlesForAddress(waitingTx.getReceiverAddress());
//                if (amountBundles > 0) {
//                    waitingTx.setStatus(IotaTransactionsEM.STATUS_PENDING);
//                    iotaTransactionsEM.merge(waitingTx);
//                    //if we send to extern, userBE is null
//                    if (waitingTx.getUserBE() != null) {
//                        WebsocketEndpoint.sendMessage(WebsocketEndpoint.TRANSACTION_PENDING,
//                                iotaAddressesBF.addChecksum(waitingTx.getReceiverAddress()), false,
//                                waitingTx.getReceiverId(), waitingTx.getSenderId());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Caught Exception");
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    private void startCheckingOrders() {
//        try {
//            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Orders", true);
//            List<IotaOrderBE> allOrders = iotaOrderBF.getAllOrders();
//            if (allOrders.size() == 0) {
//                return;
//            }
//            outerFor:
//            for (IotaOrderBE orderBE : allOrders) {
//                if (orderBE.isCurrentlyProcessed()) {
//                    continue outerFor;
//                }
//                orderBE.setCurrentlyProcessed(true);
//                iotaOrderBF.mergeIotaOrderBE(orderBE);
//                //get all corresponding addresses
//                List<IotaAddressBE> neededAddresses = iotaAddressesBF.getAddressesForOrder(orderBE.getId());
//                iotaTransactionSender.send(orderBE.getSeed(),
//                        neededAddresses,
//                        orderBE.getRemainderAddress(),
//                        orderBE.getReceiverAddress(),
//                        orderBE.getAmount(),
//                        orderBE.getId());
//            }
//        } catch (Exception e) {
//            System.out.println("Caught Exception");
//            e.printStackTrace();
//        }
//    }
//
//    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
//    private void startCheckingServerStatus() {
//        try{
//            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Server Status", true);
//
//            int amountBundles = iotaClient.getAmountBundlesForAddress("ADRVKAHILBCTMQAPEIEHZVEQMIEEXVH9NPJUOIHKUZBPYHSSPACM9VOAGGSATNXSA9DB9LGZPQQLWCYTADSLFYDPSD");
//            if(amountBundles >= 0){
//                WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_SERVER_STATUS, "up", true);
//            }else{
//                WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_SERVER_STATUS, "down", true);
//            }
//        }catch (Exception e){
//            System.out.println("Caught Expection");
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * increases the times checked counter of the transaction and updates it in the db
//     *
//     * @param _transactionBE
//     */
//    private void increaseTimesChecked(IotaTransactionsBE _transactionBE) {
//        _transactionBE.setTimesChecked(_transactionBE.getTimesChecked() + 1);
//        iotaTransactionsEM.merge(_transactionBE);
//    }
//
//
//    /**
//     * sets the transaction to confiremd and updates it in the db
//     *
//     * @param _transactionBE
//     */
//    private void setConfirmedAndUpdateAddress(IotaTransactionsBE _transactionBE, long _balance) {
//        _transactionBE.setStatus(IotaTransactionsEM.STATUS_CONFIRMED);
//        _transactionBE.setAmount(_balance);
//        iotaTransactionsEM.merge(_transactionBE);
//
//        AddressTO addressTO = new AddressTO();
//        addressTO.setIndex(_transactionBE.getIndex());
//        addressTO.setBalance(_balance);
//        addressTO.setAddress(iotaAddressesBF.addChecksum(_transactionBE.getReceiverAddress()));
//        addressTO.setRemainder(_transactionBE.isRemainder());
//
//        //Notify sender and receiver
//        WebsocketEndpoint.sendMessage(WebsocketEndpoint.TRANSACTION_CONFIRMED, addressTO, false,
//                _transactionBE.getReceiverId(), _transactionBE.getSenderId());
//
//
//
//        //Update Address Balance and iota balance of receiver
//        if (_transactionBE.getReceiverId() != 0) {
//            //is 0 if we send to extern address
//            //for that we dont have a address in store.
//            IotaAddressBE addressBE = iotaAddressesBF.getAddressForUserByIndex(_transactionBE.getReceiverId(),
//                    _transactionBE.getIndex());
//            addressBE.setBalance(_balance);
//            iotaAddressesBF.mergeAddressBE(addressBE);
//
//            WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_AVAILABLE_BALANCE,
//                    iotaBF.getBalance(_transactionBE.getReceiverId()), false,
//                    _transactionBE.getReceiverId());
//
//            IotaBE iotaReceiver = iotaBF.getIotaByUserId(_transactionBE.getReceiverId());
//            iotaReceiver.setCurrentBalance(iotaReceiver.getCurrentBalance() + _balance);
//            iotaBF.mergeIotaBE(iotaReceiver);
//
//            //Create Revenues Action
//            if (_transactionBE.isRemainder()) {
//                actionsBF.create(ActionsEM.CATEGORY_REVENUES,
//                        "remainder",
//                        "Remainder;" + _balance,
//                        "/dashboard/crypto",
//                        _transactionBE.getReceiverId());
//            } else {
//                if (_transactionBE.getSenderId() == 0) {
//                    //Sender was extern
//                    actionsBF.create(ActionsEM.CATEGORY_REVENUES,
//                            "extern",
//                            "Extern;" + _balance,
//                            "/dashboard/crypto",
//                            _transactionBE.getReceiverId());
//                } else {
//                    //Sender was intern
//                    String senderName = userBA.getUserById(_transactionBE.getSenderId()).getUserName();
//                    actionsBF.create(ActionsEM.CATEGORY_REVENUES,
//                            "intern",
//                            "" + senderName + ";" + _balance,
//                            "/dashboard/crypto",
//                            _transactionBE.getReceiverId());
//                }
//            }
//
//
//        }
//
//
//    }
//
//}
