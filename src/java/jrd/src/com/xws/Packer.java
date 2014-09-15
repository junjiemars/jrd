package com.xws;

import org.msgpack.annotation.Message;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.util.Date;

import static java.lang.System.out;

/**
 * Created by junjie on 9/15/2014.
 */
public final class Packer {
  public static final void run() {
    jsonPacker();
    binaryPacker();
  }

  public static final void jsonPacker() {
    Jedis R = new Jedis("192.168.4.11", 6379);
    R.connect();

    // Json: serializer/deserializer

    // Json: string:
    final String s = "Hello,World";
    final String s_json = Jsoner.to(s);
    out.format("set JSON:STR %s\n", R.set("JSON:STR", s_json));
    out.format("get JSON:STR %s\n", R.get("JSON:STR"));

    // Json: object:
    final _User u = new _User();
    u.name = "John";
    u.birth = new Date();
    final String u_json = Jsoner.to(u);
    out.format("set JSON:USER %s\n", R.set("JSON:USER", u_json));
    out.format("get JSON:USER %s\n", R.get("JSON:USER"));

    R.close();

  }

  public static final void binaryPacker() {
    BinaryJedis R = new BinaryJedis("192.168.4.11", 6379);
    R.connect();

    // Binary: serializer/deserializer

    // Binary: string
    final String s = "Hello,World";
    out.format("set BIN:STR %s\n", R.set("BIN:STR".getBytes(), Biner.to(s)));
    out.format("get BIN:STR %s\n", Biner.from(R.get("BIN:STR".getBytes()), String.class));

    // Binary: object
    final _User u = new _User();
    u.name = "Targaryen";
    u.birth = new Date();
    out.format("set BIN:USER %s\n", R.set("BIN:USER".getBytes(), Biner.to(u)));
    out.format("get BIN:USER %s\n", Biner.from(R.get("BIN:USER".getBytes()), u.getClass()));

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
