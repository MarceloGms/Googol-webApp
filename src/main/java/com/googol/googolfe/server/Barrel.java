package com.googol.googolfe.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.googol.googolfe.server.interfaces.IBarrel;
import com.googol.googolfe.server.interfaces.IGatewayBrl;

import java.time.Duration;
import java.time.Instant;

/**
 * The Barrel Class implements the interface IBarrel
 * It is responsible for storing all the information, such as, the inverted index, the linked pages and the top 10 searches
 * It also listens for multicast messages from the Gateway and updates the information accordingly
 */
public class Barrel extends UnicastRemoteObject implements IBarrel, Runnable {
    /**
     * The id of the barrel
     */
    private int id;
    /**
     * The total searches time of the barrel
     */
    private long totalTime;
    /**
     * The count of searches of the barrel
     */
    private int count;
    /**
    * The remote gateway interface used for communication with the gateway.
    */
    private IGatewayBrl gw;
    /**
     * The set of stop words to be ignored in the search.
     */
    private Set<String> stopWords;
    /**
     * The hashmap used to store the inverted index.
     */
    private HashMap<String, HashSet<String>> invertedIndex;
    /**
     * The boolean used to check if the barrel is running.
     */
    private boolean running;
    /**
     * The hashmap used to store the links of each page.
     */
    private HashMap<String, HashSet<String>> pageLinks;
    /**
     * The hashmap used to store the linked pages of each page.
     */
    private HashMap<String, HashSet<String>> linkedPage;
    /**
     * The hashmap used to store the title and citation of each page.
     */
    private HashMap<String, LinkedHashSet<String>> title_citation;
    /**
     * The multicast socket used for communication.
     */
    private MulticastSocket multicastSocket;
    /**
    * The IP address of the gateway RMI server.
    */
    private static String SERVER_IP_ADDRESS;

    /**
     * The port of the gateway RMI server.
     */
    private static String SERVER_PORT;

    /**
     * The multicast address used for communication.
     */
    private static String MULTICAST_ADDR;

    /**
     * The multicast port used for communication.
     */
    private static int MULTICAST_PORT;

    /**
     * The Barrel constructor is used to create a new barrel.
     * @throws RemoteException if there is an error creating the barrel
     */
    public Barrel() throws RemoteException {
        invertedIndex = new HashMap<>();
        running = true;
        pageLinks = new HashMap<>();
        linkedPage = new HashMap<>();
        title_citation = new HashMap<>();
        stopWords = new HashSet<>();
        this.totalTime = 0;
        this.count = 0;
        // Load stop words from file
        loadStopWords("assets/stop_words.txt");
        // Create the multicast socket
        try {
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
        } catch (IOException e) {
            System.err.println("Error creating multicast socket: " + e.getMessage());
            System.exit(1);
        }
        // handle SIGINT
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));
    }

    /**
     * The send method is used to send a message to the barrel.
     */
    @Override
    public void send(String s) throws RemoteException {
        if (s.equals("Gateway shutting down.")) {
            running = false;
            multicastSocket.close();
            System.out.println("Received shutdown signal from server. Shutting down...");
            try {
                UnicastRemoteObject.unexportObject(this, true);
            } catch (NoSuchObjectException e) {
            }
            System.exit(0);
        } else {
            System.out.println(s + "\n");
        }
    }

    /**
     * The search method is used to search for a given string in the inverted index.
     */
    @Override
    public String search(String s) throws RemoteException {
        Instant start = Instant.now();
        String sep_words_aux[] = s.split(" ");
        ArrayList<String> sep_words = new ArrayList<>();
        // Remove punctuation from words and add to sep_words
        for (String sep_word : sep_words_aux) {
            sep_word = sep_word.replaceAll("\\p{Punct}", "");
            if (!isStopWord(sep_word)) {
                normalizeWord(sep_word);
                sep_words.add(sep_word);

                // Read top10.dat file and update the search count
                HashMap<String, Integer> searchCount = readHashMapFromFileTop10("top10.dat");
                if(searchCount == null){
                    searchCount = new HashMap<>();
                }
                if(!searchCount.containsKey(sep_word)){
                    searchCount.put(sep_word, 1);
                }else{
                    searchCount.put(sep_word, searchCount.get(sep_word) + 1);
                }
                // Save the updated search count to top10.dat file
                saveHashMapToFileTop10(searchCount, "top10.dat");
            }
        }
        
        // Search for the words in the inverted index, and adds the links with all sep_words to links_search
        List<String> links_search = new ArrayList<>(), links_search_aux = new ArrayList<>(), linksToRemove = new ArrayList<>();;
        int words_count = 0;
        for (String sep_word : sep_words) {
            for(String key : invertedIndex.keySet()){
                if(key.equals(sep_word)){
                    words_count++;
                    for (String link : invertedIndex.get(sep_word)) {
                        links_search_aux.add(link);
                    }
                    if(!links_search.isEmpty()){
                        for (String link : links_search) {
                            if(!links_search_aux.contains(link)){
                                linksToRemove.add(link);
                            }
                        }
                        links_search.removeAll(linksToRemove);
                    }else{
                        links_search.addAll(links_search_aux);
                    }
                    links_search_aux.clear();
                    break;
                }
            }
        }

        // Verify if all words were found in the inverted index
        if (words_count != sep_words.size()) {
            return "";
        }

        // Order the links by the number of links that point to them
        links_search.sort(Comparator.comparingInt(url -> {
            HashSet<String> values = linkedPage.get(url);
            return values != null ? values.size() : 0;
        }).reversed());
        
        // Create the string with title, citation and the links
        String string_links = "";
        for (String link : links_search) {
            for (Map.Entry<String, LinkedHashSet<String>> entry : title_citation.entrySet()) {
                String key = entry.getKey();
                if (key.equals(link)) {
                    HashSet<String> values = entry.getValue();
                    for (String value : values) {
                        string_links += value + "\n";
                    }
                    break;
                }
            }
            string_links += link + "\n<>";
        }
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long timeElapsedNanos = timeElapsed.toNanos();

        totalTime += timeElapsedNanos;
        count++;
        return string_links;
    }

    /**
     * The findSubLinks method is used to find the links pointing to a given link.
     */
    @Override
    public String findSubLinks(String s) throws RemoteException {
        HashSet<String> links = linkedPage.get(s);
        String string_links = "";
        if (links != null) {
            for (String link : links) {
                string_links += link + "\n";
            }
        }
        return string_links;
    }

    /**
     * The getTop10Searches method is used to get the top 10 searches.
     */
    @Override
    public String getTop10Searches() throws RemoteException {
        // If file does not exist, return empty string
        File top10File = new File("assets/top10.dat");
        if (!top10File.exists()) {
            return "";
        }
        
        String string_links = "";
        HashMap<String, Integer> searchCount = readHashMapFromFileTop10("top10.dat");
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(searchCount.entrySet());
        Comparator<Map.Entry<String, Integer>> comparator = Comparator.comparingInt(Map.Entry::getValue);

        // Sort the entryList by the number of searches
        entryList.sort(comparator.reversed());
        for (int i = 0; i < entryList.size(); i++) {
            string_links += entryList.get(i).getKey() + " - " + entryList.get(i).getValue() + "\n";
        }
        return string_links;
    }

    /**
     * The getId method is used to get the id of the barrel.
     */
    @Override
    public int getId() throws RemoteException {
        return id;
    }

    /**
     * The getAverageTime method is used to get the average time of the barrel.
     */
    @Override
    public double getAverageTime() throws RemoteException {
        if (count == 0) {
            return 0;
        }
        return (totalTime / 1_000_000.0) / count;
    }

    /**
     * The run method is used to connect to the gateway and listen for multicast messages.
     */
    public void run() {
        // Connect to the Gateway
        try {
            gw = (IGatewayBrl) Naming.lookup("rmi://" + SERVER_IP_ADDRESS + ":" + SERVER_PORT + "/gw");
            System.out.println("Barrel connected to Gateway.");
        } catch (NotBoundException e) {
            System.err.println("Gateway not bound. Exiting program.");
            System.exit(1);
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL. Exiting program.");
            System.exit(1);
        } catch (RemoteException e) {
            System.err.println("Gateway down. Exiting program.");
            System.exit(1);
        }

        // Add the barrel to the Gateway
        try {
            synchronized (gw) {
                id = gw.AddBrl(this);
            }
            System.out.println("Barrel " + id + " bound to Gateway.");
        } catch (RemoteException e) {
            System.out.println("Error adding barrel to Gateway: " + e.getMessage());
            return;
        }

        // Listen for multicast messages
        listenForMulticastMessages();

    }

    /**
     * The listenForMulticastMessages method is used to listen for multicast messages from the Gateway to receveive information to store.
     */
    private void listenForMulticastMessages() {
        try {
            // Join the multicast group
            InetAddress group = InetAddress.getByName(MULTICAST_ADDR);
            multicastSocket.joinGroup(new InetSocketAddress(group, MULTICAST_PORT), NetworkInterface.getByIndex(0));

            System.out.println("Barrel " + id + " listening for multicast messages...");

            // Load the inverted index and linked pages from files
            if (readHashMapFromFile("Barrel" + id + "index.dat") != null) {
                invertedIndex = readHashMapFromFile("Barrel" + id + "index.dat");
            }
            if (readHashMapFromFile("Barrel" + id + "linkedPage.dat") != null) {
                linkedPage = readHashMapFromFile("Barrel" + id + "linkedPage.dat");
            }
            if (readTitleCitationFromFile("Barrel" + id + "title_citation.dat") != null) {
                title_citation = readTitleCitationFromFile("Barrel" + id + "title_citation.dat");
            }

            // Listen for multicast messages
            while (running) {
                byte[] buffer = new byte[65507];  // Maximum size of a UDP packet
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                try {
                    multicastSocket.receive(packet);
                } catch (SocketException e) {
                    return;
                }

                String message = new String(packet.getData(), 0, packet.getLength());
                //System.out.println("Barrel " + id + " received message from " + packet.getAddress().getHostAddress() + ": " + message);
                //System.out.println(message);
                // Process the message dividing it into parts
                String[] parts = message.split("\n");
                String url = parts[0].replace("URL: ", "");
                String title = parts[1].replace("Title: ", "");
                String citation = parts[2].replace("Citation: ", "");
                String keywordsString = parts[3].replace("Keywords: ", "").replace("[", "").replace("]", "");
                String linksString = parts[4].replace("Links: ", "").replace("[", "").replace("]", "");

                // Add the information to title_citation
                LinkedHashSet<String> info = title_citation.get(url);
                if (info == null) {
                    info = new LinkedHashSet<String>();
                    title_citation.put(url, info);
                }
                info.add(title);
                info.add(citation);

                // Add the keywords to the inverted index
                String[] keywords = keywordsString.split(", ");
                for (String keyword : keywords) {
                    addToIndex(keyword, url);
                }

                // Add the links to the pageLinks
                String[] links = linksString.split(", ");
                for (String link : links) {
                    addToUrls(url, link);
                }

                // Add the linked pages to the linkedPage
                addToLinkedPage(url);

                // Save the inverted index and linked pages to files
                saveHashMapToFile(invertedIndex, "Barrel" + id + "index.dat");
                saveHashMapToFile(linkedPage, "Barrel" + id + "linkedPage.dat");
                saveTitleCitationToFile(title_citation, "Barrel" + id + "title_citation.dat");
            }
        } catch (Exception e) {
            try {
                System.out.println("Barrel " + id + " crashed.");
                gw.rmvBrl(this, id);
                gw.BrlMessage("Barrel " + id + " crashed.");
            } catch (RemoteException e1) {
                System.out.println("Error removing barrel from Gateway.");
            }
        }
    }

    /**
     * The saveHashMapToFile method is used to save a hashmap to a file.
     * @param hashMap hashmap to save
     * @param filename name of the file to save
     */
    private static void saveHashMapToFile(HashMap<String, HashSet<String>> hashMap, String filename) {
        synchronized (getLockObject(filename)) {
            try {
                FileOutputStream fileOut = new FileOutputStream("assets/" + filename);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(hashMap);
                objectOut.close();
            } catch (Exception ex) {
                //System.out.println("Error writing file: " + ex.getMessage());
                return;
            }
        }
    }

    /**
     * The readHashMapFromFile method is used to read a hashmap from a file.
     * @param filename name of the file to read
     * @return hashmap read from file
     */
    private static HashMap<String, HashSet<String>> readHashMapFromFile(String filename) {
        synchronized (getLockObject(filename)) {
            File file = new File("assets/" + filename);
            // If file does not exist, return null
            if (!file.exists()) {
                return null;
            }
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                @SuppressWarnings("unchecked")
                HashMap<String, HashSet<String>> hashMap = (HashMap<String, HashSet<String>>) objectIn.readObject();
                objectIn.close();
                return hashMap;
            } catch (Exception ex) {
                //ex.printStackTrace(); //Ver o erro
                return null;
            }
        }
    }

    /**
     * The saveTitleCitationToFile method is used to save the title and citation hashmap to a file.
     * @param hashMap hashmap to save
     * @param filename name of the file to save
     */
    private static void saveTitleCitationToFile(HashMap<String, LinkedHashSet<String>> hashMap, String filename) {
        synchronized (getLockObject(filename)) {
            try {
                FileOutputStream fileOut = new FileOutputStream("assets/" + filename);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(hashMap);
                objectOut.close();
            } catch (IOException ex) {
                return;
                //System.out.println("Error writing file: " + ex.getMessage());
            }
        }
    }

    /**
     * The readTitleCitationFromFile method is used to read the title and citation hashmap from a file.
     * @param hashMap
     * @param filename
     */

    private static HashMap<String, LinkedHashSet<String>> readTitleCitationFromFile(String filename) {
        synchronized (getLockObject(filename)) {
            File file = new File("assets/" + filename);
            // If file does not exist, return null
            if (!file.exists()) {
                return null;
            }
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                @SuppressWarnings("unchecked")
                HashMap<String, LinkedHashSet<String>> hashMap = (HashMap<String, LinkedHashSet<String>>) objectIn.readObject();
                objectIn.close();
                return hashMap;
            } catch (Exception ex) {
                //System.out.println("Error reading file: " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * The saveHashMapToFileTop10 method is used to save the top10 hashmap to a file.
     * @param hashMap hashmap to save
     * @param filename name of the file to save
     */
    private static void saveHashMapToFileTop10(HashMap<String, Integer> hashMap, String filename) {
        synchronized (getLockObject(filename)) {
            try {
                FileOutputStream fileOut = new FileOutputStream("assets/" + filename);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.writeObject(hashMap);
                objectOut.close();
            } catch (IOException ex) {
                return;
                //System.out.println("Error writing file: " + ex.getMessage());
            }
        }
    }

    /**
     * The readHashMapFromFileTop10 method is used to read the top10 hashmap from a file.
     * @param filename name of the file to read
     * @return hashmap read from file
     */
    private static HashMap<String, Integer> readHashMapFromFileTop10(String filename) {
        synchronized (getLockObject(filename)) {
            File file = new File("assets/" + filename);
            // If file does not exist, return null
            if (!file.exists()) {
                return null;
            }
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);
                @SuppressWarnings("unchecked")
                HashMap<String, Integer> hashMap = (HashMap<String, Integer>) objectIn.readObject();
                objectIn.close();
                return hashMap;
            } catch (Exception ex) {
                //System.out.println("Error reading file: " + ex.getMessage());
                return null;
            }
        }
    }

    /**
     * The addToIndex method is used to add a term and a url to the inverted index.
     * @param term string to add to the index
     * @param url url associated with the term
     */
    public void addToIndex(String term, String url) {
        HashSet<String> urls = invertedIndex.get(term);
        if (urls == null) {
            urls = new HashSet<String>();
            invertedIndex.put(term, urls);
        }
        urls.add(url);
    }

    /**
     * The addToUrls method is used to add a url and its associated link to the pageLinks.
     * @param url url to add
     * @param url_new url associated with the main url
     */
    public void addToUrls(String url, String url_new) {
        HashSet<String> links = pageLinks.get(url);
        if (links == null) {
            links = new HashSet<String>();
            pageLinks.put(url, links);
        }
        links.add(url_new);
    }

    /**
     * The addToLinkedPage method is used to add a url to the linkedPage.
     * @param url url to add
     */
    public void addToLinkedPage(String url) {
        for (String key : pageLinks.keySet()) {
            HashSet<String> links = pageLinks.get(key);
            if (links.contains(url)) {
                HashSet<String> linked = linkedPage.get(url);
                if (linked == null) {
                    linked = new HashSet<String>();
                    linkedPage.put(url, linked);
                }
                linked.add(key);
            }
        }
    }

    /**
     * The isStopWord method is used to check if a word is a stop word.
     * @param word string to check
     * @return true if the word is a stop word, false otherwise
     */
    private boolean isStopWord(String word) {
        return stopWords.contains(normalizeWord(word));
    }

    /**
     * The loadStopWords method is used to load the stop words from a file.
     * @param filename name of the file to load
     */
    private void loadStopWords(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String word = line.trim().toLowerCase();
            if (!word.isEmpty()) {
            stopWords.add(normalizeWord(word));
            }
        }
        } catch (IOException e) {
        System.err.println("Error: Failed to load stop words file. Exiting program.");
        System.exit(1);
        }
    }

    /**
     * The normalizeWord method is used to normalize a word.
     * @param word string to normalize
     * @return normalized word
     */
    private String normalizeWord(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    /**
     * The loadConfig method is used to load the configuration from a file.
     * @return number of barrels
     */
    private static int loadConfig() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("assets/config.properties")) {
            prop.load(input);
            SERVER_IP_ADDRESS = prop.getProperty("server_ip");
            SERVER_PORT = prop.getProperty("server_port");
            MULTICAST_ADDR = prop.getProperty("multicast_ip");
            MULTICAST_PORT = Integer.parseInt(prop.getProperty("multicast_port"));
            return Integer.parseInt(prop.getProperty("barrels"));
        } catch (IOException ex) {
            System.out.println("Failed to load config file: " + ex.getMessage());
            System.exit(1);
            return 0;
        }
    }

    /**
     * The getLockObject method is used to get the lock object for a given filename to syncronize.
     * @param filename name of the file
     * @return lock object
     */
    private static Object getLockObject(String filename) {
        return filename.hashCode();
    }

    /**
     * The shutdown method is used to shutdown the barrel.
     */
    private void shutdown() {
        try {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
            System.out.println("Barrel " + id + " shutting down...");
            // Notify the Gateway about the shutdown
            if (gw != null) {
                synchronized (gw) {
                    gw.rmvBrl(this, id);
                }
            }
        } catch (RemoteException e) {
        }
    }

    /**
     * The main method is used to create the barrels and start the threads.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        int nBarrels = loadConfig();

        for (int i = 1; i <= nBarrels; i++) {
            try {
                Barrel barrel = new Barrel();
                Thread thread = new Thread(barrel);
                thread.start();
            } catch (RemoteException e) {
                System.out.println("Error creating Barrel: " + e.getMessage());
            }
        }
    }
}