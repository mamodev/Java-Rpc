package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lib.rpc.RpcError;
import lib.rpc.RpcHandler;
import lib.rpc.RpcMessage;

public class Session {
  private static final Map<Integer, Session> sessions = new ConcurrentHashMap<Integer, Session>();
  private static final AtomicInteger counter = new AtomicInteger();

  private final int id;
  private String username;

  public Session(String username) {
    id = counter.incrementAndGet();
    this.username = username;
    Session.addSession(this);
  }

  public int getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public static Session getSession(int id) {
    return sessions.get(id);
  }

  public static void removeSession(int id) {
    sessions.remove(id);
  }

  public static void addSession(Session session) {
    sessions.put(session.getId(), session);
  }

  public static boolean hasSession(int id) {
    return sessions.containsKey(id);
  }
}
