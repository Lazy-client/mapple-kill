package io.renren.modules.sys.controller;

import io.renren.common.utils.PageUtils;
import io.renren.modules.app.entity.UserEntity;
import io.renren.modules.app.service.UserService;
import io.renren.modules.clients.ConsumeFeignService;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AppUserController.class)
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockUserService;
    @MockBean
    private ConsumeFeignService mockConsumeFeignService;

    @Test
    void testList() throws Exception {
        // Setup
        when(mockUserService.queryPage(new HashMap<>())).thenReturn(new PageUtils(Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/app/user/list")
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
        // Configure UserService.getById(...).
        final UserEntity userEntity = new UserEntity();
        userEntity.setUserId("userId");
        userEntity.setUsername("username");
        userEntity.setRealName("realName");
        userEntity.setIdCard("idCard");
        userEntity.setTelephoneNum("telephoneNum");
        userEntity.setPassword("password");
        userEntity.setNotHasJob(false);
        userEntity.setIsOverdue(false);
        userEntity.setIsDishonest(false);
        userEntity.setBalance(new BigDecimal("0.00"));
        userEntity.setAge(0);
        userEntity.setIsDeleted(false);
        userEntity.setGmtCreate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        userEntity.setGmtModified(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        when(mockUserService.getById("userId")).thenReturn(userEntity);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/app/user/info/{userId}", "userId")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testSave() throws Exception {
        // Setup
        when(mockUserService.save(new UserEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/app/user/save")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockUserService).save(new UserEntity());
    }

    @Test
    void testUpdate() throws Exception {
        // Setup
        when(mockUserService.updateById(new UserEntity())).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/app/user/update")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockUserService).updateById(new UserEntity());
    }

    @Test
    void testDelete() throws Exception {
        // Setup
        when(mockUserService.removeByIds(Arrays.asList("value"))).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/sys/app/user/delete")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
        verify(mockUserService).removeByIds(Arrays.asList("value"));
    }

    @Test
    void testListOrderForAdmin() throws Exception {
        // Setup
        when(mockConsumeFeignService.listForAdmin(new HashMap<>())).thenReturn(new PageUtils(
                Arrays.asList(), 0, 0, 0));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/app/user/listOrderForAdmin")
                        .param("params", "params")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
