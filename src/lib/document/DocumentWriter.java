package lib.document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import com.google.gson.Gson;

public class DocumentWriter {
  public static <T extends IUniqueIdentifier> void  writeToFile (String filePath, Document<T> document) throws IOException, InterruptedException {
    
    
    File file = new File(filePath + ".tmp");
    Gson gson = new Gson();
    FileWriter writer = null;

    document.lockRead();
    try {

      boolean couldBeModified = document.getCouldBeModifiedAndReset();
      if (!couldBeModified) {
        return;
      }

      file.createNewFile();
      writer = new FileWriter(file);

      Collection<T> values = document.getRows().values();
      String json = gson.toJson(values);
      writer.write(json);
    } 
    finally {
      document.unlockRead();
      if(writer != null)
        writer.close();
    }


    File oldFile = new File(filePath);
    file.renameTo(oldFile);
  }
}
