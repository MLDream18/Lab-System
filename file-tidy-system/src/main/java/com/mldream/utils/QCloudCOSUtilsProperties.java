package com.mldream.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qcloud.cos")
public class QCloudCOSUtilsProperties {
    private String endpoint;
    private String bucketName;
}
