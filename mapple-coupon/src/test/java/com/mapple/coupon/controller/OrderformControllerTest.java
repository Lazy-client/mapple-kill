package com.mapple.coupon.controller;

import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.OrderformEntity;
import com.mapple.coupon.service.OrderformService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderformController.class)
public class OrderformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderformService mockOrderformService;

    @Test
    public void testList() throws Exception {
        // Setup
        when(mockOrderformService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/orderform/list")
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
        // Configure OrderformService.getById(...).
        final OrderformEntity orderformEntity = new OrderformEntity();
        orderformEntity.setId("id");
        orderformEntity.setSessionId("sessionId");
        orderformEntity.setProductId("productId");
        orderformEntity.setUserId("userId");
        orderformEntity.setOrderSn("orderSn");
        orderformEntity.setProductCount(0);
        orderformEntity.setTotalAmount(new BigDecimal("0.00"));
        orderformEntity.setPayAmount(new BigDecimal("0.00"));
        orderformEntity.setPayType(0);
        orderformEntity.setStatus(0);
        orderformEntity.setAutoConfirmDay(1);
        orderformEntity.setNote("note");
        orderformEntity.setIsDeleted(0);
        orderformEntity.setGmtCreate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        orderformEntity.setGmtModified(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockOrderformService.getById("id")).thenReturn(orderformEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/orderform/info/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave() throws Exception {
        // Setup
        when(mockOrderformService.save(new OrderformEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/orderform/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockOrderformService).save(new OrderformEntity());
    }

    @Test
    public void testUpdate() throws Exception {
        // Setup
        when(mockOrderformService.updateById(new OrderformEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/orderform/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockOrderformService).updateById(new OrderformEntity());
    }

    @Test
    public void testDelete() throws Exception {
        // Setup
        when(mockOrderformService.removeByIds(Arrays.asList("value"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/orderform/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockOrderformService).removeByIds(Arrays.asList("value"));
    }
}
