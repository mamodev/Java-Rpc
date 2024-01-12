package lib.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RpcSwagger implements RpcHandler {

  private final Map<String, Rpc> rpcs;

  public RpcSwagger(Map<String, Rpc> rpcs) {
    this.rpcs = rpcs;
  }

  private String formatRpc(Rpc rpc) {
    StringBuilder sb = new StringBuilder();
    sb.append(rpc.getName()).append("(");

    List <RpcField> fields = rpc.getValidator().getFields();
    for (int i = 0; i < fields.size(); i++) {
      RpcField field = fields.get(i);
      sb.append(field.getName()).append(": ").append(field.getType());
      
      if (i < fields.size() - 1) {
        sb.append(", ");
      }
    }

    sb.append(")");

    return sb.toString();
  }

  public RpcMessage handle(RpcMessage req) {

    RpcMessage res = new RpcMessage();

    for (Rpc rpc : rpcs.values()) {
      if (rpc.isVisible(req)) {
        res.setString(rpc.getName(), formatRpc(rpc));
      }
    }

    return res;
  }

  public static List<RpcField> getFields(String rpcSwagger)  {
    int firstBracket = rpcSwagger.indexOf("(");

    rpcSwagger = rpcSwagger.substring(firstBracket + 1, rpcSwagger.length() - 1);

    if (rpcSwagger.equals("")) {
      return new ArrayList<RpcField>();
    }

    String[] fields = rpcSwagger.split(", ");

    List <RpcField> res = new ArrayList<RpcField>();

    for (String field : fields) {
      String[] fieldParts = field.split(": ");
      String fieldName = fieldParts[0];
      Integer fieldType = Integer.parseInt(fieldParts[1]);

      byte type = (byte) (fieldType & 0xFF);

      RpcField rpcField = new RpcField(fieldName, type);
      res.add(rpcField);
    }

    return res;
  }
}
