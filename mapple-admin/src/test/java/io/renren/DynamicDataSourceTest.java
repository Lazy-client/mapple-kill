/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren;

import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.UserService;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import io.renren.service.DynamicDataSourceTestService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 多数据源测试
 *
 * @author Mark sunlightcs@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DynamicDataSourceTest {
    @Autowired
    private DynamicDataSourceTestService dynamicDataSourceTestService;

    @Autowired
    UserService userService;
    @Test
    public void test(){
        Long id = 1L;

        dynamicDataSourceTestService.updateUser(id);
        dynamicDataSourceTestService.updateUserBySlave1(id);
        dynamicDataSourceTestService.updateUserBySlave2(id);
    }

    //注入 sysConfigService
    @Autowired
    private SysConfigService sysConfigService;
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                SysConfigEntity sysConfigEntity = sysConfigService.getById("2");
                System.out.println(sysConfigEntity);
                System.out.println(sysConfigEntity.getId()+"======");
                return sysConfigEntity.getParamValue();
            } catch (Exception ignored) {
                System.out.println("获取失败");
            }
            return null;
        });

        System.out.println(future.get());
    }

    @Test
    public void test1() throws IOException {
        //新建一个客户对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8081/renren-fast/app/login");
//        List<LoginForm> loginForms = new ArrayList<>();
//        loginForms.add(new LoginForm("user1","123456"));
//        List<NameValuePair> list = new ArrayList<>();
//        list.add(new BasicNameValuePair("username","user1"));
//        list.add(new BasicNameValuePair("password","123456"));
//        //4.对打包好的参数，使用UrlEncodedFormEntity工具类生成实体的类
//        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, Consts.UTF_8);
        //5.将含有请求参数的实体对象放到post请求中
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", "user1"));
        formparams.add(new BasicNameValuePair("password", "123456"));
        UrlEncodedFormEntity uefEntity;
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("username", "user1");
//        jsonObject.put("password", "123456");
//        httpPost.setEntity(new StringEntity(jsonObject.toString()));
       //6.新建一个响应对象来接收客户端执行post的结果
        HttpResponse response = httpClient.execute(httpPost);

        HttpEntity httpEntity= response.getEntity();
        String resp = EntityUtils.toString(httpEntity, "UTF-8");
        System.out.println(response);
        for (int i = 0; i < 10000; i++) {
            UserEntity user = new UserEntity();

        }
    }

}
