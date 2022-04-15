package com.mapple.common.config;

import lombok.Setter;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author hxx
 * @date 2022/4/15 16:34
 */

/**
 * 自定义keepAlive长连接的配置，减少客户端和服务器的连接请求次数
 */
@Setter
@ConfigurationProperties("custom-keep-alive")
@Configuration
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    /**keepalive timeout时间*/
    private int keepAliveTimeout;
    /**核心线程数*/
    private int maxKeepAliveRequests;
    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        //使用对应工厂类提供给我们的接口，定制化Tomcat connector
        ((TomcatServletWebServerFactory) factory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
                //定制化KeepAlive Timeout为30秒
                protocol.setKeepAliveTimeout(keepAliveTimeout);
                //10000个请求则自动断开
                protocol.setMaxKeepAliveRequests(maxKeepAliveRequests);
            }
        });

    }
}
