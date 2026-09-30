package com.intellikart.user_service.dto;

public record AuthResponse(String token, String customerId, String email) {}
