package com.mldream.task;


import com.mldream.mapper.ApplyFormMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@EnableAsync
public class MultithreadScheduleTask {

    @Autowired
    private ApplyFormMapper applyFormMapper;

    /**
     * 检查申请是否过期
     */
    @Async
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkApplyFormTask() {
        applyFormMapper.checkApplyForm();
    }

}
