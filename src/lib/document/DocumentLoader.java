package lib.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class DocumentLoader {
  public static <T extends IUniqueIdentifier> Document<T> loadFromFile (String filePath, Class<T> clazz) throws FileNotFoundException {

    Map<String, T> rows = new HashMap<String, T>();
    File file = new File(filePath);

    if (!file.exists()) {
      try {
        file.createNewFile();
        FileWriter  writer = new FileWriter(file);
        writer.write("[]");
        writer.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    Reader reader = new FileReader(file);
    
    Gson gson = new Gson();

    Object[] array = (Object[]) Array.newInstance(clazz, 0);
    Object[] objects = gson.fromJson(reader, array.getClass());

    for (Object obj : objects) {
      if (clazz.isInstance(obj)) {
        try {
          T casted = clazz.cast(obj);
          rows.put(casted.getId(), casted);
        } catch (ClassCastException e) {
          System.out.println("Found an object that is not of type " + clazz.getName());
          System.out.println("Ignoring it...");
        }

      }
    }

    return new Document<T>(rows, clazz);
  }
}
