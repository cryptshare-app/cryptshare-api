package com.uni.share.products.boundary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.products.types.ProductTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Endpoint for receiving product related requests.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Path("products")
@JWTSecured
@Api(tags = "Products")
public class ProductResource {


    @Inject
    private ProductBF productBF;


    @Inject
    private UserTransaction userTx;


    /**
     * Endpoint for creating a new product
     *
     * @param product the product to create
     * @return the newly created product
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new product for a given group",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products successfully created"),
            @ApiResponse(code = 400, message = "Group with the given title does not exist or User is not member of this group"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public ProductTO create(@ApiParam(value = "The product to create", required = true) final ProductTO product) {
        return productBF.create(product, userTx.getCurrentUser());
    }


    /**
     * Get all available products of a given group if the user is participating in this group
     *
     * @param groupTitle the name of the group to which the fetched products should belong
     * @return a list of products that belong to the given group
     */
    @GET
    @Path("{groupTitle}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all available products for a given group",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products successfully fetched", response = ProductTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Group with the given title does not exist or User is not member of this group"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public List<ProductTO> getAllProductsForGroup(@ApiParam @PathParam("groupTitle") final String groupTitle) {
        return productBF.getAllProductsForGroup(groupTitle, userTx.getCurrentUser());
    }


    /**
     * Endpoint for updating a product in a group
     *
     * @param productTO   the new values for the product
     * @param productName the name of the product
     * @param groupTitle  the name of the group to which the product belongs
     * @return the newly updated product
     */
    @PUT
    @Path("{productName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update a given product for a given group",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Products successfully updated", response = ProductTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Product with the given name does not exist or User is not allowed to update the group"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public ProductTO update(@ApiParam final ProductTO productTO,
                            @ApiParam @PathParam("productName") final String productName,
                            @ApiParam @QueryParam("groupTitle") final String groupTitle) {
        return productBF.updateProductForGroup(productTO, productName, groupTitle, userTx.getCurrentUser());
    }


    /**
     * Endpoint for deleting products
     *
     * @param productName the name of the product to delete
     * @param groupName   the name of the group to which the product belongs.
     */
    @DELETE
    @ApiOperation(value = "Deletes a given product for a given group",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Products successfully deleted", response = ProductTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Product with the given name does not exist or User is not allowed to delete the group"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})

    public void delete(@QueryParam("productName") final String productName,
                       @QueryParam("groupName") final String groupName) {
        productBF.deleteProduct(productName, groupName, userTx.getCurrentUser());
    }
}
