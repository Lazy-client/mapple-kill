package io.renren.modules.job.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.job.entity.ScheduleJobLogEntity;
import io.renren.modules.job.service.ScheduleJobLogService;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ScheduleJobLogController.class)
class ScheduleJobLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleJobLogService mockScheduleJobLogService;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockScheduleJobLogService.queryPage(new HashMap<>())).thenReturn(new PageUtils(
                Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/scheduleLog/list")
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
        // Configure ScheduleJobLogService.getById(...).
        final ScheduleJobLogEntity scheduleJobLogEntity = new ScheduleJobLogEntity();
        scheduleJobLogEntity.setLogId(0L);
        scheduleJobLogEntity.setJobId(0L);
        scheduleJobLogEntity.setBeanName("beanName");
        scheduleJobLogEntity.setParams("params");
        scheduleJobLogEntity.setStatus(0);
        scheduleJobLogEntity.setError("error");
        scheduleJobLogEntity.setTimes(0);
        scheduleJobLogEntity.setCreateTime(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockScheduleJobLogService.getById(0L)).thenReturn(scheduleJobLogEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/scheduleLog/info/{logId}", 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
