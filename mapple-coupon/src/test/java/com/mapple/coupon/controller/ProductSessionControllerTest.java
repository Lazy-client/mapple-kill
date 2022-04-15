package com.mapple.coupon.controller;

import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.ProductSessionEntity;
import com.mapple.coupon.entity.vo.productSessionVo;
import com.mapple.coupon.entity.vo.productSessionVo_new;
import com.mapple.coupon.service.ProductSessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductSessionController.class)
public class ProductSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductSessionService mockProductSessionService;

    @Test
    public void testList() throws Exception {
        // Setup
        when(mockProductSessionService.queryPage(new HashMap<>())).thenReturn(new PageUtils(
                Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/productsession/list")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testInfo() throws Exception {
        // Setup
        // Configure ProductSessionService.getById(...).
        final ProductSessionEntity productSessionEntity = new ProductSessionEntity();
        productSessionEntity.setId("id");
        productSessionEntity.setSessionId("sessionId");
        productSessionEntity.setProductId("productId");
        productSessionEntity.setSeckillPrice(new BigDecimal("0.00"));
        productSessionEntity.setTotalCount(0);
        productSessionEntity.setIsDeleted(0);
        productSessionEntity.setGmtCreate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        productSessionEntity.setGmtModified(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockProductSessionService.getById("id")).thenReturn(productSessionEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/productsession/info/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave1() throws Exception {
        // Setup
        when(mockProductSessionService.saveProductSession(new productSessionVo())).thenReturn("result");

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/productsession/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave2() throws Exception {
        // Setup
        when(mockProductSessionService.saveProductSession_new(new productSessionVo_new())).thenReturn(
                Arrays.asList("value"));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/productsession/saveNew")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave2_ProductSessionServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockProductSessionService.saveProductSession_new(new productSessionVo_new())).thenReturn(
                Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/productsession/saveNew")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testUpdate() throws Exception {
        // Setup
        when(mockProductSessionService.updateBatchById(Arrays.asList(new ProductSessionEntity()))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/productsession/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockProductSessionService).updateBatchById(Arrays.asList(new ProductSessionEntity()));
    }

    @Test
    public void testDeductStock() throws Exception {
        // Setup
        when(mockProductSessionService.deductStock("productId", "sessionId")).thenReturn(0);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        post("coupon/productsession/deductStock/{productId}/{sessionId}", "productId", "sessionId")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testRefundStock() throws Exception {
        // Setup
        when(mockProductSessionService.refundStock("productId", "sessionId")).thenReturn(0);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(
                        post("coupon/productsession/refundStock/{productId}/{sessionId}", "productId", "sessionId")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testDelete() throws Exception {
        // Setup
        when(mockProductSessionService.removeByIds(Arrays.asList("value"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/productsession/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockProductSessionService).removeByIds(Arrays.asList("value"));
    }
}
