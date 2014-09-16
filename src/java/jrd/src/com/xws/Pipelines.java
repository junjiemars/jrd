package com.xws;

import redis.clients.jedis.*;

import java.util.List;

import static java.lang.System.out;

/**
 * Redis管线
 */
public final class Pipelines {

  public static final void run() {
    final JedisPoolConfig conf = new JedisPoolConfig();
    final JedisPool pool = new JedisPool(conf, "192.168.4.11", 6379);
    final Jedis R = pool.getResource();

    Pipeline P = R.pipelined();
    P.set("PIPELINE:STR:A", "A");
    P.set("PIPELINE:STR:B", "B");
    P.set("PIPELINE:STR:C", "C");
    List<Object> response = P.syncAndReturnAll();

    for (Object o : response) {
      out.format("set PIPELINE:STR:* %s\n", o);
    }

    P.get("PIPELINE:STR:A");
    P.get("PIPELINE:STR:B");
    P.get("PIPELINE:STR:C");
    response.clear();
    response.addAll(P.syncAndReturnAll());

    for (Object o : response) {
      out.format("get PIPELINE:STR:* %s\n", o);
    }

    pool.returnResource(R);
  }
}
