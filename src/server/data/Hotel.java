package server.data;

import lib.document.IUniqueIdentifier;
import server.ratings.Rating;

public class Hotel implements IUniqueIdentifier, Comparable<Hotel> {

  private int id;
  private String name;
  private String description;
  private String city;
  private String phone;
  private int rate;
  private Rating ratings;

  public Hotel() {}

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public String getCity () {
    return this.city;
  }


  public String getId() {
    return "hotel-" + this.id;
  }

  public Rating getRatings() {
    return this.ratings;
  }

  public void setRatings(Rating ratings) {
    this.ratings = ratings;
  }

  public int getRate() {
    return this.rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  @Override
  public int compareTo(Hotel other) {
    return this.rate - other.rate;
  }

  public String toDetailedString () {
    String str = "";

    str += String.format("%-" + 20 + "s", "Name: ") + name + "\n";
    str += String.format("%-" + 20 + "s", "Description: ") + description + "\n";
    str += String.format("%-" + 20 + "s", "City: ") + city + "\n";
    str += String.format("%-" + 20 + "s", "Phone: ") + phone + "\n";

    if (ratings == null) {
      return str;
    }

    str += "\n\nDetailed Ratings: \n";
    str += ratings.toString();
    return str;
  }

  @Override 
  public String toString() {
    return String.format("%-5d", id) + String.format("%-25s", name) + String.format("Rate: %-20s", rate);
  }
}
