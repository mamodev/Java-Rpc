package server.ratings;

public class Rating implements Cloneable {

  public static final int MAX_RATING = 5;

  public static boolean isValid(int rating) {
    return rating > 0 && rating <= MAX_RATING;
  }

  private int cleaning;
  private int position;
  private int services;
  private int quality;

  public Rating(int cleaning, int position, int services, int quality) {
    this.cleaning = cleaning;
    this.position = position;
    this.services = services;
    this.quality = quality;
  }

  public int getCleaning() {
    return this.cleaning;
  }

  public int getPosition() {
    return this.position;
  }

  public int getServices() {
    return this.services;
  }

  public int getQuality() {
    return this.quality;
  }


  @Override
  public Rating clone() {
    return new Rating(this.cleaning, this.position, this.services, this.quality);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Rating)) {
      return false;
    }

    Rating other = (Rating) obj;
    return this.cleaning == other.cleaning && this.position == other.position && this.services == other.services
        && this.quality == other.quality;
  }

  @Override 
  public String toString() {
    String str = "";
    int maxLen = 0;

    String tmpStr = "";

    String s = String.format("|%-" + 20 + "s", "Cleaning: ") + cleaning + "|\n";
    maxLen = Math.max(maxLen, s.length());
    tmpStr += s;

    s = String.format("|%-" + 20 + "s", "Position: ") + position + "|\n";
    maxLen = Math.max(maxLen, s.length());
    tmpStr += s;

    s = String.format("|%-" + 20 + "s", "Services: ") + services + "|\n";
    maxLen = Math.max(maxLen, s.length());
    tmpStr += s;

    s = String.format("|%-" + 20 + "s", "Quality: ") + quality + "|\n";
    maxLen = Math.max(maxLen, s.length());
    tmpStr += s;

    for (int i = 0; i < maxLen; i++) {
      str += "-";
    }
    str += "\n" + tmpStr;
    for (int i = 0; i < maxLen; i++) {
      str += "-";
    }

    return str;
  }

}
