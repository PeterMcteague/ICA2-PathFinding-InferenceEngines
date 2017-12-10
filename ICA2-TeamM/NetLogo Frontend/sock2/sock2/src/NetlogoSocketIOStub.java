
/*
 * this uses the SocketCommunicator classes because I have them from other work,
 * because of this I use queue for the input read from the socket rather than
 * allowing input to queue on the sockets input stream
 */
import java.net.InetAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s
 */
class NetlogoSocketIOStub implements SocketCommunicator, StreamReadWriter
{
	private SocketConnection cnx;
	private LinkedBlockingQueue<String> inputQ;

	/**
	 * connect a socket and build IO comms streams. These sockets are backed by
	 * LinkedBlockingQueue for incoming messages to allow some extra functionality.
	 * 
	 * @param hostId the host name eg: "127.168.57.63"
	 * @param port   port number
	 * @param mode   as server (true) or client (false)
	 */
	public NetlogoSocketIOStub( String hostId, int port, boolean mode)
	{	// connect as client
		cnx = new SocketConnection( this, hostId, port, mode );
		inputQ = new LinkedBlockingQueue<String>();
	}
	public NetlogoSocketIOStub( int port, boolean mode)
	{	// connect as client or server
		cnx = new SocketConnection( this, port, mode );
		inputQ = new LinkedBlockingQueue<String>();
	}

	@Override
	public void inputReceived(String text)
	{	inputQ.add(text);
	}

	@Override
	public void notify(String msg)
	{	System.err.println(msg);
	}
	public void close()
	{	cnx.close();
	}
	public void clear()
	{	inputQ.clear();
	}
	public boolean isInputWaiting()
	{	return (inputQ.peek() != null);
	}
	public boolean isConnected()
	{	return cnx.isConnected();
	}
	public String peek() {
		return inputQ.peek();
	}

	public String poll() {
		return inputQ.poll();
	}

	public String take() {
		String s = null;
		try {
			s = inputQ.take();
		} catch (InterruptedException ex) {
			Logger.getLogger(NetlogoSocketIOStub.class.getName()).log(Level.SEVERE, null, ex);
		}
		return s;
	}
	public boolean isEmpty() {
		return (inputQ.peek() == null);
	}
	public void write(String msg)
	{	cnx.write(msg);
	}
}
