package com.xws;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static java.lang.System.out;

/**
 * 测试: Redis连接
 */
public final class Connector {
  public static final void run()  throws Exception {
    timeout();
    pooling();
  }

  static final void timeout() throws InterruptedException {
    /*
     服务端是无状态连接，Jedis 客户端timeout仅是socket超时；
     服务端timeout配置默认是0(无超时限制)， 如果timeout>0则在timeout后的一段
     时间内关闭该客户端连接；
    */
    Integer timeout = 120;
    Jedis R = new Jedis("192.168.4.11", 6379, timeout);
    R.connect();

    timeout = 30;
    R.configSet("timeout", timeout.toString());
    out.format("config get timeout: %s\n", R.configGet("timeout").get(1));
    timeout = Integer.parseInt(R.configGet("timeout").get(1));

    Thread.sleep(timeout*2000);

    /*
      由于服务端主动关闭连接会导致如下异常:
      redis.clients.jedis.exceptions.JedisConnectionException:
      It seems like server has closed the connection.
      NOTE:
      在实际应用中合适的做法是将客户端的连接timeout < 1/2 * 服务端timeout
    */
    try {
      out.format("set CON:STR %s after %d timeout\n",
          R.set("CON:STR", timeout.toString()), timeout);
    } catch (JedisConnectionException e) {
      out.println(e);
    } finally {
      R.close();
    }
  }

  static final void pooling()  {
    /*
      如果客户端采用连接池管理则服务端请将timeout置为0；
     */
    final JedisPoolConfig conf = new JedisPoolConfig();
    conf.setMaxTotal(16/*8*/);
    conf.setMaxIdle(1200);
    conf.setMinIdle(120);
    final JedisPool pool = new JedisPool(conf, "192.168.4.11", 6379);

    Jedis R = pool.getResource();
    R.configSet("timeout", "0");
    out.format("set POOL:STR %s\n", R.getSet("POOL:STR", conf.toString()));

    pool.returnResource(R);

    R = pool.getResource();
    out.format("get POOL:STR %s\n", R.get("POOL:STR"));

    pool.returnResource(R);
  }
}
