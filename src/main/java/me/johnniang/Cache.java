package me.johnniang;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Cache, for storing data associated with keys.
 */
public interface Cache {

	void put(String key, Object value);

	<T> Optional<T> get(String key, Class<T> expectedClass) throws JsonMappingException;
}
