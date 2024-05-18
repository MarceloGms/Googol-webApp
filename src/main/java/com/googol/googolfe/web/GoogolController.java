package com.googol.googolfe.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.googol.googolfe.web.api.HNRequestBody;

import org.springframework.ui.Model;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.googol.googolfe.server.interfaces.IGatewayCli;
import com.googol.googolfe.objects.HackerNewsItemRecord;
import com.googol.googolfe.objects.Result;
import com.googol.googolfe.server.interfaces.IClient;
import com.googol.googolfe.objects.BrlObj;
import com.googol.googolfe.objects.Top10Obj;


@Controller
public class GoogolController extends UnicastRemoteObject implements IClient {

   private static final Logger logger = Logger.getLogger(GoogolController.class.getName());

   /**
   * The IP address of the gateway RMI server.
   */
   private String SERVER_IP_ADDRESS;

   /**
   * The port number of the gateway RMI server.
   */
   private String SERVER_PORT;

   IGatewayCli gw;

   @Autowired
   private SimpMessagingTemplate template;

   GoogolController() throws RemoteException{
      loadConfig();
      initializeLogger();
      gw = connectToGateway();
      if (gw != null)
         gw.subscribe(this);
      logger.info("Googol Controller started.");
   }

   @GetMapping("/")
   public String showGoogolPage(Model model) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               logger.warning("Error subscribing client.");
               model.addAttribute("error", "Error subscribing client.");
               return "error";
            }
         else {
            logger.warning("Gateway is down.");
            model.addAttribute("error", "Gateway is down.");
            return "error";
         }
      }
      return "googol";
   }

   @PostMapping("/sendUrl")
   public ResponseEntity<String> sendUrlToServer(@RequestBody UrlRequestBody requestBody) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               logger.warning("Error subscribing client.");
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subscribing client.");
            }
         else {
            logger.warning("Gateway is down.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending URL to the Gateway.");
         }
      }
      String url = requestBody.getUrl();
      logger.info("Received URL: " + url + " sending to Gateway.");
      try {
         gw.send(url, this);
      } catch (RemoteException e) {
         logger.warning("Error occurred while sending URL to the Gateway.");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending URL to the Gateway.");
      }
      return ResponseEntity.status(HttpStatus.OK).body("URL received successfully");
   }

   @GetMapping("/search")
   public String showSearchPage(Model model, @RequestParam() String query) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               model.addAttribute("error", "Error subscribing client.");
            return "error";
            }
         else {
            model.addAttribute("error", "Gateway is down.");
            return "error";
         }
      }
      String result = null;
      try {
         result = gw.search(query);
         if (result != null) {
           if (result.equals("")) {
             logger.warning("No results found.");
             model.addAttribute("group", "No results found.");
           } else {
               String[] parts;
               String[] new_parts = new String[3];
               String[] results = result.split("<>");
               if (results.length != 0){
                  Result[] res = new Result[results.length];
                  for (int i = 0; i < results.length; i++) {
                     parts = results[i].split("\n");
                     if (parts.length == 2){
                        new_parts[0] = parts[0];
                        new_parts[1] = "";
                        new_parts[2] = parts[1];
                     }else{
                        new_parts[0] = parts[0];
                        new_parts[1] = parts[1];
                        new_parts[2] = parts[2];
                     }
                     res[i] = new Result(new_parts[0], new_parts[1], new_parts[2]);
                  }
            
                  model.addAttribute("group", res);
               }
           }
         } else {
            logger.warning("No results found.");
            model.addAttribute("group", "No results found.");
         }
      } catch (RemoteException e) {
         logger.warning("Error occurred during search.");
         model.addAttribute("group", "Error occurred during search.");
      }
      model.addAttribute("query", query);
      return "search";
   }

   @GetMapping("/urls")
   public String showSubUrlsPage(Model model, @RequestParam() String url) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               model.addAttribute("error", "Error subscribing client.");
            return "error";
            }
         else {
            model.addAttribute("error", "Gateway is down.");
            return "error";
         }
      }
      String result = null;
      try {
         result = gw.findSubLinks(url);
         if (result != null) {if (result.equals("")) {
            logger.warning("No results found.");
            model.addAttribute("urls", "No results found.");
         } else if (result.equals("Invalid URL.")) {
            logger.warning("Invalid URL.");
            model.addAttribute("urls", "Invalid URL.");
         } else {
            String[] urls = result.split("\n");
            model.addAttribute("urls", urls);
         }
      } else {
         logger.warning("No results found.");
         model.addAttribute("urls", "No results found.");
      }
   } catch (RemoteException e) {
      logger.warning("Error occurred getting sub links.");
      model.addAttribute("urls", "Error occurred getting sub links.");
   }
      model.addAttribute("url", url);
      return "urls";
   }

   @GetMapping("/admin")
   public String showAdminPage(Model model) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               model.addAttribute("error", "Error subscribing client.");
            return "error";
            }
         else {
            model.addAttribute("error", "Gateway is down.");
            return "error";
         }
      }
      try {
         ArrayList<BrlObj> barrels = gw.getActiveBarrels();
         if (barrels != null)
            model.addAttribute("barrels", barrels);
         String stringTop10 = gw.getTop10Searches();
         ArrayList<Top10Obj> top10 = new ArrayList<>();
         String[] top10Array = stringTop10.split("\n");
         for (String top : top10Array) {
            String[] split = top.split(" - ");
            top10.add(new Top10Obj(split[0], Integer.parseInt(split[1])));
         }
         model.addAttribute("searches", top10);
      } catch (RemoteException e) {
         model.addAttribute("error", "Error occurred while getting barrels and top 10 searches.");
         return "error";
      }
      return "admin";
   }

   @PostMapping("/sendHackerNews")
   public ResponseEntity<String> sendUrlToServer(@RequestBody HNRequestBody requestBody) {
      if (gw == null) {
         gw = connectToGateway();
         if (gw != null)
            try {
               gw.subscribe(this);
            } catch (RemoteException e) {
               logger.warning("Error subscribing client.");
               return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subscribing client.");
            }
         else {
            logger.warning("Gateway is down.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Gateway is down.");
         }
      }
      String query = requestBody.getQuery();
      logger.info("Received query for Hacker News: " + query);

      List<String> matchedStories = hackerNewsStories(query);
      if (matchedStories == null) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No matching stories found.");
      }

      for (String story : matchedStories) {
         logger.info("Matched story: " + story);
         try {
            gw.send(story, this);
         } catch (RemoteException e) {
            logger.warning("Error occurred while sending URL to the Gateway.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending URL to the Gateway.");
         }
      }

      return ResponseEntity.status(HttpStatus.OK).body("Successfully sent Hacker News stories.");
   }

   private List<String> hackerNewsStories(String query) {
      List<String> resultado = new ArrayList<>();

      String topStoriesEndpoint = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
      RestTemplate restTemplate = new RestTemplate();
      List<Integer> topStoriesIds = restTemplate.getForObject(topStoriesEndpoint, List.class);

      if (topStoriesIds == null) {
         return null;
      }

      for (Integer storyId : topStoriesIds) {
         String storyURL = "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json?print=pretty";
         HackerNewsItemRecord oneStory = restTemplate.getForObject(storyURL, HackerNewsItemRecord.class);

         if (oneStory != null && (query.isEmpty() || oneStory.title().contains(query))) {
               resultado.add(oneStory.url());
         }
      }

      return resultado;
   }

   /**
   * Connects to the RMI gateway by looking up the server's remote object.
   * If connection fails, it handles the error and exits the program.
   */
   private IGatewayCli connectToGateway() {
      IGatewayCli gw = null;
      try {
         gw = (IGatewayCli) Naming.lookup("rmi://" + SERVER_IP_ADDRESS + ":" + SERVER_PORT + "/gw");
         logger.info("Connected to the Gateway.");
      } catch (RemoteException | NotBoundException | MalformedURLException e) {
         logger.warning("Error connecting to the Gateway.");
      }
      return gw;
   }

   /**
   * Loads the server IP address from the config file.
   */
   private void loadConfig() {
      Properties prop = new Properties();
      try (FileInputStream input = new FileInputStream("assets/config.properties")) {
         prop.load(input);
         SERVER_IP_ADDRESS = prop.getProperty("server_ip");
         SERVER_PORT = prop.getProperty("server_port");
      } catch (IOException ex) {
         logger.warning("Error occurred while trying to load config file.");
         System.exit(1);
      }
   }

   @Override
   public void printOnClient(String s) throws RemoteException {
      logger.info("Received message from Gateway: " + s);
   }

   /**
   * Initializes the logger for the Gateway.
   */
   private void initializeLogger() {
      try {
         FileHandler fileHandler = new FileHandler("webapp.log");
         fileHandler.setFormatter(new SimpleFormatter());
         logger.addHandler(fileHandler);
         logger.setLevel(Level.INFO);
      } catch (IOException e) {
         System.err.println("Failed to configure logger: " + e.getMessage());
      }
   }

   @Override
   public void sendBrls(ArrayList<BrlObj> activeBarrels) throws RemoteException {
      if (this.template != null) {
         this.template.convertAndSend("/topic/barrelUpdates", activeBarrels);
     } else {
         logger.warning("SimpMessagingTemplate is null, unable to send barrel updates.\n");
     }
   }

   @Override
   public void sendTop10(ArrayList<Top10Obj> top10) throws RemoteException {
      if (this.template != null) {
         this.template.convertAndSend("/topic/searchUpdates", top10);
     } else {
         logger.warning("SimpMessagingTemplate is null, unable to send top 10 updates.\n");
     }
   }
}