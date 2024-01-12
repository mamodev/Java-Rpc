package lib.rpc;

public class RpcText extends RpcMessage {

  public RpcText(String text) {
    super();
    setString("message", text);
  }
  
}
