package io.renren.modules.sys.controller;

import io.renren.modules.sys.entity.SysMenuEntity;
import io.renren.modules.sys.service.ShiroService;
import io.renren.modules.sys.service.SysMenuService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysMenuController.class)
class SysMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysMenuService mockSysMenuService;
    @MockBean
    private ShiroService mockShiroService;

    @Test
    void testNav() throws Exception {
        // Setup
        // Configure SysMenuService.getUserMenuList(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        final List<SysMenuEntity> sysMenuEntities = Arrays.asList(sysMenuEntity);
        when(mockSysMenuService.getUserMenuList(0L)).thenReturn(sysMenuEntities);

        when(mockShiroService.getUserPermissions(0L)).thenReturn(new HashSet<>(Arrays.asList("value")));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/nav")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testNav_SysMenuServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockSysMenuService.getUserMenuList(0L)).thenReturn(Collections.emptyList());
        when(mockShiroService.getUserPermissions(0L)).thenReturn(new HashSet<>(Arrays.asList("value")));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/nav")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testNav_ShiroServiceReturnsNoItems() throws Exception {
        // Setup
        // Configure SysMenuService.getUserMenuList(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        final List<SysMenuEntity> sysMenuEntities = Arrays.asList(sysMenuEntity);
        when(mockSysMenuService.getUserMenuList(0L)).thenReturn(sysMenuEntities);

        when(mockShiroService.getUserPermissions(0L)).thenReturn(Collections.emptySet());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/nav")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testList() throws Exception {
        // Setup
        // Configure SysMenuService.list(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        final List<SysMenuEntity> sysMenuEntities = Arrays.asList(sysMenuEntity);
        when(mockSysMenuService.list()).thenReturn(sysMenuEntities);

        // Configure SysMenuService.getById(...).
        final SysMenuEntity sysMenuEntity1 = new SysMenuEntity();
        sysMenuEntity1.setMenuId(0L);
        sysMenuEntity1.setParentId(0L);
        sysMenuEntity1.setParentName("name");
        sysMenuEntity1.setName("name");
        sysMenuEntity1.setUrl("url");
        sysMenuEntity1.setPerms("perms");
        sysMenuEntity1.setType(0);
        sysMenuEntity1.setIcon("icon");
        sysMenuEntity1.setOrderNum(0);
        sysMenuEntity1.setOpen(false);
        sysMenuEntity1.setList(Arrays.asList());
        when(mockSysMenuService.getById(0L)).thenReturn(sysMenuEntity1);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testList_SysMenuServiceListReturnsNoItems() throws Exception {
        // Setup
        when(mockSysMenuService.list()).thenReturn(Collections.emptyList());

        // Configure SysMenuService.getById(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        when(mockSysMenuService.getById(0L)).thenReturn(sysMenuEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/list")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    void testSelect() throws Exception {
        // Setup
        // Configure SysMenuService.queryNotButtonList(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        final List<SysMenuEntity> sysMenuEntities = Arrays.asList(sysMenuEntity);
        when(mockSysMenuService.queryNotButtonList()).thenReturn(sysMenuEntities);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/select")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSelect_SysMenuServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockSysMenuService.queryNotButtonList()).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/select")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo() throws Exception {
        // Setup
        // Configure SysMenuService.getById(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        when(mockSysMenuService.getById(0L)).thenReturn(sysMenuEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/menu/info/{menuId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSave() throws Exception {
        // Setup
        // Configure SysMenuService.getById(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        when(mockSysMenuService.getById(0L)).thenReturn(sysMenuEntity);

        when(mockSysMenuService.save(new SysMenuEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/menu/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysMenuService).save(new SysMenuEntity());
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        // Configure SysMenuService.getById(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        when(mockSysMenuService.getById(0L)).thenReturn(sysMenuEntity);

        when(mockSysMenuService.updateById(new SysMenuEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/menu/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysMenuService).updateById(new SysMenuEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Configure SysMenuService.queryListParentId(...).
        final SysMenuEntity sysMenuEntity = new SysMenuEntity();
        sysMenuEntity.setMenuId(0L);
        sysMenuEntity.setParentId(0L);
        sysMenuEntity.setParentName("name");
        sysMenuEntity.setName("name");
        sysMenuEntity.setUrl("url");
        sysMenuEntity.setPerms("perms");
        sysMenuEntity.setType(0);
        sysMenuEntity.setIcon("icon");
        sysMenuEntity.setOrderNum(0);
        sysMenuEntity.setOpen(false);
        sysMenuEntity.setList(Arrays.asList());
        final List<SysMenuEntity> sysMenuEntities = Arrays.asList(sysMenuEntity);
        when(mockSysMenuService.queryListParentId(0L)).thenReturn(sysMenuEntities);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/menu/delete/{menuId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysMenuService).delete(0L);
    }

    @Test
    void testDelete_SysMenuServiceQueryListParentIdReturnsNoItems() throws Exception {
        // Setup
        when(mockSysMenuService.queryListParentId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/menu/delete/{menuId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysMenuService).delete(0L);
    }
}
