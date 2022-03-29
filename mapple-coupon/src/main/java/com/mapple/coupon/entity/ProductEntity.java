package com.mapple.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.*;

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
//	/**
//	 * 产品默认图片地址
//	 */
//	@NotNull
//	@ApiModelProperty("产品默认图片地址")
//	private String defaultImg;

	@NotNull
	@ApiModelProperty("标题")
	private String title;
	/**
	 * 年利率
	 */
	@NotNull
	@Min(value = 0,message = "年利率输入错误")
	@ApiModelProperty("年利率")
	private BigDecimal interestRate;

	@NotNull
	@Pattern(regexp = "\\d{1,2}-\\d{1,2}",message = "存款时间格式错误")
	@ApiModelProperty("存款时间 比如存5年2个月 格式为：5-2")
	private String depositTime;

	@NotNull
	@ApiModelProperty("风险等级 低：1 中：2 高： 3")
	private Integer riskLevel;

	@NotNull
	@ApiModelProperty("是否能提前取钱 1表示能提前 0表示不能")
	private Boolean cashAdvance;

	@NotNull
	@ApiModelProperty("是否能自动赎回 1表示自动赎回 0表示不自动")
	private Boolean autoRedemption;

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
