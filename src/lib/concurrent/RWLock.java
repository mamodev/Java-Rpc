package lib.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RWLock {
  private int readers;
  private int writers;
  private int writeRequests;

  private boolean blocked = false;

  private Lock lock;
  private Condition goWriters;
  private Condition goReaders;

  public RWLock() {
      this.readers = 0;
      this.writers = 0;
      this.writeRequests = 0;
      this.lock = new ReentrantLock();
      this.goWriters = lock.newCondition();
      this.goReaders = lock.newCondition();
  }

  public void readLock() throws InterruptedException {
      lock.lock();
      try {
          while (writers > 0 || writeRequests > 0 || blocked) {
              goReaders.await();
          }
          readers++;
      } finally {
          lock.unlock();
      }
  }

  public void readUnlock() {
      lock.lock();
      try {
          readers--;
          if (readers == 0 && writeRequests > 0) {
              goWriters.signal();
          }
      } finally {
          lock.unlock();
      }
  }

  public void writeLock() throws InterruptedException {
      lock.lock();
      try {
          writeRequests++;

          while (readers > 0 || writers > 0 || blocked) {
              goWriters.await();
          }

          writeRequests--;
          writers++;
      } finally {
          lock.unlock();
      }
  }

  public void writeUnlock() {
      lock.lock();
      try {
          writers--;

          if (writeRequests > 0) {
              goWriters.signal();
          } else {
              goReaders.signalAll();
          }

      } finally {
          lock.unlock();
      }
  }

  public void block() {
      lock.lock();
      try {
          blocked = true;
      } finally {
          lock.unlock();
      }
  }

  public void unblock() {
      lock.lock();
      try {
          blocked = false;
          goWriters.signalAll();
          goReaders.signalAll();
      } finally {
          lock.unlock();
      }
  }

}