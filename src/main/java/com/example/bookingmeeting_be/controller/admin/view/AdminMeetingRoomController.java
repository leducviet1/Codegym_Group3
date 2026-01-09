//package com.example.bookingmeeting_be.controller.admin.view;
//
//import com.example.bookingmeeting_be.model.MeetingRoom;
//import com.example.bookingmeeting_be.services.MeetingRoomService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/admin/meeting-rooms")
//public class AdminMeetingRoomController {
//
//    @Autowired
//    private MeetingRoomService meetingRoomService;
//
//    // Hiển thị danh sách tất cả phòng họp
//    @GetMapping
//    public String listMeetingRooms(Model model) {
//        model.addAttribute("meetingRooms", meetingRoomService.getAll());
//        return "admin/admin-meeting-rooms";
//    }
//
//    // Hiển thị form tạo phòng họp mới
//    @GetMapping("/create")
//    public String showCreateForm(Model model) {
//        model.addAttribute("meetingRoom", new MeetingRoom());
//        return "admin/admin-meeting-room-form";
//    }
//
//    // Lưu phòng họp mới
//    @PostMapping
//    public String createMeetingRoom(@ModelAttribute MeetingRoom meetingRoom, RedirectAttributes redirectAttributes) {
//        meetingRoomService.save(meetingRoom);
//        redirectAttributes.addFlashAttribute("message", "Phòng họp đã được tạo thành công!");
//        return "redirect:/admin/meeting-rooms";
//    }
//
//    // Hiển thị chi tiết phòng họp
//    @GetMapping("/{id}")
//    public String getMeetingRoomDetail(@PathVariable Long id, Model model) {
//        Optional<MeetingRoom> meetingRoom = meetingRoomService.getById(id);
//        if (meetingRoom.isPresent()) {
//            model.addAttribute("meetingRoom", meetingRoom.get());
//            return "admin/admin-meeting-room-detail";
//        }
//        return "redirect:/admin/meeting-rooms";
//    }
//
//    // Hiển thị form chỉnh sửa phòng họp
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        Optional<MeetingRoom> meetingRoom = meetingRoomService.getById(id);
//        if (meetingRoom.isPresent()) {
//            model.addAttribute("meetingRoom", meetingRoom.get());
//            return "admin/admin-meeting-room-form";
//        }
//        return "redirect:/admin/meeting-rooms";
//    }
//
//    // Cập nhật phòng họp
//    @PostMapping("/{id}")
//    public String updateMeetingRoom(@PathVariable Long id, @ModelAttribute MeetingRoom meetingRoom, RedirectAttributes redirectAttributes) {
//        Optional<MeetingRoom> existingRoom = meetingRoomService.getById(id);
//        if (existingRoom.isPresent()) {
//            meetingRoom.setId(id);
//            meetingRoomService.save(meetingRoom);
//            redirectAttributes.addFlashAttribute("message", "Phòng họp đã được cập nhật thành công!");
//            return "redirect:/admin/meeting-rooms";
//        }
//        return "redirect:/admin/meeting-rooms";
//    }
//
//    // Xóa phòng họp
//    @DeleteMapping("/{id}")
//    public String deleteMeetingRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        Optional<MeetingRoom> meetingRoom = meetingRoomService.getById(id);
//        if (meetingRoom.isPresent()) {
//            meetingRoomService.delete(id);
//            redirectAttributes.addFlashAttribute("message", "Phòng họp đã được xóa thành công!");
//        }
//        return "redirect:/admin/meeting-rooms";
//    }
//
//    // Xóa phòng họp qua POST (vì HTML form không hỗ trợ DELETE)
//    @PostMapping("/{id}/delete")
//    public String deleteMeetingRoomPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
//        return deleteMeetingRoom(id, redirectAttributes);
//    }

//}
//
package com.example.bookingmeeting_be.controller.admin.view;

import com.example.bookingmeeting_be.model.MeetingRoom;
import com.example.bookingmeeting_be.model.dto.MeetingRoomForm;
import com.example.bookingmeeting_be.services.DeviceService;
import com.example.bookingmeeting_be.services.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/meeting-rooms")
public class AdminMeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private DeviceService deviceService;

    // LIST
    @GetMapping
    public String listMeetingRooms(Model model) {
        model.addAttribute("meetingRooms", meetingRoomService.getAllWithAssets());
        return "admin/admin-meeting-rooms";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("meetingRoomForm", new MeetingRoomForm());
        model.addAttribute("devices", deviceService.getAll());
        return "admin/admin-meeting-room-form";
    }

    // CREATE SUBMIT
    @PostMapping("/create")
    public String createMeetingRoom(
            @ModelAttribute("meetingRoomForm") MeetingRoomForm form,
            RedirectAttributes redirectAttributes
    ) {
        meetingRoomService.saveFromForm(form);
        redirectAttributes.addFlashAttribute("message", "Phòng họp đã được tạo thành công!");
        return "redirect:/admin/meeting-rooms";
    }

    // DETAIL
    @GetMapping("/{id}")
    public String getMeetingRoomDetail(@PathVariable Integer id, Model model) {
        MeetingRoom room = meetingRoomService.getById(id).orElse(null);
        if (room == null) return "redirect:/admin/meeting-rooms";

        model.addAttribute("meetingRoom", room);
        return "admin/admin-meeting-room-detail";
    }

    // EDIT FORM
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("meetingRoomForm", meetingRoomService.buildFormForEdit(id));
        model.addAttribute("devices", deviceService.getAll());
        return "admin/admin-meeting-room-form";
    }

    // UPDATE SUBMIT
    @PostMapping("/{id}")
    public String updateMeetingRoom(
            @PathVariable Integer id,
            @ModelAttribute("meetingRoomForm") MeetingRoomForm form,
            RedirectAttributes redirectAttributes
    ) {
        form.setId(id);
        meetingRoomService.saveFromForm(form);
        redirectAttributes.addFlashAttribute("message", "Phòng họp đã được cập nhật thành công!");
        return "redirect:/admin/meeting-rooms";
    }

    // DELETE
    @PostMapping("/{id}/delete")
    public String deleteMeetingRoom(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        meetingRoomService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Phòng họp đã được xóa thành công!");
        return "redirect:/admin/meeting-rooms";
    }
}
