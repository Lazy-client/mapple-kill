package com.mapple.seckill.controller;

import com.mapple.common.utils.result.CommonResult;
import com.mapple.common.vo.Sku;
import com.mapple.seckill.service.SecKillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SeckillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Resource
    private SeckillController seckillController;
    @BeforeEach
    void setupBeforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(seckillController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
    }
    @Resource
    private SecKillService mockSecKillService;
    @Resource
    private RedissonClient mockRedissonClient;

    @Test
    void testKill() throws Exception {
        // Setup
//        when(mockSecKillService.kill("key", "id", "token")).thenReturn("result");

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/kill/{token}/{key}", "key", "token")
                        .param("sessionId", "sessionId")
                        .param("productId", "productId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testKill_SecKillServiceThrowsInterruptedException() throws Exception {
        // Setup
        when(mockSecKillService.kill("key", "id", "token")).thenThrow(InterruptedException.class);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/kill/{token}/{key}", "key", "token")
                        .param("sessionId", "sessionId")
                        .param("productId", "productId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSearch() throws Exception {
        // Setup
        // Configure SecKillService.search(...).
        final Sku sku = new Sku();
        sku.setId("id");
        sku.setSessionName("sessionName");
        sku.setInterestRate(new BigDecimal("0.00"));
        sku.setDepositTime("depositTime");
        sku.setRiskLevel(0);
        sku.setCashAdvance(false);
        sku.setAutoRedemption(false);
        sku.setProductId("productId");
        sku.setProductName("productName");
        sku.setSeckillPrice(new BigDecimal("0.00"));
        sku.setDescription("description");
        sku.setTitle("title");
        sku.setTotalCount(0);
        sku.setStartTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        sku.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<Sku> skus = Arrays.asList(sku);
        when(mockSecKillService.search("sessionId")).thenReturn(skus);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/search")
                        .param("sessionId", "sessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSearch_SecKillServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockSecKillService.search("sessionId")).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/search")
                        .param("sessionId", "sessionId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSearchById() throws Exception {
        // Setup
        when(mockSecKillService.searchById("sessionId", "productId")).thenReturn(null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/searchById")
                        .param("sessionId", "sessionId")
                        .param("productId", "productId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSearchSessions() throws Exception {
        // Setup
        when(mockSecKillService.searchSessions("token")).thenReturn(new HashMap<>());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/searchSessions")
                        .header("token", "token")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testRedisKey() throws Exception {
        // Setup
        when(mockRedissonClient.getMapCache("SECKILL_USER_PREFIX")).thenReturn(null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/redisKey")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSendOrder() throws Exception {
        // Setup
        when(mockSecKillService.sendOrder()).thenReturn(new CommonResult());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sendOrder")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSendOrder_SecKillServiceReturnsError() throws Exception {
        // Setup
        when(mockSecKillService.sendOrder()).thenReturn(CommonResult.error());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sendOrder")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
