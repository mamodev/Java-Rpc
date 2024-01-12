package server.ratings;

import java.util.Date;

public class SyntheticHotelRating {
  private final static double DECAY_FACTOR = 0.99;
  private final static double LINEAR_DECAY_FACTOR = 0.0004;
  private final static double DECAY_BASE = 0.4;
  private final static int AFFIDABILITY_THRESHOLD = 40;

  private final HotelRating hotelRating;
  private Rating syntheticRating;

  public SyntheticHotelRating(HotelRating hotelRating) {
    this.hotelRating = hotelRating;
    this.calculateSyntheticRating();
  }

  private double calculateWeight(double timeDifferenceDays) {
    
    double weight = Math.pow(DECAY_FACTOR, timeDifferenceDays) + DECAY_BASE - (timeDifferenceDays * LINEAR_DECAY_FACTOR);

    if (weight < 0) {
      weight = 0;
    }

    if (weight > 1) {
      weight = 1;
    }

    return weight;
  }

  private void calculateSyntheticRating() {
    double totalWeight = 0;
    double weightedSumCleaning = 0;
    double weightedSumPosition = 0;
    double weightedSumServices = 0;
    double weightedSumQuality = 0;

    Date latestDate = new Date(0);

    for (TimedRating timedRating : hotelRating.getRatings()) {
      if (timedRating.getDate().after(latestDate)) {
        latestDate = timedRating.getDate();
      }
    }

    for (TimedRating timedRating : hotelRating.getRatings()) {
      double timeDifferenceDays = (latestDate.getTime() - timedRating.getDate().getTime()) / 1000 / 60 / 60 / 24;
      double weight = calculateWeight(timeDifferenceDays);

      totalWeight += weight;
      weightedSumCleaning += timedRating.getCleaning() * weight;
      weightedSumPosition += timedRating.getPosition() * weight;
      weightedSumServices += timedRating.getServices() * weight;
      weightedSumQuality += timedRating.getQuality() * weight;
    }

    if (totalWeight == 0) {
      return;
    }

    this.syntheticRating = new Rating(
      (int) Math.round(weightedSumCleaning / totalWeight),
      (int) Math.round(weightedSumPosition / totalWeight),
      (int) Math.round(weightedSumServices / totalWeight),
      (int) Math.round(weightedSumQuality / totalWeight)
    );
  }

  public String getHotelId() {
    return hotelRating.getHotelId();
  }

  public Rating getSyntheticRating() {
    return syntheticRating;
  }

  public int getOverall() {
    if (syntheticRating == null) {
      calculateSyntheticRating();
    }
    
    int overall = 
      syntheticRating.getCleaning() 
      + syntheticRating.getPosition() 
      + syntheticRating.getServices() 
      + syntheticRating.getQuality();

    int affidability = hotelRating.getRatings().size() / AFFIDABILITY_THRESHOLD;

    if (affidability > 1) {
      affidability = 1;
    }

    return overall * (1 + (affidability * overall/5));
  }
}
