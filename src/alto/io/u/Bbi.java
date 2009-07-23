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
package alto.io.u ;

/**
 * Wrapper for Bbuf exporting standard input API.
 * 
 * @author John Pritchard (john@syntelos.org)
 * 
 * @see Bbuf
 * @see Bbo
 * @since 1.1
 */
public class Bbi
    extends java.io.InputStream 
{

    private Bbuf buf ;

    public Bbi( int capacity){
        super();
        buf = new Bbuf(capacity);
    }
    public Bbi(){
        super();
        buf = new Bbuf();
    }
    public Bbi ( byte[] buf){
        super();
        if ( null == buf)
            throw new IllegalArgumentException("Null or empty input for `Bbi'.");
        else
            this.buf = new Bbuf(buf);
    }
    public Bbi ( Bbuf buf){
        super();
        if ( null == buf)
            throw new IllegalArgumentException("Null or empty input for `Bbi'.");
        else {
            this.buf = buf;
            buf.reset_read();
        }
    }
    /**
     * Read `many' bytes from the input stream `src' into this buffer.
     */
    public Bbi ( java.io.InputStream src, int many) 
        throws java.io.IOException 
    {
        super();
        this.buf = new Bbuf(src,many);

    }

    // Bbi

    /**
     * @return Offset of the last byte read, or negative one before
     * the first byte has been read
     */
    public int offset(){
        return this.buf.offset_read();
    }

    public Bbuf getByteBuffer(){ return buf;}

    public byte[] toByteArray()
        throws java.io.IOException
    {
        return buf.toByteArray();
    }
    public byte[] verbatim(){ return buf.verbatim();}

    public int length()
        throws java.io.IOException
    {
        return buf.length();
    }

    public String toString(){ return buf.toString();}

    /**
     * @param ch last char read, only unread once per read cycle
     */
    public void unread(int ch){
        this.buf.unread(ch);
    }
    public void unreadn(int n){
        this.buf.unread(n);
    }
    public void unread(){
        this.buf.unread();
    }
    // InputStream 

    public int read() throws java.io.IOException {
        return buf.read();
    }
    public int read(byte b[]) throws java.io.IOException {
        return buf.read(b, 0, b.length);
    }
    public int read( byte[] b, int ofs, int len) throws java.io.IOException {
        return buf.read(b, ofs, len);
    }
    public byte[] readMany(int many) throws java.io.IOException {
        return buf.readMany(many);
    }
    public java.lang.String readLine() throws java.io.IOException {
        return buf.readLine();
    }
    public int available() throws java.io.IOException {
        return buf.available();
    }
    public long skip(long n) throws java.io.IOException { 
        if (0L < n && n < Integer.MAX_VALUE)
            return buf.skip_read( (int)n);
        else if (0L != n)
            throw new IllegalArgumentException(String.valueOf(n));
        else
            return 0L;
    }
    public void close() throws java.io.IOException { 
        /*
         * Calling reset_read on closing the input permits the buffer
         * to be read again.
         */
        this.buf.reset_read();
    }
    public void mark(int m){ 
        this.buf.mark_read();
    }
    public void reset() throws java.io.IOException { 
        this.buf.reset_read();
    }
    public boolean markSupported() { 
        return true;
    }
    public byte[] marked(){
        return this.buf.marked_read();
    }
}
