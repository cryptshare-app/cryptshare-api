package com.uni.share.products.control;

import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.group.entity.GroupEM;
import com.uni.share.group.entity.GroupMembershipEM;
import com.uni.share.group.types.GroupBE;
import com.uni.share.iota.boundary.IotaBF;
import com.uni.share.payments.control.PaymentBA;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.payments.types.PaymentStatus;
import com.uni.share.products.entity.ProductEM;
import com.uni.share.products.types.ProductBE;
import com.uni.share.products.types.ProductTO;
import com.uni.share.user.types.UserBE;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * BA for performing product business logic.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class ProductBA {

    @Inject
    private GroupEM groupEM;

    @Inject
    private GroupMembershipEM groupMembershipEM;
    @Inject
    private ProductEM productEM;
    @Inject
    private ProductMapperBA mapper;

    @Inject
    private PaymentBA paymentBA;

    @Inject
    private IotaBF iotaBF;
    @Inject
    private ActionsBF actionsBF;

    /**
     * The current user creates a new product and tries to persist it.
     * The group to which the product belongs has to be valid. Also the user that started this request
     * has to participate in the group in which the product was created.
     * This call fails if the product does already exist in this specific group.
     *
     * @param product     the product the user has created but not yet persisted
     * @param currentUser the user that started this request
     * @return the newly persisted product
     */
    public ProductTO createProduct(final ProductTO product, final UserBE currentUser) {
        //1. Check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(product.getGroupName())
                .orElseThrow(this::throwGroupNotFound);
        //2. Check if user participates in this group
        groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        //3. Check if product with this name already exists in this group
        final Optional<ProductBE> productBE = productEM.findByNameAndGroup(product.getProductName(), groupBE.getId());
        if (productBE.isPresent()) {
            throwProductAlreadyExists(product.getProductName(), groupBE.getTitle());
        }

        //3. Map to BE
        final ProductBE entityToPersist = mapper.toBE(product, currentUser, groupBE);
        final List<PaymentBE> payments = paymentBA.mapPayment(product.getPayments(), groupBE, entityToPersist);
        //fix jpa relationships
        entityToPersist.setPayments(payments);

        final ProductBE persistedProduct = productEM.persist(entityToPersist);

        handleAutoPayForPayments(persistedProduct, groupBE);

        if (product.getPayments().isEmpty()) {
            //4. Create payments for each user participating in this group
            paymentBA.createPaymentsForGroup(persistedProduct, groupBE, currentUser);
        }

        return mapper.toTO(persistedProduct);
    }


    private void handleAutoPayForPayments(ProductBE persistedProduct, GroupBE groupBE){
        //handle autopay & create action
        persistedProduct.getPayments().forEach((paymentBE) -> {

            //Create Action
            actionsBF.create(ActionsEM.CATEGORY_PAYMENTS,
                    "new",
                    "You have a new payment to confirm in group " + paymentBE.getGroupBE().getTitle(),
                    "/dashboard/groups/" + paymentBE.getGroupBE().getTitle() + "/payments",
                    paymentBE.getSender().getId());

            groupBE.getGroupMembershipBES().forEach((gmBE) -> {
                if (gmBE.getUserBE().getId().equals(paymentBE.getSender().getId())) {
                    if (gmBE.getAutoPay()) {
                        try {
                            iotaBF.sendInternTransaction(paymentBE.getSender(),
                                    paymentBE.getReceiver(),
                                    (long) paymentBE.getAmount(),
                                    paymentBE.getGroupBE().getTitle());

                            paymentBE.setStatus(PaymentStatus.CLOSED);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            //not enough balance
                        }
                    }
                }
            });
        });
    }

    /**
     * Fetch all products in a group. This can be executed by each group member in the group that belongs
     * to this product.
     *
     * @param groupTitle  the name of the group to which the product belongs.
     * @param currentUser the current user.
     * @return a list of products that belong to the given group.
     */
    public List<ProductTO> getAllProductsForGroup(final String groupTitle, final UserBE currentUser) {
        //1. Check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle)
                .orElseThrow(this::throwGroupNotFound);
        //2. Check if user participates in this group
        groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        final List<ProductBE> allProductsForGroup = productEM.getAllForGroup(groupTitle);
        return mapper.toTO(allProductsForGroup);
    }


    /**
     * Updates a existing product if the current user is the one that has created
     * this product
     *
     * @param productTO   the new values for the product
     * @param productName the name of the product
     * @param groupTitle  the name of the group
     * @param currentUser the current user
     * @return the newly updated product.
     */
    public ProductTO updateProductForGroup(final ProductTO productTO, final String productName,
                                           final String groupTitle,
                                           final UserBE currentUser) {

        //1. Check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle)
                .orElseThrow(this::throwGroupNotFound);
        //2. Check if user participates in this group
        groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        //3. Check if product exists
        final ProductBE productBE = productEM.findByNameAndGroup(productName, groupBE.getId())
                .orElseThrow(this::throwProductDoesNotExists);
        //4. Check if User has also created the product
        if (!productBE.getUserBE().getId().equals(currentUser.getId())) {
            throw new BusinessValidationException(CryptShareErrors.USER_NOT_ALLOWED_UPDATE_GROUP, Response.Status.FORBIDDEN);
        }
        productBE.setPrice(productTO.getPrice());
        productBE.setProductName(productTO.getProductName());
        productBE.setImageUrl(productTO.getImageUrl());
        productBE.setProductDescription(productTO.getProductDescription());
        return mapper.toTO(productEM.merge(productBE));

    }


    /**
     * Delete a given product if the current user is the one that has created the product
     *
     * @param productName the name of the product
     * @param groupTitle  the name of the group
     * @param currentUser the current user
     */
    public void deleteProductForGroup(final String productName, final String groupTitle, final UserBE currentUser) {
        //1. Check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle)
                .orElseThrow(this::throwGroupNotFound);
        //2. Check if user participates in this group
        groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        //3. Check if product exists
        final ProductBE productBE = productEM.findByNameAndGroup(productName, groupBE.getId())
                .orElseThrow(this::throwProductDoesNotExists);
        //4. Check if User has also created the product
        if (!productBE.getUserBE().getId().equals(currentUser.getId())) {
            throw new BusinessValidationException(CryptShareErrors.USER_NOT_ALLOWED_UPDATE_GROUP, Response.Status.FORBIDDEN);
        }
        //5. check if there are open payments for this product
        productBE.getPayments().forEach(paymentBE -> {
            if (paymentBE.getStatus().equals(PaymentStatus.OPEN)) {
                throw new BusinessValidationException(CryptShareErrors.DELETE_PRODUCT_NOT_POSSIBLE_OPEN_PAYMENTS,
                        Response.Status.FORBIDDEN);
            }
        });
        productEM.delete(productBE.getId());
    }


    private BusinessValidationException throwProductDoesNotExists() {
        return new BusinessValidationException(CryptShareErrors.PRODUCT_NOT_FOUND, Response.Status.BAD_REQUEST);

    }


    private void throwProductAlreadyExists(final String productName, final String groupName) {

        throw new BusinessValidationException(
                CryptShareErrors.PRODUCT_ALREADY_EXISTS,
                Response.Status.BAD_REQUEST);
    }


    private BusinessValidationException throwGroupNotFound() {
        return new BusinessValidationException(CryptShareErrors.GROUP_NOT_FOUND, Response.Status.BAD_REQUEST);
    }


    private BusinessValidationException throwGroupMembershipNotFound() {
        return new BusinessValidationException(CryptShareErrors.GROUP_MEMBERSHIP_NOT_FOUND, Response.Status.BAD_REQUEST);
    }


}
