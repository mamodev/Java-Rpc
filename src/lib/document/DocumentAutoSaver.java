package lib.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DocumentAutoSaver {

  private class TaskReference {
    public Runnable task;
    public Future<?> taskFuture;

    public TaskReference(Runnable task, Future<?> taskFuture) {
      this.task = task;
      this.taskFuture = taskFuture;
    }
  }

  private ScheduledThreadPoolExecutor scheduler;
  private Map<Document<?>, TaskReference> tasks = new HashMap<Document<?>, TaskReference>();

  public DocumentAutoSaver() {
    this.scheduler = new ScheduledThreadPoolExecutor(1);
    scheduler.setRemoveOnCancelPolicy(true);
  }

  public void addDocument(String path, Document<?> document, long delay) {
    Runnable task = () -> {
      try {
        DocumentWriter.writeToFile(path, document);
      } catch (Exception e) {
        System.out.println("Error saving document to file: " + path);
        e.printStackTrace();
      }
    };
    
    
    Future<?> future = scheduler.scheduleWithFixedDelay(task, delay, delay, TimeUnit.MILLISECONDS);
    this.tasks.put(document, new TaskReference(task, future));
  }

  // Warning: this method will block until the document is saved
  public void removeDocument(Document<?> document) throws InterruptedException {
    TaskReference taskReference = this.tasks.get(document);
    if (taskReference == null) {
      return;
    }

    taskReference.taskFuture.cancel(true);
    try {
      taskReference.taskFuture.get();
    } catch (Exception e) {}

    // persist the document one last time
    taskReference.task.run();
    this.tasks.remove(document);
  }

  public void removeAllDocuments() {
    List<Document<?>> documents = new ArrayList<>();

    for (Document<?> document : this.tasks.keySet()) {
      documents.add(document);
    }

    for (Document<?> document : documents) {
      try {
        this.removeDocument(document);
      } catch (InterruptedException e) {
        System.out.println("Error removing document");
      }
    }

    this.tasks.clear();
  }

  // Warning: this method will block until the document is saved
  public void shutdown() 
  {
    this.removeAllDocuments();
    this.scheduler.shutdown();
    try {
      this.scheduler.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      System.out.println("DocumentAutoSaver threadpool shutdown interrupted");
    }
  }
}
