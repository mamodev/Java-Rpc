package server.data;

import java.util.Date;

import lib.document.IUniqueIdentifier;
import server.ratings.Rating;

public class Review implements IUniqueIdentifier {
  
  private String hotelId;
  private String username;
  private Date date;
  private Rating ratings;

  public Review(String hotelId, String username, Rating ratings) {
    this.hotelId = hotelId;
    this.username = username;
    this.date = new Date();
    this.ratings = ratings;
  }

  public String getId () {
    return this.hotelId + "-" + this.username + "-" + this.date.getTime();
  }

  public String getHotelId() {
    return hotelId;
  }

  public String getUsername() {
    return username;
  }

  public Date getDate() {
    return date;
  }

  public Rating getRatings() {
    return ratings;
  }

  @Override
  public String toString() {
    String str = "";
    str += String.format("%-" + 20 + "s", "Hotel: ") + this.hotelId + "\n";
    str += String.format("%-" + 20 + "s", "Username: ") + this.username + "\n";
    str += String.format("%-" + 20 + "s", "Date: ") + this.date + "\n";
    str += "\n\nDetailed Ratings: \n";
    str += ratings.toString();
    return str;
  }

}
