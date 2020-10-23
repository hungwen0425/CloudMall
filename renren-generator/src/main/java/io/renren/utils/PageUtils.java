package io.renren.utils;

import java.io.Serializable;
import java.util.List;

/**
 * 分頁工具類
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月4日 下午12:59:00
 */
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	//总记录數
	private int totalCount;
	//每頁记录數
	private int pageSize;
	//总頁數
	private int totalPage;
	//当前頁數
	private int currPage;
	//列表資料
	private List<?> list;
	
	/**
	 * 分頁
	 * @param list        列表資料
	 * @param totalCount  总记录數
	 * @param pageSize    每頁记录數
	 * @param currPage    当前頁數
	 */
	public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		this.list = list;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
	
}
