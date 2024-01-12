package lib.rpc;

public class RpcField {
  private String name;
  private Object value;
  private byte type;

  public RpcField(String name, byte type, Object value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public RpcField(String name, byte type) {
    this.name = name;
    this.type = type;
  }

  public byte getType() {
    return type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }

  public void set(Object value, byte type) {
    this.value = value;
    this.type = type;
  }

}
