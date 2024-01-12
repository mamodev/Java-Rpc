package client;

import java.net.InetAddress;

import lib.notify.NotifyReciever;
import lib.rpc.RpcClient;
import lib.rpc.RpcError;
import lib.rpc.RpcMessage;
import lib.rpccli.RpcCLI;

public class ClientMain {
  static public void main(String argv[]) {

    if (argv.length != 2) {
      System.out.println("Usage: java ClientMain <host> <port>");
      return;
    }

    String host = argv[0];
    int port = Integer.parseInt(argv[1]);

    try {
      RpcClient client = new RpcClient(host, port);
      client.connect();
      
      RpcMessage notificationChannel = RpcMessage.newRPC("_notificationChannel");
      RpcMessage ncResponse = client.call(notificationChannel);
      String nhost = ncResponse.getString("host");
      int nport = ncResponse.getInteger("port");

      if (RpcError.isError(ncResponse)) {
        System.out.println(RpcError.fromMessage(ncResponse));
        return;
      }

      InetAddress address = InetAddress.getByName(nhost);
      NotifyReciever reciever = new NotifyReciever(nport, address);


      RpcCLI cli = new RpcCLI(client);

      reciever.addHandler((msg) -> {
        // convert byte array to string
        String message = new String(msg);
        cli.addAlert(message);
      });
      

      reciever.start();
      cli.start();

      client.close();
      reciever.interrupt();
      reciever.join();
    }
    catch (Exception e) {
      System.out.println(e);
      System.out.println("Error creating client");
    }

  }
}