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
package alto.sys;

import alto.io.Authentication;
import alto.io.Code;
import alto.io.Check;
import alto.io.Principal;
import alto.io.Uri;
import alto.lang.Type;

/**
 * <p> Collection of I/O interfaces, primarily {@link IO$Edge}. </p>
 * 
 * <p> A member of the IO class may be a socket or file.  One may be
 * open or closed, while the other may not. </p>
 * 
 * @see IO$Edge
 * @author jdp
 * @since 1.5
 */
public interface IO {

    /**
     * One degree of separation in case the super interface is not in
     * the current runtime classpath (client side).
     */
    public interface Location 
        extends javax.tools.JavaFileManager.Location
    {
        /**
         * This kind of location object must never be held reference
         * to.  Typically an object with a short lifespan, or
         * otherwise not necessarily sharable.  Not a simple location
         * identifier object.
         */
        public interface Transient 
            extends Location
        {}
    }

    /**
     * One degree of separation in case the super interface is not in
     * the current runtime classpath (client side).
     */
    public interface FileObject 
        extends javax.tools.JavaFileObject
    {
    }

    /**
     * Interface for classes subclassing java io filter streams.
     */
    public interface Filter {
        /**
         * Interface for classes extending {@link java.io.FilterInputStream}.
         */
        public interface Source 
            extends Filter,
                    alto.io.Input
        {
            public java.io.InputStream getFilterSource();
        }
        /**
         * Interface for classes extending {@link java.io.FilterOutputStream}.
         */
        public interface Target 
            extends Filter,
                    alto.io.Output
        {
            public java.io.OutputStream getFilterTarget();
        }

        public IO.Edge getIOEdge();
    }

    /**
     * @see IO$Edge
     * @see IO$Filter#getIOEdge()
     */
    public interface Source 
        extends IO
    {
        /**
         * 
         */
        public interface Fio 
            extends Source
        {
            public java.lang.String getFioFragment();

            public Uri getFioReference();

            public Type.List getFioTypes();
        }
        /**
         * 
         */
        public interface Parameters
            extends Source
        {
            /**
             * 
             */
            public interface H 
                extends Parameters,
                        IO.H
            {}

            public java.lang.String getParameter(java.lang.String key);

            public java.math.BigInteger getParameterHex(java.lang.String key);

            public java.lang.Number getParameterNumber(java.lang.String key);

            public java.lang.Number getParameterNumber(java.lang.String key, java.lang.Number def);
        }

        public Uri getUri();

        /**
         * @return May be null.  If not, must be closed.
         */
        public java.nio.channels.ReadableByteChannel openChannelReadable()
            throws java.io.IOException;
        /**
         * @return May be null.  If null, get input stream.
         */
        public java.nio.channels.ReadableByteChannel getChannelReadable();
        /**
         * A network request represents both a referenced storage file
         * and in the case of an HTTP PUT, an incoming resource.  This
         * method represents the storage file.
         * 
         * @return Zero for unavailable operation or source not found.
         * Otherwise a positive timestamp value.
         */
        public long lastModified();
        /**
         * HTTP date time
         */
        public java.lang.String lastModifiedString();
        /**
         * A network request represents both a referenced storage file
         * and in the case of an HTTP PUT, an incoming resource.  This
         * method represents the incoming resource.
         * 
         * @return Zero for unavailable operation or source not found.
         * Otherwise a positive timestamp value.
         */
        public long getLastModified();
        /**
         * HTTP date time
         */
        public java.lang.String getLastModifiedString();
        /**
         * Streams from "open" are closed by the user of this interface.
         * @return If null, then output unavailable.  Value by
         * reference should be identical to {@link #openInput()}.
         */
        public java.io.InputStream openInputStream()
            throws java.io.IOException;
        /**
         * Streams from "open" are closed by the user of this interface.
         * @return If null, then output unavailable.  Value by
         * reference should be identical to {@link
         * #openInputStream()}.
         */
        public alto.io.Input openInput()
            throws java.io.IOException;
        /**
         * Streams from "get" are not closed by the user of this interface.
         * @return If null, then use "open".  Value by reference
         * should be identical to {@link #getInput()}.
         */
        public java.io.InputStream getInputStream()
            throws java.io.IOException;
        /**
         * Streams from "get" are not closed by the user of this interface.
         * @return If null, then use "open".  Value by reference
         * should be identical to {@link #getInputStream()}.
         */
        public alto.io.Input getInput()
            throws java.io.IOException;
    }
    /**
     * @see IO$Edge
     * @see IO$Filter#getIOEdge()
     */
    public interface Target 
        extends IO
    {
        public Uri getUri();

        /**
         * @return May be null.  If not, must be closed.
         */
        public java.nio.channels.WritableByteChannel openChannelWritable()
            throws java.io.IOException;
        /**
         * @return May be null. 
         */
        public java.nio.channels.WritableByteChannel getChannelWritable();
        /**
         * @param last Timestamp in milliseconds since Jan 1 1970
         * 00:00h GMT.  Returns silently on no effect.
         * @return Success
         */
        public boolean setLastModified(long last);
        /**
         * Streams from "open" are closed by the user of this interface.
         * @return If null, then input unavailable.  Value by
         * reference should be identical to {@link #openOutput()}.
         */
        public java.io.OutputStream openOutputStream()
            throws java.io.IOException;
        /**
         * Streams from "open" are closed by the user of this interface.
         * @return If null, then input unavailable.  Value by
         * reference should be identical to {@link
         * #openOutputStream()}.
         */
        public alto.io.Output openOutput()
            throws java.io.IOException;
        /**
         * Streams from "get" are not closed by the user of this interface.
         * @return If null, then use "open".  Value by reference
         * should be identical to {@link #getOutput()}
         */
        public java.io.OutputStream getOutputStream()
            throws java.io.IOException;

        /**
         * Streams from "get" are not closed by the user of this interface.
         * @return If null, then use "open".  Value by reference
         * should be identical to {@link #getOutputStream()}
         */
        public alto.io.Output getOutput()
            throws java.io.IOException;
    }
    /**
     * An {@link IO$Source} may implement this interface
     */
    public interface H 
        extends IO
    {
        /**
         * An {@link IO$Target} may implement this interface
         */
        public interface Target 
            extends H
        {
            public void setHeader(java.lang.String name, java.lang.String value);
            public void setHeader(java.lang.String name, java.lang.String[] value);
            public void setHeader(java.lang.String name, int value);
            public void setHeader(java.lang.String name, long value);
            public void setHeaderDate(java.lang.String name, long value);
            public void setHeader(java.lang.String name, Object value);
        }

        public boolean hasHeader(java.lang.String name);
        public Boolean getHeaderBoolean(java.lang.String name);
        public boolean getHeaderBool(java.lang.String name);
        public boolean getHeaderBool(java.lang.String name, boolean defv);
        public Number getHeaderNumber(java.lang.String name);
        public Integer getHeaderInteger(java.lang.String name);
        public int getHeaderInt(java.lang.String name, int defv);
        public Float getHeaderFloat(java.lang.String name);
        public float getHeaderFloat(java.lang.String name, float defv);
        public Long getHeaderLong(java.lang.String name);
        public long getHeaderLong(java.lang.String name, long defv);
        public Object getHeaderObject(java.lang.String name);
        public java.lang.String getHeaderString(java.lang.String name);
        public java.lang.String getHeaderString(java.lang.String name, java.lang.String defv);
        public java.lang.String[] getHeaderStringArray(java.lang.String name);
        public long getHeaderDate(java.lang.String name);
        public java.net.URL getHeaderURL(java.lang.String name);
        public java.lang.Class getHeaderClass(java.lang.String name);

    }

    /**
     * An IO "device" is an edge between content data copying source
     * and target (communication).
     * 
     * @see IO$Filter#getIOEdge()
     */
    public interface Edge
        extends IO.Source,
                IO.Target
    {}

    /**
     * Abstracts the association between a {@link Thread} and a {@link
     * alto.sec.Principal}.
     * 
     * @see Thread
     */
    public interface Context 
        extends IO
    {
        /**
         * Notational parameter for {@link Thread#Context(boolean)}.
         */
        public final static boolean Required = true;
        /**
         * Notational parameter for {@link Thread#Context(boolean)}.
         */
        public final static boolean Peek = false;

        /**
         * The principal is a placeholder ready for authentication
         */
        public boolean isAuthenticating();
        /**
         * (Care with locks)
         */
        public void authenticate() throws java.io.IOException;
        /**
         * The principal has been authenticated
         */
        public boolean isAuthenticated();

        public Authentication getAuthentication();
        /**
         * @return May be null
         */
        public Principal getPrincipal();

        public Principal.Actual pushPrincipal();

        public Principal.Actual popPrincipal();
        /**
         * Employed in the implementation of {@link #push}.  
         * 
         * This is the argument to {@link #push}, and retains the
         * reference 'precursor' which this returns from {@link #pop}.
         */
        @Code(Check.OnlyServer)
        public IO.Context defineContext(IO.Context precursor);
        /**
         * Employed by SX Service to enter an IO Context.  The
         * argument is returned, holding a reference to 'this', it's
         * precursor.
         * 
         * This is the IO Context inherited by the thread from the
         * system.
         */
        @Code(Check.OnlyServer)
        public IO.Context pushContext(IO.Context context);
        /**
         * Employed by SX Service to exit an IO Context.
         * 
         * @return The precursor or former context
         */
        @Code(Check.OnlyServer)
        public alto.sys.IO.Context popContext();
    }
}
