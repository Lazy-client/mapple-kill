package io.renren.modules.sys.controller;

import io.renren.common.utils.R;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysCaptchaService;
import io.renren.modules.sys.service.SysUserService;
import io.renren.modules.sys.service.SysUserTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysLoginController.class)
class SysLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserService mockSysUserService;
    @MockBean
    private SysUserTokenService mockSysUserTokenService;
    @MockBean
    private SysCaptchaService mockSysCaptchaService;

    @Test
    void testCaptcha() throws Exception {
        // Setup
        when(mockSysCaptchaService.getCaptcha("63a3a495-92ba-4e1b-8f3c-20c279647371"))
                .thenReturn(new BufferedImage(0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/captcha.jpg")
                        .param("uuid", "63a3a495-92ba-4e1b-8f3c-20c279647371")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testCaptcha_ThrowsIOException() throws Exception {
        // Setup
        when(mockSysCaptchaService.getCaptcha("63a3a495-92ba-4e1b-8f3c-20c279647371"))
                .thenReturn(new BufferedImage(0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/captcha.jpg")
                        .param("uuid", "63a3a495-92ba-4e1b-8f3c-20c279647371")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testLogin() throws Exception {
        // Setup
        when(mockSysCaptchaService.validate("uuid", "captcha")).thenReturn(false);

        // Configure SysUserService.queryByUserName(...).
        final SysUserEntity sysUserEntity = new SysUserEntity();
        sysUserEntity.setUserId(0L);
        sysUserEntity.setUsername("username");
        sysUserEntity.setPassword("password");
        sysUserEntity.setSalt("salt");
        sysUserEntity.setEmail("email");
        sysUserEntity.setMobile("mobile");
        sysUserEntity.setStatus(0);
        sysUserEntity.setRoleIdList(Arrays.asList(0L));
        sysUserEntity.setCreateUserId(0L);
        sysUserEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockSysUserService.queryByUserName("username")).thenReturn(sysUserEntity);

        when(mockSysUserTokenService.createToken(0L)).thenReturn(new R());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/login")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testLogin_SysUserTokenServiceReturnsError() throws Exception {
        // Setup
        when(mockSysCaptchaService.validate("uuid", "captcha")).thenReturn(false);

        // Configure SysUserService.queryByUserName(...).
        final SysUserEntity sysUserEntity = new SysUserEntity();
        sysUserEntity.setUserId(0L);
        sysUserEntity.setUsername("username");
        sysUserEntity.setPassword("password");
        sysUserEntity.setSalt("salt");
        sysUserEntity.setEmail("email");
        sysUserEntity.setMobile("mobile");
        sysUserEntity.setStatus(0);
        sysUserEntity.setRoleIdList(Arrays.asList(0L));
        sysUserEntity.setCreateUserId(0L);
        sysUserEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockSysUserService.queryByUserName("username")).thenReturn(sysUserEntity);

        when(mockSysUserTokenService.createToken(0L)).thenReturn(R.error());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/login")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testLogout() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/logout")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysUserTokenService).logout(0L);
    }
}
