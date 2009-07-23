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

import alto.io.u.Hex;

import java.net.URL;

/**
 * <p> Element of {@link Headers} list. </p>
 * 
 * @see Headers
 * @author jdp
 * @since 1.5
 */
public interface Header {

    /**
     * Header classes implementing this interface will have this
     * method called immediately before toString on writing headers to
     * output.  Implementors reconstruct stale strings.
     */
    public interface Update {

        public void update();
    }

    /**
     * Authentication moved into lang/Headers so that any Headers
     * object could be employed as a client authentication context.
     */
    public interface SAuth {

        public final static java.lang.String Version   = "SAuth";
        public final static java.lang.String UID       = "SAuth-UID";
        public final static java.lang.String Nonce     = "SAuth-Nonce";
        public final static java.lang.String Signature = "SAuth-Signature";
    }
    public interface SDFS {

        public final static java.lang.String Version    = "SDFS";
        public final static java.lang.String MID        = "SDFS-MID";
        public final static java.lang.String TTL        = "SDFS-TTL";
        public final static java.lang.String Affinity   = "SDFS-Affinity";
        public final static java.lang.String Method     = "SDFS-Method";
        public final static java.lang.String MSig       = "SDFS-MSig";
        public final static java.lang.String Notify     = "SDFS-Notify";
    }
    public interface Http {

        public final static java.lang.String Date              = "Date";
        public final static java.lang.String Location          = "Location";
        public final static java.lang.String Host              = "Host";
        public final static java.lang.String UserAgent         = "User-Agent";
        public final static java.lang.String Server            = "Server";
        public final static java.lang.String Method            = "Method";
        public final static java.lang.String Connection        = "Connection";

        /**
         * Request to authenticate
         */
        public final static java.lang.String Authorization     = "Authorization";
        /**
         * Response header for authentication failure.
         */
        public final static java.lang.String WWWAuthenticate   = "WWW-Authenticate";

        public final static java.lang.String ContentType       = "Content-Type";
        public final static java.lang.String ContentLength     = "Content-Length";
        public final static java.lang.String ContentEncoding   = "Content-Encoding";
        public final static java.lang.String ContentLocation   = "Content-Location";
        public final static java.lang.String ContentRange      = "Content-Range";
        public final static java.lang.String ContentMD5        = "Content-MD5";
        public final static java.lang.String ETag              = "ETag";
        public final static java.lang.String LastModified      = "Last-Modified";
        public final static java.lang.String IfModifiedSince   = "If-Modified-Since";
        public final static java.lang.String IfUnmodifiedSince = "If-Unmodified-Since";
        public final static java.lang.String IfNoneMatch       = "If-None-Match";
        public final static java.lang.String Expires           = "Expires";

        public final static java.lang.String Destination       = "Destination";
        public final static java.lang.String VersionTag        = "X-Version-Tag";
        public final static java.lang.String XPrincipal        = "X-Principal";
    }
    /**
     * 
     */
    public static class Malformed
        extends alto.sys.Error.State
    {
        public Malformed(java.lang.String line){
            super(line);
        }
        public Malformed(alto.sys.Reference ref, Malformed exc){
            super(ref.toString(),exc);
        }
    }


    public java.lang.String getName();

    public java.lang.String getValue();

    public boolean hasParsed();

    public Object getParsed();

    public java.lang.String[] getParsedStringArray();

    public Boolean getParsedBoolean();

    public Number getParsedNumber();

    public Integer getParsedInteger();

    public Float getParsedFloat();

    public Long getParsedLong();

    public java.math.BigInteger getParsedHex();

    public URL getParsedURL();

    public java.lang.Class getParsedClass();

    public Type getParsedType();

    public alto.sys.Reference getParsedReference();

    public void setParsed(Object p);

    public long getDate();

    public void writeln(alto.io.Output out)
        throws java.io.IOException;

    public java.lang.String toString();

    /**
     * <p> Header line equivalence, hash of name and value in header
     * line format. </p>
     */
    public int hashCode();

    /**
     * <p> Case sensitive header line string equivalence. </p>
     */
    public boolean equals(java.lang.Object ano);

    public boolean equals(java.lang.String name, java.lang.String value);

}
