package com.digibank.agentmanagement.deserializer;

import com.digibank.agentmanagement.deserializer.responses.ApiResponse;
import com.digibank.agentmanagement.deserializer.responses.ExistingCustomerRegistration;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class ExistingCustomerRegistrationDeserializer implements JsonDeserializer<ExistingCustomerRegistration> {

    @Override
    public ExistingCustomerRegistration deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        log.debug("{}", jsonElement);
        ExistingCustomerRegistration existingCustomerRegistration = null;
        if (jsonElement.getAsJsonObject().isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            final String iAmId = jsonObject.get("iAmId") == null ? "" : jsonObject.get("iAmId").getAsString();
            return new ExistingCustomerRegistration(iAmId);
        }
        return null;
    }
}
