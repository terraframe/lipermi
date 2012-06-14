/*
 * LipeRMI - a light weight Internet approach for remote method invocation
 * Copyright (C) 2006  Felipe Santos Andrade
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * For more information, see http://lipermi.sourceforge.net/license.php
 * You can also contact author through lipeandrade@users.sourceforge.net
 */

package net.sf.lipermi.net;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import net.sf.lipermi.exception.LipeRMITimeoutException;
import net.sf.lipermi.handler.AsynchronousCallHandlerIF;
import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.handler.CallProxy;
import net.sf.lipermi.handler.ConnectionHandler;
import net.sf.lipermi.handler.IConnectionHandlerListener;
import net.sf.lipermi.handler.filter.DefaultFilter;
import net.sf.lipermi.handler.filter.IProtocolFilter;


/**
 * The LipeRMI client.
 * Connects to a LipeRMI Server in a address:port
 * and create local dynamic proxys to call remote
 * methods through a simple interface.
 *
 * @author lipe
 * @date   05/10/2006
 *
 * @see    net.sf.lipermi.handler.CallHandler
 * @see    net.sf.lipermi.net.Server
 */
public class Client {

    private Socket socket;

    private ConnectionHandler connectionHandler;

    private final IConnectionHandlerListener connectionHandlerListener = new IConnectionHandlerListener() {
        public void connectionClosed() {
            for (IClientListener listener : listeners)
                listener.disconnected();
        }
    };

    private List<IClientListener> listeners = new LinkedList<IClientListener>();

    public void addClientListener(IClientListener listener) {
        listeners.add(listener);
    }

    public void removeClientListener(IClientListener listener) {
        listeners.remove(listener);
    }

    public Client(String address, int port, CallHandler callHandler) throws IOException {
        this(address, port, callHandler, new DefaultFilter());
    }

    public Client(String address, int port, CallHandler callHandler, IProtocolFilter filter) throws IOException {
        socket = new Socket(address, port);
        connectionHandler = ConnectionHandler.createConnectionHandler(socket, callHandler, filter, connectionHandlerListener);
    }

    public void close() throws IOException {
        socket.close();
    }

    public Object getGlobal(Class<?> clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new CallProxy(connectionHandler));
    }
    
    /**
     * Invokes a method on a remote object in an asynchronous manner.
     * 
     * @param handler Your custom handler for when the response returns from the server. Your handler must implement the AsynchronousCallHandlerIF interface;
     * @param timeout How long to wait before the call to the server times out, in milliseconds. If the invocation times out, a LipeRMITimeoutException will be passed as an argument to your onFailure handler.
     * @param remoteObject An object on a remote server, obtained by calling Client.getGlobal.
     * @param method The java reflection method to invoke on the remote object.
     * @param arguments Arguments for the method invocation.
     */
    public void invokeAsynchronously(final AsynchronousCallHandlerIF handler, final long timeout, final Object remoteObject, final Method method, final Object... arguments) {
      final Runnable workerRunnable = new Runnable() {
        @Override
        public void run() {
          try
          {
            Object rtn = method.invoke(remoteObject, arguments);
            handler.onSuccess(rtn);
          }
          catch (Exception e) {
            if (e instanceof InvocationTargetException && e.getCause() instanceof RuntimeException && e.getCause().getCause() instanceof InterruptedException) {
              handler.onFailure(new LipeRMITimeoutException("The asynchronous invocation has timed out."));
            }
            else {
              handler.onFailure(e);
            }
          }
        }
      };
      
      final Runnable timeoutRunnable = new Runnable() {
        private Thread workerThread = new Thread(workerRunnable, "Lipe RMI Asynchronous Dispatcher");
        
        @Override
        public void run()
        {
          try
          {
            workerThread.setDaemon(true);
            workerThread.start();
            
            workerThread.join(timeout);
            
            if (workerThread.isAlive()) {
              workerThread.interrupt();
            }
          }
          catch (InterruptedException e)
          {
            System.out.println("Interrupted excpetion");
          }
        }
      };
      
      Thread t = new Thread(timeoutRunnable, "Lipe RMI Asynchronous Timeout Handler");
      t.setDaemon(true);
      t.start();
    }
}

// vim: ts=4:sts=4:sw=4:expandtab
