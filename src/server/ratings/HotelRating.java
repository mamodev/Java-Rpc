package server.ratings;

import java.util.ArrayList;
import java.util.List;

public class HotelRating {

  private String hotelId;
  private List<TimedRating> reviewRatings = new ArrayList<TimedRating>();

  public HotelRating(String hotelId) {
    this.hotelId = hotelId;
  }

  public String getHotelId() {
    return hotelId;
  }
  
  public  List<TimedRating> getRatings() {
    return reviewRatings;
  }
  
  public void addTimedRating(TimedRating rating) {
    reviewRatings.add(rating);
  }


}
