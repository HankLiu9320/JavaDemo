package com.jd.myutils.log.match;

import lombok.Data;

import java.util.regex.Pattern;

@Data
public class RuleInfo {
    private Integer rowIdx;
    private String firstType;
    private String secondType;
    private String pattern;
    private Integer matchType;

    Pattern compile;
    private Long matchNum = 0L;

    public void incrNum() {
        this.matchNum++;
    }
}