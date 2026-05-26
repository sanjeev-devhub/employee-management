package com.company.employeemanagement.service.impl;

import com.company.employeemanagement.config.RedisCacheConfig;
import com.company.employeemanagement.dto.request.TitleRequest;
import com.company.employeemanagement.dto.response.PageResponse;
import com.company.employeemanagement.dto.response.TitleResponse;
import com.company.employeemanagement.entity.Title;
import com.company.employeemanagement.exception.DuplicateResourceException;
import com.company.employeemanagement.exception.ResourceNotFoundException;
import com.company.employeemanagement.mapper.TitleMapper;
import com.company.employeemanagement.repository.TitleRepository;
import com.company.employeemanagement.service.TitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TitleServiceImpl implements TitleService {

    private final TitleRepository titleRepository;
    private final TitleMapper titleMapper;

    @Override
    @Transactional
    @Caching(
            put   = { @CachePut(value = RedisCacheConfig.TITLE_CACHE, key = "#result.titleId") },
            evict = { @CacheEvict(value = RedisCacheConfig.TITLE_CACHE, key = "'ALL'") }
    )
    public TitleResponse createTitle(TitleRequest request) {
        log.info("Creating title with ID: {}", request.getTitleId());
        if (titleRepository.existsById(request.getTitleId())) {
            throw new DuplicateResourceException("Title", "titleId", request.getTitleId());
        }
        if (titleRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Title", "title", request.getTitle());
        }
        Title title = titleMapper.toEntity(request);
        Title saved = titleRepository.save(title);
        log.info("Title created successfully: {}", saved.getTitleId());
        return titleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @Caching(
            put   = { @CachePut(value = RedisCacheConfig.TITLE_CACHE, key = "#titleId") },
            evict = { @CacheEvict(value = RedisCacheConfig.TITLE_CACHE, key = "'ALL'") }
    )
    public TitleResponse updateTitle(String titleId, TitleRequest request) {
        log.info("Updating title: {}", titleId);
        Title title = findTitleOrThrow(titleId);
        if (!title.getTitle().equals(request.getTitle()) &&
                titleRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Title", "title", request.getTitle());
        }
        titleMapper.updateEntity(request, title);
        Title updated = titleRepository.save(title);
        log.info("Title updated: {}", titleId);
        return titleMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.TITLE_CACHE, key = "#titleId"),
            @CacheEvict(value = RedisCacheConfig.TITLE_CACHE, key = "'ALL'")
    })
    public void deleteTitle(String titleId) {
        log.info("Deleting title: {}", titleId);
        Title title = findTitleOrThrow(titleId);
        titleRepository.delete(title);
        log.info("Title deleted: {}", titleId);
    }

    @Override
    @Cacheable(value = RedisCacheConfig.TITLE_CACHE, key = "#titleId")
    public TitleResponse getTitleById(String titleId) {
        log.info("Fetching title from DB: {}", titleId);
        return titleMapper.toResponse(findTitleOrThrow(titleId));
    }

    @Override
    @Cacheable(
            value = RedisCacheConfig.TITLE_CACHE,
            key = "'ALL::' + #pageable.pageNumber + '::' + #pageable.pageSize + '::' + #pageable.sort.toString()"
    )
    public PageResponse<TitleResponse> getAllTitles(Pageable pageable) {
        log.info("Fetching all titles from DB, page: {}", pageable.getPageNumber());
        Page<TitleResponse> page = titleRepository.findAll(pageable)
                .map(titleMapper::toResponse);
        return PageResponse.of(page);
    }

    private Title findTitleOrThrow(String titleId) {
        return titleRepository.findById(titleId)
                .orElseThrow(() -> new ResourceNotFoundException("Title", "titleId", titleId));
    }
}
