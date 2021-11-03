package com.uni.share.iota.boundary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import com.uni.share.iota.entity.IotaTransactionsEM;
import com.uni.share.iota.types.BalanceTO;
import com.uni.share.iota.types.IotaTransactionsBE;
import com.uni.share.iota.types.TransactionTO;
import com.uni.share.user.control.UserBA;
import com.uni.share.user.types.UserBE;


@Stateless
public class IotaTransactionsBF {

    @Inject
    IotaTransactionsEM iotaTransactionsEM;

    @Inject
    UserBA userBA;

    @Inject
    IotaAddressesBF iotaAddressesBF;


    /**
     * creates and persits a new IotaTransactionsBE
     *
     * @param _receiverAddress
     * @param _amount
     * @param _ownerOfAddress
     * @return
     */
    public IotaTransactionsBE create(String _receiverAddress, long _amount, UserBE _ownerOfAddress,
                                     boolean _isRemainder, String _groupTitle, Long _senderId, Long _receiverId,
                                     int _status, int _index) {
        IotaTransactionsBE newTransaction = new IotaTransactionsBE();
        newTransaction.setReceiverAddress(_receiverAddress);
        newTransaction.setAmount(_amount);
        newTransaction.setTimesChecked(0);
        newTransaction.setRemainder(_isRemainder);
        newTransaction.setStatus(_status);
        newTransaction.setIndex(_index);

        //Optionals haben hier iwie ned geklappt :D

        if (_ownerOfAddress != null) {
            newTransaction.setUserBE(_ownerOfAddress);
        }

        if (_groupTitle != null) {
            newTransaction.setGroupTitle(_groupTitle);
        }

        if (_senderId != null) {
            newTransaction.setSenderId(_senderId);
        }

        if (_receiverId != null) {
            newTransaction.setReceiverId(_receiverId);
        }
        return iotaTransactionsEM.persist(newTransaction);
    }


    /**
     * gets all currently stored transactions for the user
     * returns an empty list of none are found
     *
     * @param _userID
     * @return
     */
    public List<IotaTransactionsBE> getAllTransactionsForUser(Long _userID) {
        Optional<List<IotaTransactionsBE>> o = iotaTransactionsEM.findTransactionsByUserID(_userID);
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    public List<IotaTransactionsBE> getAllConfirmedTransactionsForUser(Long _userID) {
        return iotaTransactionsEM.findConfirmedTransactionsByUserID(_userID);
    }


    /**
     * @param _userId    the user to get the transactions from
     * @param _startTime the timestamp at which to get the transactions after
     * @return the combined balance of all transactions before timestamp and a list of transactions past the timestamp
     */
    private Pair<Long, List<IotaTransactionsBE>> getConfirmedTransactionsAfterTimestamp(Long _userId,
                                                                                        LocalDateTime _startTime) {
        Long balanceBeforeTimestamp = 0L;
        List<IotaTransactionsBE> txsPastTimestamp = new ArrayList<>();

        for (IotaTransactionsBE txBE : getAllConfirmedTransactionsForUser(_userId)) {
            if (txBE.getUpdatedAt().isBefore(_startTime)) {
                if (txBE.isRemainder()) {
                    //dont change anything
                } else if (txBE.getReceiverId() == _userId) {

                    balanceBeforeTimestamp += txBE.getAmount();
                } else if (txBE.getSenderId() == _userId) {
                    balanceBeforeTimestamp -= txBE.getAmount();
                }
            } else {
                txsPastTimestamp.add(txBE);
            }
        }

        return Pair.of(balanceBeforeTimestamp, txsPastTimestamp);
    }


    public BalanceTO[] getDayBalances(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusDays(31));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildDayIntervals(txAfterTimestamp, timeNow);

        return calcBalancesForInterval(intervals, balanceBefore, _userId, 5);
    }


    public BalanceTO[] getHourBalances(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusHours(25));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildHourIntervals(txAfterTimestamp, timeNow);

        return calcBalancesForInterval(intervals, balanceBefore, _userId, 2);
    }


    public BalanceTO[] getMinuteBalances(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusMinutes(61));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildMinuteIntervals(txAfterTimestamp, timeNow);

        return calcBalancesForInterval(intervals, balanceBefore, _userId, 5);
    }


    public BalanceTO[] getDayExpenses(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusDays(31));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildDayIntervals(txAfterTimestamp, timeNow);

        return calcExpensesForInterval(intervals, _userId, 5);
    }


    public BalanceTO[] getHourExpenses(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusHours(25));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildHourIntervals(txAfterTimestamp, timeNow);

        return calcExpensesForInterval(intervals, _userId, 2);
    }


    public BalanceTO[] getMinuteExpenses(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusMinutes(61));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildMinuteIntervals(txAfterTimestamp, timeNow);

        return calcExpensesForInterval(intervals, _userId, 5);
    }


    public BalanceTO[] getDayRevenues(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusDays(31));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildDayIntervals(txAfterTimestamp, timeNow);

        return calcRevenuesForInterval(intervals, _userId, 5);
    }


    public BalanceTO[] getHourRevenues(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusHours(25));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildHourIntervals(txAfterTimestamp, timeNow);

        return calcRevenuesForInterval(intervals, _userId, 2);
    }


    public BalanceTO[] getMinuteRevenues(Long _userId) {
        LocalDateTime timeNow = LocalDateTime.now();

        Pair<Long, List<IotaTransactionsBE>> balanceBeforeAndTxs = getConfirmedTransactionsAfterTimestamp(_userId,
                timeNow.minusMinutes(61));
        Long balanceBefore = balanceBeforeAndTxs.getKey();
        List<IotaTransactionsBE> txAfterTimestamp = balanceBeforeAndTxs.getValue();

        List<IotaTransactionsBE>[] intervals = buildMinuteIntervals(txAfterTimestamp, timeNow);

        return calcRevenuesForInterval(intervals, _userId, 5);
    }


    private BalanceTO[] calcBalancesForInterval(List<IotaTransactionsBE>[] _intervals, long _balanceBefore,
                                                Long _userId, int _timestampEveryXRecords) {
        BalanceTO[] tos = new BalanceTO[_intervals.length];
        long currentBalance = _balanceBefore;
        //start at oldest interval and since index 0 is latest start at eg. 23 so at array length -1
        for (int i = tos.length - 1; i >= 0; i--) {
            BalanceTO to = new BalanceTO();
            for (IotaTransactionsBE txBE : _intervals[i]) {
                if (txBE.isRemainder()) {

                } else if (txBE.getReceiverId() == _userId) {
                    currentBalance += txBE.getAmount();
                } else if (txBE.getSenderId() == _userId) {
                    currentBalance -= txBE.getAmount();
                }
            }
            if (i % _timestampEveryXRecords == 0) {
                to.setTimestamp("" + i);
            } else {
                to.setTimestamp("");
            }
            to.setBalance(currentBalance);
            tos[tos.length - i - 1] = to;
        }
        return tos;
    }


    private BalanceTO[] calcExpensesForInterval(List<IotaTransactionsBE>[] _intervals, Long _userId,
                                                int _timestampEveryXRecords) {
        BalanceTO[] tos = new BalanceTO[_intervals.length];

        for (int i = tos.length - 1; i >= 0; i--) {
            BalanceTO to = new BalanceTO();
            long intervalExpenses = 0;
            for (IotaTransactionsBE txBE : _intervals[i]) {
                if (txBE.isRemainder()) {

                } else if (txBE.getSenderId() == _userId) {
                    intervalExpenses += txBE.getAmount();
                }
            }
            if (i % _timestampEveryXRecords == 0) {
                to.setTimestamp("" + i);
            } else {
                to.setTimestamp("");
            }
            to.setBalance(intervalExpenses);
            tos[tos.length - i - 1] = to;
        }
        return tos;
    }


    private BalanceTO[] calcRevenuesForInterval(List<IotaTransactionsBE>[] _intervals, Long _userId,
                                                int _timestampEveryXRecords) {
        BalanceTO[] tos = new BalanceTO[_intervals.length];

        for (int i = tos.length - 1; i >= 0; i--) {
            BalanceTO to = new BalanceTO();
            long intervalRevenues = 0;
            for (IotaTransactionsBE txBE : _intervals[i]) {
                if (txBE.isRemainder()) {

                } else if (txBE.getReceiverId() == _userId) {
                    intervalRevenues += txBE.getAmount();
                }
            }
            if (i % _timestampEveryXRecords == 0) {
                to.setTimestamp("" + i);
            } else {
                to.setTimestamp("");
            }
            to.setBalance(intervalRevenues);
            tos[tos.length - i - 1] = to;
        }
        return tos;
    }


    private List<IotaTransactionsBE>[] buildDayIntervals(List<IotaTransactionsBE> _txAfterTimestamp,
                                                         LocalDateTime _timeNow) {
        List<IotaTransactionsBE>[] intervals = new ArrayList[31];
        for (int i = 0; i < 31; i++) {
            intervals[i] = new ArrayList<>();
            for (IotaTransactionsBE txBE : _txAfterTimestamp) {
                if (isBetween(txBE, _timeNow.minusDays(i + 1), _timeNow.minusDays(i))) {
                    intervals[i].add(txBE);
                }
            }
        }
        return intervals;
    }


    private List<IotaTransactionsBE>[] buildHourIntervals(List<IotaTransactionsBE> _txAfterTimestamp,
                                                          LocalDateTime _timeNow) {
        List<IotaTransactionsBE>[] intervals = new ArrayList[25];
        for (int i = 0; i < 25; i++) {
            intervals[i] = new ArrayList<>();
            for (IotaTransactionsBE txBE : _txAfterTimestamp) {
                if (isBetween(txBE, _timeNow.minusHours(i + 1), _timeNow.minusHours(i))) {
                    intervals[i].add(txBE);
                }
            }
        }
        return intervals;
    }


    private List<IotaTransactionsBE>[] buildMinuteIntervals(List<IotaTransactionsBE> _txAfterTimestamp,
                                                            LocalDateTime _timeNow) {
        List<IotaTransactionsBE>[] intervals = new ArrayList[61];
        for (int i = 0; i < 61; i++) {
            intervals[i] = new ArrayList<>();
            for (IotaTransactionsBE txBE : _txAfterTimestamp) {
                if (isBetween(txBE, _timeNow.minusMinutes(i + 1), _timeNow.minusMinutes(i))) {
                    intervals[i].add(txBE);
                }
            }
        }

        return intervals;
    }


    private boolean isBetween(IotaTransactionsBE txBE, LocalDateTime localDateStart, LocalDateTime localDateEnd) {
        return txBE.getUpdatedAt().isAfter(localDateStart) && txBE.getUpdatedAt().isBefore(localDateEnd);
    }


    /**
     * gets all currently stored transactions that are pending regardless of the user they belong to
     * returns an empty list of none are found
     *
     * @return
     */
    public List<IotaTransactionsBE> getAllPendingTransactions() {
        Optional<List<IotaTransactionsBE>> o = iotaTransactionsEM.findTransactionsByStatus(
                IotaTransactionsEM.STATUS_PENDING);
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * gets all currently stored transactions that are waiting regardless of the user they belong to
     * returns an empty list of none are found
     *
     * @return
     */
    public List<IotaTransactionsBE> getAllWaitingTransactions() {
        Optional<List<IotaTransactionsBE>> o = iotaTransactionsEM.findTransactionsByStatus(
                IotaTransactionsEM.STATUS_WAITING);
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * @param bes the IotaTransactionsBE list to convert to a TransactionTO List
     * @return the corresponding TransactionToList
     */
    public List<TransactionTO> toTO(List<IotaTransactionsBE> bes) {
        List<TransactionTO> tos = bes.stream().map(be -> {
            return toTO(be);
        }).collect(Collectors.toList());
        return tos;

    }


    /**
     * @param be a IotaTransactionsBE to convert to a TransactionTO
     * @return the corresponding TO
     */
    public TransactionTO toTO(IotaTransactionsBE be) {
        TransactionTO newTransactionTO = new TransactionTO();
        newTransactionTO.setReceiverAddress(iotaAddressesBF.addChecksum(be.getReceiverAddress()));
        newTransactionTO.setAmount(be.getAmount());
        newTransactionTO.setCreatedAt(be.getCreatedAt());
        newTransactionTO.setRemainder(be.isRemainder());
        newTransactionTO.setStatus(be.getStatus());
        newTransactionTO.setReceiverId(be.getReceiverId()); //could be null
        newTransactionTO.setSenderId(be.getSenderId()); // could be null
        newTransactionTO.setIndex(be.getIndex());

        if (be.getGroupTitle() != null && be.getGroupTitle().trim().length() > 0) {
            newTransactionTO.setGroupTitle(be.getGroupTitle());
        } else {
            newTransactionTO.setGroupTitle("/");
        }

        try {
            newTransactionTO.setReceiverName(userBA.getUserById(be.getReceiverId()).getUserName());
        } catch (Exception e) {
            newTransactionTO.setReceiverName("/");
        }

        try {
            newTransactionTO.setSenderName(userBA.getUserById(be.getSenderId()).getUserName());
        } catch (Exception e) {
            newTransactionTO.setSenderName("/");
        }
        return newTransactionTO;
    }


}
