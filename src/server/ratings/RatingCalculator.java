package server.ratings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.notify.NotifySender;
import server.DB;
import server.data.Hotel;
import server.data.Review;

public class RatingCalculator {
  private final NotifySender notifySender;

  public RatingCalculator(NotifySender notifySender) {
    this.notifySender = notifySender;
  }

  private Map<String, HotelRating> getHotelsReviws () throws InterruptedException {
    Map<String, HotelRating> reviewsByHotel = new HashMap<String, HotelRating>();

    DB.reviews.lockRead();
    try {
      for (Review review : DB.reviews.getRows().values()) {
        TimedRating rating = new TimedRating(review.getDate(), review.getRatings());
        
        if (!reviewsByHotel.containsKey(review.getHotelId())) {
          reviewsByHotel.put(review.getHotelId(), new HotelRating(review.getHotelId()));
        }

        reviewsByHotel.get(review.getHotelId()).addTimedRating(rating);
      }
    } finally {
      DB.reviews.unlockRead();
    }
    
    return reviewsByHotel;
  }

  private List<SyntheticHotelRating> hotelsToUpdate (Map<String, HotelRating> reviewsByHotel) throws InterruptedException {
    List<SyntheticHotelRating> hotelsToUpdate = new ArrayList<SyntheticHotelRating>();

    Map<String, Hotel> topHotelByCity = new HashMap<String, Hotel>();

    DB.hotels.lockRead();
    try {
      for (Hotel hotel : DB.hotels.getRows().values()) {
        if (!topHotelByCity.containsKey(hotel.getCity())) {
          topHotelByCity.put(hotel.getCity(), null);
        }

        Hotel topHotel = topHotelByCity.get(hotel.getCity());
        if (topHotel == null || topHotel.getRate() < hotel.getRate()) {
          topHotelByCity.put(hotel.getCity(), hotel);
          continue;
        }
      }

      for (String hotelId : reviewsByHotel.keySet()) {
        Hotel hotel = DB.hotels.get(hotelId);
        if (hotel == null) {
          System.out.println("Hotel " + hotelId + " not found when calculating ratings");
          continue;
        }

        HotelRating hotelRating = reviewsByHotel.get(hotelId);
        SyntheticHotelRating syntheticHotelRating = new SyntheticHotelRating(hotelRating);
        
        int newRating = syntheticHotelRating.getOverall();
        
        if (hotel.getRate() == newRating) {
          continue;
        }

        Hotel localTopHotel = topHotelByCity.get(hotel.getCity());
        if(localTopHotel.getId() != hotel.getId() && localTopHotel.getRate() <= newRating) {
          String message = "Hotel " + hotel.getName() + " is now the top hotel in " + hotel.getCity();
          try {
            System.out.println("Sending notification: " + message);
            notifySender.sendNotification(message.getBytes());  
          } catch (Exception e) {
            System.out.println("Failed to send notification: " + e.getMessage());
          }
        }

        hotelsToUpdate.add(syntheticHotelRating);
      }
    } finally {
      DB.hotels.unlockRead();
    }

    return hotelsToUpdate;
  }

  private void updateHotels(List<SyntheticHotelRating> hotelsToUpdate) throws InterruptedException {
    DB.hotels.lockWrite();
    try {
      for (SyntheticHotelRating hotelRating : hotelsToUpdate) {
        Hotel hotel = DB.hotels.get(hotelRating.getHotelId());
        hotel.setRatings(hotelRating.getSyntheticRating());
        hotel.setRate(hotelRating.getOverall());
      }
    } finally {
      DB.hotels.unlockWrite();
    }
  }

  public void calculateAndUpdate() throws InterruptedException {
    Map<String, HotelRating> reviewsByHotel = getHotelsReviws();
    List<SyntheticHotelRating> hotelsToUpdate = hotelsToUpdate(reviewsByHotel);
    updateHotels(hotelsToUpdate);
  }
}
