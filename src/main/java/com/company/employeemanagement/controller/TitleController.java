package com.company.employeemanagement.controller;

import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.ApiResponse;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.dto.response.TitleResponse;
import com.company.employeemanagement.service.TitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/titles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Titles", description = "Title management APIs")
public class TitleController {

    private final TitleService titleService;

    @PostMapping
    @Operation(summary = "Create a new title")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Title created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Title already exists")
    })
    public ResponseEntity<ApiResponse<TitleResponse>> createTitle(@Valid @RequestBody TitleRequest request) {
        log.info("POST /api/v1/titles - Creating title: {}", request.getTitleId());
        TitleResponse response = titleService.createTitle(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Title created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a title")
    public ResponseEntity<ApiResponse<TitleResponse>> updateTitle(
            @PathVariable String id,
            @Valid @RequestBody TitleRequest request) {
        log.info("PUT /api/v1/titles/{}", id);
        TitleResponse response = titleService.updateTitle(id, request);
        return ResponseEntity.ok(ApiResponse.success("Title updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a title")
    public ResponseEntity<ApiResponse<Void>> deleteTitle(@PathVariable String id) {
        log.info("DELETE /api/v1/titles/{}", id);
        titleService.deleteTitle(id);
        return ResponseEntity.ok(ApiResponse.success("Title deleted successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get title by ID")
    public ResponseEntity<ApiResponse<TitleResponse>> getTitleById(@PathVariable String id) {
        log.info("GET /api/v1/titles/{}", id);
        TitleResponse response = titleService.getTitleById(id);
        return ResponseEntity.ok(ApiResponse.success("Title fetched successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all titles with pagination")
    public ResponseEntity<ApiResponse<PageResponse<TitleResponse>>> getAllTitles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "titleId,asc") String[] sort) {
        log.info("GET /api/v1/titles - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrders(sort)));
        PageResponse<TitleResponse> response = titleService.getAllTitles(pageable);
        return ResponseEntity.ok(ApiResponse.success("Titles fetched successfully", response));
    }

    private Sort.Order[] parseSortOrders(String[] sort) {
        if (sort.length == 2) {
            return new Sort.Order[]{new Sort.Order(
                    Sort.Direction.fromString(sort[1]), sort[0])};
        }
        return new Sort.Order[]{Sort.Order.asc("titleId")};
    }
}
