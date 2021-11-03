package com.uni.share.iota.types;

public class AddressTO {
    private String address;

    private int index;

    private long balance;

    private boolean isRemainder;

    public boolean isRemainder() {
        return isRemainder;
    }

    public void setRemainder(boolean remainder) {
        isRemainder = remainder;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String _address) {
        address = _address;
    }
}
