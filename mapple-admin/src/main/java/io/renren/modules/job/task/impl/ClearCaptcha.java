/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.task.impl;

import io.renren.modules.job.task.ITask;
import io.renren.modules.sys.service.SysCaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 测试定时任务(演示Demo，可删除)
 *
 * testTask为spring bean的名称
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component("clearCaptcha")
public class ClearCaptcha implements ITask {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private SysCaptchaService sysCaptchaService;
	@Override
	public void run(String params){
		logger.info("clearCaptcha定时任务正在执行，参数为：{}", params);
		sysCaptchaService.truncate();
	}
}
