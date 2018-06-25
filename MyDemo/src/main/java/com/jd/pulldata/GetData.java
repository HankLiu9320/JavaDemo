package com.jd.pulldata;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GetData {
    private int success;
    private List<Map<String, String>> rows;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<Map<String, String>> getRows() {
        return rows;
    }

    public void setRows(List<Map<String, String>> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(rows != null) {
            for(Map<String, String> row : rows) {
                sb.append(row);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
