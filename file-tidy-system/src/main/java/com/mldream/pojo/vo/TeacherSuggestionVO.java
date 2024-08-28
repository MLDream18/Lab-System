package com.mldream.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherSuggestionVO {
    private Integer id;
    private String courseName;
    private String teacherName;
    private String labName;
    private String semester;
    private String environmentCommand;
    private String appUrl;
    private LocalDate submitDate;
    private String adminReply;
    private LocalDateTime adminReplyDate;
}
