package com.muzlive.kitpage.kitpage.config.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String build(CommonException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("code", ex.getCode());
		response.put("message", ex.getMessage());
		response.put("data", ex.getData());

		try {
			return objectMapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			return "{\"code\": \"99999\", \"message\": \"Internal error while building error response.\", \"data\": null}";
		}
	}

	public static String buildFallback() {
		return "{\"code\": \"401\", \"message\": \"Unauthorized\", \"data\": null}";
	}
}
