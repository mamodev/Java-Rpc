package lib.nioserver;

import java.util.Queue;

public class NIOWorker extends Thread {

  private final Queue<NIOClient> clients;
  private final NIOHandler handler;

  public NIOWorker(Queue<NIOClient> clients, NIOHandler handler) {
    this.clients = clients;
    this.handler = handler;
  }

  public void run() {
    while (Thread.currentThread().isAlive()) {

      NIOClient client = clients.poll();
      if (client == null) {
        continue;
      }

      try {
        NIORequest req = client.getDispatchRequest();
        handler.handle(req);
      } catch (Exception e) {
        e.printStackTrace();  
      }

      if(!client.done()) 
        clients.offer(client);
    }
  }
}
