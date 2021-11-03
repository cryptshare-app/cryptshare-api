package com.uni.share.products.control;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import com.uni.share.group.types.GroupBE;
import com.uni.share.payments.control.PaymentMapperBA;
import com.uni.share.products.types.ProductBE;
import com.uni.share.products.types.ProductTO;
import com.uni.share.user.types.UserBE;

/**
 * Mapper betweend transport objects and business entities for products.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class ProductMapperBA {

    @Inject
    private PaymentMapperBA paymentMapperBA;


    /**
     * Maps a given Business Entity to its corresponding Transport Object
     *
     * @param source the source object
     * @return the newly created transport object.
     */
    public ProductTO toTO(final ProductBE source) {
        final ProductTO target = new ProductTO();
        target.setCreatorId(source.getUserBE().getId());
        target.setGroupId(source.getGroupBE().getId());
        target.setImageUrl(source.getImageUrl());
        target.setPrice(source.getPrice());
        target.setProductDescription(source.getProductDescription());
        target.setProductName(source.getProductName());
        target.setId(source.getId());
        target.setGroupName(source.getGroupBE().getTitle());
        target.setPayments(paymentMapperBA.toTO(source.getPayments()));
        return target;
    }


    /**
     * Maps a transport object into a business entity
     *
     * @param product     the transport object to map
     * @param currentUser the corresponding user (e.g. the user who created the product)
     * @param groupBE     the group in which the product was created.
     * @return the newly created business entity.
     */
    public ProductBE toBE(final ProductTO product, final UserBE currentUser, final GroupBE groupBE) {
        final ProductBE entityToPersist = new ProductBE();
        entityToPersist.setProductName(product.getProductName());
        entityToPersist.setGroupBE(groupBE);
        entityToPersist.setImageUrl(product.getImageUrl());
        entityToPersist.setPrice(product.getPrice());
        entityToPersist.setProductDescription(product.getProductDescription());
        entityToPersist.setUserBE(currentUser);
        return entityToPersist;
    }


    public List<ProductTO> toTO(final List<ProductBE> allProductsForGroup) {
        return allProductsForGroup.stream().map(this::toTO).collect(Collectors.toList());
    }
}
