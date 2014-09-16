package com.xws;

import org.apache.log4j.Logger;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.Templates;

import java.io.IOException;

/**
 * 2元序列化/反序列化
 */
public final class Biner {

  public static final <T> byte[] to(final T t) {
    if (null == t)
      return null;

    final MessagePack p = new MessagePack();

    try {
      return (p.write(t));
    } catch (IOException e) {
      _logger.error(e);
    }

    return null;
  }

  public static final <T> T from(final byte[] b, final Class<T> type) {
    final MessagePack p = new MessagePack();
    try {
      return p.read(b, type);
    } catch (IOException e) {
      _logger.error(e);
    }
    return null;
  }


  private static final Logger _logger = Logger.getLogger(Biner.class);
}
