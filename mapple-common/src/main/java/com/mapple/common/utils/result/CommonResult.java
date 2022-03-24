package com.mapple.common.utils.result;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年10月27日 下午9:59:27
 */


public class CommonResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;


    public CommonResult() {
        put("code", 0);
        put("msg", "success");
    }

    public static CommonResult error() {
        return CommonResult.error(ResultCode.ERROR, "未知错误,联系管理员");
    }

    public static CommonResult error(String msg) {
        return CommonResult.error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public static CommonResult error(int code, String msg) {
        CommonResult r = new CommonResult();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static CommonResult ok() {
        return new CommonResult();
    }

    public static  CommonResult ok(String msg) {
        CommonResult r = new CommonResult();
        r.put("msg", msg);
        return r;
    }

    public CommonResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public CommonResult put(Map<String, Object> map) {
        putAll(map);
        return this;
    }
}
