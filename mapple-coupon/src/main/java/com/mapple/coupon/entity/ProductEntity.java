package com.mapple.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 产品信息表
 * 
 * @author sicheng
 * @email sicheng_zhou@qq.com
 * @date 2022-03-13 15:23:08
 */
@Data
@ApiModel("产品信息类")
@TableName("mk_product")
public class ProductEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 主键id
	 */
	@TableId
	@ApiModelProperty(value = "产品id",required = false)
	private String productId;
	/**
	 * 产品名称
	 */
	@NotNull
	@ApiModelProperty("产品名称")
	private String productName;
	/**
	 * 产品介绍描述
	 */
	@NotNull
	@ApiModelProperty("产品介绍描述")
	private String description;
	/**
	 * 产品默认图片地址
	 */
	@NotNull
	@ApiModelProperty("产品默认图片地址")
	private String defaultImg;
	/**
	 * 标题
	 */
	@NotNull
	@ApiModelProperty("标题")
	private String title;
	/**
	 * 逻辑删除 1（true）已删除， 0（false）未删除
	 */
	@TableLogic(value = "0", delval = "1")
	@ApiModelProperty(value = "逻辑删除 1（true）已删除， 0（false）未删除",required = false)
	private Integer isDeleted;

	@ApiModelProperty(value = "创建时间",required = false)
	@TableField(fill = FieldFill.INSERT)
	private Date gmtCreate;

	@ApiModelProperty(value = "更新时间",required = false)
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date gmtModified;

}
