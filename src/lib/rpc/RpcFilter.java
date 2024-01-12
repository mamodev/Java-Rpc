package lib.rpc;


public interface RpcFilter extends RpcHandler {
  // This method is called by the Rpc class when a request is received.
  // if the filter returns a message, it will be sent back to the client
  // without calling the handler.
  public RpcError handle (RpcMessage req);
} 
