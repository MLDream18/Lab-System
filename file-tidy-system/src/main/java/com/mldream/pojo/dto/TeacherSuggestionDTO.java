package com.mldream.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherSuggestionDTO {
    private Integer courseNameId;
    private Integer teacherId;
    private Integer labId;
    private Integer semesterId;
    private String environmentCommand;
}
