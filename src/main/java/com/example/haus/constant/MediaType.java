package com.example.haus.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum MediaType {

    @JsonProperty("image")
    Image,
    @JsonProperty("video")
    Video
}