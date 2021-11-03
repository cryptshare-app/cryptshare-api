package com.uni.share.iota.boundary;

import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.common.mapper.ResponseMapper;
import com.uni.share.iota.entity.IotaTransactionsEM;
import com.uni.share.iota.helper.IotaClient;
import com.uni.share.iota.types.*;
import com.uni.share.payments.control.PaymentBA;
import com.uni.share.payments.types.PaymentTO;
import com.uni.share.user.boundary.UserBF;
import com.uni.share.user.types.UserBE;
import io.swagger.annotations.Api;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path("iota")
@Api
@JWTSecured
public class IotaResource {

    @Context
    private SecurityContext securityContext;

    @Inject
    private IotaBF iotaBF;

    @Inject
    private UserBF userBF;

    @Inject
    private IotaAddressesBF iotaAddressesBF;

    @Inject
    private IotaTransactionsBF iotaTransactionsBF;

    @Inject
    private IotaClient iotaClient;

    @Inject
    private PaymentBA paymentBA;


    @Path("getAllAddresses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AddressTO> getAllAddresses() {
        List<IotaAddressBE> allAddresses = iotaAddressesBF.getAllAddressesForUser(getUserId());
        return iotaAddressesBF.toTO(allAddresses);
    }


    @Path("getAllTransactions")
    @GET
    public List<TransactionTO> getAllTransactions() {
        List<IotaTransactionsBE> allTransactions = iotaTransactionsBF.getAllTransactionsForUser(getUserId());
        return iotaTransactionsBF.toTO(allTransactions);
    }

    @Path("getAvailableBalance")
    @GET
    public long getAvailableBalance() {
        return iotaBF.getBalance(getUserId());
    }


    /**
     * initiates a transaction between two users of the system
     *
     * @param _senderName   userName of the sender
     * @param _receiverName userName of the receiver
     * @param _amount       amount of the transaction in IOTA
     * @param _groupTitle   groupTitle of the group the transaction was send in
     * @return A withdraw to with all used addresses and createdTransactions. Search in created Transactions for transaction which is remainder to be able
     * to reconstruct created remainder address.
     */
    @Path("sendInternTransaction")
    @POST
    public Response sendInternTransaction(@QueryParam("senderName") String _senderName,
                                          @QueryParam("receiverName") String _receiverName,
                                          @QueryParam("amount") Long _amount,
                                          @QueryParam("groupTitle") String _groupTitle) {
        UserBE sender = userBF.findUserByUserName(_senderName);
        UserBE receiver = userBF.findUserByUserName(_receiverName);
        if (sender.getId() == receiver.getId()) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NOT_ALLOWED_TRANSACTION,
                    Response.Status.BAD_REQUEST);
        }
        //If user is not existing, it already throws a BusinessValidationException at user retrieval
        if (_amount <= 0) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_MISSING_AMOUNT_FOR_TRANSACTION,
                    Response.Status.BAD_REQUEST);
        }

        //TODO
        //Check if users are in the same group if a groupTitle is provided
        //If no group title is provided then the request came from the crypto view where you can just send another user sth

        WithdrawTO withdrawTO = iotaBF.sendInternTransaction(sender, receiver, _amount, _groupTitle);
        return ResponseMapper.map(withdrawTO);
    }

    @Path("confirmPayment")
    @POST
    public PaymentTO confirmPayment(PaymentTO paymentTO) {
        sendInternTransaction(paymentTO.getSenderUserName(),
                paymentTO.getReceiverUserName(),
                (long) Math.ceil(paymentTO.getAmount()),
                paymentTO.getGroupTitle());

        return paymentBA.confirmPayment(paymentTO.getId());
    }


    /**
     * initiates a transaction from a user of the system to an external address
     *
     * @param _receiverAddress IOTA address of the receiver
     * @param _amount          amount of the transaction in IOTA
     */
    @Path("sendExternTransaction")
    @POST
    public Response sendExternTransaction(@QueryParam("receiverAddress") String _receiverAddress,
                                          @QueryParam("amount") long _amount) {

        if (_amount <= 0) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_MISSING_AMOUNT_FOR_TRANSACTION,
                    Response.Status.BAD_REQUEST);
        }

        if (!_receiverAddress.matches("[A-Z9]{90}")) {
            throw new BusinessValidationException(CryptShareErrors.IOTA_INVALID_RECEIVER_ADDRESS,
                    Response.Status.BAD_REQUEST);
        }
        UserBE sender = userBF.getCurrentUser(securityContext);
        WithdrawTO withdrawTO = iotaBF.sendExternTransaction(sender,
                iotaClient.removeChecksumFromAddress(_receiverAddress), _amount);
        return ResponseMapper.map(withdrawTO);
    }


    /**
     * Creates a new address and transaction with the user as receiver and returns this transaction
     *
     * @return
     */
    @Path("deposit")
    @GET
    public Response deposit() {
        IotaAddressBE newAddress = iotaAddressesBF.createNewAddressForUser(userBF.getCurrentUser(securityContext));
        IotaTransactionsBE iotaTransactionsBE = iotaTransactionsBF.create(newAddress.getAddress(),
                0, userBF.getCurrentUser(securityContext),
                false, null, null, getUserId(), IotaTransactionsEM.STATUS_WAITING, newAddress.getIndex());
        return ResponseMapper.map(iotaTransactionsBF.toTO(iotaTransactionsBE));
    }


    @Path("getDayBalances")
    @GET
    public Response getDayBalances() {
        BalanceTO[] dailyBalances = iotaTransactionsBF.getDayBalances(getUserId());
        return ResponseMapper.map(dailyBalances);
    }


    @Path("getHourBalances")
    @GET
    public Response getHourBalances() {
        BalanceTO[] hourlyBalances = iotaTransactionsBF.getHourBalances(getUserId());
        return ResponseMapper.map(hourlyBalances);
    }


    @Path("getMinuteBalances")
    @GET
    public Response getMinuteBalances() {
        BalanceTO[] minutelyBalances = iotaTransactionsBF.getMinuteBalances(getUserId());
        return ResponseMapper.map(minutelyBalances);
    }


    @Path("getDayExpenses")
    @GET
    public Response getDayExpenses() {
        return ResponseMapper.map(iotaTransactionsBF.getDayExpenses(getUserId()));
    }


    @Path("getHourExpenses")
    @GET
    public Response getHourExpenses() {
        return ResponseMapper.map(iotaTransactionsBF.getHourExpenses(getUserId()));
    }


    @Path("getMinuteExpenses")
    @GET
    public Response getMinuteExpenses() {
        return ResponseMapper.map(iotaTransactionsBF.getMinuteExpenses(getUserId()));
    }


    @Path("getDayRevenues")
    @GET
    public Response getDayRevenues() {
        return ResponseMapper.map(iotaTransactionsBF.getDayRevenues(getUserId()));
    }


    @Path("getHourRevenues")
    @GET
    public Response getHourRevenues() {
        return ResponseMapper.map(iotaTransactionsBF.getHourRevenues(getUserId()));
    }


    @Path("getMinuteRevenues")
    @GET
    public Response getMinuteRevenues() {
        return ResponseMapper.map(iotaTransactionsBF.getMinuteRevenues(getUserId()));
    }


    @Path("getDayRevenuesAndExpenses")
    @GET
    public Response getDayRevenuesAndExpenses() {
        BalanceTO[] revenues = iotaTransactionsBF.getDayRevenues(getUserId());
        BalanceTO[] expenses = iotaTransactionsBF.getDayExpenses(getUserId());
        RevenuesAndExpensesTO to = new RevenuesAndExpensesTO();
        to.setRevenues(revenues);
        to.setExpenses(expenses);
        return ResponseMapper.map(to);
    }


    @Path("getHourRevenuesAndExpenses")
    @GET
    public Response getHourRevenuesAndExpenses() {
        BalanceTO[] revenues = iotaTransactionsBF.getHourRevenues(getUserId());
        BalanceTO[] expenses = iotaTransactionsBF.getHourExpenses(getUserId());
        RevenuesAndExpensesTO to = new RevenuesAndExpensesTO();
        to.setRevenues(revenues);
        to.setExpenses(expenses);
        return ResponseMapper.map(to);
    }


    @Path("getMinuteRevenuesAndExpenses")
    @GET
    public Response getMinuteRevenuesAndExpenses() {
        BalanceTO[] revenues = iotaTransactionsBF.getMinuteRevenues(getUserId());
        BalanceTO[] expenses = iotaTransactionsBF.getMinuteExpenses(getUserId());
        RevenuesAndExpensesTO to = new RevenuesAndExpensesTO();
        to.setRevenues(revenues);
        to.setExpenses(expenses);
        return ResponseMapper.map(to);
    }


    private long getUserId() {
        return Long.parseLong(securityContext.getUserPrincipal().getName());
    }

}
