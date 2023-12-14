package server;

public class City {
  private Hotel[] hotels; 
  private String name;

  public City(String name, Hotel[] hotels) {
    this.hotels = hotels;
    this.name = name;
  }

  public Hotel[] getHotels() {
    return hotels;
  }

  public String getName() {
    return name;
  }

}
