package net.sf.lipermi.handler;

public interface AsynchronousCallHandlerIF
{
  public void onSuccess(Object returnValue);
  public void onFailure(Throwable e);
}
