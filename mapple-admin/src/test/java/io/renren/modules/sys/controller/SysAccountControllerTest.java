package io.renren.modules.sys.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SysAccountController.class)
class SysAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ValueOperations<String, String> mockValueOperations;

    @Test
    void testInfo() throws Exception {
        // Setup
        when(mockValueOperations.get("PUBLIC_ACCOUNT")).thenReturn("result");

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/account/public")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testInfo_ValueOperationsReturnsNull() throws Exception {
        // Setup
        when(mockValueOperations.get("PUBLIC_ACCOUNT")).thenReturn(null);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/sys/account/public")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
