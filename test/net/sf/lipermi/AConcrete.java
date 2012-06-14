package net.sf.lipermi;

import java.io.Serializable;

public class AConcrete implements AnInterface, Serializable
{
  static final long serialVersionUID = 10275539472837495L;
  
  public String str;
  public int integr;
  public boolean bool;
  
  public AConcrete() {
    str = "This is a string";
    integr = 11;
    bool = true;
  }
  
  @Override
  public int callThisMethod(boolean b)
  {
    System.out.println("o dis metd gat kalld");
    
    if (b)
    return 11;
    else return 4;
  }

  @Override
  public void imCool()
  {
    System.out.println("I am kewl!!");
  }
  
  @Override
  public void dontReturnInstantly() {
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
  
  @Override
  public void takeForever()
  {
    try
    {
      synchronized(this) {
        System.out.println("Waiting for 10 seconds...");
        this.wait(10000);
      }
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }
  
  @Override
  public void throwAnException()
  {
    throw new RuntimeException("This exception is coming to you...");
  }

}
