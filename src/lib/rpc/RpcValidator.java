package lib.rpc;

import java.util.ArrayList;
import java.util.List;

public class RpcValidator {


  private List<RpcField> fields = new ArrayList<RpcField>();

  public RpcValidator() {}

  public RpcValidator have(String name, byte type) {
    fields.add(new RpcField(name, type));
    return this;
  }

  public RpcValidator str(String name) {
    fields.add(new RpcField(name, RpcType.String));
    return this;
  }

  public RpcValidator intg(String name) {
    fields.add(new RpcField(name, RpcType.Integer));
    return this;
  }

  public List<RpcField> getFields() {
    return fields;
  }
  
  public RpcMessage withValidation (RpcMessage req, RpcHandler handler) throws Exception {

    for (RpcField field : fields) {
      RpcField reqField = req.get(field.getName());
      if (reqField == null) {
        return new RpcError(400, "Missing field " + field.getName());
      }
      if (reqField.getType() != field.getType()) {
        return new RpcError(400, "Wrong type for field " + field.getName() + " expected " + field.getType() + " got " + reqField.getType());
      }
    }

    return handler.handle(req);
  }
}
