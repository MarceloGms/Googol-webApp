package com.googol.googolfe;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class GoogolController extends UnicastRemoteObject implements IClient {

   /**
   * The IP address of the gateway RMI server.
   */
   private String SERVER_IP_ADDRESS;

   /**
   * The port number of the gateway RMI server.
   */
   private String SERVER_PORT;

   GoogolController() throws RemoteException{
      loadConfig();
   }

   @GetMapping("/")
   public String showGoogolPage() {
      return "googol";
   }

   @PostMapping("/sendUrl")
   public ResponseEntity<String> sendUrlToServer(@RequestBody UrlRequestBody requestBody) {
      IGatewayCli gw = connectToGateway();
      if (gw == null) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error connecting to the Gateway.");
      }
      String url = requestBody.getUrl();
      System.out.println("Received URL: " + url);
      try {
         gw.subscribe(this);
         gw.send(url, this);
         gw.unsubscribe(this);
      } catch (RemoteException e) {
         System.out.println("Error occurred while sending URL to the Gateway.");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending URL to the Gateway.");
      }

      return ResponseEntity.status(HttpStatus.OK).body("URL received successfully");
   }

   @GetMapping("/search")
   public String showSearchPage(Model model, @RequestParam() String query) {
      IGatewayCli gw = connectToGateway();
      if (gw == null) {
         return "error";
      }
      String result = null;
      try {
         result = gw.search(query);
         if (result != null) {
           if (result.equals("")) {
             System.out.println("No results found.\n");
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
           System.out.println("No results found.\n");
            model.addAttribute("group", "No results found.");
         }
       } catch (RemoteException e) {
         System.out.println("Error occurred during search.\n");
         model.addAttribute("group", "Error occurred during search.");
       }
       model.addAttribute("query", query);
       return "search";
   }

   @GetMapping("/urls")
   public String showSubUrlsPage(Model model, @RequestParam() String url, @RequestParam(defaultValue = "0") String page) {
      List<String> urls = new ArrayList<>();
      for (int i = 1; i <= 30; i++) {
         urls.add("URL" + i);
      }
      model.addAttribute("url", url);
      model.addAttribute("urls", urls);
      return "urls";
   }

   @GetMapping("/admin")
   public String showAdminPage(Model model) {
      List<String> brls = new ArrayList<>();
      for (int i = 1; i <= 7; i++) {
         brls.add("Barrel " + i);
      }
      model.addAttribute("barrels", brls);
      model.addAttribute("searches", new String[] {"Search 1", "Search 2", "Search 3", "Search 4", "Search 5", "Search 6", "Search 7", "Search 8", "Search 9", "Search 10"});
      return "admin";
   }

   /**
   * Connects to the RMI gateway by looking up the server's remote object.
   * If connection fails, it handles the error and exits the program.
   */
   private IGatewayCli connectToGateway() {
      IGatewayCli gw = null;
      try {
         gw = (IGatewayCli) Naming.lookup("rmi://" + SERVER_IP_ADDRESS + ":" + SERVER_PORT + "/gw");
      } catch (RemoteException | NotBoundException | MalformedURLException e) {
         System.out.println("Error connecting to the Gateway.");
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
         System.out.println("Error occurred while trying to load config file.");
         System.exit(1);
      }
   }

   @Override
   public void printOnClient(String s) throws RemoteException {
      if (s.equals("Gateway shutting down.")) {
         System.out.println("Received shutdown signal from server. Exiting program...");
         try {
             UnicastRemoteObject.unexportObject(this, true);
         } catch (NoSuchObjectException e) {
         }
         System.exit(0);
       } else {
         System.out.println(s);
       }
   }
}