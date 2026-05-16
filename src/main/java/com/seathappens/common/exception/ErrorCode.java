package com.seathappens.common.exception;

public enum ErrorCode {

    VENUE_NOT_FOUND("VENUE_NOT_FOUND", "Venue not found"),
    EVENT_NOT_FOUND("EVENT_NOT_FOUND", "Event not found"),
    TICKET_TYPE_NOT_FOUND("TICKET_TYPE_NOT_FOUND", "Ticket type not found"),
    INVENTORY_NOT_FOUND("INVENTORY_NOT_FOUND", "Inventory not found"),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", "Reservation not found"),
    INSUFFICIENT_INVENTORY("INSUFFICIENT_INVENTORY", "Insufficient inventory"),
    RESERVATION_ALREADY_CANCELLED("RESERVATION_ALREADY_CANCELLED", "Reservation is already cancelled"),
    RESERVATION_NOT_ACTIVE("RESERVATION_NOT_ACTIVE", "Reservation is not active"),

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
