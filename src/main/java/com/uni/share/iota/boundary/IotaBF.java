package com.uni.share.iota.boundary;

import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.iota.entity.IotaEM;
import com.uni.share.iota.entity.IotaTransactionsEM;
import com.uni.share.iota.types.*;
import com.uni.share.user.types.UserBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Stateless
public class IotaBF {

    @Inject
    private IotaAddressesBF iotaAddressesBF;

    @Inject
    IotaEM iotaEM;

    @Inject
    IotaTransactionsBF iotaTransactionsBF;


    @Inject
    IotaOrderBF iotaOrderBF;

    @Inject
    ActionsBF actionsBF;

    @Inject
    IotaBF iotaBF;

    /**
     * @return SeedTO with random 81 trytes long seed
     */
    public String generateSeed() {
        SecureRandom random = new SecureRandom();
        byte randoms[] = random.generateSeed(81);

        for (int k = 0; k < 5; k++) { //shuffle random 5 times
            Random rnd = ThreadLocalRandom.current();
            for (int i = randoms.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);
                byte a = randoms[index];
                randoms[index] = randoms[i];
                randoms[i] = a;
            }
        }

        StringBuilder stringbuilder = new StringBuilder();
        for (byte b : randoms) {
            char c = (char) ((((b + 128) / 256d) * 27d) + 65); //Zufallszahlen zwischen 65 und 91
            if (c == 91) {
                stringbuilder.append("9");
            } else {
                stringbuilder.append(Character.toString(c));
            }
        }
        return stringbuilder.toString();
    }


    /**
     * Gets all addresses for the user and adds their balance together, returns this balance.
     *
     * @param _userID
     * @return
     */
    public long getBalance(long _userID) {
        List<IotaAddressBE> addresses = iotaAddressesBF.getAllAddressesForUser(_userID);
        long balance = 0;
        for (IotaAddressBE address : addresses) {
            balance += address.getBalance();
        }
        return balance;
    }


    /**
     * returns the lowest unused index without balance for the user
     *
     * @param _userId
     * @return
     */
    public int getLowestUnusedIndexWithoutBalance(long _userId) {
        Optional<IotaBE> o = iotaEM.findIotaByUserID(_userId);
        if (o.isPresent()) {
            return o.get().getLowestUnusedIndexWithoutBalance();
        } else {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NO_LOWEST_UNUSED_INDEX_WITHOUT_BALANCE,
                    Response.Status.NO_CONTENT);
        }
    }


    /**
     * returns the iotaBE referenced by the userId
     *
     * @param _userId
     * @return
     */
    public IotaBE getIotaByUserId(long _userId) {
        Optional<IotaBE> o = iotaEM.findIotaByUserID(_userId);
        if (o.isPresent()) {
            return o.get();
        } else {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NOT_FOUND, Response.Status.NO_CONTENT);
        }
    }


    /**
     * Gets all necessary sender addresses to match the amount
     * creates remainder addr for sender if necessary
     * creates receiver addr
     * sends the transfer
     *
     * @param _sender
     * @param _receiver
     * @param _amount
     * @return
     */
    public WithdrawTO sendInternTransaction(UserBE _sender, UserBE _receiver, long _amount, String _groupTitle) {
        //get needed addresses from sender
        List<IotaAddressBE> senderAddresses = iotaAddressesBF.getAddressesNeededForTransfer(_sender, _amount);
        List<IotaTransactionsBE> createdTransactions = new ArrayList<>();
        //check rest of balance
        long restOfBalance = -_amount;
        IotaAddressBE remainderAddr = null;
        for (IotaAddressBE address : senderAddresses) {
            restOfBalance += address.getBalance();
        }
        if (restOfBalance < 0) {
            //Did not have enough balance
            throw new BusinessValidationException(CryptShareErrors.IOTA_NOT_ENOUGH_BALANCE,
                    Response.Status.BAD_REQUEST);
        } else if (restOfBalance == 0) {
            //Did have exactly the balance, no remainder addr needed
        } else {
            //Need a remainder addr
            remainderAddr = iotaAddressesBF.createNewAddressForUser(_sender);
            createdTransactions.add(
                    iotaTransactionsBF.create(remainderAddr.getAddress(), restOfBalance, _sender, true, _groupTitle,
                            _sender.getId(), _sender.getId(), IotaTransactionsEM.STATUS_PENDING,
                            remainderAddr.getIndex()));
        }

        //create new receiver Addr
        IotaAddressBE receiverAddr = iotaAddressesBF.createNewAddressForUser(_receiver);

        //create new transaction for receiver
        IotaTransactionsBE receiverTransactionBE = iotaTransactionsBF.create(receiverAddr.getAddress(), _amount,
                _receiver, false, _groupTitle,
                _sender.getId(), _receiver.getId(), IotaTransactionsEM.STATUS_PENDING, receiverAddr.getIndex());
        createdTransactions.add(receiverTransactionBE);

        //Tell receiver that he got new pending payment
        ReceivedPendingPaymentTO receivedPendingPaymentTO = new ReceivedPendingPaymentTO();
        receivedPendingPaymentTO.setAddress(iotaAddressesBF.toTO(receiverAddr));
        receivedPendingPaymentTO.setTransaction(iotaTransactionsBF.toTO(receiverTransactionBE));
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.RECEIVED_PENDING_PAYMENT, receivedPendingPaymentTO, false,
                _receiver.getId());

        //updateBalance of sender
        IotaBE senderIotaBE = getIotaByUserId(_sender.getId());
        senderIotaBE.setCurrentBalance(senderIotaBE.getCurrentBalance() - _amount - restOfBalance);
        mergeIotaBE(senderIotaBE);


        IotaOrderBE order;
        //Create Order
        if (remainderAddr != null) {
            order = iotaOrderBF.create(senderIotaBE.getCurrentSeed(),
                    _amount,
                    receiverAddr.getAddress(),
                    remainderAddr.getAddress(),
                    _sender.getId());
        } else {
            order = iotaOrderBF.create(senderIotaBE.getCurrentSeed(),
                    _amount,
                    receiverAddr.getAddress(),
                    null,
                    _sender.getId());
        }
        //Create Expenses Action
        actionsBF.create(ActionsEM.CATEGORY_EXPENSES,
                "intern",
                "" + _receiver.getUserName() + ";" + _amount,
                "/dashboard/crypto",
                _sender.getId());

        //Set balance of sender addresses to 0 and update usedInOrder
        for (IotaAddressBE senderAddr : senderAddresses) {
            senderAddr.setBalanceBeforeOrder(senderAddr.getBalance());
            senderAddr.setBalance(0);
            senderAddr.setUsedInOrder(order.getId());
            iotaAddressesBF.mergeAddressBE(senderAddr);
        }
        //Notify sender that his balance changed
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_AVAILABLE_BALANCE,
                iotaBF.getBalance(_sender.getId()), false,
                _sender.getId());

        List<TransactionTO> createdTransactionsTO = iotaTransactionsBF.toTO(createdTransactions);
        List<AddressTO> usedAddressesTO = iotaAddressesBF.toTO(senderAddresses);
        WithdrawTO withdrawTO = new WithdrawTO();
        withdrawTO.setCreatedTransactions(createdTransactionsTO);
        withdrawTO.setUsedAddresses(usedAddressesTO);
        return withdrawTO;
    }


    public WithdrawTO sendExternTransaction(UserBE _sender, String _receiverAddr, long _amount) {

        //get needed addresses from sender
        List<IotaAddressBE> senderAddresses = iotaAddressesBF.getAddressesNeededForTransfer(_sender, _amount);

        if (senderAddresses.isEmpty()) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NOT_ENOUGH_BALANCE,
                    Response.Status.BAD_REQUEST);
        }

        for (IotaAddressBE neededAddr : senderAddresses) {
            System.out.println(
                    "Needed Address: " + neededAddr.getAddress() + " | " + neededAddr.getBalance() + " | " + neededAddr.getIndex());
        }

        List<IotaTransactionsBE> createdTransactions = new ArrayList<>();

        //check rest of balance
        long restOfBalance = -_amount;

        IotaAddressBE remainderAddr = null;

        for (IotaAddressBE address : senderAddresses) {
            restOfBalance += address.getBalance();
        }
        if (restOfBalance < 0) {
            //Did not have enough balance
            throw new BusinessValidationException(CryptShareErrors.IOTA_NOT_ENOUGH_BALANCE,
                    Response.Status.BAD_REQUEST);
        } else if (restOfBalance == 0) {
            //Did have exactly the balance, no remainder addr needed
        } else {
            //Need a remainder addr
            remainderAddr = iotaAddressesBF.createNewAddressForUser(_sender);
            createdTransactions.add(
                    iotaTransactionsBF.create(remainderAddr.getAddress(), restOfBalance, _sender, true, null,
                            _sender.getId(), _sender.getId(), IotaTransactionsEM.STATUS_PENDING,
                            remainderAddr.getIndex()));
        }


        //create new transaction for receiver
        createdTransactions.add(iotaTransactionsBF.create(_receiverAddr, _amount, null, false, null,
                _sender.getId(), null, IotaTransactionsEM.STATUS_PENDING, -1));

        //updateBalance of sender
        IotaBE senderIotaBE = getIotaByUserId(_sender.getId());
        senderIotaBE.setCurrentBalance(senderIotaBE.getCurrentBalance() - _amount - restOfBalance);
        mergeIotaBE(senderIotaBE);


        IotaOrderBE order;
        //Create Order
        if (remainderAddr != null) {
            order = iotaOrderBF.create(senderIotaBE.getCurrentSeed(),
                    _amount,
                    _receiverAddr,
                    remainderAddr.getAddress(),
                    _sender.getId());
        } else {
            order = iotaOrderBF.create(senderIotaBE.getCurrentSeed(),
                    _amount,
                    _receiverAddr,
                    null,
                    _sender.getId());
        }

        //Create Expenses Action
        actionsBF.create(ActionsEM.CATEGORY_EXPENSES,
                "extern",
                "Extern;" + _amount,
                "/dashboard/crypto",
                _sender.getId());

        //Set balance of sender addresses to 0 and update usedInOrder
        for (IotaAddressBE senderAddr : senderAddresses) {
            IotaAddressBE senderAddrBE = iotaAddressesBF.getAddressForUserByIndex(_sender.getId(),
                    senderAddr.getIndex());
            senderAddrBE.setBalanceBeforeOrder(senderAddrBE.getBalance());
            senderAddrBE.setBalance(0);
            senderAddrBE.setUsedInOrder(order.getId());
            iotaAddressesBF.mergeAddressBE(senderAddrBE);
        }

        List<TransactionTO> createdTransactionsTO = iotaTransactionsBF.toTO(createdTransactions);
        List<AddressTO> usedAddressesTO = iotaAddressesBF.toTO(senderAddresses);
        WithdrawTO withdrawTO = new WithdrawTO();
        withdrawTO.setCreatedTransactions(createdTransactionsTO);
        withdrawTO.setUsedAddresses(usedAddressesTO);
        return withdrawTO;
    }


    /**
     * creates and persits an IotaBE
     *
     * @param _user
     * @return
     */
    public void create(UserBE _user) {
        IotaBE newIota = new IotaBE();
        newIota.setUserBE(_user);
        newIota.setCurrentSeed(generateSeed());
        newIota.setLowestUnusedIndexWithoutBalance(0);
        newIota.setCurrentBalance(0L);
        iotaEM.persist(newIota);
        IotaAddressBE createdAddress = iotaAddressesBF.createNewAddressForUser(_user);
        iotaTransactionsBF.create(createdAddress.getAddress(), 0, _user, false,
                null, null, _user.getId(), IotaTransactionsEM.STATUS_WAITING, 0);
    }


    /**
     * merges the given iotaBE
     *
     * @param _iotaBE
     */
    public IotaBE mergeIotaBE(IotaBE _iotaBE) {
        return iotaEM.merge(_iotaBE);
    }
}
