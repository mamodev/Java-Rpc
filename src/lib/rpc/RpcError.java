package lib.rpc;

public class RpcError extends RpcMessage {

  public RpcError(int code, String message) {
    super();
    set("error-code", RpcType.Integer, code);
    set("message", RpcType.String, message);
  }

  public String getMessage() {
    return getString("message");
  }

  public RpcError setInteger(String name, Integer value) {
    return (RpcError) super.setInteger(name, value);
  }

  public RpcError setString(String name, String value) {
    return (RpcError) super.setString(name, value);
  }

  public int getCode() {
    return getInteger("error-code");
  }

  public static boolean isError(RpcMessage msg) {
    return msg.getInteger("error-code") != null;
  }

  public static RpcError fromMessage(RpcMessage msg) {
    return new RpcError(msg.getInteger("error-code"), msg.getString("message"));
  }

  @Override
  public String toString() {
    return "Error: " + getCode() + " - " + getMessage();
  }
}
