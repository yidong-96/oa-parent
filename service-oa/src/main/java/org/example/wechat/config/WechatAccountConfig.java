package org.example.wechat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data // 生成get和set方法
@Component // 在spring中进行注册
@ConfigurationProperties(prefix="wechat")  // 读取配置文件
public class WechatAccountConfig {
    private String mpAppId="wxfbe93428bd643f71";
    private String mpAppSecret="71f3401cd51c718d8645e46b1223baa2";
}
