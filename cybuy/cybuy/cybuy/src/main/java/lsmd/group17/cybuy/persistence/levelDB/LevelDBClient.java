package lsmd.group17.cybuy.persistence.levelDB;

import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

// LevelDBClient class
public class LevelDBClient {
    private final ArrayList<Pair<String, Integer>> servers;
    private int currentIndex;
    private String currentHost;
    private int currentPort;

    public LevelDBClient(ArrayList<Pair<String, Integer>> servers) {
        this.servers = servers;
        currentIndex = getCurrentIndex(servers);
        currentHost = servers.get(currentIndex).getKey();
        currentPort = servers.get(currentIndex).getValue();
    }

    private int getCurrentIndex(ArrayList<Pair<String, Integer>> servers) {
        int max = servers.size() - 1;
        Random rn = new Random();
        int currentIndex = rn.nextInt(max+1);
        return currentIndex;
    }

    // driver code
    public Message sendMessage(Message message) throws Exception {
        Socket socket = null;
        int tries = 0;

        while(tries < 3) {
            try {
                socket = new Socket(currentHost, currentPort);
                break;
            } catch (IOException e) {
                currentIndex = (currentIndex + 1) % servers.size();
                currentHost = servers.get(currentIndex).getKey();
                currentPort = servers.get(currentIndex).getValue();
                tries++;
            }
        }

        // writing to server
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutput objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(message);

        // reading from server
        // get the input stream from the connected socket
        InputStream inputStream = socket.getInputStream();
        // create a DataInputStream so we can read data from it.
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        Message messageReceived = (Message) objectInputStream.readObject();

        if(messageReceived == null)
            throw new Exception("NULL MESSAGE");

        if (!messageReceived.getType().equals("OK"))
            throw new Exception(messageReceived.getType());

        socket.close();

        return messageReceived;
    }
}

