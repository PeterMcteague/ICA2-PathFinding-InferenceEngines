
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s
 */
public class SocketConnection
{
	private final static int defaultPortNo = 2222;		// some arbitrary port no. for this example
	private Socket socket;
	private SocketCommunicator babbler;
	private BufferedReader inStream;
	private PrintWriter outStream;

	
	public final static boolean SERVER = true;
	public final static boolean CLIENT = false;

	public SocketConnection(SocketCommunicator babbler, String ipAddress, int portNo, boolean isServer )
	{	this.babbler = babbler;
		if( isServer != CLIENT )
			throw( new IllegalArgumentException("only clients are allowed to use IP addresses") );
		else
			connectAsClient(getHostFromIP(ipAddress), portNo);
	}
	public SocketConnection(SocketCommunicator babbler, int portNo, boolean isServer )
	{	this.babbler = babbler;
		if( isServer == SERVER )
			advertiseAsServer(portNo);
		else
			try
			{	connectAsClient(InetAddress.getLocalHost(), portNo);
			} catch (UnknownHostException ex)
			{	Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
			}
	}
	public boolean isConnected()
	{	return socket.isConnected();
	}
	public void write( String msg )
	{	if( isConnected() )
			outStream.println(msg);
	}
	public void close()
	{	try
		{
			socket.close();
		} catch (IOException ex)
		{	Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	private void advertiseAsServer(final int portNo)
	{	new Thread()
		{	public void run()
			{	try
				{	ServerSocket server = new ServerSocket(portNo);
					babbler.notify("server advertising");
					socket = server.accept();
					makeComms(socket);
					readerLoop();
				}
				catch (IOException ex)
				{	Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}.start();
	}
	
	private void connectAsClient(final InetAddress address, final int portNo)
	{	new Thread()
		{	public void run()
			{	try
				{	babbler.notify("client attaching");
					socket = new Socket( address, portNo );
					makeComms(socket);
					readerLoop();
				}
				catch (IOException ex)
				{	Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}.start();
	}
	
	private void makeComms(Socket socket) throws IOException
	{	babbler.notify("making IO streams");
		inStream = new BufferedReader(
							new InputStreamReader( socket.getInputStream()) );
		outStream = new PrintWriter( socket.getOutputStream(), true );
		babbler.notify("IO streams ok");
	}
	
	private void readerLoop() throws IOException
	{	while( isConnected() )
		{	babbler.inputReceived( inStream.readLine() );
		}
		babbler.notify("socket reader closing");
	}
	private InetAddress getHostFromIP( String ipAddress )
	{	InetAddress host = null;
		String[] ips = ipAddress.split("[.]");
		byte[] ipb = new byte[4];
		for( int i=0; i<4; i++ )
			ipb[i] = (byte) Integer.parseInt( ips[i] );
		try
		{	host = InetAddress.getByAddress( ipb );
		}
		catch( UnknownHostException e )
		{	throw new RuntimeException( e.toString() );
		}
		return host;
	}
}
