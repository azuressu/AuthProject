package com.auth.config;

import com.auth.presentation.dto.LoginRequest;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("사용자 API")
                .pathsToMatch("/login", "/signup", "/admin/signup", "/admin/users/**")
                // API 관련 인터페이스가 있는 패키지 명시
                .packagesToScan("com.auth.presentation")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentation").version("1.0"))
                .components(new Components()
                        .addSchemas("LoginRequest", new Schema<LoginRequest>()
                                .type("object")
                                .addProperty("username", new Schema<>().type("string").example("username"))
                                .addProperty("password", new Schema<>().type("string").example("password"))
                        )
                        .addSchemas("LoginSuccessResponse", new Schema<>()
                                .type("object")
                                .addProperty("token", new Schema<>().type("string").example("Bearer eKDIkdfjoakIdkfjpekdkcjdkoIOdjOKJDFOlLDKFJKL"))
                        )
                        .addSchemas("ErrorDetails", new Schema<>()
                                .type("object")
                                .addProperty("code", new Schema<String>().type("string").example("INVALID_CREDENTIALS"))
                                .addProperty("message", new Schema<String>().type("string").example("아이디 또는 비밀번호가 올바르지 않습니다."))
                        )
                        .addSchemas("LoginFailResponse", new Schema<>()
                                .type("object")
                                .addProperty("error", new Schema<>().$ref("#/components/schemas/ErrorDetails"))
                        )
                )
                .addServersItem(new Server().url("/"))
                .path("/login", new PathItem()
                        .post(new Operation()
                                .tags(Arrays.asList("사용자 API"))
                                .summary("로그인")
                                .description("로그인 API")
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content()
                                                .addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginRequest"))
                                                )
                                        )
                                )
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse()
                                                .description("로그인 성공")
                                                .content(new Content()
                                                        .addMediaType("application/json",
                                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginSuccessResponse"))
                                                        )
                                                )
                                        )
                                        .addApiResponse("400", new ApiResponse()
                                                .description("로그인 실패")
                                                .content(new Content()
                                                        .addMediaType("application/json",
                                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginFailResponse"))
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

}
