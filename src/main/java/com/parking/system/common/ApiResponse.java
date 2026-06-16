package com.parking.system.common;

public record ApiResponse<T>(boolean success, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> message(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> todo(String message) {
        return new ApiResponse<>(true, message, null);
    }
}
