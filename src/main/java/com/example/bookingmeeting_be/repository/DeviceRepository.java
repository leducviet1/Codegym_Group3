package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
    List<Device> findByCategory(String category);
    List<Device> findByStatus(String status);
}

