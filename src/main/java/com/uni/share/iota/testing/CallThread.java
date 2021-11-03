package com.uni.share.iota.testing;

import com.uni.share.iota.boundary.IotaBF;
import com.uni.share.iota.helper.IotaClient;
import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Bundle;
import jota.utils.Checksum;
import jota.utils.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class CallThread extends Thread {

    private IotaAPI api;
    private String seed;
    private String host;
    private long startTime;
    public CallThread(String _host, String _seed){
        startTime = System.currentTimeMillis();
        seed = _seed;
        host = _host;
        api = new IotaAPI.Builder()
                .protocol("https")
                .host(_host)
                .port("443")
                .build();
    }
    public void run(){
        IotaClient client = new IotaClient();
        client.init();



    }

    public String removeChecksum(String _checksum){
        try {
            return Checksum.removeChecksum(_checksum);
        } catch (ArgumentException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String genSeedTroll(int i){
        return i < 0 ? "" : genSeedTroll((i / 26) - 1) + (char)(65 + i % 26);
    }

    public void gotSeedUsed(String seed, int value){
        try {
            String address = api.getNewAddress(seed,2,0,false,1,false).getAddresses().get(0);
            List l = new ArrayList();
            l.add(address);
            boolean spentFrom = api.checkWereAddressSpentFrom(address);
            long balance = api.getBalanceAndFormat(l,null,0,0,new StopWatch(),2).getTotalBalance();

            if(balance != 0 || spentFrom){
                //System.out.println(seed+" "+balance+" "+spentFrom);
                getBalance(seed, value);
            }

        } catch (Exception e) {

        }catch(IllegalAccessError ia){

        }

    }

    public void getBalance(String seed, int value){
        try {
            long balance = api.getBalanceAndFormat(api.getNewAddress(seed,2,0,false,600,false).getAddresses(),null,0,0,new StopWatch(),2).getTotalBalance();
            System.out.println(value+"  |  "+seed+" "+balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
