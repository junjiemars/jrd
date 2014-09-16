package com.xws;

import java.lang.reflect.Type;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;

/*
 * JSON序列化/反序列化
 */
public final class JsonSerializer {

	public static final <T> T from(final String json, final Class<T> type) {

		if (null == json || null == type) {
			return (null);
		}

		final Gson gson = new Gson();

		try {
			final T t = gson.fromJson(json, type);
			return (t);
		} catch (JsonSyntaxException e) {
			_logger.error(e);
		} catch (JsonParseException e) {
			_logger.error(e);
		}
		
		return (null);
	}

	//!NOTE: calling:
	//		Type type = new TypeToken<T>(){}.getType();
	public static final <T> T from(final String json, final Type type) {
		if (null == json || null == type) {
			return (null);
		}
		
		final Gson g = new Gson();
		T t = null;
		try {
			t = g.fromJson(json, type);
		} catch (JsonSyntaxException e) {
			_logger.error(e);
		} catch (JsonParseException e) {
			_logger.error(e);
		}
		
		return (t);
	}
	
	public static final <T> T from(final String json,
                                 final Class<T> type,
                                 final TypeAdapter<T> adapter) {
		
		if (null == json || null == type) {
			return (null);
		}

		final GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(type, adapter);
		final T t = _from(gb.create(), json, type);

		return (t);
	}

	public static final <T> String to(final T t) {
		if (null == t) {
			return (null);
		}

		final Gson gson = new Gson();
		final String j = gson.toJson(t);

		return (j);
	}
	
	public static final <T> T[] append(T[] src, final T last) {
		if (null == src) {
			return (null);
		}

		final int n = src.length;
		src = Arrays.copyOf(src, n+1);
		src[n] = last;
		return (src);
	}
	
	public static final <T> T[] prepend(T[] src, final T first) {
		if (null == src) {
			return (null);
		}
		
		final int n = src.length;
		src = Arrays.copyOf(src, n+1);
		System.arraycopy(src, 0, src, 1, n);
		src[0] = first;
		return (src);
	}

	static final <T> T _from(final Gson gson, final String json,
			final Class<T> type) {
		
		try {
			final T t = gson.fromJson(json, type);
			return (t);
		} catch (JsonSyntaxException e) {
			_logger.error(e);
		} catch (JsonParseException e) {
			_logger.error(e);
		}

		return (null);
	}

	private static final Logger _logger = Logger.getLogger(JsonSerializer.class);
}
