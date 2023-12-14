package server;

public class Hotel {
  private String name;
  private Review[] reviews;
  private HotelInfo[] info;

  public Hotel(String name, Review[] reviews, HotelInfo[] info) {
    this.name = name;
    this.reviews = reviews;
    this.info = info;
  }

  public String getName() {
    return name;
  }

  public Review[] getReviews() {
    return reviews;
  }

  public HotelInfo[] getInfo() {
    return info;
  }

}
