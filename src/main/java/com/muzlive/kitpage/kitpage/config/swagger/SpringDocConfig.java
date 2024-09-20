package com.muzlive.kitpage.kitpage.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Autowired
    private SwaggerCustomApi swaggerKitCustomApi;

    // Default
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
//                .externalDocs(new ExternalDocumentation().description("Other Document").url("http://wldv2.kihnoframe.com/swagger-ui/index.html"))
                .servers(List.of(new Server().url("https://kit-page-dev.kitbetter.com").description("General Server")))
                //.servers(List.of(new Server().url("http://localhost:8080").description("General Server")))
                .info(apiInfo());
    }
    @Bean
    public GroupedOpenApi createAlbumApi(){
        return GroupedOpenApi.builder()
                .group("ComicBook")
                .pathsToMatch("/v1/comic/**")
                .addOpenApiCustomiser(swaggerKitCustomApi)
                .build();
    }

    private Info apiInfo() {
        return new Info()
                //.title("Spring-doc 1.7 Interface Document")
                .title("KitPage API 1.0 Interface Document")
                .description("If you have any questions, please contact the development engineer Ki Hong.<br><br>" +
                        "그 외, 수정이 필요한 부분이나 추가/삭제가 필요한 API는 아래 문서함에 건의 부탁드립니다.")
                .contact(new Contact().name("KitPlayer Backend Team").url("https://www.notion.so/muzlive/8f5f838ffcc84652997f3466fba7156b?v=48d38013e3c94995b444d2f62e9324b4"))
                .version("0.0.1");
    }
}
