package com.uni.share.payments.control;

import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.group.boundary.GroupBF;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.iota.boundary.IotaBF;
import com.uni.share.payments.entity.PaymentEM;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.payments.types.PaymentStatus;
import com.uni.share.payments.types.PaymentTO;
import com.uni.share.products.types.ProductBE;
import com.uni.share.user.control.UserBA;
import com.uni.share.user.types.UserBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business activity to handle logic related to payments. FOr example creating payments for a certain group.
 * Or update payments for a certain group
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class PaymentBA {

    @Inject
    private PaymentEM paymentEM;

    @Inject
    private PaymentMapperBA paymentMapperBA;

    @Inject
    private ActionsBF actionsBF;

    @Inject
    private UserBA userBA;

    @Inject
    private IotaBF iotaBF;

    @Inject
    private GroupBF groupBF;


    /**
     * @param id
     * @return
     */
    public PaymentTO confirmPayment(final Long id) {
        PaymentTO toReturn = null;
        Optional<PaymentBE> optional = paymentEM.findById(id);
        if (optional.isPresent()) {
            PaymentBE paymentBE = optional.get();
            paymentBE.setStatus(PaymentStatus.CLOSED);
            PaymentBE result = paymentEM.merge(paymentBE);
            toReturn = paymentMapperBA.toTO(result);

            WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED, paymentBE.getGroupBE().getTitle(), false, groupBF.getAllUserIdsByGroupTitle(paymentBE.getGroupBE().getTitle()));


        } else {
            throw new BusinessValidationException(CryptShareErrors.PAYMENT_NOT_FOUND, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return toReturn;
    }

    /**
     * Create payment based on a product for a given group. The receiver is the user that created this request.
     * The sender's are the users that are participating in this group.
     *
     * @param persistedProduct the product on which this payment will be created
     * @param groupBE          the group in which the product was created
     * @param receiver         the current user that will receive the newly created payment
     */
    public void createPaymentsForGroup(final ProductBE persistedProduct, final GroupBE groupBE,
                                       final UserBE receiver) {
        final int numberGroupMembers = groupBE.getGroupMembershipBES().size();
        final double totalAmount = Math.ceil(persistedProduct.getPrice() / numberGroupMembers);

        //1. Create necessary payments
        groupBE.getGroupMembershipBES().stream()
                .filter(it -> it.getStatus().equals(GroupMembershipStatus.ACCEPTED))
                .forEach(membershipBE -> {

                    if (!membershipBE.getUserBE().getId().equals(receiver.getId())) {
                        final PaymentBE paymentBE = new PaymentBE();
                        paymentBE.setAmount(totalAmount);
                        paymentBE.setGroupBE(groupBE);
                        paymentBE.setReceiver(receiver);
                        paymentBE.setSender(membershipBE.getUserBE());
                        paymentBE.setStatus(PaymentStatus.OPEN);
                        paymentBE.setProduct(persistedProduct);

                        persistedProduct.addPayment(paymentBE);
                        groupBE.addPayment(paymentBE);
                        membershipBE.getUserBE().addSendingPayment(paymentBE);
                        receiver.addReceivingPayment(paymentBE);


                        //Create Action
                        actionsBF.create(ActionsEM.CATEGORY_PAYMENTS,
                                "new",
                                "You have a new payment to confirm in group " + paymentBE.getGroupBE().getTitle(),
                                "/dashboard/groups/" + paymentBE.getGroupBE().getTitle() + "/payments",
                                paymentBE.getSender().getId());

                        //check if autoPay is set for user and if yes then start paying tx
                        try {
                            if (membershipBE.getAutoPay()) {
                                iotaBF.sendInternTransaction(paymentBE.getSender(),
                                        paymentBE.getReceiver(),
                                        (long) paymentBE.getAmount(),
                                        paymentBE.getGroupBE().getTitle());
                                paymentBE.setStatus(PaymentStatus.CLOSED);
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                            //not enough balance
                            int i=0;
                        }

                        paymentEM.persist(paymentBE);
                    }
                });
    }


    /**
     * Update all payments in a group
     *
     * @param groupBE the group in which all payments are updated
     * @param sender  the current user (f.e. the user that newly joined the group) that will be added as sender
     */
    public void updatePaymentsForGroup(final GroupBE groupBE, final UserBE sender) {

        final int numberGroupMembers = groupBE.getGroupMembershipBES().size();
        groupBE.getProducts().forEach(productBE -> {

            //1. create new receiver payment
            final UserBE receiver = productBE.getUserBE();

            //2. Calculate new total amount for each payment based on the new group memberships.
            final double totalAmount = productBE.getPrice() / numberGroupMembers;
            productBE.getPayments().forEach(paymentBE -> {
                //3. Update amount and merge the payments
                paymentBE.setAmount(totalAmount);
                paymentEM.merge(paymentBE);
            });

            //4. Create a new payment for the newly added group member
            final PaymentBE paymentBE = new PaymentBE();
            paymentBE.setAmount(totalAmount);
            paymentBE.setGroupBE(groupBE);
            paymentBE.setReceiver(receiver);
            paymentBE.setSender(sender);
            paymentBE.setStatus(PaymentStatus.OPEN);
            paymentBE.setProduct(productBE);

            //5. Set JPA parent/child relations
            productBE.addPayment(paymentBE);
            groupBE.addPayment(paymentBE);
            sender.addSendingPayment(paymentBE);
            receiver.addReceivingPayment(paymentBE);

            //5. Persist this new payment
            paymentEM.persist(paymentBE);
        });


    }

    /**
     * Create a list of payments that correspond to a group and a product
     *
     * @param payments  the list of payments transport objects to create the payments from
     * @param groupBE   the corresponding group
     * @param productBE the corresponding product
     * @return a list of mapped payments
     */
    public List<PaymentBE> mapPayment(final List<PaymentTO> payments, final GroupBE groupBE, final ProductBE productBE) {
        return payments.stream().map(paymentTO -> mapPayment(paymentTO, groupBE, productBE)).collect(Collectors.toList());
    }

    /**
     * Create a payment
     * Also fetches sender and receiver from the db and set the jppa reference for it
     *
     * @param paymentTO the basis of the payment
     * @param groupBE   the corresponding group
     * @param productBE the corresponding product
     * @return the mapped payment
     */
    public PaymentBE mapPayment(final PaymentTO paymentTO, final GroupBE groupBE, final ProductBE productBE) {
        final PaymentBE result = new PaymentBE();

        final UserBE sender = userBA.findUserByUserName(paymentTO.getSenderUserName());
        final UserBE receiver = userBA.findUserByUserName(paymentTO.getReceiverUserName());

        result.setGroupBE(groupBE);
        result.setReceiver(receiver);
        result.setSender(sender);
        result.setStatus(PaymentStatus.OPEN);
        result.setAmount(paymentTO.getAmount());
        result.setProduct(productBE);

        return result;
    }
}
