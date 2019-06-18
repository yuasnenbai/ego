package com.ego.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ego.sms")
public class SmsProperties {
    private String accessKeyId;
    private String accessKeySecret;
}
