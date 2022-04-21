/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.mapple.consume.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.baomidou.mybatisplus.annotation.IdType.ASSIGN_ID;


/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("mk_user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@TableId(type = ASSIGN_ID)
	@ApiModelProperty(value = "用户id",required = false)
	private String userId;
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "用户名",required = true)
	private String username;

	/**
	 * 真实姓名
	 */
	@ApiModelProperty(value = "真实姓名",required = true)
	private String realName;

	/**
	 * 身份证号码
	 */
	@ApiModelProperty(value = "身份证号",required = true)
	private String idCard;

	/**
	 * 手机号
	 */
	@ApiModelProperty(value = "手机号",required = true)
	private String telephoneNum;
	/**
	 * 密码
	 */
	@ApiModelProperty(value = "密码",required = true)
	private String password;

	/**
	 * 有无职业 1表示无业 0表示有工作
	 */
	@ApiModelProperty(value = "是否有工作",required = true)
	private Boolean notHasJob;

	/**
	 * 是否逾期 1表示逾期 0表示未逾期
	 */
	@ApiModelProperty(value = "是否逾期",required = true)
	private Boolean isOverdue;

	/**
	 * 是否失信 1 失信 0 没有失信
	 */
	@ApiModelProperty(value = "是否失信",required = true)
	private Boolean isDishonest;

	/**
	 * 余额
	 */
	@ApiModelProperty(value = "余额",required = true)
	private BigDecimal balance;

	/**
	 * 余额
	 */
	@ApiModelProperty(value = "年龄",required = true)
	private Integer age;

	/**
	 * 逻辑删除 1（true）已删除， 0（false）未删除
	 */
	@TableLogic(value = "0", delval = "1")
	@ApiModelProperty(value = "是否删除",required = false)
	private Boolean isDeleted;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT)
	private Date gmtCreate;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Date gmtModified;

}
