package com.mldream.controller.admin;

import com.mldream.pojo.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentifyController {
    @GetMapping("/getUserInfo")
    public Result getUserInfo() {
        return Result.success();
    }
}
