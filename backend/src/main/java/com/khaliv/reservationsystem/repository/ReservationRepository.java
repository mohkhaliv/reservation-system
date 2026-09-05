package com.khaliv.reservationsystem.repository;

import com.khaliv.reservationsystem.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}