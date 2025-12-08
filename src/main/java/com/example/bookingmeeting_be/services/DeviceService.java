package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    @Autowired
    private DeviceRepository repository;

    public List<Device> getAll() { return repository.findAll(); }
    public Device create(Device device) { return repository.save(device); }
    public Device update(Long id, Device device) {
        device.setId(id);
        return repository.save(device);
    }
    public void delete(Long id) { repository.deleteById(id); }
    public void updateQuantity(Long id, int delta) {
        Device d = repository.findById(id).orElseThrow();
        d.setQuantity(d.getQuantity() + delta);
        repository.save(d);
    }
}
