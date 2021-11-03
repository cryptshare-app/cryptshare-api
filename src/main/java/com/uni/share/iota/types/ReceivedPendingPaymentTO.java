package com.uni.share.iota.types;


public class ReceivedPendingPaymentTO {
    private AddressTO address;
    private TransactionTO transaction;

    public ReceivedPendingPaymentTO(){

    }

    public AddressTO getAddress() {
        return address;
    }

    public void setAddress(AddressTO address) {
        this.address = address;
    }

    public TransactionTO getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionTO transaction) {
        this.transaction = transaction;
    }
}
