package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Date;


public class HotelStorage {

  private City[] cities;
  private Path root;

  public HotelStorage(String path) {
    cities = new City[0];
    this.root = Path.of(path);
  }

  public void load () throws FileNotFoundException {
    if(!Files.isDirectory(this.root)) {
      throw new FileNotFoundException("Path is not a directory");
    } 

    File rootFolder = this.root.toFile();
    String[] cityNames = rootFolder.list();

    this.cities = new City[cityNames.length];

    for (int c = 0; c < cityNames.length; ++c) {
      String[] hotelNames = new File(rootFolder, cityNames[c]).list();
      Hotel[] hotels = new Hotel[hotelNames.length];
      
      for (int h = 0; h < hotelNames.length; ++h) {
        if (!hotelNames[h].endsWith(".json")) {
          System.out.println("Skipping " + hotelNames[h] + " because it is not a json file");
          continue;
        }

        String name = hotelNames[h].substring(0, hotelNames[h].length() - 5);

        // Fake data
        Review[] reviews = new Review[10];
        for (int r = 0; r < reviews.length; ++r) {

          long DateOffset = (long) (1000 * 60 * 60 * 24 * Math.random() * 365);
          Date date = new Date(System.currentTimeMillis() - DateOffset);

          int Position = (int) (Math.random() * 5);
          int Cleaning = (int) (Math.random() * 5);
          int Service = (int) (Math.random() * 5);
          int Price = (int) (Math.random() * 5);

          reviews[r] = new Review(Position, Cleaning, Service, Price, date);
        }

        HotelInfo[] infos = new HotelInfo[4];
        for (int i = 0; i < infos.length; ++i) {
          infos[i] = new HotelInfo("title" + i, "text" + i);
        }

        hotels[h] = new Hotel(name, reviews, infos);
      }

      this.cities[c] = new City(cityNames[c], hotels);
    }

  }


  public City[] getCities() {
    return cities;
  }

}
