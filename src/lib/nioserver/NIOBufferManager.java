package lib.nioserver;

import java.nio.ByteBuffer;
import java.util.Queue;

public class NIOBufferManager {

  public NIOBufferManager() {

  
  }

  public ByteBuffer getBuffer(int size) throws Exception {
    return ByteBuffer.allocate(size);
  }

  public void freeBuffer(ByteBuffer buffer) {
    buffer = null;
  }

  public ByteBuffer getIntBuffer() throws Exception {
    return ByteBuffer.allocate(4);
  }
}
