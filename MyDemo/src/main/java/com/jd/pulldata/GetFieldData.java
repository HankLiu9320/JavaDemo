package com.jd.pulldata;

import java.util.List;

public class GetFieldData {
    private boolean success;
    private List<Field> keys;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Field> getKeys() {
        return keys;
    }

    public void setKeys(List<Field> keys) {
        this.keys = keys;
    }
}
