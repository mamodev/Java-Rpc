package lib.rpc;

public class Rpc implements RpcHandler {

  private final RpcValidator validator;
  private final RpcHandler handler;
  private final String name;
  private RpcFilter filter;

  public Rpc(String name, RpcValidator validator, RpcHandler handler) {
    this.validator = validator;
    this.handler = handler;
    this.name = name;
  }

  public Rpc(String name, RpcFilter filter, RpcValidator validator, RpcHandler handler) {
    this.validator = validator;
    this.handler = handler;
    this.name = name;
    this.filter = filter;
  }

  public Rpc(String name, RpcFilter filter, RpcHandler handler) {
    this.validator = new RpcValidator();
    this.handler = handler;
    this.name = name;
    this.filter = filter;
  }
  
  public Rpc(String name, RpcHandler handler) {
    this.validator = new RpcValidator();
    this.handler = handler;
    this.name = name;
  }

  public RpcMessage handle(RpcMessage req) throws Exception{
    if (filter != null) {
      RpcMessage res = filter.handle(req);
      if (res != null) {
        return res;
      }
    }

    return validator.withValidation(req, handler);
  }

  public String getName() {
    return name;
  }

  public RpcValidator getValidator() {
    return validator;
  }

  public boolean isVisible (RpcMessage req) {
    if (filter == null) {
      return true;
    }

    return filter.handle(req) == null;
  }
}
