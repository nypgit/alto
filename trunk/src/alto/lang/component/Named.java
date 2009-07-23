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
package alto.lang.component;

import alto.hash.Function;
import alto.lang.Component;
import alto.lang.Sio;
import alto.io.Input;
import alto.io.Output;
import alto.io.u.Utf8;

import java.io.IOException;

/**
 * The last or terminal component of an address is an item
 * version.  This component of an address may be a {@link
 * #IsValid(int) name string}, in which case it will be
 * represented by an instance of this class.
 */
public abstract class Named 
    extends java.lang.Object
    implements Component.Named
{

    public final static boolean IsValid(byte[] scan){
        int count = scan.length;
        for (int cc = 0; cc < count; cc++){
            if (!IsValid(scan[cc]))
                return false;
        }
        return (0 < count);
    }
    public final static boolean IsValid(java.lang.String identifier){
        char[] scan = identifier.toCharArray();
        return IsValid(scan);
    }
    public final static boolean IsValid(char[] scan){
        int count = scan.length;
        for (int cc = 0; cc < count; cc++){
            if (!IsValid(scan[cc]))
                return false;
        }
        return (0 < count);
    }
    /**
     * Accepts character values in the ASCII/UTF-8 class: hyphen,
     * dot, zero through nine, 'A' through 'Z', underscore, 'a'
     * through 'z' for valid terminal identifiers.  Note that
     * hexidecimal is a smaller but overlapping character class.
     */
    public final static boolean IsValid(int ch){
        switch(ch){
        case '-':
        case '.':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case '_':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            return true;
        default:
            return false;
        }
    }



    private final java.lang.String string;
    private final byte[] stringBits;
    private final int stringHash32;

    private Function hashFunction;


    public Named(byte[] bits){
        super();
        if (null != bits){
            this.string = new java.lang.String(Utf8.decode(bits));
            this.stringBits = bits;
            this.stringHash32 = Function.Xor.Hash32(bits);
        }
        else
            throw new java.lang.IllegalArgumentException("Null argument 'byte[] bits'.");
    }
    public Named(java.lang.String string){
        super();
        if (null != string){
            this.string = string;
            this.stringBits = Utf8.encode(string);
            this.stringHash32 = Function.Xor.Hash32(this.stringBits);
        }
        else
            throw new java.lang.IllegalArgumentException("Null argument 'String string'.");
    }
    public Named(Function H, java.lang.String string){
        super();
        if (null != string){
            this.string = string;
            this.stringBits = Utf8.encode(string);
            this.stringHash32 = H.hash32(this.stringBits);
            this.hashFunction = H;
        }
        else
            throw new java.lang.IllegalArgumentException("Null argument 'String string'.");
    }


    public final byte[] toByteArray(){
        return this.stringBits;
    }
    public final byte[] getBuffer(){
        return this.stringBits;
    }
    public final int getBufferLength(){
        return this.stringBits.length;
    }
    public void sioRead(Input in) 
        throws IOException 
    {
        throw new UnsupportedOperationException();
    }
    public void sioWrite(Output out) 
        throws IOException
    {
        byte[] content = this.toByteArray();
        Sio.Field.Write(content,out);
    }
    public boolean hasHashFunction(){
        return (null != this.hashFunction);
    }
    public Function getHashFunction(){
        return this.hashFunction;
    }
    public java.lang.String toString(){
        return this.string;
    }
    public int hashCode(){
        return this.stringHash32;
    }
    public boolean equals(java.lang.Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano instanceof java.lang.String)
            return this.string.equals( (java.lang.String)ano);
        else
            return this.string.equals(ano.toString());
    }
    public int compareTo(Component ano){
        if (this == ano)
            return 0;
        else if (null == ano)
            return 1;
        else if (ano instanceof Named)
            return this.string.compareTo(ano.toString());
        else
            return 1;
    }
}
