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

/**
 * <p> Protocol name and version, e.g., <code>"HTTP/1.0"</code>.  Any
 * string in format: <i>{ Name</i> '/' <i>Major</i> '.' <i>Minor
 * }</i>, for Major and Minor as base ten integers and Name a string
 * not containing '/'.  </p>
 * 
 * <p> Note that the empty string represents <code>"HTTP/0.9"</code>.
 * 
 * @author jdp
 * @since 1.5
 */
public final class Protocol 
    extends java.lang.Object
{
    /*
     * (read-only)
     */
    private final static alto.io.u.Objmap Set = new alto.io.u.Objmap();

    /**
     * @param string Protocol identifier string
     * 
     * @exception alto.sys.UnknownProtocolException For input
     * string parsing errors, the exception message is the input
     * string.
     */
    public final static Protocol Instance(java.lang.String string)
        throws alto.sys.UnknownProtocolException
    {
        Protocol protocol = (Protocol)Set.get(string);
        if (null == protocol)
            return new Protocol(string);
        else
            return protocol;
    }
    private final static Protocol Instance(Protocol protocol){
        Set.put(protocol.string,protocol);
        return protocol;
    }

    public final static Protocol HTTP09 = Instance(new Protocol());
    public final static Protocol HTTP10 = Instance(new Protocol("HTTP/1.0"));
    public final static Protocol HTTP11 = Instance(new Protocol("HTTP/1.1"));
    public final static Protocol SDFS10 = Instance(new Protocol("SDFS/1.0"));


    public final java.lang.String string, name;
    public final int major, minor;

    private Protocol(){
        super();
        this.string = "";
        this.name = "HTTP";
        this.major = 0;
        this.minor = 9;
    }
    /**
     * @param string Protocol identifier string
     * 
     * @exception alto.sys.UnknownProtocolException For input
     * string parsing errors, the exception message is the input
     * string.
     */
    public Protocol(java.lang.String string)
        throws alto.sys.UnknownProtocolException
    {
        super();
        this.string = string;
        int idx;
        java.lang.String substring;
        if (string.startsWith("HTTP/")){
            this.name = "HTTP";
            substring = string.substring("HTTP/".length());
        }
        else {
            idx = string.indexOf('/');
            if (1 > idx)
                throw new alto.sys.UnknownProtocolException(string);
            else {
                this.name = string.substring(0,idx);
                substring = string.substring(idx+1);
            }
        }
        idx = substring.indexOf('.');
        if (1 > idx)
            throw new alto.sys.UnknownProtocolException(string);
        else {
            try {
                java.lang.String majorS = substring.substring(0,idx);
                this.major = java.lang.Integer.parseInt(majorS);
                java.lang.String minorS = substring.substring((idx+1));
                this.minor = java.lang.Integer.parseInt(minorS);
            }
            catch (java.lang.ArrayIndexOutOfBoundsException exc){
                throw new alto.sys.UnknownProtocolException(string,exc);
            }
            catch (java.lang.NumberFormatException exc){
                throw new alto.sys.UnknownProtocolException(string,exc);
            }
        }
    }


    public java.lang.String getName(){
        return this.name;
    }
    public int getVersionMajor(){
        return this.major;
    }
    public int getVersionMinor(){
        return this.minor;
    }
    public java.lang.String toString(){
        return this.string;
    }
    public int hashCode(){
        return this.string.hashCode();
    }
    /**
     * Case sensitive string protocol name and version identifier
     * equivalence.
     */
    public boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else 
            return this.string.equals(ano.toString());
    }
}
