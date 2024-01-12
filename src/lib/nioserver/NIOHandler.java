package lib.nioserver;

public interface NIOHandler {
  public void handle(NIORequest req);
}
