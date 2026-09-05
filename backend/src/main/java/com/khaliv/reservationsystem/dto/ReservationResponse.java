package com.khaliv.reservationsystem.dto;

import com.khaliv.reservationsystem.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        ReservationStatus status,
        LocalDateTime createdAt
) {
}