package rocks.trunk.java.jrd;

import org.apache.log4j.Logger;
import org.msgpack.MessagePack;

import java.io.IOException;

/**
 * 2元序列化/反序列化
 */
public final class BinarySerializer {

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


  private static final Logger _logger = Logger.getLogger(BinarySerializer.class);
}
