package lib.nioserver;

import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class NIOClient {
  private final SocketChannel socket;
  private final NIOBufferManager bufferManager;

  private AtomicBoolean dispatched = new AtomicBoolean(false);
  private final Queue<NIORequest> readyRequests = new ConcurrentLinkedQueue<NIORequest>();
  
  private NIORequest currentRequest;
  private NIORequest dispatchRequest;

  public NIOClient(SocketChannel socket, NIOBufferManager bufferManager) {
    this.socket = socket;
    this.bufferManager = bufferManager;
    this.currentRequest = new NIORequest(socket, bufferManager);
  }

  public void read () throws Exception {
    currentRequest.read();
    if (currentRequest.isReady()) {
      readyRequests.offer(currentRequest);
      currentRequest = new NIORequest(socket, bufferManager);
    }
  }

  // INFO this method is thread safe
  // If a request is dispatched, means that there is a request being processed by a worker thread
  // @return true if the request was dispatched
  public boolean tryDispatch () {
    if (!dispatched.compareAndSet(false, true)) {
      return false;
    }

    NIORequest req = readyRequests.poll();
    if (req == null) {
      dispatched.set(false);
      return false;
    }

    dispatchRequest = req;
    return true;
  }

  // WARNING: this method must be called from the same thread wich own the dispached request
  NIORequest getDispatchRequest() {
    return dispatchRequest;
  }

  // return true if there is no more requests to dispatch
  // WARNING: this method must be called from the same thread wich own the dispached request
  public boolean done() {
    dispatchRequest = null;
    NIORequest req = readyRequests.poll();

    if (req == null) {
      dispatched.set(false);
      return true;
    }

    dispatchRequest = req;
    return false;
  }

  public void close() {
    try {
      socket.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
