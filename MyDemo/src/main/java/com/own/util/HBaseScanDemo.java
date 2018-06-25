package com.own.util;

import java.util.List;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseScanDemo {
    public static void main(String[] args) {
        ConnectionFactory.createConnection(getConfig());
        HTable table = (HTable) hbaseDao.getTable(bo);
        Scan scan = new Scan();
        scan.setCaching(10);
        scan.setCacheBlocks(false);
        ResultScanner rs = table.getScanner(scan);

        for(int i = 0; i < 10; i++) {
            Result r = rs.next();
            List<Cell> cells = r.listCells();

            for(Cell cell : cells) {
                String rowkey = Bytes.toString(CellUtil.cloneRow(cell));
                String f = Bytes.toString(CellUtil.cloneFamily(cell));
                String col = Bytes.toString(CellUtil.cloneQualifier(cell));
                long ts = cell.getTimestamp();
                String value = Bytes.toString(CellUtil.cloneValue(cell));
                String log = rowkey + "/" + f + ":" + col + "/" + ts + "=" + value;
            }
        }
    }
}
