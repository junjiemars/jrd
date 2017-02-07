package rocks.trunk.java.jrd;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;

import static java.lang.System.out;

/**
 * 基础Redis数据类型及操作
 */
public final class Basic {
  public static final void run() {
    Jedis R = new Jedis("192.168.4.11", 6379);
    R.connect();

    R.select(1);

    out.println(R.configGet("maxclients"));

    // K/V operations
    out.format("set K0 abc: %s\n", R.set("K0", "abc"));
    out.format("get K0: %s\n", R.get("K0"));

    // List operations
    R.del("L0");
    out.format("lpush L0 a b c: %s\n", R.lpush("L0", "a", "b", "c"));
    out.format("llen L0: %d\n", R.llen("L0"));
    out.format("lrange L0 0 -1 : %s\n", R.lrange("L0", 0, -1));

    // Hash operations
    R.del("H0");
    out.format("hset H0 f_a v_0 : %s\n", R.hset("H0", "f_a", "v_0"));
    out.format("hget H0 f_a : %s\n", R.hget("H0", "f_a"));

    // Set operations
    R.del("S0");
    out.format("sadd S0 a b c : %s\n", R.sadd("S0", "a", "b", "c"));
    out.format("scard S0 : %s\n", R.scard("S0"));
    out.format("scan S0 : ");
    for (String i : R.sscan("S0", "0").getResult()) {
      out.format("%s ", i);
    }

    // Sorted Set operations
    R.del("Z0");
    out.format("zadd Z0 1.0 v_0 : %s\n", R.zadd("Z0", 1.0f, "v_0"));
    out.format("zscan Z0 0 : ");
    for (Tuple i : R.zscan("Z0", "0").getResult()) {
      out.format("score=%s value=%s ", i.getScore(), i.getElement());
    }
    out.println();

    R.close();
  }
}
