package me.johnniang;


import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.MinimumDurationRunningStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Indefinite wait one shot startup test.
 *
 * @author johnniang
 */
@Testcontainers
class IndefiniteWaitOneShotStartupTest {

	@Container
	GenericContainer<?> bboxWithIndefiniteOneShot = new GenericContainer<>(DockerImageName.parse("busybox:1.31.1"))
			.withCommand("sh", "-c", String.format("sleep 5 && echo \"%s\"", "Hello world!"))
			.withStartupCheckStrategy(new MinimumDurationRunningStartupCheckStrategy(Duration.ofSeconds(1)));

	@Test
	void contextLoad() {

	}

}
