/*
 * Copyright (C) 1998, 2009  John Pritchard and the Alto Project Group.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package alto.lang;

import alto.io.Input;
import alto.io.Output;

/**
 * <p> Abstracts all socket classes, TCP, UDP, MDP and SRV for the
 * benefit of socket related code sharing.  The SRV socket has no I/O
 * streams, but accepts TCP sockets.  The TCP socket is a (unicast)
 * connection to a remote host.  Note that the UDP (Unicast &amp;
 * Broadcast) and MDP (Multicast) sockets are senders and receivers.
 * </p>
 * 
 * <p> This socket will also wrap an SHM connection.  Although in this
 * case the use needs to employ the {@link alto.sys.Lock$Semaphore}
 * locking protocol.  Note that the SHM connection has a size
 * boundary, default 2048 bytes. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public interface Socket
    extends Closeable,
            alto.sys.Lock.Semaphore,
            alto.sys.IO.Edge
{
    public final static alto.io.Uri UriDefault = new alto.io.u.Uri("socket:default");

    /*
     * Need 'server_name' handshake extension from TLS for stateless
     * virtual hosting.  See RFC 3546.
     */
    public final static java.lang.String[] SSL_PROT = {"TLSv1","SSLv2Hello"};


    public final static int TYPE_SRV = 0x0;
    public final static int TYPE_TCP = 0x1;//(client, server accepted or client originated)
    public final static int TYPE_UDP = 0x2;
    public final static int TYPE_MDP = 0x6;//(UDP&MDP) != (TCP)
    public final static int TYPE_SHM = 0x8;


    public int getType();

    public boolean isService();

    public boolean isNotService();

    public boolean isTcpServer();

    public boolean isNotTcpServer();

    public boolean isTcpClient();

    public boolean isNotTcpClient();

    public boolean isConnection();

    public boolean isNotConnection();

    public boolean isUdp();

    public boolean isNotUdp();

    public boolean isPeer();

    public boolean isNotPeer();

    public boolean isMdp();

    public boolean isNotMdp();

    public boolean isMulticast();

    public boolean isNotMulticast();

    /**
     * When true, requires use of {@link
     * alto.sys.Lock$Semaphore} operations, defined here.
     */
    public boolean isShm();

    public boolean isNotShm();

    public boolean isSecure();

    /**
     * @return This is secure, and the cipher suite employs a
     * connection privacy stage.
     */
    public boolean isSecurePrivate();

    /**
     * @return The value from {@link #getSecureRemote()} is not null.
     */
    public boolean isSecureAuthenticated();

    /**
     * @return Never null, otherwise Bug exception.
     * @see #isSecure()
     */
    public javax.net.ssl.SSLSession getSecureSession();

    public javax.net.ssl.SSLSession getSecureSession(boolean exc);

    /**
     * @return Null for unauthenticated client.
     * @see #isSecure()
     */
    public java.security.Principal getSecureRemote();

    /**
     * @return Null for unauthenticated client.
     * @see #isSecure()
     */
    public java.security.cert.Certificate[] getSecureRemoteCertificates();

    /**
     * @return Never null, otherwise Bug exception.
     * @see #isSecure()
     */
    public java.security.Principal getSecureHost();

    public java.net.InetAddress getInetAddress();

    public java.net.InetAddress getLocalAddress();

    public int getPort();

    public int getLocalPort();

    public java.net.SocketAddress getRemoteSocketAddress();

    public java.net.SocketAddress getLocalSocketAddress();

    public java.nio.channels.SocketChannel getChannelTcp();

    public java.nio.channels.DatagramChannel getChannelUdp();

    public java.lang.String[] getEnabledCiphers();

    public java.lang.String getEnabledCiphersString();

    /**
     * Overridden in {@link org.syntels.sx.Socket} to return a new
     * instance of that class.
     * @param client May be an instance of {@link javax.net.ssl.SSLSocket}
     */
    public Socket acceptNewSocket(java.net.Socket client)
        throws java.io.IOException;

    public Socket connect()
        throws java.io.IOException;

    public void handshake()
        throws java.io.IOException;

    public boolean isQueued();

    public Socket enqueue();

    public Socket dequeue();

    public Socket release();

    public Socket accept()
        throws java.io.IOException;

    public void send(java.net.DatagramPacket p)
        throws java.io.IOException;

    public java.net.DatagramPacket receive(java.net.DatagramPacket p)
        throws java.io.IOException;

    public java.io.InputStream getInputStreamTcp() 
        throws java.io.IOException;

    public java.io.OutputStream getOutputStreamTcp() 
        throws java.io.IOException;

    public java.io.InputStream getInputStreamShm() 
        throws java.io.IOException;

    public java.io.OutputStream getOutputStreamShm() 
        throws java.io.IOException;

    public void setTcpNoDelay(boolean on) 
        throws java.net.SocketException;

    public boolean getTcpNoDelay() 
        throws java.net.SocketException;

    public void setSoLinger(boolean on, int linger) 
        throws java.net.SocketException;

    public int getSoLinger() 
        throws java.net.SocketException;

    public void setSoTimeout(int timeout) 
        throws java.net.SocketException;

    public int getSoTimeout() 
        throws java.io.IOException;

    public void setSendBufferSize(int size)
        throws java.net.SocketException;

    public int getSendBufferSize() 
        throws java.net.SocketException;

    public void setReceiveBufferSize(int size)
        throws java.net.SocketException;

    public int getReceiveBufferSize()
        throws java.net.SocketException;

    public void setKeepAlive(boolean on) 
        throws java.net.SocketException;

    public boolean getKeepAlive() 
        throws java.net.SocketException;

    public void setTrafficClass(int tc) 
        throws java.net.SocketException;

    public int getTrafficClass() 
        throws java.net.SocketException;

    public void setReuseAddress(boolean on) 
        throws java.net.SocketException;

    public boolean getReuseAddress() 
        throws java.net.SocketException;

    public void close() 
        throws java.io.IOException;

    public boolean isConnected();

    public boolean isNotConnected();

    public boolean isBound();

    public boolean isClosed();

    public boolean isNotClosed();
}
