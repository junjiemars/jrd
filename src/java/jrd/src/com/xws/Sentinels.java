package com.xws;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static java.lang.System.out;

/**
 * Redis监控: 在Sentinel切换Master后，Redis客户端如何自动连接
 * 到正确的Master?
*/
public final class Sentinels {

  public static final void run() {
    final String master = "mymaster";
    final Set<String> sentinels = new HashSet<String>(){
      //{ add(""); }
      { add("192.168.40.34:26379"); }
    };
    final JedisPoolConfig conf = new JedisPoolConfig();
    final JedisSentinelPool pool = new JedisSentinelPool(master, sentinels, conf);

    out.println(pool.getCurrentHostMaster());
    final Jedis R = pool.getResource();
    out.format("set SEN:STR:A %s\n", R.set("SEN:STR:A", "fine"));
    out.format("get SEN:STR:A %s\n", R.get("SEN:STR:A"));

    pool.returnResource(R);
    pool.destroy();
  }
}
