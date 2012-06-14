package net.sf.lipermi.exception;

/**
 * A LipeRMI Exception returned when an asynchronous request times out.
 *
 * @author Richard Rowlands
 * @date   6/13/2012
 */
public class LipeRMITimeoutException extends RuntimeException
{
  private static final long serialVersionUID = 2381189819968452319L;

  public LipeRMITimeoutException() {
    super();
  }
  
  public LipeRMITimeoutException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public LipeRMITimeoutException(String message) {
      super(message);
  }
  
  public LipeRMITimeoutException(Throwable cause) {
      super(cause);
  }
}
