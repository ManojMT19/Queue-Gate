package com.fairwaiting.logger_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import com.fairwaiting.logger_service.consumer.QueueEventListener;
import com.fairwaiting.logger_service.repository.QueueEventRecordRepository;
import com.fairwaiting.logger_service.entity.QueueEventRecord;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@Testcontainers
class LoggerServiceApplicationTests
{
	@Autowired
	private QueueEventListener listener;

	@Autowired
	private QueueEventRecordRepository repository;

	@Container
	static PostgreSQLContainer postgres =
			new PostgreSQLContainer("postgres:16");

	@DynamicPropertySource
	static void configureDatabase(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void shouldSaveEvent() {

		repository.deleteAll();

		String message = """
            {
                "eventId": "JOINED:Ravi:1000",
                "userId": "Ravi",
                "type": "JOINED",
                "occurredAt": "2026-11-13T12:00:00Z"
            }
            """;

		listener.onMessage(message);

		assertEquals(1, repository.count());

		QueueEventRecord saved = repository.findById("JOINED:Ravi:1000").orElseThrow();

		assertEquals("JOINED:Ravi:1000", saved.getEventId());
		assertEquals("Ravi", saved.getUserId());
		assertEquals("JOINED", saved.getType());
	}

	@Test
	void shouldIgnoreDuplicateEvent() {

		repository.deleteAll();

		String message = """
            {
                "eventId": "JOINED:Ravi:1000",
                "userId": "Ravi",
                "type": "JOINED",
                "occurredAt": "2026-11-13T12:00:00Z"
            }
            """;

		listener.onMessage(message);
		listener.onMessage(message);

		assertEquals(1, repository.count());
	}

	@Test
	void shouldSaveDifferentJoinsBySameUser() {

		repository.deleteAll();

		String firstMessage = """
            {
                "eventId": "JOINED:Ravi:1000",
                "userId": "Ravi",
                "type": "JOINED",
                "occurredAt": "2026-11-13T12:00:00Z"
            }
            """;

		String secondMessage = """
            {
                "eventId": "JOINED:Ravi:5000",
                "userId": "Ravi",
                "type": "JOINED",
                "occurredAt": "2026-11-13T12:05:00Z"
            }
            """;

		listener.onMessage(firstMessage);
		listener.onMessage(secondMessage);

		assertEquals(2, repository.count());
	}
}
