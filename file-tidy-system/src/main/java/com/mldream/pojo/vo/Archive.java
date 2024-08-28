package com.mldream.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Archive {
    private String Sid;
    private String fileName;
    private String originalName;
    private String path;
    private Long size;
    private String fileType;
}
