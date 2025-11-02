package com.huy.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Configuration
public class JacksonConfig {
	@Bean
	public ObjectMapper objectMapper() {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(
						new DateTimeFormatterBuilder()
						.appendPattern("yyyy-MM-dd'T'HH:mm:ss")
						.optionalStart()
						.appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
						.optionalEnd()
						.toFormatter()));
		return new ObjectMapper().registerModule(javaTimeModule)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//				.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
}
