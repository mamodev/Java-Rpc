package lib.nioserver;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NIOWorkerPool {

  private final NIOWorker[] workers;
  private final Queue<NIOClient> dispatchedClients = new ConcurrentLinkedQueue<NIOClient>();

  public NIOWorkerPool(NIOHandler handler) {
    int cores = Runtime.getRuntime().availableProcessors();
    workers = new NIOWorker[cores];

    for (int i = 0; i < cores; i++) {
      workers[i] = new NIOWorker(dispatchedClients, handler);
    }
  }

  public void start() {
    for (NIOWorker worker : workers) {
      worker.start();
    }
  }

  public void handleClient (NIOClient client) {
    dispatchedClients.offer(client);
  }
}
