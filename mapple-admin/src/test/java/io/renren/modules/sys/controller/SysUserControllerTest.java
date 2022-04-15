package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SysUserEntity;
import io.renren.modules.sys.service.SysUserRoleService;
import io.renren.modules.sys.service.SysUserService;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysUserController.class)
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserService mockSysUserService;
    @MockBean
    private SysUserRoleService mockSysUserRoleService;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockSysUserService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/user/list")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo1() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/user/info")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testPassword() throws Exception {
        // Setup
        when(mockSysUserService.updatePassword(0L, "password", "newPassword")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/user/password")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo2() throws Exception {
        // Setup
        // Configure SysUserService.getById(...).
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
        when(mockSysUserService.getById(0L)).thenReturn(sysUserEntity);

        when(mockSysUserRoleService.queryRoleIdList(0L)).thenReturn(Arrays.asList(0L));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/user/info/{userId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo2_SysUserRoleServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SysUserService.getById(...).
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
        when(mockSysUserService.getById(0L)).thenReturn(sysUserEntity);

        when(mockSysUserRoleService.queryRoleIdList(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/user/info/{userId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSave() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/user/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysUserService).saveUser(new SysUserEntity());
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/user/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysUserService).update(new SysUserEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/user/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysUserService).deleteBatch(any(Long[].class));
    }
}
