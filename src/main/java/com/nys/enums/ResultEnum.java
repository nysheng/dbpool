package com.nys.enums;
/**
 * 异常消息
 * @author nysheng
 *
 */
public enum ResultEnum {
	
	DRIVER_NOT_FOUND(10000,"加载驱动失败"),
	CONNECTION_FAIL(10001,"建立连接失败"),
	CONNECTION_STATUS_ERROE(10002,"连接状态异常"),
	CONNECTION_CLOSE_FAIL(10003,"连接关闭失败"),
	INTERRUPT_ERROR(10004,"阻塞异常");
	
	
	private Integer code;
	private String msg;
	ResultEnum(Integer code,String msg){
		this.code=code;
		this.msg=msg;
	}
	ResultEnum(){}
	public String getMsg() {
		return msg;
	}
	public Integer getCode() {
		return code;
	}
}
