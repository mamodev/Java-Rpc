package lib.rpc;

import java.nio.ByteBuffer;

public class RpcType {
  
  private RpcType() {
  }

  public static final byte Integer = 0;
  public static final byte String = 1;

  public static int sizeofInt () {
    return 4;
  }
  
  public static int sizeofString (String s) {
    return s.length() + 1;
  }

  public static void serializeInt (ByteBuffer buffer, int value) {
    buffer.putInt(value);
  }

  public static void serializeString (ByteBuffer buffer, String value) {
    for (int i = 0; i < value.length(); i++) {
      buffer.put( (byte) value.charAt(i));
    }
    buffer.put( (byte) 0);
  }

  public static int deserializeInt (ByteBuffer buffer) {
    return buffer.getInt();
  }

  public static String deserializeString (ByteBuffer buffer) {
    StringBuilder builder = new StringBuilder();
    byte b = buffer.get();
    while (b != 0) {
      builder.append((char) b);
      b = buffer.get();
    }
    return builder.toString();
  }


  public static String toString (byte type) {
    switch (type) {
      case Integer:
        return "Integer";
      case String:
        return "String";
      default:
        return "Unknown";
    }
  }
}
