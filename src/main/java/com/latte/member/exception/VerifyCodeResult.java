package com.latte.member.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeResult {

    private boolean valid;
    private String message;

    public VerifyCodeResult(String message, boolean valid) {
        this.valid = valid;
        this.message = message;
    }
}
