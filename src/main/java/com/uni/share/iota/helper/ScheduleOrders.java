package com.uni.share.iota.helper;

import com.uni.share.iota.boundary.IotaAddressesBF;
import com.uni.share.iota.boundary.IotaOrderBF;
import com.uni.share.iota.types.IotaAddressBE;
import com.uni.share.iota.types.IotaOrderBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import java.util.List;


@Singleton
public class ScheduleOrders {
    @Inject
    IotaAddressesBF iotaAddressesBF;

    @Inject
    IotaOrderBF iotaOrderBF;

    @Inject
    IotaTransactionSender iotaTransactionSender;



    @PostConstruct
    public void init() {
        System.out.println("Schedule Orders started");
    }


    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    private void startCheckingOrders() {
        try {
            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Orders", true);
            List<IotaOrderBE> allOrders = iotaOrderBF.getAllOrders();
            if (allOrders.size() == 0) {
                return;
            }
            outerFor:
            for (IotaOrderBE orderBE : allOrders) {
                if (orderBE.isCurrentlyProcessed()) {
                    continue outerFor;
                }
                orderBE.setCurrentlyProcessed(true);
                iotaOrderBF.mergeIotaOrderBE(orderBE);
                //get all corresponding addresses
                List<IotaAddressBE> neededAddresses = iotaAddressesBF.getAddressesForOrder(orderBE.getId());
                iotaTransactionSender.send(orderBE.getSeed(),
                        neededAddresses,
                        orderBE.getRemainderAddress(),
                        orderBE.getReceiverAddress(),
                        orderBE.getAmount(),
                        orderBE.getId());
            }
        } catch (Exception e) {
            System.out.println("Caught Exception in orders");
            e.printStackTrace();
        }
    }
}
