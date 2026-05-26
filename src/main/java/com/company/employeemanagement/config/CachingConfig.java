package com.company.employeemanagement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's cache abstraction application-wide.
 * <p>
 * The actual cache provider (Redis in production, simple in-memory in tests)
 * is chosen via {@code spring.cache.type}. Keeping {@code @EnableCaching} here
 * means caching works even when {@link RedisCacheConfig} is skipped (e.g. tests).
 */
@Configuration
@EnableCaching
public class CachingConfig {
}
