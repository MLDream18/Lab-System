package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherSuggestion {
    private Integer id;
    private Integer courseNameId;
    private Integer teacherId;
    private Integer labId;
    private Integer semesterId;
    private String environmentCommand;
    private String appUrl;
    private LocalDate submitDate;
    private String adminReply;
    private LocalDateTime adminReplyDate;
//    private Integer submitTeacherId;
}
