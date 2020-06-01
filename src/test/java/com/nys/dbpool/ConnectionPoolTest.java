package com.nys.dbpool;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.nys.config.DbConfig;

public class ConnectionPoolTest {

	@Test
	public void test() {
		ThreadPoolExecutor executor=new ThreadPoolExecutor(15, 15, 60*1000L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>());
		ThreadConnection threadConnection=new ThreadConnection();
		for(int i=0;i<100;i++) {
			executor.execute(threadConnection);
		}
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
class ThreadConnection implements Runnable {
	private static ConnectionPool connectionPool;
	static {
		DbConfig dbConfig=new DbConfig();
		dbConfig.setDriver("com.mysql.jdbc.Driver");
		dbConfig.setUrl("jdbc:mysql://localhost:3306/api-list?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false");
		dbConfig.setUsername("root");
		dbConfig.setPassword("nys123456");
		connectionPool=new ConnectionPool(dbConfig);
	}
	@Override
    public void run() {
        for (int i = 0; i < 100; i++) {
        	Connection connection = connectionPool.getConnecion();
            try {
            	PreparedStatement prepareStatement= connection.prepareStatement("insert into test1(`name`) value(?)");
            	prepareStatement.setString(1, "nys"+i);
            	prepareStatement.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				connectionPool.releaseConnection(connection);
			}
        }
    }
 
}
