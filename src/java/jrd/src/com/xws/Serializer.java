package com.xws;

import org.msgpack.annotation.Message;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.util.Date;

import static java.lang.System.out;

/**
 * String(K/V)的序列化与反序列化
 */
public final class Serializer {
  public static final void run() {
    // JSON序列化/反序列化
    jsonPacker();

    // 2元序列化/反序列化
    binaryPacker();
  }

  public static final void jsonPacker() {
    Jedis R = new Jedis("192.168.4.11", 6379);
    R.connect();

    // Json: string:
    final String s = "Hello,World";
    final String s_json = JsonSerializer.to(s);
    out.format("set JSON:STR %s\n", R.set("JSON:STR", s_json));
    out.format("get JSON:STR %s\n", R.get("JSON:STR"));

    // Json: object:
    final _User u = new _User();
    u.name = "John";
    u.birth = new Date();
    final String u_json = JsonSerializer.to(u);
    out.format("set JSON:USER %s\n", R.set("JSON:USER", u_json));
    out.format("get JSON:USER %s\n", R.get("JSON:USER"));

    R.close();

  }

  public static final void binaryPacker() {
    BinaryJedis R = new BinaryJedis("192.168.4.11", 6379);
    R.connect();

    // Binary: string
    final String s = "Hello,World";
    out.format("set BIN:STR %s\n", R.set("BIN:STR".getBytes(), BinarySerializer.to(s)));
    out.format("get BIN:STR %s\n", BinarySerializer.from(R.get("BIN:STR".getBytes()), String.class));

    // Binary: object
    final _User u = new _User();
    u.name = "Targaryen";
    u.birth = new Date();
    out.format("set BIN:USER %s\n", R.set("BIN:USER".getBytes(), BinarySerializer.to(u)));
    out.format("get BIN:USER %s\n", BinarySerializer.from(R.get("BIN:USER".getBytes()), u.getClass()));

    R.close();
  }

  @Message
  public static final class _User {
    public String name;
    public Date birth;

    @Override
    public String toString() {
      return String.format("name: %s, birth: %s", name, birth);
    }
  }
}
