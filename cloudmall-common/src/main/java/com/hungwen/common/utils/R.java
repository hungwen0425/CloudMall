package com.hungwen.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回資料
 *
 * @author hungwen.tseng@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

//	private T data;
//
//	public T getData() {
//		return data;
//	}
//
//	public void setData(T data) {
//		this.data = data;
//	}

	public R data(Object data) {
		put("data", data);
		return this;
	}

	/**
	 * 把 map 裡 key 為 data 的資料轉換成某個類型的資料
	 * @param typeReference
	 * @param <T>
	 * @return
	 */
	public <T> T getData(TypeReference<T> typeReference) {
		Object obj = get("data");
		String json = JSON.toJSONString(obj);
		T t = JSON.parseObject(json, typeReference);
		return t;
	}

	public <T> T getData(String key, TypeReference<T> typeReference) {
		Object obj = get(key);
		String json = JSON.toJSONString(obj);
		T t = JSON.parseObject(json, typeReference);
		return t;
	}

	public R setData(Object data) {
		put("data",data);
		return this;
	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知異常，請聯繫管理員");
	}

	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public  Integer getCode() {
		return (Integer) this.get("code");
	}
}
