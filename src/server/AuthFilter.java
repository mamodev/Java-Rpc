package server;

import lib.rpc.RpcError;
import lib.rpc.RpcFilter;
import lib.rpc.RpcMessage;

public class AuthFilter {
  public static RpcFilter anonymous = (RpcMessage rpc) -> {
    if (rpc.getInteger("cookie-session-id") != null) {
      return new RpcError(401, "this action is only available to anonymous users");
    }
    return null;
  };

  public static RpcFilter authenticated = (RpcMessage rpc) -> {
    Integer sessionId = rpc.getInteger("cookie-session-id");
    if (sessionId == null) {
      return new RpcError(401, "this action is only available to authenticated users");
    }

    if (!Session.hasSession(sessionId)) {
      return new RpcError(401, "Your session has expired").setInteger("delete-cookie-session-id", 0);
    }

    return null;
  };
}
