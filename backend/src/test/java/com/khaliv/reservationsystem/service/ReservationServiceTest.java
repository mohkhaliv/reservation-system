package com.khaliv.reservationsystem.service;

import com.khaliv.reservationsystem.dto.CreateReservationRequest;
import com.khaliv.reservationsystem.dto.ReservationResponse;
import com.khaliv.reservationsystem.entity.Product;
import com.khaliv.reservationsystem.entity.Reservation;
import com.khaliv.reservationsystem.entity.ReservationStatus;
import com.khaliv.reservationsystem.exception.ConflictException;
import com.khaliv.reservationsystem.exception.ResourceNotFoundException;
import com.khaliv.reservationsystem.repository.ProductRepository;
import com.khaliv.reservationsystem.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Product product;
    private Reservation reservation;

    @BeforeEach
    void setUp() {

        product = Product.builder()
                .id(1L)
                .name("Concert Ticket A")
                .description("Regular ticket")
                .price(new BigDecimal("250000"))
                .stock(5)
                .build();

        reservation = Reservation.builder()
                .id(10L)
                .product(product)
                .quantity(2)
                .status(ReservationStatus.PENDING)
                .createdAt(LocalDateTime.of(2026, 9, 5, 19, 0))
                .build();
    }

    @Test
    void createReservation_shouldReduceStockAndCreatePendingReservation() {

        CreateReservationRequest request =
                new CreateReservationRequest(1L, 2);

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation saved = invocation.getArgument(0);
                    saved.setId(10L);
                    saved.setCreatedAt(
                            LocalDateTime.of(2026, 9, 5, 19, 0)
                    );
                    return saved;
                });

        ReservationResponse result =
                reservationService.createReservation(request);

        assertThat(product.getStock()).isEqualTo(3);
        assertThat(result.quantity()).isEqualTo(2);
        assertThat(result.status())
                .isEqualTo(ReservationStatus.PENDING);

        verify(productRepository).save(product);
        verify(reservationRepository)
                .save(any(Reservation.class));
    }

    @Test
    void createReservation_shouldThrowWhenProductNotFound() {

        CreateReservationRequest request =
                new CreateReservationRequest(999L, 1);

        when(productRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void createReservation_shouldThrowWhenStockInsufficient() {

        product.setStock(2);

        CreateReservationRequest request =
                new CreateReservationRequest(1L, 5);

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                ConflictException.class,
                () -> reservationService.createReservation(request)
        );

        verify(reservationRepository, never())
                .save(any());
    }

    @Test
    void getReservationById_shouldReturnReservation() {

        when(reservationRepository.findById(10L))
                .thenReturn(Optional.of(reservation));

        ReservationResponse result =
                reservationService.getReservationById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.productId()).isEqualTo(1L);
        assertThat(result.quantity()).isEqualTo(2);
    }

    @Test
    void getAllReservations_shouldReturnReservations() {

        when(reservationRepository.findAll())
                .thenReturn(List.of(reservation));

        List<ReservationResponse> result =
                reservationService.getAllReservations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
    }

    @Test
    void cancelReservation_shouldRestoreStock() {

        product.setStock(3);

        when(reservationRepository.findById(10L))
                .thenReturn(Optional.of(reservation));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse result =
                reservationService.cancelReservation(10L);

        assertThat(result.status())
                .isEqualTo(ReservationStatus.CANCELLED);

        assertThat(product.getStock())
                .isEqualTo(5);

        verify(productRepository).save(product);
    }

    @Test
    void confirmReservation_shouldSetStatusConfirmed() {

        when(reservationRepository.findById(10L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse result =
                reservationService.confirmReservation(10L);

        assertThat(result.status())
                .isEqualTo(ReservationStatus.CONFIRMED);

        assertThat(product.getStock())
                .isEqualTo(5);
    }
}