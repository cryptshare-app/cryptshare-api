package com.uni.share.iota.types;

import java.time.LocalDateTime;

public class BalanceTO {
    long balance;
    String timestamp;

    public BalanceTO() {
    }


    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
