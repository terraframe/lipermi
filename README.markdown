LipeRMI
=======

LipeRMI is a light-weight RMI implementation for Java. This fork adds asynchronous RMI functionality and has been well tested on Android and will be used in a production environment. This project is not limited to usage only on Android, it should work on any standard JVM.

This project was forked from:
https://github.com/jorgenpt/lipermi

Which, in turn, was forked from the original project, which can be found here:

 * http://lipermi.sourceforge.net
 * http://www.sourceforge.net/project/lipermi


Asynchronous RMI
-------
The only real change to the original LipeRMI API is the addition of a new method on Client:
    client.invokeAsynchronously(handler, timeout, remoteObject, method);

If the invocation times out, a LipeRMITimeoutException will be passed as an argument to your onFailure handler.

Heres an example of how to make an asynchronous call:

    // Create our callback handler
    AsynchronousCallHandlerIF handler = new AsynchronousCallHandlerIF() {
        @Override
        public void onSuccess(Object returnValue)
        {
          // Put your success logic here
        }
        
        @Override
        public void onFailure(Throwable e)
        {
          // Put what you want to do on failure here
        }
      };
    
    // Configuration Values:
    String remoteHost = "localhost";
    int port = 4455;
    int timeout = 5000;
    
    // Instantiate standard LipeRMI objects
    CallHandler callHandler = new CallHandler();
    Client client = new Client(remoteHost, port, callHandler);
    
    // Get an instance of a remote object using standard LipeRMI method getGlobal
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    // Use java reflection to get a handle to the method we want to call on the remote object
    Method method = AnInterface.class.getMethod("nameOfMethodToCallOnInterface");
    
    // Invoke the method asynchronously.
    client.invokeAsynchronously(handler, timeout, remoteObject, method);


The timoeut is in milliseconds.


JavaDoc
-------

You can find Jorgen's JavaDoc here: http://jorgenpt.github.com/lipermi/
However, this javadoc is not updated to include documentation for the asynchronous additions.