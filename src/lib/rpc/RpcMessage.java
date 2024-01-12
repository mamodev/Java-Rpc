package lib.rpc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RpcMessage {

  private final List<RpcField> fields;

  public RpcMessage() {
    this.fields = new ArrayList<RpcField>();
  }

  public RpcMessage(List<RpcField> fields) {
    this.fields = fields;
  }

  public List<RpcField> getFields() {
    return fields;
  }

  public List<RpcField> addFields(List<RpcField> fields) {
    this.fields.addAll(fields);
    return this.fields;
  }

  public RpcMessage set(String name, byte type, Object value) {
    for (RpcField field : fields) {
      if (field.getName().equals(name)) {
        field.set(value, type);
        return this;  
      }
    }

    fields.add(new RpcField(name, type, value));
    return this;
  }

  public RpcMessage setString(String name, String value) {
    set(name, RpcType.String, value);
    return this;
  }

  public RpcMessage setInteger(String name, Integer value) {
    set(name, RpcType.Integer, value);
    return this;
  }

  public RpcField get(String name) {
    for (RpcField field : fields) {
      if (field.getName().equals(name)) {
        return field;
      }
    }

    return null;
  }

  public String getString(String name) {
    RpcField field = get(name);
    if (field == null || field.getType() != RpcType.String) {
      return null;
    }
    return (String) field.getValue();
  }

  public Integer getInteger(String name) {
    RpcField field = get(name);
    if (field == null || field.getType() != RpcType.Integer) {
      return null;
    }
    return (Integer) field.getValue();
  }

  public String getRpcName() {
    for (RpcField field : fields) {
      if (field.getType() ==RpcType.String && field.getName().equals("rpc")) {
        return  (String) field.getValue();
      }
    }
    return null;
  }

  public void remove(String name) {
    fields.removeIf(field -> field.getName().equals(name));
  }

  public ByteBuffer serialize() throws Exception {
    int capacity = 0;
    for (RpcField field : fields) {
      capacity += RpcType.sizeofString(field.getName());
      capacity += 1;
      switch (field.getType()) {
        case RpcType.Integer:
          capacity += RpcType.sizeofInt();
          break;
        case RpcType.String:
          capacity += RpcType.sizeofString((String) field.getValue());
          break;
        default:
          throw new Exception("Invalid argument type");
      }
    }

    ByteBuffer buffer = ByteBuffer.allocate(capacity);

    for (RpcField field : fields) {
      RpcType.serializeString(buffer, field.getName());
      buffer.put(field.getType());
      switch (field.getType()) {
        case RpcType.Integer:
          RpcType.serializeInt(buffer, (Integer) field.getValue());
          break;
        default:
          RpcType.serializeString(buffer, (String) field.getValue());
          break;
      }
    }

    buffer.flip();
    return buffer;
  }

  public byte[] toByteArray() throws Exception {
    return serialize().array();
  }

  public static RpcMessage  deserialize(ByteBuffer buffer) throws Exception {
    RpcMessage message = new RpcMessage();

    while (buffer.hasRemaining()) {
      String name = RpcType.deserializeString(buffer);
      byte type = buffer.get();
      switch (type) {
        case RpcType.Integer:
          message.set(name, type, RpcType.deserializeInt(buffer));
          break;
        case RpcType.String:
          message.set(name, type, RpcType.deserializeString(buffer));
          break;
        default:
          throw new Exception("Invalid argument type");
      }
    }

    return message;
  }

  public static RpcMessage newRPC(String name) {
    RpcMessage message = new RpcMessage();
    message.set("rpc", RpcType.String, name);
    return message;
  }

  @Override
  public String toString() {
    String result = "";
    for (RpcField field : fields) {
      result += field.getName() + ": " + field.getValue() + "\n";
    }
    return result;
  }
}
