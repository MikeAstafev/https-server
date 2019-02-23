import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

//checking client response

public class ClientSession implements Runnable
{
    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;

    private static final String DEF_FILES = "/www";
    @Override
    public void run()
    {
        try
        {
            //get head from client message
            String header = readHeader();
            System.out.println(header + "\n");
            //get from header
            String url = getURIFromHeader(header);
            System.out.println("Resource: " + url + "\n");
            //send client uncludes from uri
            int code = send(url);
            System.out.println("Result code: " + code + "\n");
        }catch(IOException ex)
        {
            ex.printStackTrace();
        }finally
        {
            try
            {
                socket.close();
            }catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException
    {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException
    {
        //Get input - there will be messages from client
        in = socket.getInputStream();
        //output - answer to client
        out = socket.getOutputStream();
    }

    //get header from client file
    private String readHeader() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while(true)
        {
            ln = reader.readLine();
            if(ln == null || ln.isEmpty())
                break;
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    //get identifier of client resource
    private String getURIFromHeader(String header)
    {
        int from = header.indexOf(" ") + 1;
        int to = header.indexOf(" ",from);
        String uri = header.substring(from,to);
        int paramIndex = uri.indexOf("?");
        if(paramIndex != -1)
            uri = uri.substring(0, paramIndex);
        return DEF_FILES + uri;
    }
    //send answer to the client - http of chosen resource
    //if nothing is chosen - send list of free resources
    private int send (String url) throws IOException
    {
        InputStream strm = HttpServer.class.getResourceAsStream(url);
        int code = (strm != null) ? 200 : 404;
        String header = getHeader(code);
        PrintStream answer = new PrintStream(out, true, "UTF-8");
        answer.print(header);
        if(code == 200)
        {
            int count = 0;
            byte[] buffer = new byte[1024];
            while((count = strm.read(buffer)) != -1)
                out.write(buffer, 0, count);
            strm.close();
        }
        return code;
    }
    //return http-header of answer
    private String getHeader(int code)
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("HTTP/1.1 " + code + " " + getAnswer(code) + "\n");
        buffer.append("Date: " + new Date().toString() + "\n");
        buffer.append("Accept-Ranges: none\n");
        //buffer.append("Content-Type: " + contentType + "\n");
        buffer.append("\n");
        return buffer.toString();
    }
    //return comments to the code of result
    private String getAnswer(int code)
    {
        switch (code)
        {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            default:
                return "Internal Server Error";
        }
    }
}
