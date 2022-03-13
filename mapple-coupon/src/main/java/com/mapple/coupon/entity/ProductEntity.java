package com.mapple.coupon.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品信息表
 * 
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Data
@TableName("mk_product")
public class ProductEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private String id;
	/**
	 * 产品名称
	 */
	private String name;
	/**
	 * 产品介绍描述
	 */
	private String description;
	/**
	 * 产品默认图片地址
	 */
	private String defaultImg;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 产品销量
	 */
	private Integer saleCount;
	/**
	 * 逻辑删除 1（true）已删除， 0（false）未删除
	 */
	private Integer isDeleted;

	@ApiModelProperty(value = "创建时间")
	@TableField(fill = FieldFill.INSERT)
	private Date gmtCreate;

	@ApiModelProperty(value = "更新时间")
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date gmtModified;

}
