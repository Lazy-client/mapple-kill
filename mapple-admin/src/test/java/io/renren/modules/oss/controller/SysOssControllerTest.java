package io.renren.modules.oss.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.oss.cloud.CloudStorageConfig;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.sys.service.SysConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysOssController.class)
class SysOssControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysOssService mockSysOssService;
    @MockBean
    private SysConfigService mockSysConfigService;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockSysOssService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("sys/oss/list")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testConfig() throws Exception {
        // Setup
        // Configure SysConfigService.getConfigObject(...).
        final CloudStorageConfig cloudStorageConfig = new CloudStorageConfig();
        cloudStorageConfig.setType(0);
        cloudStorageConfig.setQiniuDomain("qiniuDomain");
        cloudStorageConfig.setQiniuPrefix("qiniuPrefix");
        cloudStorageConfig.setQiniuAccessKey("qiniuAccessKey");
        cloudStorageConfig.setQiniuSecretKey("qiniuSecretKey");
        cloudStorageConfig.setQiniuBucketName("qiniuBucketName");
        cloudStorageConfig.setAliyunDomain("aliyunDomain");
        cloudStorageConfig.setAliyunPrefix("aliyunPrefix");
        cloudStorageConfig.setAliyunEndPoint("aliyunEndPoint");
        cloudStorageConfig.setAliyunAccessKeyId("aliyunAccessKeyId");
        cloudStorageConfig.setAliyunAccessKeySecret("aliyunAccessKeySecret");
        cloudStorageConfig.setAliyunBucketName("aliyunBucketName");
        cloudStorageConfig.setQcloudDomain("qcloudDomain");
        cloudStorageConfig.setQcloudPrefix("qcloudPrefix");
        cloudStorageConfig.setQcloudAppId(0);
        when(mockSysConfigService.getConfigObject("CLOUD_STORAGE_CONFIG_KEY", CloudStorageConfig.class))
                .thenReturn(cloudStorageConfig);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("sys/oss/config")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSaveConfig() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("sys/oss/saveConfig")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysConfigService).updateValueByKey("CLOUD_STORAGE_CONFIG_KEY", "value");
    }

    @Test
    void testUpload() throws Exception {
        // Setup
        when(mockSysOssService.save(new SysOssEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(multipart("sys/oss/upload")
                        .file(new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                                "content".getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysOssService).save(new SysOssEntity());
    }

    @Test
    void testUpload_ThrowsException() throws Exception {
        // Setup
        when(mockSysOssService.save(new SysOssEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(multipart("sys/oss/upload")
                        .file(new MockMultipartFile("file", "originalFilename", MediaType.APPLICATION_JSON_VALUE,
                                "content".getBytes()))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysOssService).save(new SysOssEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        when(mockSysOssService.removeByIds(Arrays.asList("value"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("sys/oss/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockSysOssService).removeByIds(Arrays.asList("value"));
    }
}
