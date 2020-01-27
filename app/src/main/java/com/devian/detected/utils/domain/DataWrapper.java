package com.devian.detected.utils.domain;

@SuppressWarnings("unused")
public class DataWrapper<T> {
    private T object;
    private boolean error;
    private int code;

    public DataWrapper(T object) {
        this.object = object;
        this.error = false;
    }

    public DataWrapper(int code) {
        this.code = code;
        this.error = true;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
