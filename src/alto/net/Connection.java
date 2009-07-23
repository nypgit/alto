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
package alto.net;

import alto.io.MemberOf;
import alto.sys.Reference;

/**
 * <p> Interface to connections both plain {@link
 * java.net.URLConnection} and {@link java.net.HttpURLConnection}.
 * </p>
 * 
 * @author jdp
 * @since 1.5
 */
@MemberOf(java.net.URLConnection.class)
public interface Connection 
    extends alto.sys.Lock.Semaphore
{

    public Reference getReference();

    public void setReference(Reference ref);

    public java.lang.String getHost();

    public java.lang.String getPath();

    public int getPort();
    /**
     * <p> Called to initialize the connection for I/O.  </p>
     * @see #release()
     * @see #disconnect()
     */
    public void connect()
        throws java.io.IOException;
    /**
     * <p> Called subsequent to connect, after an I/O process sequence
     * has completed, when another call to connect will follow.  </p>
     */
    public void release();
    /**
     * <p> Called subsequent to connect, when no further use of this
     * connection is possible.  To destroy and discard. </p>
     */
    public void disconnect();

    public java.io.InputStream getInputStream() throws java.io.IOException;

    public java.io.OutputStream getOutputStream() throws java.io.IOException;

    public boolean isShm();
    /**
     * <p> SHM connections are only rarely disconnected. </p>
     */
    public boolean isNotShm();

    public boolean isOpen();

    public boolean isNotOpen();
}
