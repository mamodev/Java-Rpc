package lib.notify;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class NotifySender {
  private final InetAddress address;
  private final int port;

  private final int MAX_MESSAGE_SIZE;

  private final MulticastSocket socket;

  public NotifySender(int port, InetAddress address) throws IOException, SecurityException, IllegalArgumentException  {
    this.port = port;
    this.address = address;
    this.socket = new MulticastSocket();
    // this.socket.joinGroup(address);
    this.MAX_MESSAGE_SIZE = this.socket.getSendBufferSize();
  }

  public void sendNotification (byte[] data) throws IOException, NotifyMessageTooBigException {

    if (data.length > this.MAX_MESSAGE_SIZE) {
      throw new NotifyMessageTooBigException("Message too big: " + data.length + " bytes (max: " + this.MAX_MESSAGE_SIZE + ")");
    }

    ByteBuffer sizeBuffer = ByteBuffer.allocate(4 + data.length);
    sizeBuffer.putInt(data.length);
    sizeBuffer.put(data);
    sizeBuffer.flip();

    DatagramPacket packet = new DatagramPacket(sizeBuffer.array(), sizeBuffer.capacity(), this.address, port);
    this.socket.send(packet);
  }

  public void close() {
    this.socket.close();
  }
}
