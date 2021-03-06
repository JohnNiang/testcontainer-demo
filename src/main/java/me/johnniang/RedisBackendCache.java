package me.johnniang;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Redis backend cache.
 *
 * @author johnniang
 */
public class RedisBackendCache implements Cache {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Jedis jedis;

	private final ObjectMapper objectMapper;

	public RedisBackendCache(String host, int port) {
		this.jedis = new Jedis(host, port);
		objectMapper = new ObjectMapper();
	}

	@Override
	public void put(String key, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("Trying to put key: {} and value: {} into redis", key, value);
		}
		try {
			String valueString = this.objectMapper.writeValueAsString(value);
			this.jedis.set(key, valueString);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Cannot convert value to json", e);
		}
	}

	@Override
	public <T> Optional<T> get(String key, Class<T> expectedClass) {
		if (log.isDebugEnabled()) {
			log.debug("Trying to fetch data with key: {} and expected class type: {}", key, expectedClass);
		}
		String valueString = this.jedis.get(key);
		if (valueString == null) {
			return Optional.empty();
		}
		try {
			T value = objectMapper.readValue(valueString, expectedClass);
			return Optional.of(value);
		}
		catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Failed to convert value to expected type: " + expectedClass, e);
		}
	}
}
