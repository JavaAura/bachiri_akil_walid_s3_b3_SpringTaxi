package com.springtaxi.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.springtaxi.app.dao.ReservationDao;
// import com.springtaxi.app.dto.ReservationDto;
import com.springtaxi.app.entity.Reservation;
import com.springtaxi.app.entity.enums.DriverStatut;
import com.springtaxi.app.exception.ElementNotFoundException;
import com.springtaxi.app.mapper.ReservationMapper;
import com.springtaxi.app.exception.ElementAlreadyExistsException;
import com.springtaxi.app.repository.ReservationRepository;
import com.springtaxi.app.util.ResponseObj;

import java.util.HashMap;
import java.util.List;
// import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ReservationService {
    @Autowired private ReservationRepository repository;
    @Autowired private ReservationDao dao;
    // @Autowired private ReservationMapper mapper;
        


    public List<Reservation> getAll() {
        List<Reservation> reservations = repository.findAll();
        // return reservations.stream().map(mapper::toDto).collect(Collectors.toList());
        return reservations;
    }

    public ResponseObj add(Reservation reservation) {        
        Reservation foundReservation = repository.findByPickupAddressAndDropoffAddressAndPrice(
            reservation.getPickupAddress(),
            reservation.getDropoffAddress(),
            reservation.getPrice());

        if (foundReservation == null) {
            if (reservation.getDriver().getStatut().toString().equals(DriverStatut.AVAILABLE.toString())) {
                repository.save(reservation);
                return new ResponseObj(HttpStatus.CREATED.value(), "Reservation created.");
            } else throw new ElementNotFoundException("Driver's not available at the moment.");
            
        } else 
            throw new ElementAlreadyExistsException(
                "Reservation with addresses " +
                reservation.getPickupAddress() + ", " +
                reservation.getDropoffAddress() + " and the price " +
                reservation.getPrice() + " already exists.");
    }

    public Reservation getById(Long id){
        return repository.findById(id).orElseThrow(() -> new ElementNotFoundException("Reservation not found."));
    }

    public ResponseObj delete(Long id) {
        Reservation foundReservation = getById(id);
        repository.delete(foundReservation);
        return new ResponseObj(HttpStatus.OK.value(), "Reservation deleted.");
    }

    public ResponseObj update(Reservation reservation) {
        Reservation foundReservation = getById(reservation.getId());
        int changes = 0;
        if (reservation.getPickupAddress() != null) {foundReservation.setPickupAddress(reservation.getPickupAddress()); changes++;}
        if (reservation.getDropoffAddress() != null) {foundReservation.setDropoffAddress(reservation.getDropoffAddress()); changes++;}
        if (reservation.getPrice() != null) {foundReservation.setPrice(reservation.getPrice()); changes++;}
        if (reservation.getStatus() != null) {foundReservation.setStatus(reservation.getStatus()); changes++;}
        if (reservation.getDistanceKm() != null) {foundReservation.setDistanceKm(reservation.getDistanceKm()); changes++;}
        if (reservation.getVehicle() != null) {foundReservation.setVehicle(reservation.getVehicle()); changes++;}
        if (reservation.getDriver() != null) {
            if (reservation.getDriver().getStatut().toString().equalsIgnoreCase(DriverStatut.AVAILABLE.toString())) {
                foundReservation.setDriver(reservation.getDriver());
                changes++;
            } else throw new ElementNotFoundException("Driver's not available at the moment.");
        }

        repository.save(foundReservation);
        return new ResponseObj(HttpStatus.OK.value(), "Reservation updated with("+ changes+ ") changes.");
    }

    public HashMap<String, Object> analytics() {
        return dao.getAllAnalytics(getAll());
    }
}
