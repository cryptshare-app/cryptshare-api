package com.uni.share.products.boundary;

import com.uni.share.group.boundary.GroupBF;
import com.uni.share.products.control.ProductBA;
import com.uni.share.products.types.ProductTO;
import com.uni.share.user.types.UserBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Facade for wrapping product logic.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class ProductBF {

    @Inject
    private ProductBA productBA;

    @Inject
    private GroupBF groupBF;


    /**
     * Entrypoint for creating new products
     *
     * @param product     the product to persist
     * @param currentUser the current user
     * @return the newly created product
     */
    public ProductTO create(final ProductTO product, final UserBE currentUser) {
        ProductTO productTO = productBA.createProduct(product, currentUser);
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED, productTO.getGroupName(), false, groupBF.getAllUserIdsByGroupTitle(productTO.getGroupName()));
        return productTO;
    }


    /**
     * Entrypoint for fetching all products in a group
     *
     * @param groupTitle  the title of the group to which the products belong
     * @param currentUser the current user
     * @return a list of products belonging to the given group.
     */
    public List<ProductTO> getAllProductsForGroup(final String groupTitle, final UserBE currentUser) {
        return productBA.getAllProductsForGroup(groupTitle, currentUser);
    }


    /**
     * Entrypoint for updating products
     *
     * @param productTO   the new product values
     * @param productName the old product name for identifying the product to update
     * @param groupTitle  the name of the group to which the product should belong
     * @param currentUser the current user.
     * @return the newly updated product.
     */
    public ProductTO updateProductForGroup(final ProductTO productTO, final String productName,
                                           final String groupTitle,
                                           final UserBE currentUser) {
        return productBA.updateProductForGroup(productTO, productName, groupTitle, currentUser);
    }


    /**
     * Entrypoint for deleting products.
     *
     * @param productName the name of the product which should be deleted
     * @param groupName   the name of the group to which the product should belong
     * @param currentUser the current user.
     */
    public void deleteProduct(final String productName, final String groupName, final UserBE currentUser) {
        productBA.deleteProductForGroup(productName, groupName, currentUser);
    }
}
