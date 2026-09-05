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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {

        Product product = productRepository.findByIdForUpdate(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.quantity()) {
            throw new ConflictException("Insufficient stock");
        }

        product.setStock(product.getStock() - request.quantity());
        productRepository.save(product);

        Reservation reservation = Reservation.builder()
                .product(product)
                .quantity(request.quantity())
                .status(ReservationStatus.PENDING)
                .build();

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException("Reservation already cancelled");
        }

        Product product = productRepository.findByIdForUpdate(
                reservation.getProduct().getId()
        ).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStock(product.getStock() + reservation.getQuantity());
        productRepository.save(product);

        reservation.setStatus(ReservationStatus.CANCELLED);

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse confirmReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException("Cancelled reservation cannot be confirmed");
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new ConflictException("Reservation already confirmed");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);

        return toResponse(reservationRepository.save(reservation));
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getProduct().getId(),
                reservation.getProduct().getName(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}