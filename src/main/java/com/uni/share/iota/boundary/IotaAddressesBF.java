package com.uni.share.iota.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.iota.entity.IotaAddressEM;
import com.uni.share.iota.helper.IotaClient;
import com.uni.share.iota.types.AddressTO;
import com.uni.share.iota.types.IotaAddressBE;
import com.uni.share.iota.types.IotaBE;
import com.uni.share.user.types.UserBE;

@Stateless
public class IotaAddressesBF {

    @Inject
    IotaAddressEM iotaAddressEM;

    @Inject
    IotaBF iotaBF;

    @Inject
    IotaClient iotaClient;


    /**
     * creates and persists a new IotaAddressBE
     *
     * @param _address
     * @param _index
     * @param _balance
     * @param _user
     * @return
     */
    private IotaAddressBE create(String _address, int _index, long _balance, UserBE _user) {
        IotaAddressBE newAddress = new IotaAddressBE();
        newAddress.setAddress(_address);
        newAddress.setUserBE(_user);
        newAddress.setBalance(_balance);
        newAddress.setIndex(_index);
        return iotaAddressEM.persist(newAddress);
    }


    /**
     * generates and persits a new Address for the user
     * also handles all index related stuff like increasing lowestUnusedIndexWOB
     *
     * @param _user
     * @return
     */
    public IotaAddressBE createNewAddressForUser(UserBE _user) {
        IotaBE iotaBE = iotaBF.getIotaByUserId(_user.getId());
        int index = iotaBE.getLowestUnusedIndexWithoutBalance();
        String address = iotaClient.generateAddress(iotaBE.getCurrentSeed(), index);
        iotaBE.setLowestUnusedIndexWithoutBalance(index + 1);
        iotaBF.mergeIotaBE(iotaBE);
        //balance immer 0 da balance erst nach transaction confirmation gesetzt wird
        return create(address, index, 0, _user);
    }


    /**
     * gets all addresses for the user that are currently stored in t_iota_addresses
     * returns an empty list if none were found
     *
     * @param _userID
     * @return
     */
    public List<IotaAddressBE> getAllAddressesForUser(Long _userID) {
        Optional<List<IotaAddressBE>> o = iotaAddressEM.findAddressesByUserID(_userID);
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * Gets the first n addresses that are needed to add up to the neededTotalBalance
     *
     * @param _sender
     * @param _neededTotalBalance
     * @return
     */
    public List<IotaAddressBE> getAddressesNeededForTransfer(UserBE _sender, long _neededTotalBalance) {
        List<IotaAddressBE> allAddresses = getAllAddressesForUser(_sender.getId());
        Collections.sort(allAddresses, (a1, a2) -> a1.getIndex() - a2.getIndex());
        List<IotaAddressBE> addressesNeeded = new ArrayList<>();
        long currentAddedBalances = 0;

        for (int i = 0; i < allAddresses.size(); i++) {
            IotaAddressBE address = allAddresses.get(i);
            if (address.getBalance() > 0) {
                currentAddedBalances += address.getBalance();
                addressesNeeded.add(address);
            }
            if (currentAddedBalances >= _neededTotalBalance) {
                return addressesNeeded;
            }
        }
        return new ArrayList<>();
    }


    /**
     * gets the address corresponding to the user and the index,
     * throws BusinessValidationException if not found
     *
     * @param _userID
     * @param _index
     * @return
     */
    public IotaAddressBE getAddressForUserByIndex(Long _userID, int _index) {
        Optional<IotaAddressBE> o = iotaAddressEM.findAddressesByUserIDAndIndex(_userID, _index);
        if (o.isPresent()) {
            return o.get();
        } else {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NO_ADRESS_BY_INDEX,
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @param _address the Iota Address
     * @return the address with checksum
     */
    public String addChecksum(String _address) {
        return iotaClient.addChecksum(_address);
    }


    /**
     * @param bes the IotaAddressBE list to convert to a AddressTO List
     * @return the corresponding AddressTO List
     */
    public List<AddressTO> toTO(List<IotaAddressBE> bes) {
        List<AddressTO> tos = bes.stream().map(be -> {
            return toTO(be);
        }).collect(Collectors.toList());
        return tos;
    }


    /**
     * @param be the IotaAddressBE to be convertet to AddressTO
     * @return the corresponding AddressTO
     */
    public AddressTO toTO(IotaAddressBE be) {
        AddressTO newAddressTo = new AddressTO();
        newAddressTo.setAddress(addChecksum(be.getAddress()));
        newAddressTo.setBalance(be.getBalance());
        newAddressTo.setIndex(be.getIndex());
        return newAddressTo;
    }


    public List<IotaAddressBE> getAddressesForOrder(Long _orderId) {
        Optional<List<IotaAddressBE>> o = iotaAddressEM.findAddressesForOrder(_orderId);
        if (o.isPresent()) {
            return o.get();
        } else {
            return new ArrayList<>();
        }
    }


    /**
     * merges the given iotaAddressBE
     *
     * @param _iotaAddressBE the IotaAddressBE to be merged
     */
    public void mergeAddressBE(IotaAddressBE _iotaAddressBE) {
        iotaAddressEM.merge(_iotaAddressBE);
    }

}
