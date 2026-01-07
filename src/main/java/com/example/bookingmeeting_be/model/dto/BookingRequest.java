package com.example.bookingmeeting_be.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingRequest {
    private String title;
    private Integer roomId;
    private Integer hostUserId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    private List<DeviceRequest> devices;

    @Data
    public static class DeviceRequest {
        private Integer deviceId;
        private int quantity;
    }
}