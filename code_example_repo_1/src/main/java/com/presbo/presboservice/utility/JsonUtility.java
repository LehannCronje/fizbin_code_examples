package com.presbo.presboservice.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public class JsonUtility {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new JSR310Module());
        OBJECT_MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String convertObjectToString(Object objectToConvertToString) throws Exception {

        try {
            return OBJECT_MAPPER.writeValueAsString(objectToConvertToString);
        } catch (Exception jpe) {
            throw new Exception("Could not convert objectToConvertToString to string. Object = " + objectToConvertToString, jpe);
        }
    }

    public static <T> T convertStringToObject(String stringToConvertToObject, Class<T> expectedClassType) throws Exception {

        try {
            return OBJECT_MAPPER.readValue(stringToConvertToObject, expectedClassType);
        } catch (JsonProcessingException ex) {
            String message = String.format("Json Processing Exception while converting string to object. String = %s, errorMessage = %s, Class = ", stringToConvertToObject, ex.getMessage(), expectedClassType.toString());
            throw new Exception(message, ex);
        }
    }

}
