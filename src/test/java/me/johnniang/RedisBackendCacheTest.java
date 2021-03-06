package me.johnniang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tester.
 *
 * @author johnniang
 */
@Testcontainers
class RedisBackendCacheTest {

	@Container
	GenericContainer redis = new GenericContainer(DockerImageName.parse("redis:5.0.3-alpine")).withExposedPorts(6379);

	RedisBackendCache cache;

	@BeforeEach
	void setUp() {
		cache = new RedisBackendCache(redis.getHost(), redis.getFirstMappedPort());
	}

	@Test
	void contextLoad() {
	}

	@Test
	void testSimplePutAndGet() {
		cache.put("hello", "world");
		String retrieved = cache.get("hello", String.class).orElseThrow();
		assertEquals("world", retrieved);
	}
}
