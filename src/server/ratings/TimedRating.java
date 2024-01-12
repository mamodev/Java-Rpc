package server.ratings;

import java.util.Date;

public class TimedRating extends Rating {

  private final Date date;

  public TimedRating(Date date, int cleaning, int position, int services, int quality) {
    super(cleaning, position, services, quality);
    this.date = date;
  }

  public TimedRating(Date date, Rating rating) {
    super(rating.getCleaning(), rating.getPosition(), rating.getServices(), rating.getQuality());
    this.date = date;
  }

  public Date getDate() {
    return this.date;
  }
}
