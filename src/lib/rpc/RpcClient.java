package lib.rpc;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RpcClient {
  private final String host;
  private final int port;
  private Socket socket;

  private List<RpcField> cookies = new ArrayList<RpcField>();

  public RpcClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void connect() throws Exception {
    if (socket == null) 
      socket = new Socket(host, port);
  }

  public void close() throws Exception {
    socket.close();
  }

  private RpcMessage addCookies(RpcMessage rpc) {
    for (RpcField cookie : cookies) {
      rpc.set(cookie.getName(), cookie.getType(), cookie.getValue());
    }
    return rpc;
  }

  public RpcMessage call(RpcMessage rpc) throws Exception {
    rpc = addCookies(rpc);
    byte[] bytes = rpc.toByteArray();

    ByteBuffer sizeBuffer = ByteBuffer.allocate(4).putInt(bytes.length);
    sizeBuffer.flip();

    byte[] sizeBytes = sizeBuffer.array();

    socket.getOutputStream().write(sizeBytes);
    socket.getOutputStream().write(bytes);
    socket.getOutputStream().flush();

    for (RpcField cookie : cookies) {
      rpc.remove(cookie.getName());
    }
    
    // read first 4 bytes
    byte[] size = new byte[4];
    socket.getInputStream().read(size);
    int length = ByteBuffer.wrap(size).getInt();

    byte[] response = new byte[length];
    socket.getInputStream().read(response);

    RpcMessage responseMessage = RpcMessage.deserialize(ByteBuffer.wrap(response));

    for (RpcField field : responseMessage.getFields()) {
      if (field.getName().startsWith("set-cookie-")) {
        field.setName(field.getName().substring(4));
        cookies.add(field);
      }

      if (field.getName().startsWith("delete-cookie-")) {
        field.setName(field.getName().substring(7));
        cookies.removeIf(cookie -> cookie.getName().equals(field.getName()));
      }
    }
    
    return responseMessage;
  }


  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
  
}
