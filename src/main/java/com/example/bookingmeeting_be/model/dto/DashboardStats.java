package com.example.bookingmeeting_be.model.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    // Card: Lịch họp hôm nay
    private long meetingsToday;
    private long deltaVsYesterday;

    // Card: Cuộc họp tháng này
    private long meetingsThisMonth;
    private double growthPct;

    // Card: Người tham gia (hoạt động)
    private long activeParticipants;

    // Bảng: Lịch họp gần đây
    private List<RecentMeetingDTO> recentMeetings;

    // Bảng: Phòng họp được sử dụng nhiều
    private List<RoomUsageDTO> topRooms;

    // Chart: Thống kê hôm nay (theo giờ)
    private List<String> chartLabels;
    private List<Long> chartValues;
}