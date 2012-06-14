LipeRMI
=======

LipeRMI is a light-weight RMI implementation for Java. This fork adds asynchronous RMI functionality, has been well tested on Android, and will be used in a production environment. This project is not limited to usage only on Android, it should work on any standard JVM.

This project is licensed under LGPL.

A jar is available for easy download in the downloads section of my Github project, you don't have to checkout my entire project.

This project was forked from:
https://github.com/jorgenpt/lipermi

Which, in turn, was forked from the original project, which can be found here:

 * http://lipermi.sourceforge.net
 * http://www.sourceforge.net/project/lipermi

Asynchronous RMI
-------
The only real change to the original LipeRMI API is the addition of a new method on Client. From the updated Javadoc:

    public void invokeAsynchronously(AsynchronousCallHandlerIF handler,
                                     long timeout,
                                     java.lang.Object remoteObject,
                                     java.lang.reflect.Method method,
                                     java.lang.Object... arguments)
                                     
    Invokes a method on a remote object in an asynchronous manner.
    
    Parameters:
      handler - Your custom handler for when the response returns from the server. Your handler must implement the AsynchronousCallHandlerIF interface;
      timeout - How long to wait before the call to the server times out, in milliseconds. If the invocation times out, a LipeRMITimeoutException will be passed as an argument to your onFailure handler.
      remoteObject - An object on a remote server, obtained by calling Client.getGlobal.
      method - The java reflection method to invoke on the remote object.
      arguments - Arguments for the method invocation.


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


JavaDoc
-------
An updated javadoc can be found here: http://terraframe.github.com/lipermi/

Contact
-------
If you need to contact me my email address is:
    l2.rowlands@gmail.com