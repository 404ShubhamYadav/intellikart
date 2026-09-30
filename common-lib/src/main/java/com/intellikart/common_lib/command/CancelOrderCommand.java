package com.intellikart.common_lib.command;

public record CancelOrderCommand(Long orderId, String reason) {}
