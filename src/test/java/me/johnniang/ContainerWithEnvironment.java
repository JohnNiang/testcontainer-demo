package me.johnniang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Container with environment.
 *
 * @author johnniang
 */
@Testcontainers
class ContainerWithEnvironment {

	Logger log = LoggerFactory.getLogger(getClass());

	DockerImageName alpineImage = DockerImageName.parse("alpine");

	@Container
	GenericContainer<?> alpine = new GenericContainer<>(alpineImage)
			.withExposedPorts(18888)
			.withEnv("HELLO", "WORLD")
			.withCommand("/bin/sh", "-c", "while true; do echo -n \"$HELLO\" | nc -l -p 18888; done")
			.withLogConsumer(outputFrame -> {
				log.debug("Container output ===|{}", outputFrame.getUtf8String());
			});

	@Test
	void fetchEnvironmentWithSocket() throws IOException {
		int port = alpine.getFirstMappedPort();

		try (Socket socket = new Socket("localhost", port)) {
			try (InputStream is = socket.getInputStream()) {
				try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = is.read(buffer)) != -1) {
						os.write(buffer, 0, read);
					}
					String response = os.toString(StandardCharsets.UTF_8);
					Assertions.assertEquals("WORLD", response);
				}
			}
		}
	}
}
