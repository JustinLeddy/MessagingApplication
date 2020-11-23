import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Message Server
 *
 * The server for the social messaging application which uses the MessageHandler
 * class to respond to the MessageClient on multiple threads
 * Purpose/Functions (To be implemented from MessageHandler):
 *      Check Username and Password Format, if they exist, and then add/reject them to accounts.txt
 *      Check if there are conversations and messages within conversations
 *      Have add/edit/delete functionality of conversations and messages
 *
 *
 * @author Alex Frey, Justin Leddy, Maeve Tra, Yifie Mao, Naveena Erranki
 * @version November 30th, 2020
 */
public class MessageServer {
    public void serveClient() {
        //TODO Serve Client using MessageHandler threading
    }

    public static void main(String[] args) {
        //Temporary

        try {
            var serverSocket = new ServerSocket(8888);
            System.out.println("Connected");
            var socket = serverSocket.accept();
            System.out.println("Connected to Client");
            while (true) {

                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.println(reader.readLine());

                writer.write("true");
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Temporary




    }






}