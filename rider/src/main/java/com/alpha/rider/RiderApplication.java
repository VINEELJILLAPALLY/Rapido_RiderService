package com.alpha.rider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@SpringBootApplication
public class RiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(RiderApplication.class, args);
	}
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
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
