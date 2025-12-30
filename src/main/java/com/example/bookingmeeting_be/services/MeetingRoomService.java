package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.MeetingRoom;
import com.example.bookingmeeting_be.repository.MeetingRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomService {
    @Autowired
    private MeetingRoomRepository repository;

    public List<MeetingRoom> getAll() {
        return repository.findAll();
    }

    public Optional<MeetingRoom> getById(Long id) {
        return repository.findById(id);
    }

    public MeetingRoom save(MeetingRoom meetingRoom) {
        return repository.save(meetingRoom);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
