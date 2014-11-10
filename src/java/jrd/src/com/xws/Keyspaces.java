package com.xws;

import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.System.out;

/**
 * Created by junjie on 10/16/14.
 */
public class Keyspaces {

    public static void run() {
        basic();
        weighted();
        patterned();
    }

    private static void basic() {
        ShardedJedisPool pool = new ShardedJedisPool(conf, info);
        ShardedJedis R = pool.getResource();

        out.format("set K:BASIC %s\n", R.set("K:BASIC", "I'm Sharded Jedis"));
        out.format("get K:BASIC %s\n", R.get("K:BASIC"));

        pool.returnResource(R);
    }

    private static void weighted() {
        ShardedJedisPool pool = new ShardedJedisPool(conf, info);
        ShardedJedis R = pool.getResource();

        out.format("set K:WEIGHTED %s\n", R.set("K:WEIGHTED", "I'm Sharded Jedis"));
        out.format("get K:WEIGHTED %s\n", R.get("K:WEIGHTED"));

        pool.returnResource(R);
    }

    private static void patterned() {
        ShardedJedisPool pool = new ShardedJedisPool(conf, info,
                //ShardedJedis.DEFAULT_KEY_TAG_PATTERN
                Pattern.compile("K:S:P:(.+?)")
        );
        ShardedJedis R = pool.getResource();

        out.format("set K{P} %s\n", R.set("K{P}", "I'm Sharded Jedis, .+?\\{.+?\\}"));
        out.format("get K{P} %s\n", R.get("K{P}"));

        out.format("set K:S:P:X %s\n", R.set("K:S:P:X", "I'm Sharding Jedis, K:S:P:(.+?)"));
        out.format("set K:S:P:X %s\n", R.get("K:S:P:X"));

        pool.returnResource(R);
    }

    private static final JedisPoolConfig conf = new JedisPoolConfig() {
        { setMaxTotal(16/*8*/); }
        { setMaxIdle(1200); }
        { setMinIdle(120); }
    };

    private static final String host = "192.168.4.11";
    private static final int port = 6379;
    private static final int timeout = 2000;

    private static final List<JedisShardInfo> info = new ArrayList() {
        { add(new JedisShardInfo("192.168.4.11", 6379, 2000, 2)); }
        { add(new JedisShardInfo("192.168.21.168", 6379, 2000, 2)); }
        { /* other sharded redis node */ }
    };
}
