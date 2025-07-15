package com.jd.download;

import lombok.Data;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;


/**
 * hbaseService
 */
@Data
public class HbaseServiceLogService {
    private Connection connection;
    private String env;
    private String erp;
    private String instance;
    private String accesskey;
    private String domain;

    public void init() throws Exception {
        try {
            Configuration configuration = new Configuration();
            configuration.set("bdp.hbase.erp", erp);//你的erp
            configuration.set("bdp.hbase.instance.name", instance);//申请的实例名称
            configuration.set("bdp.hbase.accesskey", accesskey);//实例对应的accesskey，请妥善保管你的AccessKey
            if("dev".equals(env) || "test".equals(env)) {
                configuration.set("hbase.policyserver.domain", domain);
            }
            connection = ConnectionFactory.createConnection(configuration);//保持单例
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
