package net.sf.lipermi;

import java.io.IOException;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

public class SandboxClient
{
  public static void main(String[] args) {
    CallHandler callHandler = new CallHandler();
    String remoteHost = "localhost";
    int portWasBinded = 4455;
    
    try
    {
      Client client = new Client(remoteHost, portWasBinded, callHandler);
      
      AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
      
      remoteObject.callThisMethod(true);
      remoteObject.imCool();
      
      System.out.println("Calling take forever...");
      remoteObject.takeForever();
      System.out.println("Done calling take forever");
      
      remoteObject.throwAnException();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
