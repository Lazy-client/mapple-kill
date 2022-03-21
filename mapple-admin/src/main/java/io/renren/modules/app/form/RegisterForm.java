/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.app.form;

import io.renren.modules.app.annotation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 注册表单
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "注册表单")
public class RegisterForm {
    @ApiModelProperty(value = "姓名")
    @NotBlank(message="姓名不能为空")
    private String realName;

    @ApiModelProperty(value = "手机号")
    //校验手机号
    @Phone
    @NotBlank(message="手机号不能为空")
    private String telephoneNum;

    @ApiModelProperty(value = "身份证号")
    @NotBlank(message="身份证号不能为空")
    private String idCard;


    @ApiModelProperty(value = "密码")
    @NotBlank(message="密码不能为空")
    private String password;

    @ApiModelProperty(value = "用户名")
    @NotBlank(message="用户名不能为空")
    private String username;

}
