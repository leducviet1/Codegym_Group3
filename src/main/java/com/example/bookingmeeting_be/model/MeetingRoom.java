package com.example.bookingmeeting_be.model;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Meeting_Rooms")
public class MeetingRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "features")
    private String features;

    @Column(name = "status")
    private String status;

    // Quan hệ 1-n với RoomAsset
    @OneToMany(mappedBy = "meetingRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomAsset> roomAssets = new HashSet<>();

    // Constructor
    public MeetingRoom() {
    }

    public MeetingRoom(String name, Integer capacity, String features, String status) {
        this.name = name;
        this.capacity = capacity;
        this.features = features;
        this.status = status;
    }

    // Getter và Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<RoomAsset> getRoomAssets() {
        return roomAssets;
    }

    public void setRoomAssets(Set<RoomAsset> roomAssets) {
        this.roomAssets = roomAssets;
    }
    @Transient
    public String getDescription() {
        return ""; // hoặc return this.note;
    }
}
