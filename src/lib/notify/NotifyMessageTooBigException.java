package lib.notify;

import java.io.IOException;

public class NotifyMessageTooBigException extends IOException {
  public NotifyMessageTooBigException(String msg) {
    super(msg);
  }
}
