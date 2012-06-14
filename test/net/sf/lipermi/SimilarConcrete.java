package net.sf.lipermi;

import java.io.Serializable;

public class SimilarConcrete implements AnInterface, Serializable
{
  static final long serialVersionUID = 10275539472837495L;
  
  public String str;
  public int integr;
  public boolean bool;
  
  @Override
  public int callThisMethod(boolean b)
  {
    return 8;
  }
  
  @Override
  public void imCool()
  {
    System.out.println("I am similiarly kewl!!");
  }
  
  @Override
  public void takeForever()
  {
    System.out.println("I don't really believe in taking forever");
  }

  @Override
  public void throwAnException()
  {
    throw new RuntimeException("This exception is coming to you...");
  }

  @Override
  public void dontReturnInstantly()
  {
    try
    {
      synchronized(this) {
        System.out.println("Waiting for 1/2 a second...");
        this.wait(500);
      }
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  
}
