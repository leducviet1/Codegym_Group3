package com.example.bookingmeeting_be.controller.user;

import com.example.bookingmeeting_be.model.MeetingRoom;
import com.example.bookingmeeting_be.services.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller(value = "UserMeetingRoomController")
@RequestMapping("/users")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @GetMapping("/meeting-rooms")
    public String listRooms(Model model) {
        List<MeetingRoom> rooms = meetingRoomService.getAll();
        model.addAttribute("rooms", rooms);
        return "users/meeting-room-list";
    }
}