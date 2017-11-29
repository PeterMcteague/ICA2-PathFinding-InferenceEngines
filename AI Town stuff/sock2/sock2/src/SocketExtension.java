

import org.nlogo.api.*;


/**
 * this extension contains a hook-up to some of the methods provided by
 * Java's String class. For further documentation check the Java API docs.
 * 
 * @author s
 */
public class SocketExtension
    extends org.nlogo.api.DefaultClassManager {
	
	private static NetlogoSocketIOStub comm;

  public void load(org.nlogo.api.PrimitiveManager primManager) {
    primManager.addPrimitive("connect-local",	new ConnectLocal());
	primManager.addPrimitive("connect-distant",	new ConnectDist());
	primManager.addPrimitive("serve-socket",	new Advertise());
	primManager.addPrimitive("is-connected",	new IsConnected());
	primManager.addPrimitive("disconnect",		new Disconnect());
	
	primManager.addPrimitive("clear",		new Clear());
	primManager.addPrimitive("peek",		new Peek());
	primManager.addPrimitive("poll",		new Poll());
	primManager.addPrimitive("read",		new Take());
	primManager.addPrimitive("write",		new Write());
	primManager.addPrimitive("is-empty",	new IsEmpty());
  }


  //===========================================================================
  // extension requirements
  //===========================================================================

    public String getExtensionName() {
      return "sock1";
    }

    public String getNLTypeName() {
      // following comment plagarised from NetLogo table extension...
	  // since this extension only defines one type, we don't
      // need to give it a name; "table:" is enough,
      // "table:table" would be redundant
      return "";
    }

   // exportWorld & importWorld not specified because there is no persistent data



//===========================================================================
// extension primitives
//===========================================================================

//--- Boolean connect( int portNo ) ---------------------------------------

/** connect to a socket via a served port number, return value indicates success/failure */
public static class ConnectLocal extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{Syntax.NumberType()});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      int port = (int)args[0].getIntValue();
	  comm = new NetlogoSocketIOStub(port, SocketConnection.CLIENT);
    }
}

/** connect to a socket via a served port number, return value indicates success/failure */
public static class ConnectDist extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{Syntax.StringType(), Syntax.NumberType()});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      String address = (String)args[0].get();
	  int    port    = (int)args[1].getIntValue();
	  comm = new NetlogoSocketIOStub(address, port, SocketConnection.CLIENT);
    }
}

/** connect to a socket via a served port number, return value indicates success/failure */
public static class Advertise extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{Syntax.NumberType()});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      int port = (int)args[0].getIntValue();
	  comm = new NetlogoSocketIOStub(port, SocketConnection.SERVER);
    }
}

//--- void clear() --------------------------------

/** clear any waiting input from the socket connection */
public static class Clear extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      comm.clear();
    }
}


//--- String peek() --------------------------------

/**
 * Retrieves, but does not remove, the next line of input from the socket.
 * Returns null if there is no input.
 */
public static class Peek extends DefaultReporter
{
    public Syntax getSyntax() {
      return Syntax.reporterSyntax
          (new int[]{},
              Syntax.StringType());
    }

    public String getAgentClassString() { return "OTPL"; }

    public Object report(Argument args[], Context context)
        throws ExtensionException, LogoException {
      return comm.peek();
    }
}

//--- String poll() --------------------------------

/**
 * Retrieves and removes the next input line from the socket.
 * Returns null if there is no input. 
 */
public static class Poll extends DefaultReporter
{
    public Syntax getSyntax() {
      return Syntax.reporterSyntax
          (new int[]{},
              Syntax.StringType());
    }

    public String getAgentClassString() { return "OTPL"; }

    public Object report(Argument args[], Context context)
        throws ExtensionException, LogoException {
      return comm.poll();
    }
}

//--- String take() --------------------------------

/**
 * Retrieves and removes the next input line from the socket.
 * Waits if there is no input. 
 */
public static class Take extends DefaultReporter
{
    public Syntax getSyntax() {
      return Syntax.reporterSyntax
          (new int[]{},
              Syntax.StringType());
    }

    public String getAgentClassString() { return "OTPL"; }

    public Object report(Argument args[], Context context)
        throws ExtensionException, LogoException {
      return comm.take();
    }
}


//--- boolean isConnected() --------------------------------

/**
 * Returns true/false if the socket is connected 
 */
public static class IsConnected extends DefaultReporter
{
    public Syntax getSyntax() {
      return Syntax.reporterSyntax
          (new int[]{},
              Syntax.BooleanType());
    }

    public String getAgentClassString() { return "OTPL"; }

    public Object report(Argument args[], Context context)
        throws ExtensionException, LogoException {
      return comm.isConnected();
    }
}

	
	//--- boolean isEmpty() --------------------------------

/**
 * Returns true/false if the socket is connected 
 */
public static class IsEmpty extends DefaultReporter
{
    public Syntax getSyntax() {
      return Syntax.reporterSyntax
          (new int[]{},
              Syntax.BooleanType());
    }

    public String getAgentClassString() { return "OTPL"; }

    public Object report(Argument args[], Context context)
        throws ExtensionException, LogoException {
      return comm.isEmpty();
    }
}


//--- void write( String msg ) --------------------------------

/** write a line of text onto the socket */
public static class Write extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{Syntax.StringType()});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      String str = (String)args[0].get();
	  comm.write(str);
    }
}

/** write a line of text onto the socket */
public static class Disconnect extends DefaultCommand
{
    public Syntax getSyntax() {
      return Syntax.commandSyntax
          (new int[]{});
    }

    public String getAgentClassString() { return "OTPL"; }

    public void perform(Argument args[], Context context)
        throws ExtensionException, LogoException {
      comm.close();
    }
}
}
