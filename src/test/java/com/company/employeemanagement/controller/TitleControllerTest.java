package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.dto.response.TitleResponse;
import com.company.employeemanagement.exception.GlobalExceptionHandler;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.service.TitleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TitleControllerTest {

    @Mock
    private TitleService titleService;

    @InjectMocks
    private TitleController titleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TitleResponse titleResponse;
    private TitleRequest titleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(titleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        titleResponse = TitleResponse.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();

        titleRequest = TitleRequest.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/titles - Should create title and return 201")
    void createTitle_Returns201() throws Exception {
        when(titleService.createTitle(any(TitleRequest.class))).thenReturn(titleResponse);

        mockMvc.perform(post("/api/v1/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(titleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.titleId").value("T001"))
                .andExpect(jsonPath("$.data.title").value("Software Engineer"));
    }

    @Test
    @DisplayName("GET /api/v1/titles/{id} - Should return title")
    void getTitleById_Returns200() throws Exception {
        when(titleService.getTitleById("T001")).thenReturn(titleResponse);

        mockMvc.perform(get("/api/v1/titles/T001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleId").value("T001"))
                .andExpect(jsonPath("$.data.title").value("Software Engineer"));
    }

    @Test
    @DisplayName("GET /api/v1/titles/{id} - Should return 404 when not found")
    void getTitleById_NotFound_Returns404() throws Exception {
        when(titleService.getTitleById("T999"))
                .thenThrow(new ResourceNotFoundException("Title", "titleId", "T999"));

        mockMvc.perform(get("/api/v1/titles/T999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/titles - Should return paginated titles")
    void getAllTitles_Returns200() throws Exception {
        PageResponse<TitleResponse> pageResponse = PageResponse.<TitleResponse>builder()
                .content(List.of(titleResponse))
                .pageNumber(0).pageSize(10).totalElements(1).totalPages(1)
                .first(true).last(true).build();

        when(titleService.getAllTitles(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/titles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].titleId").value("T001"));
    }

    @Test
    @DisplayName("DELETE /api/v1/titles/{id} - Should delete title")
    void deleteTitle_Returns200() throws Exception {
        doNothing().when(titleService).deleteTitle("T001");

        mockMvc.perform(delete("/api/v1/titles/T001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Title deleted successfully"));
    }

    @Test
    @DisplayName("PUT /api/v1/titles/{id} - Should update title")
    void updateTitle_Returns200() throws Exception {
        when(titleService.updateTitle(eq("T001"), any(TitleRequest.class))).thenReturn(titleResponse);

        mockMvc.perform(put("/api/v1/titles/T001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(titleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleId").value("T001"));
    }

    @Test
    @DisplayName("POST /api/v1/titles - Should return 400 for blank title")
    void createTitle_BlankTitle_Returns400() throws Exception {
        TitleRequest invalid = TitleRequest.builder().titleId("T001").title("").build();

        mockMvc.perform(post("/api/v1/titles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
