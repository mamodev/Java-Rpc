package lib.document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lib.concurrent.RWLock;

public class Document<T extends IUniqueIdentifier> {
  private Map<String, T> rows = new HashMap<String, T>();
  private RWLock tableLock = new RWLock();
  private Class<T> clazz;

  private AtomicBoolean couldBeModified = new AtomicBoolean(false);

  public Document(Map<String, T> rows, Class<T> clazz) {
    this.clazz = clazz;
    this.rows = rows;
  }

  public Class<T> getClazz() {
    return this.clazz;
  }
  
  public void lockRead() throws InterruptedException {
    this.tableLock.readLock();
  }

  public void unlockRead() {
    this.tableLock.readUnlock();
  }

  public void lockWrite() throws InterruptedException {
    this.tableLock.writeLock();
    this.couldBeModified.set(true);
  }

  public void unlockWrite() {
    this.tableLock.writeUnlock();
  }

  public Map<String, T> getRows() {
    return this.rows;
  }

  public T get(String id) {
    return rows.get(id);
  }

  public void add(T row) {
    rows.put(row.getId(), row);
  }

  public void remove(String id) {
    rows.remove(id);
  }

  public boolean exists(String id) {
    return rows.containsKey(id);
  }

  public boolean getCouldBeModifiedAndReset() {
    return this.couldBeModified.getAndSet(false);
  }
}