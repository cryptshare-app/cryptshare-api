package com.uni.share.iota.types;


public class RevenuesAndExpensesTO {
    private BalanceTO[] revenues;
    private BalanceTO[] expenses;

    public RevenuesAndExpensesTO() {
    }

    public BalanceTO[] getRevenues() {
        return revenues;
    }

    public void setRevenues(BalanceTO[] revenues) {
        this.revenues = revenues;
    }

    public BalanceTO[] getExpenses() {
        return expenses;
    }

    public void setExpenses(BalanceTO[] expenses) {
        this.expenses = expenses;
    }
}
