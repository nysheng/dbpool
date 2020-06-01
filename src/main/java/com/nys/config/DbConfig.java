package com.nys.config;

/**
 * 配置连接池属性信息
 * @author nysheng
 *
 */
public class DbConfig {
	private String driver; //连接驱动
	private String url; //连接地址
	private String username; //连接名
	private String password; //连接密码
	private Integer minFreeConnections=2; //空闲连接池，最小连接数，默认为2
	private Integer maxFreeConnections=8; //空闲连接池，最大连接数，默认为8
	private Integer maxActiveConnection=8; //活跃连接池，最大连接数，默认为8
	private Integer initConnections=2; //初始化连接数，默认为2个
	private Long connectionTimeOut=1000*60*20L; //连接超时时间，默认为20分钟
	private Long recheckTime=1000*60L; //自检循环时间，默认为60秒
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getMinFreeConnections() {
		return minFreeConnections;
	}
	public void setMinFreeConnections(Integer minFreeConnections) {
		this.minFreeConnections = minFreeConnections;
	}
	public Integer getMaxFreeConnections() {
		return maxFreeConnections;
	}
	public void setMaxFreeConnections(Integer maxFreeConnections) {
		this.maxFreeConnections = maxFreeConnections;
	}
	public Integer getMaxActiveConnection() {
		return maxActiveConnection;
	}
	public void setMaxActiveConnection(Integer maxActiveConnection) {
		this.maxActiveConnection = maxActiveConnection;
	}
	public Integer getInitConnections() {
		return initConnections;
	}
	public void setInitConnections(Integer initConnections) {
		this.initConnections = initConnections;
	}
	public Long getConnectionTimeOut() {
		return connectionTimeOut;
	}
	public void setConnectionTimeOut(Long connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}
	public Long getRecheckTime() {
		return recheckTime;
	}
	public void setRecheckTime(Long recheckTime) {
		this.recheckTime = recheckTime;
	}
	
	
}
