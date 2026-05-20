package com.company.employeemanagement.service;

import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.dto.response.TitleResponse;
import org.springframework.data.domain.Pageable;

public interface TitleService {

    TitleResponse createTitle(TitleRequest request);

    TitleResponse updateTitle(String titleId, TitleRequest request);

    void deleteTitle(String titleId);

    TitleResponse getTitleById(String titleId);

    PageResponse<TitleResponse> getAllTitles(Pageable pageable);
}
