package com.company.employeemanagement.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration.
 * <p>
 * Active only when {@code spring.cache.type=redis} (default in application.yml).
 * Tests override this to {@code simple} so they don't need a running Redis.
 * <p>
 * - Keys: String
 * - Values: JSON (Jackson) — human-readable in Redis CLI, no Serializable needed
 * - Per-cache TTLs override the global default
 */
@Configuration
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
public class RedisCacheConfig {

    /** Names of all caches used in the application. Reference these in @Cacheable annotations. */
    public static final String EMPLOYEE_CACHE   = "employees";
    public static final String DEPARTMENT_CACHE = "departments";
    public static final String TITLE_CACHE      = "titles";
    public static final String SALARY_CACHE     = "salaries";
    public static final String MANAGER_CACHE    = "managers";

    /**
     * Dedicated ObjectMapper for cache serialization only.
     * Has default typing enabled so polymorphic types deserialize correctly.
     * <p>
     * Note: not {@code @Primary} — the web layer keeps using the default
     * Spring Boot ObjectMapper to avoid leaking type info into HTTP responses.
     */
    @Bean("cacheObjectMapper")
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(Object.class)
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        return mapper;
    }

    /**
     * Builds the RedisCacheManager with a default 10-minute TTL
     * and overrides for individual caches.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                          @org.springframework.beans.factory.annotation.Qualifier("cacheObjectMapper")
                                          ObjectMapper cacheObjectMapper) {

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(cacheObjectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .computePrefixWith(cacheName -> "emp-mgmt::" + cacheName + "::");

        // Per-cache TTL overrides
        Map<String, RedisCacheConfiguration> perCacheConfigs = new HashMap<>();
        perCacheConfigs.put(EMPLOYEE_CACHE,   defaultConfig.entryTtl(Duration.ofMinutes(15)));
        perCacheConfigs.put(DEPARTMENT_CACHE, defaultConfig.entryTtl(Duration.ofHours(1)));   // stable data
        perCacheConfigs.put(TITLE_CACHE,      defaultConfig.entryTtl(Duration.ofHours(1)));   // stable data
        perCacheConfigs.put(SALARY_CACHE,     defaultConfig.entryTtl(Duration.ofMinutes(30)));
        perCacheConfigs.put(MANAGER_CACHE,    defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCacheConfigs)
                .transactionAware()
                .build();
    }
}
