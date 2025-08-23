package org.mandarin.booking.adapter.webapi;

/**
 * Centralizes API status codes to achieve type-safety and remove string duplication.
 * JSON representation remains identical via enum.name().
 */
public enum ApiStatus {
    SUCCESS,
    BAD_REQUEST,
    UNAUTHORIZED,
    INTERNAL_SERVER_ERROR
}
