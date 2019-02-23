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
    private String getUTIFromHeader(String header)
    {
        int from = header.indexOf(" ") + 1;
        int to = header.indexOf(" ",from);
        String uri = header.substring(from,to);
        int paramIndex = uri.indexOf("?");
        if(paramIndex != -1)
            uri = uri.substring(0, paramIndex);
        return DEF_FILES + uri;
    }
}
