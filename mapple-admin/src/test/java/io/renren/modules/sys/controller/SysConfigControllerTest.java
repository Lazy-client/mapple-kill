package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.sys.entity.SysConfigEntity;
import io.renren.modules.sys.service.SysConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysConfigController.class)
class SysConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysConfigService mockSysConfigService;
    @MockBean
    private RBloomFilter<String> mockUserBloomFilter;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockSysConfigService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/config/list")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo() throws Exception {
        // Setup
        // Configure SysConfigService.getById(...).
        final SysConfigEntity sysConfigEntity = new SysConfigEntity();
        sysConfigEntity.setId(0L);
        sysConfigEntity.setParamKey("paramKey");
        sysConfigEntity.setParamValue("paramValue");
        sysConfigEntity.setRemark("remark");
        when(mockSysConfigService.getById(0L)).thenReturn(sysConfigEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/config/info/{id}", 0)
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
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/config/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysConfigService).saveConfig(new SysConfigEntity());
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        when(mockUserBloomFilter.delete()).thenReturn(false);
        when(mockUserBloomFilter.tryInit(0L, 0.0)).thenReturn(false);
        when(mockUserBloomFilter.add("null")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/config/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockUserBloomFilter).delete();
        verify(mockUserBloomFilter).tryInit(0L, 0.0);
        verify(mockUserBloomFilter).add("null");
        verify(mockSysConfigService).update(new SysConfigEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/config/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysConfigService).deleteBatch(any(Long[].class));
    }
}
