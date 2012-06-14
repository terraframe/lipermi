package net.sf.lipermi;

import java.io.IOException;

import net.sf.lipermi.exception.LipeRMIException;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Server;

public class SandboxServer
{
  private static CallHandler callHandler;
  private static Server server;
  
  public static void main(String[] args) {
    startServer();
  }
  
  public static void startServer() {
    callHandler = new CallHandler();
    AnInterface interfaceImplementation = new AConcrete();
    
    try
    {
      callHandler.registerGlobal(AnInterface.class, interfaceImplementation);
    }
    catch (LipeRMIException e)
    {
      e.printStackTrace();
    }
    
    server = new Server();
    int thePortIWantToBind = 4455;
    
    try
    {
      server.bind(thePortIWantToBind, callHandler);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void stopServer() {
    server.close();
  }
}
