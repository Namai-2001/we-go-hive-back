package com.dev.restLms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User Management API",
        version = "v1",
        description = "이 API는 사용자 관리 관련 작업을 수행합니다."
    ),
    servers = {
        @Server(url = "${EB_ENVIRONMENT_URL:http://localhost:8080}", description = "API 서버")
    }
)
public class SwaggerConfig {
    // Springdoc OpenAPI는 Swagger UI와 자동 연동됩니다.
}
