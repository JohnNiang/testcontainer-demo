package me.johnniang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Container with environment.
 *
 * @author johnniang
 */
@Testcontainers
class ContainerWithEnvironmentTest {

	Logger log = LoggerFactory.getLogger(getClass());

	DockerImageName alpineImage = DockerImageName.parse("alpine");

	@Container
	GenericContainer<?> alpine = new GenericContainer<>(alpineImage)
			.withExposedPorts(80)
			.withEnv("HELLO", "WORLD")
			.withCommand("/bin/sh", "-c", "while true; do echo -n \"$HELLO\" | nc -l -p 80; done")
			.withLogConsumer(new Slf4jLogConsumer(this.log));

	@Test
	void fetchEnvironmentWithSocket() throws IOException {
		int port = this.alpine.getFirstMappedPort();

		try (Socket socket = new Socket("localhost", port)) {
			try (OutputStream os = socket.getOutputStream();
				 InputStream is = socket.getInputStream()) {
				os.write("Hello\n".getBytes(StandardCharsets.UTF_8));
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					byte[] buffer = new byte[1024];
					int read;
					while ((read = is.read(buffer)) != -1) {
						baos.write(buffer, 0, read);
					}
					String response = baos.toString(StandardCharsets.UTF_8);
					Assertions.assertEquals("WORLD", response);
				}
			}
		}
	}
}
