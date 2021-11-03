package com.uni.share.products.entity;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.products.types.ProductBE;
import static com.uni.share.products.types.QProductBE.productBE;

/**
 * Entity Manager for products
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class ProductEM extends AbstractBaseEM<ProductBE> {
    @Override
    protected Class<ProductBE> getEntityClass() {
        return ProductBE.class;
    }


    /**
     * Tries to find a product by its name and the group it was created in
     *
     * @param productName the name of the product
     * @param groupId     the group id.
     * @return
     */
    public Optional<ProductBE> findByNameAndGroup(final String productName, final Long groupId) {
        return Optional.ofNullable(queryFactory().selectFrom(productBE)
                .where(productBE.productName.eq(productName).and(productBE.groupBE.id.eq(groupId)))
                .fetchFirst());

    }


    /**
     * Fetch all products for a given group
     *
     * @param groupTitle the name of the group for which the products will be fetched
     * @return a list of fetched products to the given group
     */
    public List<ProductBE> getAllForGroup(final String groupTitle) {
        return queryFactory()
                .selectFrom(productBE)
                .where(productBE.groupBE.title.eq(groupTitle))
                .fetch();
    }
}
