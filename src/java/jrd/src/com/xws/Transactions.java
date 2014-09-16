package com.xws;

import redis.clients.jedis.*;

import java.util.*;
import java.util.concurrent.*;

import static java.lang.System.out;

/**
 * Redis事务
 */
public final class Transactions {

  public static final void run() throws InterruptedException {

    //unwatched(_pool);
    threading(_pool);
    //watched(_pool);

  }

  static final void unwatched(final JedisPool pool) {
    final Jedis R = pool.getResource();

    out.format("set TRANS:INT:B %s\n", R.set("TRANS:INT:B", "1"));

    Transaction T = R.multi();
    T.incr("TRANS:INT:B");
    T.decr("TRANS:INT:B");
    List<Object> response = T.exec();

    for (Object o : response) {
      out.format("*n TRANS:INT:B %s\n", o);
    }
    out.format("get TRANS:INT:B %s\n", R.get("TRANS:INT:B"));

    pool.returnResource(R);
  }

  static final void watched(final JedisPool pool) {
    final Jedis R = pool.getResource();

    out.format("set TRANS:INT:A %s\n", R.set("TRANS:INT:A", "1"));
    out.format("set TRANS:INT:B %s\n", R.set("TRANS:INT:B", "1"));
    out.format("watch TRANS:INT:A %s\n", R.watch("TRANS:INT:A"));

    /*
      获取当前版本: TRANS:INT:A
      相当于if A == X then ...
    */
    out.format("get TRANS:INT:A %s\n", R.get("TRANS:INT:A"));

    final Transaction T = R.multi();

    Runnable E = new Runnable() {
      @Override
      public void run() {
        final Jedis r = pool.getResource();
        out.format("*t incr TRANS:INT:A %s\n", r.incr("TRANS:INT:A"));
        pool.returnResource(r);
        out.println("*t will rollback transaction");
      }
    };

    /*
      另起1个线程改变TRANS:INT:A的值；
      这将导致Redis执行事务回退
    */
    E.run();

    T.incr("TRANS:INT:B");
    T.incr("TRANS:INT:A");

    out.format("*w exec %s\n", T.exec());

    pool.returnResource(R);
  }

  static final void threading(final JedisPool pool) throws InterruptedException {
    /*
       Redis事务作为一个执行单元，多线程调用不会产生交错的结果，
       这是由于Redis先到先执行的单线程模型决定的；
     */
    final Random randomized = new Random();
    final Jedis R = pool.getResource();
    out.format("*t set THR:INT:B %s\n", R.set("THR:INT:B", "0"));


    final ExecutorService e = Executors.newFixedThreadPool(5);


    for (int i = 0; i < 10; i++) {
      int n = randomized.nextInt(100);
      e.execute(n % 2 == 0 ?
          new Runnable()  {
            @Override
            public void run() {
              final String n = String.format("Ten@%s", this.hashCode());
              Thread.currentThread().setName(n);
              final Jedis R = pool.getResource();

              final Transaction T = R.multi();
              T.incrBy("THR:INT:B", 10);
              T.incrBy("THR:INT:B", 10);
              T.incrBy("THR:INT:B", 10);

              try {
                Thread.sleep(randomized.nextInt(1000));
              } catch (InterruptedException ie) {
                out.format("*%s error %s\n", n, e);
              }
              out.format("*%s exec %s\n", n, T.exec());

              pool.returnResource(R);
            }
          }
          :
          new Runnable() {
            @Override
            public void run() {
              final String n = String.format("One@%s", this.hashCode());
              Thread.currentThread().setName(n);
              final Jedis R = pool.getResource();

              final Transaction T = R.multi();
              T.incrBy("THR:INT:B", 1);
              T.incrBy("THR:INT:B", 1);
              T.incrBy("THR:INT:B", 1);

              try {
                Thread.sleep(randomized.nextInt(1000));
              } catch (InterruptedException ie) {
                out.format("*%s error %s\n", n, ie);
              }
              out.format("*%s exec %s\n", n, T.exec());

              pool.returnResource(R);
            }
          }
      );
    }

    e.shutdown();
  }

  static {
    final JedisPoolConfig conf = new JedisPoolConfig();
    conf.setMaxTotal(32);
    _pool = new JedisPool(conf, "192.168.4.11", 6379);
  }

  static final JedisPool _pool;
}
