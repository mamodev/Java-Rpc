package server;

import java.io.FileReader;

import com.google.gson.Gson;

public class Config {
  private int port;
  private String hotelFile;
  private String userFile;
  private String reviewFile;

  private int autoSaveInterval;
  private int ratingCalculatorInterval;
  
  private boolean swagger;

  private String notificationAddress;
  private int notificationPort;

  public int getPort() {
    return this.port;
  }

  public String getHotelFile() {
    return this.hotelFile;
  }

  public String getUserFile() {
    return this.userFile;
  }

  public String getReviewFile() {
    return this.reviewFile;
  }

  public int getAutoSaveInterval() {
    return this.autoSaveInterval;
  }

  public int getRatingCalculatorInterval() {
    return this.ratingCalculatorInterval;
  }

  public String getNotificationAddress() {
    return this.notificationAddress;
  }

  public int getNotificationPort() {
    return this.notificationPort;
  }

  public boolean getSwagger() {
    return this.swagger;
  }

  public static Config loadFromFile (String filePath) throws Exception {
    Gson gson = new Gson();
    Config config = gson.fromJson(new FileReader(filePath), Config.class);
    return config;
  }
}
