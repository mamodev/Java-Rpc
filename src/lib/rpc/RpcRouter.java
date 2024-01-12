package lib.rpc;

import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.Map;

import lib.nioserver.NIOHandler;
import lib.nioserver.NIORequest;

public class RpcRouter implements NIOHandler {

  private Map<String, Rpc> handlers = new HashMap<String, Rpc>();
  private RpcHandler swagger;

  public RpcRouter(boolean showSwagger) {
    if (showSwagger) {
      swagger = new RpcSwagger(handlers);
    }
  }

  public void handle(NIORequest req) {
    try {
      RpcMessage msg = RpcMessage.deserialize(req.getData());
      req.freeRequestBuffer();

      String rpc = msg.getRpcName();

      if (rpc == null) {
        req.sendReponse(new RpcError(400,  "No RPC field").toByteArray());
        return;
      }

      if (rpc.equals("swagger")) {
        if (swagger == null) {
          req.sendReponse(new RpcError(404, "No swagger").toByteArray());
          return;
        }
        RpcMessage res = swagger.handle(msg);

        if (res != null) {
          req.sendReponse(res.toByteArray());
        }

        return;
      }

      Rpc handler = handlers.get(rpc);
      if (handler == null) {
        req.sendReponse(new RpcError(404, "No handler for " + rpc).toByteArray());
        return;
      }

      RpcMessage res;
      try {
        res = handler.handle(msg);
      } catch (Exception e) {
        res = new RpcError(500, "Internal server error");
      } 

      if (res != null) {
        req.sendReponse(res.toByteArray());
      }
      
    } catch (ClosedChannelException e) {
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addRpc(Rpc rpc) {
    handlers.put(rpc.getName(), rpc);
  }

  public void addHandler(String name, RpcHandler handler) {
    handlers.put(name, new Rpc(name, handler));
  }

  public void addHandler(String name, RpcFilter filter, RpcHandler handler) {
    handlers.put(name, new Rpc(name, filter, handler));
  }

  public void addHandler(String name, RpcFilter filter, RpcValidator validator, RpcHandler handler) {
    handlers.put(name, new Rpc(name, filter, validator, handler));
  }
}
