package com.khaliv.reservationsystem.controller;

import com.khaliv.reservationsystem.dto.CreateReservationRequest;
import com.khaliv.reservationsystem.dto.ReservationResponse;
import com.khaliv.reservationsystem.entity.ReservationStatus;
import com.khaliv.reservationsystem.exception.ResourceNotFoundException;
import com.khaliv.reservationsystem.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    private ReservationResponse sampleResponse(
            ReservationStatus status) {

        return new ReservationResponse(
                10L,
                1L,
                "Concert Ticket A",
                2,
                status,
                LocalDateTime.of(
                        2026, 9, 5, 19, 0
                )
        );
    }

    @Test
    void createReservation_shouldReturnCreated()
            throws Exception {

        when(reservationService.createReservation(
                any(CreateReservationRequest.class)))
                .thenReturn(
                        sampleResponse(
                                ReservationStatus.PENDING
                        )
                );

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));
    }

    @Test
    void createReservation_shouldRejectInvalidQuantity()
            throws Exception {

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 1,
                                  "quantity": 0
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(reservationService, never())
                .createReservation(any());
    }

    @Test
    void getAllReservations_shouldReturnReservations()
            throws Exception {

        when(reservationService.getAllReservations())
                .thenReturn(List.of(
                        sampleResponse(
                                ReservationStatus.PENDING
                        )
                ));

        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].productName")
                        .value("Concert Ticket A"));
    }

    @Test
    void getReservationById_shouldReturnReservation()
            throws Exception {

        when(reservationService.getReservationById(10L))
                .thenReturn(
                        sampleResponse(
                                ReservationStatus.PENDING
                        )
                );

        mockMvc.perform(get("/api/reservations/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status")
                        .value("PENDING"));
    }

    @Test
    void cancelReservation_shouldReturnCancelled()
            throws Exception {

        when(reservationService.cancelReservation(10L))
                .thenReturn(
                        sampleResponse(
                                ReservationStatus.CANCELLED
                        )
                );

        mockMvc.perform(
                        patch("/api/reservations/10/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CANCELLED"));
    }

    @Test
    void confirmReservation_shouldReturnConfirmed()
            throws Exception {

        when(reservationService.confirmReservation(10L))
                .thenReturn(
                        sampleResponse(
                                ReservationStatus.CONFIRMED
                        )
                );

        mockMvc.perform(
                        patch("/api/reservations/10/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("CONFIRMED"));
    }

    @Test
    void getReservationById_shouldReturn404WhenMissing()
            throws Exception {

        when(reservationService.getReservationById(999L))
                .thenThrow(
                        new ResourceNotFoundException(
                                "Reservation not found"
                        )
                );

        mockMvc.perform(get("/api/reservations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Reservation not found"));
    }
}