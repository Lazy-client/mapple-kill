package com.mapple.coupon.controller;

import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.ProductEntity;
import com.mapple.coupon.entity.vo.productSessionVo_Skus;
import com.mapple.coupon.service.ProductService;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService mockProductService;

    @Test
    public void testList() throws Exception {
        // Setup
        when(mockProductService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/product/list")
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
        // Configure ProductService.getById(...).
        final ProductEntity productEntity = new ProductEntity();
        productEntity.setProductId("productId");
        productEntity.setProductName("productName");
        productEntity.setDescription("description");
        productEntity.setTitle("title");
        productEntity.setInterestRate(new BigDecimal("0.00"));
        productEntity.setDepositTime("depositTime");
        productEntity.setRiskLevel(0);
        productEntity.setCashAdvance(false);
        productEntity.setAutoRedemption(false);
        productEntity.setIsDeleted(0);
        productEntity.setGmtCreate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        productEntity.setGmtModified(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockProductService.getById("id")).thenReturn(productEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/product/info/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/product/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockProductService).saveProduct(new ProductEntity());
    }

    @Test
    public void testUpdate() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/product/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockProductService).updateProductById(new productSessionVo_Skus());
    }

    @Test
    public void testDelete() throws Exception {
        // Setup
        when(mockProductService.removeByIds(Arrays.asList("value"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/product/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockProductService).removeByIds(Arrays.asList("value"));
    }
}
