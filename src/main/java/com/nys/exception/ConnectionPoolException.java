package com.nys.exception;

import com.nys.enums.ResultEnum;
/**
 * 连接池异常处理
 * @author nysheng
 *
 */
public class ConnectionPoolException extends RuntimeException {
	 private static final long serialVersionUID = 1L;
	
	 private Integer code;
	 
	 public ConnectionPoolException() {
		 super();
		 code=0;
	 }
	 public ConnectionPoolException(ResultEnum resultEnum) {
		 super(resultEnum.getMsg());
		 this.code=resultEnum.getCode();
	 }
	 
	 public Integer getCode() {
		 return code;
	 }
}
