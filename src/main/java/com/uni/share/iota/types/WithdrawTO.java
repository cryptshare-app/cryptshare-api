package com.uni.share.iota.types;

import java.util.List;

public class WithdrawTO {
    private List<AddressTO> usedAddresses;
    private List<TransactionTO> createdTransactions;

    public WithdrawTO(){

    }

    public List<AddressTO> getUsedAddresses() {
        return usedAddresses;
    }

    public void setUsedAddresses(List<AddressTO> usedAddresses) {
        this.usedAddresses = usedAddresses;
    }

    public List<TransactionTO> getCreatedTransactions() {
        return createdTransactions;
    }

    public void setCreatedTransactions(List<TransactionTO> createdTransactions) {
        this.createdTransactions = createdTransactions;
    }
}
