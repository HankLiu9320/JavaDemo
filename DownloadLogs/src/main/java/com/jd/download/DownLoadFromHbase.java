package com.jd.download;

import com.jd.bdp.common.exception.BdpException;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.List;

public class DownLoadFromHbase {
    public static final Integer MAX_LOG_COUNT = 50;
    static HbaseServiceLogService hbaseService;
    static String ideTable = "jbdp_rc:dataDevLog";

    public static void main(String[] args) throws Exception {
        hbaseService = new HbaseServiceLogService();
        hbaseService.setEnv("test");
        hbaseService.setErp("wangqiaohui");
        hbaseService.setInstance("SL1000000000008");
        hbaseService.setAccesskey("MZYH5UIKEY3BUGSNEIKNMPWGQY");
        hbaseService.setDomain("t.policyserver.jd.com:16100");
        hbaseService.init();
        System.err.println(hbaseService.getConnection());

        DataDevScriptRunDetail dataDevScriptRunDetail = new DataDevScriptRunDetail();
        dataDevScriptRunDetail.setId(2000099822L);
        dataDevScriptRunDetail.setType(1);
        Integer logCount = getLogCount(dataDevScriptRunDetail);
        System.err.println(logCount);
    }

    public static Integer getLogCount(DataDevScriptRunDetail dataDevScriptRunDetail) throws Exception {
        String hbaseStartKey = getKey(dataDevScriptRunDetail, 0);
        String hbaseEndKey = getKey(dataDevScriptRunDetail, 100000);
        int totalCount = 0;
        try {
            Table table = hbaseService.getConnection().getTable(TableName.valueOf(ideTable));
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(hbaseStartKey));
            scan.setStopRow(Bytes.toBytes(hbaseEndKey));
            scan.setBatch(200);
            ResultScanner resultScanner = table.getScanner(scan);
            Result result = resultScanner.next();
            //取连续的log，不连续则中断
            while((result = resultScanner.next()) != null) {
                totalCount++;
            }
        }
        catch(Exception e) {
            throw e;
        }
        return totalCount;
    }

    public static String getKey(DataDevScriptRunDetail dataDevScriptRunDetail, int index) {
        ScriptJdqMessageEnum messageEnum = ScriptJdqMessageEnum.LOG;
        String key = String.format(messageEnum.toFormat(), dataDevScriptRunDetail.getType(), dataDevScriptRunDetail.getId(), messageEnum.toCode(), index);
        return key;
    }

    public String getStartKey(DataDevScriptRunDetail dataDevScriptRunDetail, Integer index) throws Exception {
        ScriptJdqMessageEnum messageEnum = ScriptJdqMessageEnum.LOG;

        if(index == null) {
            index = 0;
        }

        if(StringUtils.isNotEmpty(dataDevScriptRunDetail.getStartRowKey())) {
            String rowKey = dataDevScriptRunDetail.getStartRowKey();
            String[] splits = rowKey.split("_");
            if(splits == null || splits.length < 4) {
                return rowKey;
            }
            Long runDetailId = Long.parseLong(splits[1]);
            if(runDetailId.intValue() != dataDevScriptRunDetail.getId()) {
                throw new BdpException(101, "startKey，endKey 不一致：请联系管理员或者刷新页面再试！");
            }
            if(splits.length == 4) {
                index = Integer.parseInt(splits[3]);
                index++;
                dataDevScriptRunDetail.setCurrentLogIndex(index);
                return String.format(messageEnum.toFormat(), dataDevScriptRunDetail.getType(), dataDevScriptRunDetail.getId(), messageEnum.toCode(), index);
            }
            if(splits.length == 6) {
                Long time = Long.parseLong(splits[5]);
                index = Integer.parseInt(splits[3]);
                index++;
                dataDevScriptRunDetail.setCurrentLogIndex(index);
                rowKey = StringUtils.replace(rowKey, time + "", (time++ + "1"));
                return rowKey;
            }
        }

        String key = String.format(messageEnum.toFormat(), dataDevScriptRunDetail.getType(), dataDevScriptRunDetail.getId(), messageEnum.toCode(), index);
        return key;
    }

    public static String getEndKey(DataDevScriptRunDetail dataDevScriptRunDetail, Integer index) {
        if(index == null) {
            index = 0;
        }
        ScriptJdqMessageEnum messageEnum = ScriptJdqMessageEnum.LOG;

        if(StringUtils.isNotEmpty(dataDevScriptRunDetail.getStartRowKey())) {
            String rowKey = dataDevScriptRunDetail.getStartRowKey();
            String[] splits = rowKey.split("_");
            if(splits == null || splits.length < 4) {
                index = Integer.parseInt(splits[3]);
                return rowKey;
            }
            if(splits.length == 4) {
                index = Integer.parseInt(splits[3]);
            }
            if(splits.length == 6) {
                index = Integer.parseInt(splits[3]);
            }
            index = index + MAX_LOG_COUNT + 1;
        }

        String key = String.format(messageEnum.toFormat(), dataDevScriptRunDetail.getType(), dataDevScriptRunDetail.getId(), messageEnum.toCode(), index);
        return key;
    }

}
