package com.uni.share.iota.helper;

import com.uni.share.websocket.WebsocketEndpoint;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;


@Singleton
public class ScheduleServerStatus {

    @Inject
    IotaClient iotaClient;


    @PostConstruct
    public void init() {
        System.out.println("Schedule Server Status started");
    }

    @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
    private void startCheckingServerStatus() {
        try{
            WebsocketEndpoint.sendMessage(WebsocketEndpoint.HEARTBEAT, "Start Checking Server Status", true);

            int amountBundles = iotaClient.getAmountBundlesForAddress("ADRVKAHILBCTMQAPEIEHZVEQMIEEXVH9NPJUOIHKUZBPYHSSPACM9VOAGGSATNXSA9DB9LGZPQQLWCYTADSLFYDPSD");
            if(amountBundles >= 0){
                WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_SERVER_STATUS, "up", true);
            }else{
                WebsocketEndpoint.sendMessage(WebsocketEndpoint.UPDATE_SERVER_STATUS, "down", true);
            }
        }catch (Exception e){
            System.out.println("Caught Expection in server status");
            e.printStackTrace();
        }
    }
}
