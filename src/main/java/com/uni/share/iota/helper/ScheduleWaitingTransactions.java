package com.uni.share.iota.helper;

import com.uni.share.iota.boundary.IotaAddressesBF;
import com.uni.share.iota.boundary.IotaTransactionsBF;
import com.uni.share.iota.entity.IotaTransactionsEM;
import com.uni.share.iota.types.IotaTransactionsBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;


@Singleton
public class ScheduleWaitingTransactions {

    @Inject
    IotaTransactionsBF iotaTransactionsBF;

    @Inject
    IotaClient iotaClient;

    @Inject
    IotaTransactionsEM iotaTransactionsEM;

    @Inject
    IotaAddressesBF iotaAddressesBF;

    @PostConstruct
    public void init() {
        System.out.println("Schedule Waiting Transactions started");
    }


    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    private void startCheckingWaitingTransactions() {
        try {
            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Waiting Transactions", true);
            List<IotaTransactionsBE> waitingTransactions = iotaTransactionsBF.getAllWaitingTransactions();
            if (waitingTransactions.size() == 0) {
                return;
            }
            for (IotaTransactionsBE waitingTx : waitingTransactions) {
                int amountBundles = iotaClient.getAmountBundlesForAddress(waitingTx.getReceiverAddress());
                if (amountBundles > 0) {
                    waitingTx.setStatus(IotaTransactionsEM.STATUS_PENDING);
                    iotaTransactionsEM.merge(waitingTx);
                    //if we send to extern, userBE is null
                    if (waitingTx.getUserBE() != null) {
                        WebsocketEndpoint.sendMessage(WebsocketEndpoint.TRANSACTION_PENDING,
                                iotaAddressesBF.addChecksum(waitingTx.getReceiverAddress()), false,
                                waitingTx.getReceiverId(), waitingTx.getSenderId());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Caught Exceptio in waiting Transactions");
            e.printStackTrace();
        }
    }
}
