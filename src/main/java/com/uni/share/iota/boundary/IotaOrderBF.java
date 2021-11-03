package com.uni.share.iota.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import com.uni.share.iota.entity.IotaOrderEM;
import com.uni.share.iota.types.IotaOrderBE;

@Stateless
public class IotaOrderBF {

    @Inject
    IotaOrderEM iotaOrderEM;


    public IotaOrderBE create(String _seed, Long _amount, String _receiverAddress, String _remainderAddress,
                              Long _userId) {
        IotaOrderBE newOrder = new IotaOrderBE();
        newOrder.setSeed(_seed);
        newOrder.setAmount(_amount);
        newOrder.setReceiverAddress(_receiverAddress);
        newOrder.setCurrentlyProcessed(false);
        if (_remainderAddress != null) {
            newOrder.setRemainderAddress(_remainderAddress);
        }
        newOrder.setUserId(_userId); //Id of initiator
        return iotaOrderEM.persist(newOrder);
    }


    public List<IotaOrderBE> getAllOrders() {
        Optional<List<IotaOrderBE>> o = iotaOrderEM.getAllOrders();
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    public IotaOrderBE getOrderByID(Long _id) {
        Optional<IotaOrderBE> o = iotaOrderEM.getOrderById(_id);
        if (o.isPresent()) {
            return o.get();
        } else {
            return null;
        }
    }


    public void deleteOrder(Long _orderId) {
        iotaOrderEM.delete(_orderId);
    }


    public IotaOrderBE mergeIotaOrderBE(IotaOrderBE _iotaOrderBE) {
        return iotaOrderEM.merge(_iotaOrderBE);
    }


}
