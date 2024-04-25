package com.latte.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ResponseData<T> {
    private int statusCode;
    //private String responseMessage;
    private T data;


}
