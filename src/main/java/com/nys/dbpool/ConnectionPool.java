package com.nys.dbpool;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.nys.config.DbConfig;
import com.nys.enums.ResultEnum;
import com.nys.exception.ConnectionPoolException;

import java.sql.Connection;
/**
 * 连接池
 * @author nysheng
 *
 */
public class ConnectionPool {
	//空闲连接池
	private CopyOnWriteArrayList<Connection> freePool=new CopyOnWriteArrayList<Connection>();
	//活跃连接池
	private CopyOnWriteArrayList<Connection> activePool=new CopyOnWriteArrayList<Connection>();
	//连接池配置
	private DbConfig dbConfig;
	//记录已创建的连接数
	private AtomicInteger countConnection=new AtomicInteger();
	public ConnectionPool(DbConfig dbConfig) {
		this.dbConfig=dbConfig;
		try {
			Class.forName(dbConfig.getDriver());
			//1. 初始化连接池
			for(int i=0;i<dbConfig.getInitConnections();i++) {
				Connection connection=newConnection();
				if(connection!=null) {
					freePool.add(connection);
				}
			}
			//2.新开一线程，启动自检机制
			new Thread(new Runnable() {
				@Override
				public void run() {
					recheckConnection();
				}
			}).start();
		} catch (ClassNotFoundException e) {
			throw new ConnectionPoolException(ResultEnum.DRIVER_NOT_FOUND);
		}
	}
	
	/**
	 * 创建新连接
	 * @return
	 */
	private synchronized Connection newConnection() {
		Connection connection=null;
		try {
			connection=DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
			countConnection.incrementAndGet();
		} catch (SQLException e) {
			throw new ConnectionPoolException(ResultEnum.CONNECTION_FAIL);
		}
		return connection;
	}
	/**
	 * 判断连接是否可用
	 * @param connection
	 * @return
	 */
	private boolean isAlive(Connection connection) {
		try {
			if(connection==null||connection.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			throw new ConnectionPoolException(ResultEnum.CONNECTION_STATUS_ERROE);
		}
		return true;
	}
	/**
	 * 检查当前连接数是否低于最低线程数，是则创建新线程
	 */
	private synchronized void recheckConnection() {
		//1.检查所有空闲连接是否可用，不可用的直接关闭连接
		//使用迭代器来进行数据的遍历删除，避免快速迭代失败
		Iterator<Connection> it=freePool.iterator();
		while(it.hasNext()) {
			if(!isAlive(it.next())) {
				it.remove();
			}
		}
		//2.检查当前连接数是否满足最低空闲连接数，若低于最小空闲数，新增连接放入空闲池
		final int count=countConnection.get();
		if(count<dbConfig.getMinFreeConnections()) {
			for(int i=count;i<2;i++) {
				Connection connection=newConnection();
				if(connection!=null) {
					freePool.add(connection);
				}
			}
		}
		try {
			long start=System.currentTimeMillis();
			while(System.currentTimeMillis()-start<dbConfig.getRecheckTime()) {
				wait(dbConfig.getRecheckTime());
			}
			recheckConnection();
		} catch (InterruptedException e) {
			throw new ConnectionPoolException(ResultEnum.INTERRUPT_ERROR);
		}
	}
	/**
	 * 从连接池中获取连接
	 * @return
	 */
	public synchronized Connection getConnecion() {
		Connection connection=null;
		//1. 判断当前已连接数是否小于最大活跃连接数
		if(activePool.size()<dbConfig.getMaxActiveConnection()) {
			//2.如果空闲连接池里存在连接，拿出连接
			if(freePool.size()>0) {
				connection=freePool.remove(0);
			}else {
				//3.空闲连接不够，创建新连接
				connection=newConnection();
			}
			if(isAlive(connection)) {
				activePool.add(connection);
			}
		}else {
			//4.活跃连接池内连接数已满，阻塞线程，等待唤醒
			try {
				wait(dbConfig.getConnectionTimeOut());
			} catch (InterruptedException e) {
				throw new ConnectionPoolException(ResultEnum.INTERRUPT_ERROR);
			}
			return getConnecion();
		}
		return connection;
	}
	
	public synchronized boolean releaseConnection(Connection connection) {
		//1.判断连接是否可用
		if(isAlive(connection)) {
			//2.判断空闲池是否已满
			if(freePool.size()<dbConfig.getMaxFreeConnections()) {
				//未满，回收连接
				freePool.add(connection);
			}else {
				//已满，关闭连接并减少连接计数
				try {
					countConnection.decrementAndGet();
					connection.close();
				} catch (SQLException e) {
					throw new ConnectionPoolException(ResultEnum.CONNECTION_CLOSE_FAIL);
				}
			}
			//3.从活跃池中移除连接
			activePool.remove(connection);
			//4.唤醒所有被阻塞的线程
			notifyAll();
			return true;
		}
		return false;
	}
	
}

