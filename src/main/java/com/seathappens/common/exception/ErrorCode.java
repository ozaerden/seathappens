package com.seathappens.common.exception;

public enum ErrorCode {

    VENUE_NOT_FOUND("VENUE_NOT_FOUND", "Venue not found"),
    EVENT_NOT_FOUND("EVENT_NOT_FOUND", "Event not found"),
    TICKET_TYPE_NOT_FOUND("TICKET_TYPE_NOT_FOUND", "Ticket type not found"),
    INVENTORY_NOT_FOUND("INVENTORY_NOT_FOUND", "Inventory not found"),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", "Reservation not found"),
    RESERVATION_NOT_OWNED_BY_USER("RESERVATION_NOT_OWNED_BY_USER", "Reservation does not belong to current user"),
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY", "Insufficient inventory"),
    RESERVATION_ALREADY_CANCELLED("RESERVATION_ALREADY_CANCELLED", "Reservation is already cancelled"),
    RESERVATION_NOT_ACTIVE("RESERVATION_NOT_ACTIVE", "Reservation is not active"),
    CONCURRENT_MODIFICATION("CONCURRENT_MODIFICATION", "Resource was modified by another transaction. Please retry."),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order not found"),
    ORDER_NOT_OWNED_BY_USER("ORDER_NOT_OWNED_BY_USER", "Order does not belong to current user"),
    RESERVATION_ALREADY_CONVERTED("RESERVATION_ALREADY_CONVERTED", "Reservation already converted"),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found"),
    PAYMENT_NOT_OWNED_BY_USER("PAYMENT_NOT_OWNED_BY_USER", "Payment does not belong to current user"),
    ORDER_ALREADY_PAID("ORDER_ALREADY_PAID", "Order already paid"),
    ORDER_NOT_PAYABLE("ORDER_NOT_PAYABLE", "Order is not payable"),
    TICKET_NOT_FOUND("TICKET_NOT_FOUND", "Ticket not found"),
    TICKET_NOT_OWNED_BY_USER("TICKET_NOT_OWNED_BY_USER", "Ticket does not belong to current user"),
    TICKET_ALREADY_ISSUED("TICKET_ALREADY_ISSUED", "Ticket already issued for this order"),
    TICKET_NOT_ISSUED("TICKET_NOT_ISSUED", "Ticket is not issued"),
    TICKET_QR_GENERATION_ERROR("TICKET_QR_GENERATION_ERROR", "Failed to generate ticket QR code"),
    ACCESS_DENIED("ACCESS_DENIED", "Access denied"),
    OUTBOX_SERIALIZATION_ERROR("OUTBOX_SERIALIZATION_ERROR", "Failed to serialize outbox event payload"),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid email or password"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "Invalid refresh token"),
    USER_NOT_ACTIVE("USER_NOT_ACTIVE", "User is not active"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    USER_ALREADY_INACTIVE("USER_ALREADY_INACTIVE", "User is already inactive"),
    VALIDATION_FAILED("VALIDATION_FAILED", "Validation failed"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Unexpected server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

}
