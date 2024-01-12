package lib.rpccli;

import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import lib.rpc.RpcClient;
import lib.rpc.RpcField;
import lib.rpc.RpcMessage;
import lib.rpc.RpcSwagger;

public class RpcCLI {
  
  private final RpcClient client;

  private final Queue<String> alerts = new ConcurrentLinkedQueue<>();

  public RpcCLI(RpcClient client) throws Exception {
    this.client = client;
    System.out.println("Connected to " + client.getHost() + ":" + client.getPort());
  }

  public void addAlert(String alert) {
    alerts.add(alert);
  }

  private void printAlerts() {
    StringBuilder sb = new StringBuilder();
    while (true) {
      String alert = alerts.poll();
      if (alert == null) {
        break;
      }

      sb.append("[ALERT] ").append(alert).append("\n");
    }

    if (sb.length() > 0) {
      System.out.print(sb.toString());
    }
  }

  public void start() {

    Scanner scanner = new Scanner(System.in);

  //   Runtime.getRuntime().addShutdownHook(new Thread() {
  //     public void run() { 
  //       scanner.close();
  //       try {
  //         client.close();
  //       } catch (Exception e) {
  //         System.out.println("Error closing client");
  //       }
  //     }
  //  });

  RpcMessage swagger = RpcMessage.newRPC("swagger");

  // Run the CLI loop
  System.out.print("\033[H\033[2J");
  while (true) {
      try{
        printAlerts();
        RpcMessage swaggerResponse = client.call(swagger);
        List<RpcField> fields = swaggerResponse.getFields();

        fields.removeIf((field) -> field.getName().startsWith("_"));
        fields.sort((a, b) -> a.getName().compareTo(b.getName()));
        
        
        System.out.println("Options: ");
        for (int i = 0; i < fields.size(); i++) {
          System.out.println("[" + (i + 1) + "] " + fields.get(i).getName());
        }

        System.out.print("\nSelect an option: ");
        Integer selection = IntegerReader.read(scanner);
        while(selection <= 0 || selection > fields.size()) {
            System.out.print("Invalid selection, please enter a number between 1 and " + (fields.size()));
            selection = IntegerReader.read(scanner);
        }

        RpcField selectedField = fields.get(selection - 1);
        String rpcSwagger = (String) selectedField.getValue();
        List <RpcField> requiredFields = RpcSwagger.getFields(rpcSwagger);

        RpcMessage rpc = RpcMessage.newRPC(selectedField.getName());
        RpcFieldReader.read(requiredFields, scanner);
        rpc.addFields(requiredFields);

        System.out.print("loading...");
        RpcMessage response = client.call(rpc);
  
        System.out.print("\033[H\033[2J");
        System.out.flush();

        String message = response.getString("message");
        if (message == null) {
          message = "No message, request was successful!";
        }
        System.out.println(message);
      }
      catch (Exception e) {
        System.out.println("Error happened");
        e.printStackTrace();
      }
    }
  }
}
