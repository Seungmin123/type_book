package com.muzlive.kitpage.kitpage.config.swagger;

import com.muzlive.kitpage.kitpage.domain.common.dto.resp.CommonResp;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SwaggerCustomApi implements OpenApiCustomiser {

    @Override
    public void customise(OpenAPI openApi) {

        CommonResp<Object> fail = new CommonResp<>(HttpStatus.FORBIDDEN, "related error_msg");

        openApi.getPaths().values().forEach(pathItem -> {
            pathItem.readOperations().forEach(operation -> {
                operation.getResponses().forEach((key,value) -> {
                    if(!ObjectUtils.isEmpty(value.getContent())){
                        value.getContent().forEach((s, mediaType) -> {
                            mediaType.addExamples("success", new Example().$ref(mediaType.getSchema().get$ref()));
                            mediaType.addExamples("fail", new Example().value(fail));
                        });
                    }
                });
            });
        });

        CommonResp<Object> server_error = new CommonResp<>(HttpStatus.INTERNAL_SERVER_ERROR, "error_msg");

        // 공통으로 사용되는 response 설정
        openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            ApiResponses apiResponses = operation.getResponses();

            apiResponses.addApiResponse("200", createApiResponse("success", apiResponses.get("200").getContent()));
            apiResponses.addApiResponse("500", createApiResponse("Server Error",
                    new Content().addMediaType("application/json",
                            new MediaType().schema(new JsonSchema().example(server_error)))));
        }));
    }

    public ApiResponse createApiResponse(String message, Content content){
        return new ApiResponse().description(message).content(content);
    }
}
