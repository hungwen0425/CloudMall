package com.hungwen.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 分頁工具類
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 總記錄數
	 */
	private int totalCount;
	/**
	 * 每頁記錄數
	 */
	private int pageSize;
	/**
	 * 總頁數
	 */
	private int totalPage;
	/**
	 * 當前頁數
	 */
	private int currPage;
	/**
	 * 列表數據
	 */
	private List<?> list;

	/**
	 * 分頁
	 * @param list        列表數據
	 * @param totalCount  總記錄數
	 * @param pageSize    每頁記錄數
	 * @param currPage    當前頁數
	 */
	public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
	}

	/**
	 * 分頁
	 */
	public PageUtils(IPage<?> page) {
		this.list = page.getRecords();
		this.totalCount = (int)page.getTotal();
		this.pageSize = (int)page.getSize();
		this.currPage = (int)page.getCurrent();
		this.totalPage = (int)page.getPages();
	}
}
