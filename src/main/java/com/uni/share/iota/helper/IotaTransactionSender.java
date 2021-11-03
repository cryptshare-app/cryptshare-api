package com.uni.share.iota.helper;

import com.uni.share.iota.types.IotaAddressBE;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class IotaTransactionSender {

    @Inject
    IotaClient iotaClient;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(String seed, List<IotaAddressBE> senderAddr, String remainderAddr, String receiverAddr, long amount, Long _orderId){
        iotaClient.sendTransfer(seed,senderAddr,remainderAddr,receiverAddr,amount, _orderId);
    }

}
