package lib.notify;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class NotifyReciever extends Thread {
  private final InetAddress address;
  private final int port;

  private final MulticastSocket socket;

  private final byte[] buffer;

  private final List<NotifyHandler> handlers = new LinkedList<>();

  public NotifyReciever(int port, InetAddress address) throws IOException, SecurityException, IllegalArgumentException  {
    this.port = port;
    this.address = address;
    this.socket = new MulticastSocket(port);
    socket.setSoTimeout(1000);

    this.buffer = new byte[this.socket.getReceiveBufferSize()];
    this.socket.joinGroup(address);
  }

  public void addHandler (NotifyHandler handler) {
    this.handlers.add(handler);
  }

  public void removeHandler (NotifyHandler handler) {
    this.handlers.remove(handler);
  }

  private void notifyHandlers (byte data[]) {
    for (NotifyHandler handler : handlers) {
      handler.handleNotification(data);
    }
  }

  private void recieveNotification () throws IOException {
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet);

    ByteBuffer sizeBuffer = ByteBuffer.wrap(packet.getData());  
    int size = sizeBuffer.getInt();

    if (packet.getLength() != size + 4) {
      throw new NotifyMessageTooBigException("Cannot handle message: " + packet.getLength() + " bytes (max: " + (size + 4) + ")");
    }

    byte[] data = new byte[size];
    System.arraycopy(packet.getData(), 4, data, 0, size);
    notifyHandlers(data);
  }

  public void run () {
    while (!Thread.interrupted()) {
      try {
        recieveNotification();
      } catch (SocketTimeoutException e) {
        continue;
      } 
      catch (IOException e) {
        System.err.println("Error while recieving notification: " + e.getMessage());
        continue;
      }
    }
  }
}
