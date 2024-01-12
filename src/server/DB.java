package server;

import lib.document.Document;
import lib.document.DocumentAutoSaver;
import lib.document.DocumentLoader;
import server.data.Hotel;
import server.data.Review;
import server.data.User;

public class DB {

  public static Document<Hotel> hotels;
  public static Document<User> users;
  public static Document<Review> reviews;

  private static DocumentAutoSaver autoSaver;

  public static void init(Config config) throws Exception {
    hotels = DocumentLoader.loadFromFile(config.getHotelFile(), Hotel.class);
    users = DocumentLoader.loadFromFile(config.getUserFile(), User.class);
    reviews = DocumentLoader.loadFromFile(config.getReviewFile(), Review.class);
  
    autoSaver = new DocumentAutoSaver();
    autoSaver.addDocument(config.getUserFile(), users, config.getAutoSaveInterval());
    autoSaver.addDocument(config.getHotelFile(), hotels, config.getAutoSaveInterval());
    autoSaver.addDocument(config.getReviewFile(), reviews, config.getAutoSaveInterval());
  }

  public static void shutdown() {
    autoSaver.shutdown();
  }
}
