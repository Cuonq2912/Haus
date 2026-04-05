package com.example.haus.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MultipartJacksonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;

    public MultipartJacksonHttpMessageConverter(ObjectMapper objectMapper) {
        super(MediaType.APPLICATION_OCTET_STREAM);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return objectMapper.readValue(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        objectMapper.writeValue(outputMessage.getBody(), object);
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return MediaType.APPLICATION_OCTET_STREAM.includes(mediaType);
    }
}