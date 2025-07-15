package com.jd.download;

import lombok.Data;

/**
 * Created by zhangrui25 on 2018/3/1.
 * <p>
 * 脚本运行记录
 */
@Data
public class DataDevScriptRunDetail {
    private Long id;
    private Integer type;
    //运行类型 import
    private String runModel;
    private String startRowKey;
    private String endRowKey;
    private Integer currentLogIndex;
}

