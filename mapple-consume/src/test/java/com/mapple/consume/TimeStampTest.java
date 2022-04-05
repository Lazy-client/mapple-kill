package com.mapple.consume;

import com.mapple.consume.mapper.MkOrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Gelcon
 * @date : 2022/4/5 10:40
 */
@SpringBootTest
public class TimeStampTest {

    @Test
    public void testTime() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
        System.out.println(System.currentTimeMillis());
        System.out.println(60 * 20 * 1000);
    }

}
