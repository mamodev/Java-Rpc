package lib.rpc;

public interface RpcHandler {
  
  public RpcMessage handle(RpcMessage req) throws Exception;

}
