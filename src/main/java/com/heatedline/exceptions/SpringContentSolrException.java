package com.heatedline.exceptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringContentSolrException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String moduleName;
	private String errorCode;
	private String errorDescription;
	private String actualErrorMsg;
	private Exception ex;
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SpringContentSolrException.class);
	
	public SpringContentSolrException(String moduleName, String errorCode, String errorDescription) {
		this.moduleName = moduleName;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
	}

	public SpringContentSolrException(String moduleName, String errorCode, String errorDescription, Exception ex) {
		this.moduleName = moduleName;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.ex = ex;
	}

	public SpringContentSolrException(String moduleName, String errorCode, String errorDescription, String actualErrorMsg,
			Exception ex) {
		this.moduleName = moduleName;
		this.errorCode = errorCode;
		this.errorDescription = errorDescription;
		this.actualErrorMsg = actualErrorMsg;
		this.ex = ex;
	}

	public StackTraceElement[] getStatckTrace() {
		return ex.getStackTrace();
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public Exception getEx() {
		return ex;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getActualErrorMsg() {
		return actualErrorMsg;
	}

	public static void logFinexaBusinessException(SpringContentSolrException ospEx) {
		if (null != ospEx) {
			Map<String, Object> errorMap = new HashMap<String, Object>();
			errorMap.put("Error Code", ospEx.getErrorCode());
			errorMap.put("Error Description", ospEx.getErrorDescription());
			StringBuffer sb = new StringBuffer();
			Set<Map.Entry<String, Object>> entrySet = errorMap.entrySet();
			sb.append("Module Name:" + ospEx.getModuleName() + "||");
			for (Entry<String, Object> entry : entrySet) {
				sb.append(entry.getKey() + ":" + entry.getValue() + "||");
			}
			LOGGER.error(sb.toString(), ospEx.getEx());
		}
	}
}
