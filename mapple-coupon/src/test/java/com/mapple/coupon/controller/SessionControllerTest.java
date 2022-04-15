package com.mapple.coupon.controller;

import com.mapple.common.utils.PageUtils;
import com.mapple.coupon.entity.SessionEntity;
import com.mapple.coupon.service.SessionService;
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
@WebMvcTest(SessionController.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService mockSessionService;

    @Test
    public void testList() throws Exception {
        // Setup
        when(mockSessionService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/session/list")
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
        // Configure SessionService.getById(...).
        final SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setId("id");
        sessionEntity.setSessionName("sessionName");
        sessionEntity.setStartTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        sessionEntity.setEndTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        sessionEntity.setStatus(0);
        sessionEntity.setIsDeleted(0);
        sessionEntity.setGmtCreate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        sessionEntity.setGmtModified(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        sessionEntity.setSessionStatus("sessionStatus");
        when(mockSessionService.getById("id")).thenReturn(sessionEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("coupon/session/info/{id}", "id")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
    }

    @Test
    public void testSave() throws Exception {
        // Setup
        when(mockSessionService.saveSession(new SessionEntity())).thenReturn("result");

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/session/save")
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
        when(mockSessionService.updateById(new SessionEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/session/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockSessionService).updateById(new SessionEntity());
    }

    @Test
    public void testDelete() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("coupon/session/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("expectedResponse", response.getContentAsString());
        verify(mockSessionService).delete(Arrays.asList("value"));
    }
}
