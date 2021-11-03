package com.uni.share.iota.testing;

import java.util.HashSet;

public class TestCalls {
    private static String seed = "BQLMICQTKWH99HBIOMSDRYAKJTFNAPE9ZPYIZTOWBAUPYNGXZSWDGPCWSERAR9KMWI9VHRVIIJQAFOQON";
    private static long counter = 37000;
    private static HashSet<String> checkedSeeds;
    public static void main(String[] args){
        new CallThread("nodes.thetangle.org",seed).start();


    }

    public static synchronized long getCounter(){
        if(counter%1000==0){
            System.out.println("Currently at "+counter+" ("+genSeedTroll((int)counter)+")");
        }
        return counter++;
    }

    public static synchronized void addCheckedSeed(String seed){
        checkedSeeds.add(seed);
    }

    public static synchronized boolean wasSeedAlrdyChecked(String seed){
        return checkedSeeds.contains(seed);
    }

    public static String genSeedTroll(int i){
        return i < 0 ? "" : genSeedTroll((i / 26) - 1) + (char)(65 + i % 26);
    }


}
