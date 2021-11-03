package com.uni.share.common.exceptions.errors;

public enum CryptShareErrors {

    // GROUP
    GROUP_NOT_FOUND("Group not found"),
    DELETE_GROUP_NOT_POSSIBLE_OPEN_PAYMENTS("Group  can not be deleted since there are open payments"),
    GROUP_ALREADY_EXISTS("Group does already exist"),
    LEAVE_GROUP_NOT_POSSIBLE_FOR_OWNER("You can't leave your own group"),
    LEAVE_GROUP_NOT_POSSIBLE_FOR_PENDING("Can't leave group since your are not a accepted member of this group"),
    LEAVE_GROUP_NOT_POSSIBLE_OPEN_PAYMENTS("Can't leave group because you have open payments"),

    // User
    USER_NOT_FOUND("User with name not found"),
    USER_NOT_OWNER("User is not owner of this group"),
    USER_ALREADY_EXISTS("User does already exist"),
    USER_ALREADY_INVITED("User is already invited to this group"),
    USER_ALREADY_MEMBER("User is already member of this group"),
    USER_ALREADY_IN_GROUP("User with name is already a member of group"),
    USER_NOT_ALLOWED_DELETE_GROUP("User is not allowed to delete group"),
    USER_NOT_ALLOWED_UPDATE_GROUP("User is not allowed to update group"),
    USER_ID_INVALID("Invalid user id"),
    USER_NAME_INVALID("Username may not be blank, empty or null."),
    USER_EMAIL_INVALID("Must be a valid email"),
    USER_IS_NOT_MEMBER_OF_GROUP("User is not a member of group "),
    USER_PASSWORD_INVALID("Provided user password does not match internal password"),

    // GROUP MEMBERSHIP
    REJECT_INVITATION_NOT_POSSIBLE_ALREADY_ACCEPTED(
            "Cant reject group invitation because you are already member of this group"),
    ACCEPT_INVITATION_NOT_POSSIBLE_NOT_PENDING("Can not accept group membership"),
    GROUP_MEMBERSHIP_NOT_FOUND("Group membership not found for user"),

    // PAYMENT
    PAYMENT_NOT_FOUND("Payment not found"),
    PAYMENT_ALREADY_PAID("Payment was already paid"),

    // IOTA
    IOTA_NO_ADRESS_BY_INDEX("Can´t get address for user by index"),
    IOTA_NO_ARCHIVE_SEED("No Archive Seeds found for user"),
    IOTA_NO_LOWEST_UNUSED_INDEX_WITHOUT_BALANCE("No lowest unused index without balance found for user"),
    IOTA_NOT_FOUND("No iota found for user ID"),
    IOTA_NOT_ENOUGH_BALANCE("Not enough balance to send Transaction"),
    IOTA_MISSING_AMOUNT_FOR_TRANSACTION("Can´t send transaction, no amount entered"),
    IOTA_INVALID_RECEIVER_ADDRESS("Can´t send transaction, invalid receiver address entered"),
    IOTA_NOT_ALLOWED_TRANSACTION("Transaction not allowed"),
    IOTA_NEW_ADDRESS_FAILED("Can´t generate new Address"),
    IOTA_ADD_CHECKSUM_FAILED("Can´t add checksum to address"),
    IOTA_REMOVE_CHECKSUM_FAILED("Can´t remove checksum from address"),


    // PRODUCT
    PRODUCT_NOT_FOUND("Product with title %s not found"),
    PRODUCT_ALREADY_EXISTS("Product does already exist"),
    DELETE_PRODUCT_NOT_POSSIBLE_OPEN_PAYMENTS("Product cannot be deleted since there are still open payments"),


    // JWT
    JWT_AUTHORIZATION_INVALID("Authorization Header invalid"),
    JWT_ACCESS_TOKEN_INVALID("access_token_invalid"),
    JWT_REFRESH_TOKEN_INVALID("refresh_token_invalid"),

    JWT_TOKEN_INVALID("Token invalid"),


    // Constraint validation
    INVALID_CONSTRAINT("Validation for one or more parameters failed");

    private String errorMessage;


    CryptShareErrors(String s) {
        this.errorMessage = s;
    }


    @Override
    public String toString() {
        return this.errorMessage;
    }
}
