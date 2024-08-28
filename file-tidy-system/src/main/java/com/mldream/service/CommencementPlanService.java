package com.mldream.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CommencementPlanService {

    boolean handleCommencementPlan(List<String[]> commencementPlanList, LocalDate startDate, LocalDate endDate);

}
