package com.springtaxi.app.service;

import com.springtaxi.app.dto.VehicleDto;
import com.springtaxi.app.entity.Driver;
import com.springtaxi.app.entity.Vehicle;
import com.springtaxi.app.mapper.VehicleMapper;
import com.springtaxi.app.repository.DriverRepository;
import com.springtaxi.app.repository.VehicleRepository;
import com.springtaxi.app.util.LoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final DriverRepository driverRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper , DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
        this.driverRepository =driverRepository;
    }

    public List<VehicleDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicleMapper.toDtoList(vehicles);
    }

    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found"));
        return vehicleMapper.toDto(vehicle);
    }

    public VehicleDto createVehicle(VehicleDto vehicleDto) {

        if (vehicleDto.getDriverId() == null) {
            throw new IllegalArgumentException("Driver ID cannot be null");
        }

        Driver driver = driverRepository.findById(Math.toIntExact((Long) vehicleDto.getDriverId()))
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);

        vehicle.setDriver(driver);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.toDto(savedVehicle);
    }

    public VehicleDto updateVehicle(VehicleDto vehicleDto, Long id) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found"));
        existingVehicle.setModel(vehicleDto.getModel());
        existingVehicle.setLicensePlate(vehicleDto.getLicensePlate());
        existingVehicle.setMileage(vehicleDto.getMileage());
        existingVehicle.setStatus(vehicleDto.getStatus());
        existingVehicle.setType(vehicleDto.getType());

        LoggerUtil.logInfo("Vehicle updated successfully: " + existingVehicle);
        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        return vehicleMapper.toDto(updatedVehicle);
    }

    public boolean deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle Not Found"));
        vehicleRepository.delete(vehicle);
        LoggerUtil.logInfo("Vehicle deleted successfully: " + vehicle);
        return true;
    }
}
