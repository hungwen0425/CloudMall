package com.hungwen.cloudmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu圖片
 * 
 * @author Hungwen Tseng
 * @email hungwen.tseng@gmail.com
 * @date 2020-08-25 10:21:04
 */
@Data
@TableName("pms_spu_images")
public class SpuImagesEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * spu_id
	 */
	private Long spuId;
	/**
	 * 圖片名
	 */
	private String imgName;
	/**
	 * 圖片地址
	 */
	private String imgUrl;
	/**
	 * 順序
	 */
	private Integer imgSort;
	/**
	 * 是否默認圖
	 */
	private Integer defaultImg;

}
