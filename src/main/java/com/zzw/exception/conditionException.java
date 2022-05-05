package com.zzw.exception;

public class conditionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String code;

    public conditionException(String code,String name) {
        super(name);
        this.code = code;
    }

    public conditionException(String name) {
        super(name);
        this.code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
