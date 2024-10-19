package com.biapay.agentmanagement.deserializer;

import com.biapay.agentmanagement.deserializer.responses.ApiResponse;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class ApiErrorResponseDeserializer implements JsonDeserializer<ApiResponse> {

    @Override
    public ApiResponse deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        log.debug("{}", jsonElement);
        if (jsonElement.getAsJsonObject().isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final String errorCode = jsonObject.get("errorCode") == null ? "" : jsonObject.get("errorCode").getAsString();
            final String errorType = jsonObject.get("type") == null ? "" : jsonObject.get("type").getAsString();
            final String title = jsonObject.get("title") == null ? "" : jsonObject.get("title").getAsString();
            final int status = jsonObject.get("status") == null ? 0 : jsonObject.get("status").getAsInt();
            final String detail = jsonObject.get("detail") == null ? "" : jsonObject.get("detail").getAsString();
            return ApiResponse.builder().errorCode(errorCode).type(errorType).title(title).status(status).detail(detail).build();
        }
        return null;
    }
}
