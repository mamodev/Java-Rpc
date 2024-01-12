package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lib.rpc.RpcError;
import lib.rpc.RpcHandler;
import lib.rpc.RpcMessage;
import lib.rpc.RpcText;
import server.data.Hotel;
import server.data.Review;
import server.data.User;
import server.ratings.Rating;

public class Rpcs {

  public static RpcHandler login = (RpcMessage rpc) -> {
    String username = rpc.getString("username");
    String password = rpc.getString("password");

    DB.users.lockRead();
    try {
      if (!DB.users.exists(username)) 
        return new RpcError(400, "Invalid credentials");

      User user = DB.users.getRows().get(username);

      if (!user.getPassword().equals(password)) 
        return new RpcError(400, "Invalid credentials");
    } finally {
      DB.users.unlockRead();
    }

    return new RpcText(username + " you are now logged in").setInteger("set-cookie-session-id", new Session(username).getId());
  };

  public static RpcHandler logout =  (RpcMessage rpc) -> {
    int sessionId = rpc.getInteger("cookie-session-id");
    Session session = Session.getSession(sessionId);
    Session.removeSession(sessionId);
    return new RpcText(session.getUsername() + " you are now logged out").setInteger("delete-cookie-session-id", 0);
  };

  public static RpcHandler register = (RpcMessage rpc) -> {
    String username = rpc.getString("username");
    String password = rpc.getString("password");

    DB.users.lockWrite();
    try {
      if (DB.users.exists(username)) 
        return new RpcError(400, "Username already taken");

      DB.users.add(new User(username, password));
    } finally {
      DB.users.unlockWrite();
    }
    
    return new RpcText(username + " you are now registered and logged in").setInteger("set-cookie-session-id", new Session(username).getId());
  };

  public static RpcHandler showBadges = (RpcMessage rpc) -> {
    int sessionId = rpc.getInteger("cookie-session-id");
    Session session = Session.getSession(sessionId);
    String username = session.getUsername();
    
    DB.users.lockRead();
    try {
      
      User user = DB.users.get(username);
      if (user == null) {
        Session.removeSession(sessionId);
        return new RpcError(400, "Invalid session, user not found");
      }

      return new RpcText(user.toString());
    } finally {
      DB.users.unlockRead();
    }
  };

  public static RpcHandler hotelByCity = (RpcMessage rpc) -> {
    String city = rpc.getString("city");
    DB.hotels.lockRead();
    try {
      List <Hotel> hotelsList = new ArrayList<Hotel>();
      
      for (Hotel hotel :  DB.hotels.getRows().values()) {
        if (hotel.getCity().equalsIgnoreCase(city)) {
          hotelsList.add(hotel);
        }
      }
      
      if (hotelsList.size() == 0) {
        return new RpcText("No hotels found in " + city + "\n Maby you should try another city or check your spelling \n");
      } 

      Collections.sort(hotelsList, Collections.reverseOrder());

      StringBuilder sb = new StringBuilder();

      sb.append("Hotels in " + city + "\n");
      sb.append("--------------------------------------------------\n");
      for (Hotel hotel : hotelsList) {
        sb.append(hotel.toString() + "\n");
      }

      return new RpcText(sb.toString());
    } finally {
      DB.hotels.unlockRead();
    }
  };

  public static RpcHandler hotelById = (RpcMessage rpc) -> {
    int id = rpc.getInteger("hotel-id");
    DB.hotels.lockRead();
    try {
      Hotel hotel = DB.hotels.get("hotel-" + id);
      if (hotel == null) {
        return new RpcError(404, "Hotel not found with id" + id);
      } 

      return new RpcText(hotel.toDetailedString());
    } finally {
      DB.hotels.unlockRead();
    }
  };

  public static RpcHandler addReview = (RpcMessage rpc) -> {
    int hotelId = rpc.getInteger("hotel-id");
    int cleaningRating = rpc.getInteger("cleaning-rating");
    int positionRating = rpc.getInteger("position-rating");
    int servicesRating = rpc.getInteger("services-rating");
    int qualityRating = rpc.getInteger("quality-rating");

    if(!Rating.isValid(qualityRating))
      return new RpcError(400, "Invalid quality rating, must be between 0 and " + Rating.MAX_RATING);
    
    if(!Rating.isValid(servicesRating))
      return new RpcError(400, "Invalid services rating, must be between 0 and " + Rating.MAX_RATING);

    if(!Rating.isValid(positionRating))
      return new RpcError(400, "Invalid position rating, must be between 0 and " + Rating.MAX_RATING);

    if(!Rating.isValid(cleaningRating))
      return new RpcError(400, "Invalid cleaning rating, must be between 0 and " + Rating.MAX_RATING);

    int sessionId = rpc.getInteger("cookie-session-id");
    Session session = Session.getSession(sessionId);
    String username = session.getUsername();

    Rating ratings = new Rating(cleaningRating, positionRating, servicesRating, qualityRating);
    Review review = new Review("hotel-" + hotelId, username, ratings);

    DB.reviews.lockWrite();
    try {
      DB.reviews.add(review);
    } finally {
      DB.reviews.unlockWrite();
    }

    DB.users.lockWrite();
    try {
      User user = DB.users.get(username);
      user.incrementLevel();
    } finally {
      DB.users.unlockWrite();
    }

    return new RpcText("Review added\n\n" + review.toString());
  };
}
