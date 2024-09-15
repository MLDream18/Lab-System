package com.mldream.controller.common;


import com.mldream.pojo.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfTokenController {

    private final CsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

    @GetMapping("/csrf-token")
    public Result getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        return Result.success(csrfToken.getToken());
    }

}
