package Hbase.utils;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

public class HBaseUtils {

	private static final String HBASER_MASTER_IP = "192.168.3.128";
	private static final String HBASER_MASTER_PORT = "60000";
	private static final String QUORUM_IP = "192.168.3.128";
	private static final String CLIENTPORT = "2181";
	private static Configuration conf = null;
	private static Connection conn = null;

	/**
	 * ��ȡȫ��Ψһ��Configurationʵ��
	 * 
	 * @return
	 */
	public static synchronized Configuration getConfiguration() {
		if (conf == null) {
			conf = HBaseConfiguration.create();
			conf.set("hbase.zookeeper.quorum", QUORUM_IP);
			conf.set("hbase.zookeeper.property.clientPort", CLIENTPORT);
			conf.set("hbase.master", HBASER_MASTER_IP + ":" + HBASER_MASTER_PORT);
		}
		return conf;
	}

	/**
	 * ��ȡȫ��Ψһ��HConnectionʵ��
	 * 
	 * @return
	 * @throws ZooKeeperConnectionException
	 */
	public static synchronized Connection getHConnection() {
		if (conn == null) {
			try {
				conn = ConnectionFactory.createConnection(conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conn;
	}
}