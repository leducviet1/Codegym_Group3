package com.example.bookingmeeting_be.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class MeetingRoomForm {
    private Integer id;
    private String name;
    private Integer capacity;
    private String features;
    private String status;
    private List<AssetItem> assetItems = new ArrayList<>();
    public static class AssetItem {
        private Integer deviceId;
        private Integer quantity;

        public Integer getDeviceId() { return deviceId; }
        public void setDeviceId(Integer deviceId) { this.deviceId = deviceId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

}
