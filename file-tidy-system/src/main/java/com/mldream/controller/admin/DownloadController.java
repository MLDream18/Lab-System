package com.mldream.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/admin")
public class DownloadController {

    private static final String UPLOAD_FOLDER = "upload-files/";

    @GetMapping("/download/**")
    public ResponseEntity<Resource> download(HttpServletRequest request) throws Exception {
        String url = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        url = url.replaceFirst("/admin/download/", "");
        url = URLDecoder.decode(url, StandardCharsets.UTF_8);
        Path path = Paths.get(UPLOAD_FOLDER + url);
        log.info("download file path: " + path);
        Resource resource = new PathResource(path);

        String filename = path.getFileName().toString().substring(37); // 获取文件名
        log.info("download file: " + filename);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                .body(resource);
    }

}
