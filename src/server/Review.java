package server;

import java.util.Date;

public class Review {
  private int Position, Cleaning, Service, Price;
  private Date date;

  public Review(int Position, int Cleaning, int Service, int Price, Date date) {
    this.Position = Position;
    this.Cleaning = Cleaning;
    this.Service = Service;
    this.Price = Price;
    this.date = date;
  }


  public String toString() {
    return "Position: " + Position + ", Cleaning: " + Cleaning + ", Service: " + Service + ", Price: " + Price + ", Date: " + date;
  }

}
