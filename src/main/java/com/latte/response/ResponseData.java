package com.latte.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseData<T> {
    private String message;
    //private String responseMessage;
    private T data;


    public void setData(String key, boolean value) {
    }
}
