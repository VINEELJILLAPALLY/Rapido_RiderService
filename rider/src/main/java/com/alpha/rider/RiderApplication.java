package com.alpha.rider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@SpringBootApplication
public class RiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiderApplication.class, args);
	}
	 @Bean
	    public ObjectMapper objectMapper() {
	        return new ObjectMapper();
	    }
	@Bean
	public RestTemplate restTemplate() {

	    HttpComponentsClientHttpRequestFactory factory =
	            new HttpComponentsClientHttpRequestFactory();
		return new RestTemplate(factory);
	}
	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return new StringRedisTemplate(redisConnectionFactory);
	}
	
	@Bean
	@Primary
	public RedisTemplate<String, Object> redisTemplate(
	        RedisConnectionFactory redisConnectionFactory) {

	    RedisTemplate<String, Object> template = new RedisTemplate<>();

	    template.setConnectionFactory(redisConnectionFactory);

	    StringRedisSerializer stringSerializer = new StringRedisSerializer();

	    // Redis keys
	    template.setKeySerializer(stringSerializer);

	    // Redis values / GEO members
	    template.setValueSerializer(stringSerializer);

	    // Hash keys/values
	    template.setHashKeySerializer(stringSerializer);
	    template.setHashValueSerializer(stringSerializer);

	    template.afterPropertiesSet();

	    return template;
	}
	}
