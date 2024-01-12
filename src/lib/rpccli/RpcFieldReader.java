package lib.rpccli;

import java.util.List;
import java.util.Scanner;

import lib.rpc.RpcField;
import lib.rpc.RpcType;

public class RpcFieldReader {

  public static void read(List<RpcField> fields, Scanner scanner) {
      for (RpcField field : fields) {
        System.out.print(field.getName() + " (" + RpcType.toString(field.getType()) + "): ");

        switch (field.getType()) {
          case RpcType.Integer:
            field.set(IntegerReader.read(scanner), field.getType());
            break;
          case RpcType.String:
            field.set(scanner.nextLine(), field.getType());
            break;
          default:
            System.out.println("Unknown type (This should never happen)");
            break;
        }
      }
  }
}
