package net.sf.lipermi;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import net.sf.lipermi.exception.LipeRMITimeoutException;
import net.sf.lipermi.handler.AsynchronousCallHandlerIF;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsynchronousTests
{
  private CountDownLatch lock;
  
  private static CallHandler callHandler;
  private static Client client;
  private static String remoteHost = "localhost";
  private static int port = 4455;
  
  @BeforeClass
  public static void onlyOnceBefore() throws IOException {
    SandboxServer.startServer();
    callHandler = new CallHandler();
    client = new Client(remoteHost, port, callHandler);
  }
  
  @AfterClass
  public static void onlyOnceAfter() throws IOException {
    SandboxServer.stopServer();
    client.close();
  }
  
  private AsynchronousCallHandlerIF handler = new AsynchronousCallHandlerIF() {
    @Override
    public void onSuccess(Object returnValue)
    {
      releaseLock();
    }
    
    @Override
    public void onFailure(Throwable e)
    {
      throw new RuntimeException(e);
    }
  };
  
  AsynchronousCallHandlerIF exceptionHandler = new AsynchronousCallHandlerIF() {
    @Override
    public void onSuccess(Object returnValue)
    {
      throw new RuntimeException("This test was supposed to fail.");
    }
    
    @Override
    public void onFailure(Throwable e)
    {
      assertTrue(e instanceof InvocationTargetException && e.getCause() instanceof RuntimeException);
      releaseLock();
    }
  };
  
  @Test
  public void testInstantCall() throws IOException, SecurityException, NoSuchMethodException {
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    Method m = AnInterface.class.getMethod("imCool");
    
    lock = new CountDownLatch(1);
    client.invokeAsynchronously(handler, 5000, remoteObject, m);
    waitOnCallback();
  }
  
  @Test
  public void testDontReturnInstantly() throws IOException, SecurityException, NoSuchMethodException {
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    Method m = AnInterface.class.getMethod("dontReturnInstantly");
    
    lock = new CountDownLatch(1);
    client.invokeAsynchronously(handler, 5000, remoteObject, m);
    waitOnCallback();
  }
  
  @Test
  public void testTimeout() throws IOException, SecurityException, NoSuchMethodException {
    AsynchronousCallHandlerIF timeoutHandler = new AsynchronousCallHandlerIF() {
      @Override
      public void onSuccess(Object returnValue)
      {
        throw new RuntimeException("This test was supposed to fail.");
      }
      
      @Override
      public void onFailure(Throwable e)
      {
        assertTrue(e instanceof LipeRMITimeoutException);
        releaseLock();
      }
    };
    
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    Method m = AnInterface.class.getMethod("takeForever");
    
    lock = new CountDownLatch(1);
    client.invokeAsynchronously(timeoutHandler, 1000, remoteObject, m);
    waitOnCallback();
  }
  
  @Test
  public void testException() throws SecurityException, NoSuchMethodException {
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    Method m = AnInterface.class.getMethod("throwAnException");
    
    lock = new CountDownLatch(1);
    client.invokeAsynchronously(exceptionHandler, 5000, remoteObject, m);
    waitOnCallback();
  }
  
  @Test
  public void testMultipleSimultaneous() throws SecurityException, NoSuchMethodException {
    AnInterface remoteObject = (AnInterface) client.getGlobal(AnInterface.class);
    
    Method m = AnInterface.class.getMethod("dontReturnInstantly");
    Method m2 = AnInterface.class.getMethod("imCool");
    Method m3 = AnInterface.class.getMethod("throwAnException");
    
    lock = new CountDownLatch(3);
    client.invokeAsynchronously(handler, 5000, remoteObject, m);
    client.invokeAsynchronously(handler, 5000, remoteObject, m2);
    client.invokeAsynchronously(exceptionHandler, 5000, remoteObject, m3);
    waitOnCallback();
  }
  
  private void releaseLock() {
    //synchronized(lock) {
      lock.countDown();
    //}
  }
  
  private void waitOnCallback() {
    //synchronized(lock) {
      try
      {
        lock.await();
      }
      catch (InterruptedException e)
      {
        throw new RuntimeException(e);
      }
    //}
  }
  
}
