package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.dto.response.TitleResponse;
import com.company.employeemanagement.entity.Title;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.TitleMapper;
import com.company.employeemanagement.repository.TitleRepository;
import com.company.employeemanagement.service.impl.TitleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitleServiceImplTest {

    @Mock
    private TitleRepository titleRepository;

    @Mock
    private TitleMapper titleMapper;

    @InjectMocks
    private TitleServiceImpl titleService;

    private Title title;
    private TitleRequest titleRequest;
    private TitleResponse titleResponse;

    @BeforeEach
    void setUp() {
        title = Title.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();

        titleRequest = TitleRequest.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();

        titleResponse = TitleResponse.builder()
                .titleId("T001")
                .title("Software Engineer")
                .build();
    }

    @Test
    @DisplayName("Should create title successfully")
    void createTitle_Success() {
        when(titleRepository.existsById("T001")).thenReturn(false);
        when(titleRepository.existsByTitle("Software Engineer")).thenReturn(false);
        when(titleMapper.toEntity(titleRequest)).thenReturn(title);
        when(titleRepository.save(title)).thenReturn(title);
        when(titleMapper.toResponse(title)).thenReturn(titleResponse);

        TitleResponse result = titleService.createTitle(titleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitleId()).isEqualTo("T001");
        assertThat(result.getTitle()).isEqualTo("Software Engineer");
        verify(titleRepository).save(title);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when title ID already exists")
    void createTitle_DuplicateId_ThrowsException() {
        when(titleRepository.existsById("T001")).thenReturn(true);

        assertThatThrownBy(() -> titleService.createTitle(titleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("T001");

        verify(titleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when title name already exists")
    void createTitle_DuplicateName_ThrowsException() {
        when(titleRepository.existsById("T001")).thenReturn(false);
        when(titleRepository.existsByTitle("Software Engineer")).thenReturn(true);

        assertThatThrownBy(() -> titleService.createTitle(titleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Software Engineer");

        verify(titleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return title by ID")
    void getTitleById_Success() {
        when(titleRepository.findById("T001")).thenReturn(Optional.of(title));
        when(titleMapper.toResponse(title)).thenReturn(titleResponse);

        TitleResponse result = titleService.getTitleById("T001");

        assertThat(result).isNotNull();
        assertThat(result.getTitleId()).isEqualTo("T001");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when title not found")
    void getTitleById_NotFound_ThrowsException() {
        when(titleRepository.findById("T999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.getTitleById("T999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("T999");
    }

    @Test
    @DisplayName("Should return paginated titles")
    void getAllTitles_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Title> titlePage = new PageImpl<>(List.of(title), pageable, 1);

        when(titleRepository.findAll(pageable)).thenReturn(titlePage);
        when(titleMapper.toResponse(title)).thenReturn(titleResponse);

        PageResponse<TitleResponse> result = titleService.getAllTitles(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update title successfully")
    void updateTitle_Success() {
        when(titleRepository.findById("T001")).thenReturn(Optional.of(title));
        when(titleRepository.save(title)).thenReturn(title);
        when(titleMapper.toResponse(title)).thenReturn(titleResponse);

        TitleResponse result = titleService.updateTitle("T001", titleRequest);

        assertThat(result).isNotNull();
        verify(titleMapper).updateEntity(titleRequest, title);
        verify(titleRepository).save(title);
    }

    @Test
    @DisplayName("Should delete title successfully")
    void deleteTitle_Success() {
        when(titleRepository.findById("T001")).thenReturn(Optional.of(title));

        titleService.deleteTitle("T001");

        verify(titleRepository).delete(title);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException on delete when title not found")
    void deleteTitle_NotFound_ThrowsException() {
        when(titleRepository.findById("T999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> titleService.deleteTitle("T999"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(titleRepository, never()).delete(any());
    }
}
