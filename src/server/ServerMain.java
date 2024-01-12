package server;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

import lib.nioserver.NIOServer;
import lib.notify.NotifySender;
import lib.rpc.RpcMessage;
import lib.rpc.RpcRouter;
import lib.rpc.RpcValidator;
import server.ratings.RatingCalculator;

public class ServerMain {

  static public void main(String argv[]) throws Exception {

      Config config = Config.loadFromFile("resources/config.json");
      DB.init(config);

      RpcRouter router = new RpcRouter(config.getSwagger());
      RpcValidator insertReviewValidator = new RpcValidator()
        .intg("hotel-id")
        .intg("cleaning-rating")
        .intg("position-rating")
        .intg("services-rating")
        .intg("quality-rating");

      router.addHandler("auth-login", AuthFilter.anonymous, new RpcValidator().str("username").str("password"), Rpcs.login);
      router.addHandler("auth-register", AuthFilter.anonymous, new RpcValidator().str("username").str("password"), Rpcs.register);
      router.addHandler("auth-logout", AuthFilter.authenticated, Rpcs.logout);
      router.addHandler("info-showMyBadge", AuthFilter.authenticated, Rpcs.showBadges);
      router.addHandler("info-serchAllHotels", null, new RpcValidator().str("city"), Rpcs.hotelByCity);
      router.addHandler("info-searchHotel", null, new RpcValidator().intg("hotel-id"), Rpcs.hotelById);
      router.addHandler("insertReview", AuthFilter.authenticated, insertReviewValidator, Rpcs.addReview);
      
      router.addHandler(
        "_notificationChannel", 
        null,  
        (RpcMessage rpc) -> new RpcMessage()
          .setString("host",  config.getNotificationAddress())
          .setInteger("port", config.getNotificationPort())
      );

      Timer ratingCalculatorTimer = new Timer();
      InetAddress address = InetAddress.getByName(config.getNotificationAddress());
      NotifySender sender = new NotifySender(config.getNotificationPort(), address);

      RatingCalculator ratingCalculator = new RatingCalculator(sender);
      ratingCalculatorTimer.schedule(new TimerTask() {
        @Override 
        public void run() {
          try {
        
            ratingCalculator.calculateAndUpdate();
          } catch (InterruptedException e) {
            System.out.println("Rating calculator interrupted");
          } catch (Exception e) {
            System.out.println("Error creating notify sender");
          }
        
        }
      }, 0, 1000);

      NIOServer server = new NIOServer(config.getPort(), router);
      Thread serverThread = new Thread(() -> {
        try {
          server.listen();
        } catch (Exception e) {
          System.out.println("Error starting server");
        }
      });

      serverThread.start();

      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        
        System.out.println("\n\n Shutting down server \n\n");
      
        System.out.println("Shutting down TCP server");
        serverThread.interrupt();
        server.stop();

        System.out.println("Shutting down rating calculator");
        ratingCalculatorTimer.cancel();
        ratingCalculatorTimer.purge();

        System.out.println("Shutting down auto saver");
        DB.shutdown();

        try {
          serverThread.join();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }));
    }
}
