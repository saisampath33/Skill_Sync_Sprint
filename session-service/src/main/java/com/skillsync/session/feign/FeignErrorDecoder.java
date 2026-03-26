package com.skillsync.session.feign;

import com.skillsync.session.exception.BadRequestException;
import com.skillsync.session.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Feign client error";
        try (InputStream bodyIs = response.body().asInputStream()) {
            errorMessage = new String(bodyIs.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Ignore if body can't be read
        }

        switch (response.status()) {
            case 404:
                return new ResourceNotFoundException("Resource not found via internal call. Target: " + methodKey);
            case 400:
                return new BadRequestException("Bad request in internal call: " + errorMessage);
            default:
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
