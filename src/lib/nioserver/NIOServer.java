package lib.nioserver;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
  private final int PORT;
  private final int TIMEOUT = 2000;
  private final ServerSocketChannel serverSocketChannel;
  private final Selector selector;

  private final NIOBufferManager bufferManager = new NIOBufferManager();
  private final NIOWorkerPool workerPool;

  public NIOServer(int port, NIOHandler handler) throws Exception {
    this.PORT = port;

    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.bind(new InetSocketAddress(PORT));
    serverSocketChannel.configureBlocking(false);

    selector = Selector.open();
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    workerPool = new NIOWorkerPool(handler);
  }

  public void listen () throws Exception {
    int selected = 0;
    workerPool.start();

    System.out.println("Server listening on port: " + PORT);
    while (!Thread.interrupted()) {
      selected = selector.select(TIMEOUT);
      if (selected == 0) 
        continue;
      
      handleEvents();
    }

    serverSocketChannel.close();
  }

  private void handleEvents () {
    Set<SelectionKey> selectedKeys = selector.selectedKeys();
    Iterator<SelectionKey> iterator = selectedKeys.iterator();

    while (iterator.hasNext()) {
      processIterator(iterator);
    }
  }

  private void processIterator(Iterator<SelectionKey> iterator) {
    SelectionKey key = iterator.next();
    iterator.remove();

    try {
      if(key.isAcceptable())
        accept(key);
      else if (key.isReadable()) 
        read(key);
    } 
    catch (ClosedChannelException e) {
      NIOClient client = (NIOClient) key.attachment();
      if (client != null) 
        client.close();

      System.out.println("Closed channel: " + key.channel().toString());
  
      key.cancel();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void accept (SelectionKey key) throws Exception {
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    SocketChannel clientChannel = serverChannel.accept();
    clientChannel.configureBlocking(false);
    clientChannel.register(selector, SelectionKey.OP_READ, new NIOClient(clientChannel, bufferManager));
    System.out.println("Accepted connection from: " + clientChannel.getRemoteAddress());
  }

  private void read(SelectionKey key) throws Exception {
    NIOClient client = (NIOClient) key.attachment();
    client.read();
    boolean dispatched = client.tryDispatch();
    if (dispatched) 
      workerPool.handleClient(client);
  }

  //Thread safe
  public void stop() {
    selector.wakeup();
  }
}
