package lib.nioserver;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public class NIORequest {
  private boolean initialized = false;
  private boolean ready = false;
  private SocketChannel socket;
  
  private NIOBufferManager buffManager;
  private ByteBuffer requestBuffer;
  private int readedBytes = 0;

  public NIORequest(SocketChannel socket, NIOBufferManager buffManager) {
    this.socket = socket;
    this.buffManager = buffManager;
  }

  private void initialize () throws Exception {
    ByteBuffer sizeBuffer = buffManager.getIntBuffer();
    int size = socket.read(sizeBuffer);

    if (size == -1) {
      throw new ClosedChannelException();
    }

    if (size != 4) {
      throw new NIOBadRequestException("Bad request len size: " + size + " bytes"); 
    }

    sizeBuffer.flip();
    int requestSize = sizeBuffer.getInt();
    requestBuffer = buffManager.getBuffer(requestSize);
    initialized = true;
  }

  public void read() throws Exception {
    if (!initialized) {
      initialize();
    }

    if (ready) {
      return;
    }

    int size = socket.read(requestBuffer);

    if (size == -1) {
      throw new ClosedChannelException();
    }

    readedBytes += size;

    if(readedBytes == requestBuffer.capacity()) {
      ready = true;
    }
  }

  public int bytesToRead() {
    return requestBuffer.capacity() - readedBytes;
  }

  public boolean isReady() {
    return ready;
  }
  
  public SocketChannel getChannel() {
    return socket;
  }

  public void sendReponse (byte[] bytes) throws Exception {
    ByteBuffer sizeBuffer = buffManager.getIntBuffer();
    ByteBuffer response = ByteBuffer.wrap(bytes);

    sizeBuffer.putInt(response.capacity());
    sizeBuffer.flip();

    writeAll(sizeBuffer);
    writeAll(response);
  }

  private void writeAll (ByteBuffer buffer) throws Exception {
    while (buffer.hasRemaining()) {
      socket.write(buffer);
    }
  }

  public ByteBuffer getData() {
    requestBuffer.position(0);
    return requestBuffer;
  }

  public void freeRequestBuffer() {
    buffManager.freeBuffer(requestBuffer);
    requestBuffer = null;
  }
}
