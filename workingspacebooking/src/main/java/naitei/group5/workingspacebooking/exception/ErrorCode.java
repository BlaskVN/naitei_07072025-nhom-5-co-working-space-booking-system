package naitei.group5.workingspacebooking.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // USER
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User already exists"),
    INVALID_USER_ROLE(HttpStatus.BAD_REQUEST, "Invalid user role"),

    // VENUE
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue not found"),
    VENUE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Venue already exists"),
    VENUE_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "Venue not verified"),

    // BOOKING
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, "Booking not found"),
    BOOKING_CONFLICT(HttpStatus.CONFLICT, "Booking time conflict"),
    INVALID_BOOKING_STATUS(HttpStatus.BAD_REQUEST, "Invalid booking status"),

    // PAYMENT
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment process failed"),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment not found"),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "Invalid payment status"),

    // OTP
    OTP_INVALID(HttpStatus.BAD_REQUEST, "Invalid OTP code"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP code expired"),

    // NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),

    // ACCOUNT RECOVERY
    ACCOUNT_RECOVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "Account recovery request not found"),
    ACCOUNT_RECOVERY_EXPIRED(HttpStatus.BAD_REQUEST, "Account recovery token expired"),

    // STYLE
    FAVOURITE_STYLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Favourite style not found"),
    VENUE_STYLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue style not found"),

    // SESSION
    USER_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "User session not found"),

    //OWNER
    OWNER_ID_REQUIRED(org.springframework.http.HttpStatus.BAD_REQUEST, "ownerId is required"),
    INVALID_SORT(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid sort parameter"),

    //CAPACITY
    INVALID_CAPACITY_RANGE(org.springframework.http.HttpStatus.BAD_REQUEST, "capacityMin must be <= capacityMax"),

    // TIME
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "startTime must be <= endTime"),

    // AUTH & SECURITY
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),

    // SYSTEM
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
