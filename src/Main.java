import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    

    public static void main(String[] args) throws Throwable
    {
        //try to create server with port '8080'
        int port = 1337;
        ServerSocket server = null;
        //creating server socket on port 1337
        try
        {
            server = new ServerSocket(port, 5);
            System.out.println("Server started on port: " + server.getLocalPort() + "\n");
        }catch(IOException ex)
        {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }
        //new step - waiting for clients
        while(true)
        {
            try
            {
                System.out.println("Waiting for connection...");
                Socket client = server.accept();
                //print if connected
                System.out.println("Connection accepted.");
                ClientSession session = new ClientSession(client);
                new Thread(session).start();
            }catch(IOException ex)
            {
                System.out.println("Failed to establish connection.");
                System.out.println(ex.getMessage());
                System.exit(-1);
            }
       }
//        try
//        {
//            //waiting for connection to socket from 'client' on the server side
//            System.out.println("Waiting for connection...");
//            Socket client = server.accept();
//            //print if connected
//            System.out.println("Connection accepted.");
//
//            //input/output
//            InputStream sin = client.getInputStream();
//            OutputStream sout = client.getOutputStream();
//
//            //parse to another type
//            DataInputStream in = new DataInputStream(sin);
//            DataOutputStream out = new DataOutputStream(sout);
//
//            String line = "";
//            line = in.readUTF(); // waiting for str from client
//            System.out.println("Requested message:" + line);
//        }catch(Exception ex)
//        {
//            ex.printStackTrace();
//        }
    }
}
