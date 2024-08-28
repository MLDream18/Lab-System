package com.mldream.pojo.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LaboratorySource {
    private Integer id;
    private Integer labId;
    private Integer week;
    private Integer weekday;
    private String section;
    private Integer state; /* 0：空闲 1：正常上课 2：调课 3：补课 4：培训 5：竞赛 6：考试 7：课程设计 8：其他 */
    private Integer applyFormId;
    private Integer classTimeId;
    private Integer semesterId;
}
