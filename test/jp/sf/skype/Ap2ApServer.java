/*******************************************************************************
 * Copyright (c) 2006 Koji Hisano <hisano@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Koji Hisano - initial API and implementation
 *******************************************************************************/
package jp.sf.skype;

public class Ap2ApServer {
    public static void main(String[] args) throws Exception {
        Skype.setDebug(true);
        final Application application = Skype.addApplication("AP2AP");
        final Object lock = new Object();
        application.addApplicationListener(new ApplicationAdapter() {
            public void connected(final Stream stream) {
                stream.addCommunicationListener(new StreamAdapter() {
                    @Override
                    public void textReceived(String text) {
                        try {
                            stream.write(text);
                        } catch (SkypeException e) {
                            synchronized (lock) {
                                lock.notify();
                            }
                            System.err.println("couldn't respond to " + stream.getFriend().getId() + " text");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void datagramReceived(String datagram) {
                        try {
                            stream.send(datagram);
                        } catch (SkypeException e) {
                            System.err.println("couldn't respond to " + stream.getFriend().getId() + " datagram");
                            e.printStackTrace();
                        } finally {
                            synchronized (lock) {
                                lock.notify();
                            }
                        }
                    }
                });
            }
        });
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
            }
        }
        application.finish();
    }
}