package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.Device;
import com.example.bookingmeeting_be.model.MeetingRoom;
import com.example.bookingmeeting_be.model.RoomAsset;
import com.example.bookingmeeting_be.model.dto.MeetingRoomForm;
import com.example.bookingmeeting_be.repository.DeviceRepository;
import com.example.bookingmeeting_be.repository.MeetingRoomRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomRepository repository;

    @Autowired
    private DeviceRepository deviceRepository;

    public List<MeetingRoom> getAll() {
        return repository.findAll();
    }

    // Nếu bạn có fetch join trong repo
    public List<MeetingRoom> getAllWithAssets() {
        return repository.findAllWithAssets();
    }

    public Optional<MeetingRoom> getById(Integer id) {
        return repository.findById(id);
    }

    public MeetingRoomForm buildFormForEdit(Integer roomId) {
        MeetingRoom room = repository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng họp ID = " + roomId));

        MeetingRoomForm form = new MeetingRoomForm();

        form.setId(room.getId());
        form.setName(room.getName());
        form.setCapacity(room.getCapacity());
        form.setFeatures(room.getFeatures());
        form.setStatus(room.getStatus());

        if (room.getRoomAssets() != null) {
            room.getRoomAssets().forEach(asset -> {
                MeetingRoomForm.AssetItem item = new MeetingRoomForm.AssetItem();
                item.setDeviceId(asset.getDevice().getId());
                item.setQuantity(asset.getQuantity());
                form.getAssetItems().add(item);
            });
        }

        return form;
    }

    // ✅ Tạo/cập nhật phòng + RoomAsset (LƯU DB)
    @Transactional
    public MeetingRoom saveFromForm(MeetingRoomForm form) {

        MeetingRoom room = (form.getId() != null)
                ? repository.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Room not found: " + form.getId()))
                : new MeetingRoom();

        room.setName(form.getName());
        room.setCapacity(form.getCapacity());
        room.setFeatures(form.getFeatures());
        room.setStatus(form.getStatus());

        // ✅ edit: xóa room_asset cũ trước
        room.getRoomAssets().clear();

        if (form.getAssetItems() != null) {
            for (MeetingRoomForm.AssetItem assetItem : form.getAssetItems()) {

                // ✅ FIX: assetItem null thì mới skip
                if (assetItem == null) continue;

                if (assetItem.getDeviceId() == null) continue;
                if (assetItem.getQuantity() == null || assetItem.getQuantity() <= 0) continue;

                Device device = deviceRepository.findById(assetItem.getDeviceId())
                        .orElseThrow(() -> new RuntimeException("Device not found: " + assetItem.getDeviceId()));

                RoomAsset ra = new RoomAsset();
                ra.setMeetingRoom(room);
                ra.setDevice(device);
                ra.setQuantity(assetItem.getQuantity());

                room.getRoomAssets().add(ra);
            }
        }

        return repository.save(room);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
