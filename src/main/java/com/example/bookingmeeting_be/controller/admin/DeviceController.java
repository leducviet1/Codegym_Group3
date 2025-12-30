package com.example.bookingmeeting_be.controller.admin;

import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    @Autowired
    private DeviceService service;

    @GetMapping
    public List<Device> getAll() { return service.getAll(); }

    @PostMapping
    public Device create(@RequestBody Device device) { return service.create(device); }

    @PutMapping("/{id}")
    public Device update(@PathVariable int id, @RequestBody Device device) {
        return service.update(id, device);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) { service.delete(id); }

    @PatchMapping("/{id}/quantity")
    public void updateQuantity(@PathVariable int id, @RequestParam int delta) {
        service.updateQuantity(id, delta);
    }
}

