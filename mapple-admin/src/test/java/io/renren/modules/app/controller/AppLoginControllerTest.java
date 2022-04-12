package io.renren.modules.app.controller;

import com.alibaba.fastjson.JSON;
import io.renren.modules.app.form.LoginForm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AppLoginControllerTest {

    @Resource
    private AppLoginController appLoginController;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setupBeforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(appLoginController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }

    @Resource
    private RedisTemplate<String, String> mockStringRedisTemplate;

    @Test
    void testLogin() {
        // Setup
        // Run the test
        final MockHttpServletResponse response;
        try {
            response = mockMvc
                    .perform(post("/app/login")
                            .contentType(MediaType.APPLICATION_JSON)
//                            .characterEncoding("UTF-8")
                            .content(JSON.toJSONString(new LoginForm("user1", "admin"))))
                    .andReturn()
                    .getResponse();
            // Verify the results
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isNull();
        } catch (Exception e) {
            Assertions.assertNotNull(e.getCause().getMessage());
        }
    }

    @Test
    void testLogin_RedisTemplateHasKeyReturnsNull()  {
        // Setup
        // Run the test
        try {
            final MockHttpServletResponse response = mockMvc.perform(post("/app/login")
                            .content(JSON.toJSONString(new LoginForm("user1", "admin")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            // Verify the results
            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        } catch (Exception e) {

        }
    }

    @Test
    void testLogin_ThrowsExecutionException() throws Exception {
        // Setup
        when(mockStringRedisTemplate.opsForValue()).thenReturn(null);
        when(mockStringRedisTemplate.hasKey("key")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/app/login")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testLogin_ThrowsInterruptedException() throws Exception {
        // Setup
        when(mockStringRedisTemplate.opsForValue()).thenReturn(null);
        when(mockStringRedisTemplate.hasKey("key")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/app/login")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testLogout() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/app/logout")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
