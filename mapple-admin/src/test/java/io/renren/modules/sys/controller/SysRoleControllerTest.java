package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SysRoleEntity;
import io.renren.modules.sys.service.SysRoleMenuService;
import io.renren.modules.sys.service.SysRoleService;
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
@WebMvcTest(SysRoleController.class)
class SysRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysRoleService mockSysRoleService;
    @MockBean
    private SysRoleMenuService mockSysRoleMenuService;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockSysRoleService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/role/list")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSelect() throws Exception {
        // Setup
        // Configure SysRoleService.listByMap(...).
        final SysRoleEntity sysRoleEntity = new SysRoleEntity();
        sysRoleEntity.setRoleId(0L);
        sysRoleEntity.setRoleName("roleName");
        sysRoleEntity.setRemark("remark");
        sysRoleEntity.setCreateUserId(0L);
        sysRoleEntity.setMenuIdList(Arrays.asList(0L));
        sysRoleEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<SysRoleEntity> sysRoleEntities = Arrays.asList(sysRoleEntity);
        when(mockSysRoleService.listByMap(new HashMap<>())).thenReturn(sysRoleEntities);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/role/select")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSelect_SysRoleServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockSysRoleService.listByMap(new HashMap<>())).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/role/select")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo() throws Exception {
        // Setup
        // Configure SysRoleService.getById(...).
        final SysRoleEntity sysRoleEntity = new SysRoleEntity();
        sysRoleEntity.setRoleId(0L);
        sysRoleEntity.setRoleName("roleName");
        sysRoleEntity.setRemark("remark");
        sysRoleEntity.setCreateUserId(0L);
        sysRoleEntity.setMenuIdList(Arrays.asList(0L));
        sysRoleEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockSysRoleService.getById(0L)).thenReturn(sysRoleEntity);

        when(mockSysRoleMenuService.queryMenuIdList(0L)).thenReturn(Arrays.asList(0L));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/role/info/{roleId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo_SysRoleMenuServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SysRoleService.getById(...).
        final SysRoleEntity sysRoleEntity = new SysRoleEntity();
        sysRoleEntity.setRoleId(0L);
        sysRoleEntity.setRoleName("roleName");
        sysRoleEntity.setRemark("remark");
        sysRoleEntity.setCreateUserId(0L);
        sysRoleEntity.setMenuIdList(Arrays.asList(0L));
        sysRoleEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockSysRoleService.getById(0L)).thenReturn(sysRoleEntity);

        when(mockSysRoleMenuService.queryMenuIdList(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/role/info/{roleId}", 0)
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
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/role/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysRoleService).saveRole(new SysRoleEntity());
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/role/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysRoleService).update(new SysRoleEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/role/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysRoleService).deleteBatch(any(Long[].class));
    }
}
