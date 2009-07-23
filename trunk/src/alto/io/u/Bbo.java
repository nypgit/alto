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
 * Wrapper for Bbuf exporting standard output API plus
 * others.
 * 
 * @author John Pritchard (john@syntelos.org)
 * 
 * @see Bbuf
 * @see Bbi
 * @since 1.1
 */
public class Bbo extends java.io.OutputStream {

    /**
     * Newline terminal is CRLF
     */
    protected final static char[] CRLF = {
        '\r',
        '\n'
    };
    private final static byte[] CRLF_BYTE = {
        (byte)'\r',
        (byte)'\n'
    };

    /**
     * <p> Scans output replacing lone LF with CRLF.  This is slow and
     * used only for receivers absolutely intolerant of any lone LFs
     * (QMail).</p>
     * 
     * @author jdp
     */
    public static class crlf extends Bbo {

        private final static byte LF = (byte)'\n';

        private final static byte CR = (byte)'\r';

        boolean require = true;

        public crlf( int capacity){
            super(capacity);
        }
        public crlf(){
            super();
        }
        public crlf ( Bbuf buf){
            super(buf);
        }
        public void write ( int b) throws java.io.IOException {
            switch(b){
            case CR:
                this.require = false;
                break;
            case LF:
                if (this.require)
                    super.write(CR);
                this.require = true;
                break;
            default:
                this.require = true;
                break;
            }
            super.write(b);
        }
        public void write ( byte[] b) throws java.io.IOException {
            this.write(b, 0, b.length);
        }
        public void write ( byte[] b, int ofs, int len) throws java.io.IOException {
            byte ch;
            for (int cc = ofs, mark = ofs; cc < len; cc++){
                ch = b[cc];
                switch (ch){
                case CR:
                    this.require = false;
                    super.write(ch);
                    break;

                case LF:
                    if (this.require)
                        super.write(CR);
                    super.write(ch);
                    this.require = true;
                    break;
                default:
                    this.require = true;
                    super.write(ch);
                    break;
                }
            }
        }
    }


    private Bbuf buf ;

    public Bbo( int capacity){
        super();
        buf = new Bbuf(capacity);
    }
    public Bbo(){
        super();
        buf = new Bbuf();
    }
    public Bbo ( Bbuf buf){
        super();
        if ( null == buf)
            throw new IllegalArgumentException("Null argument to `new Bbo(Bbuf)'.");
        else {
            this.buf = buf;
            buf.reset_write();
        }
    }

    // OutputStream

    public void write ( int b) throws java.io.IOException {
        buf.write(b);
    }
    public void write ( byte[] b) throws java.io.IOException {
        buf.write(b, 0, b.length);
    }
    public void write ( byte[] b, int ofs, int len) throws java.io.IOException {
        buf.write(b, ofs, len);
    }
    public void flush() throws java.io.IOException {
    }
    public void close() throws java.io.IOException {
        /*
         * Don't reset_write on closing output, because it causes the
         * buffer to look empty.
         */
    }

    // Bbuf

    /**
     * @return Offset of the last byte written, or negative one before
     * the first byte has been written
     */
    public int offset(){
        return this.buf.offset_write();
    }

    public Bbuf getByteBuffer(){ return buf;}

    public int length()
        throws java.io.IOException
    {
        return buf.length();
    }

    public void reset(){ 
        this.buf.reset_write();
    }

    public byte[] toByteArray()
        throws java.io.IOException
    {
        return buf.toByteArray();
    }

    /**
     * @see #length()
     * @return Internal buffer including optimistic extent (null tail)
     */
    public byte[] verbatim(){ return buf.verbatim();}

    public String toString(){ return buf.toString();}

    public void mark(){
        this.buf.mark_write();
    }
    public byte[] marked(){
        return this.buf.marked_write();
    }
    public boolean markSupported(){
        return true;
    }
    
    /**
     * Write to format UTF-8
     * @param ch Output character to format encoding
     */
    public void print(char ch)
        throws java.io.IOException
    {
        if (ch < 0x80)
            this.write(ch);
        else {
            char[] cary = new char[]{ch};
            byte[] bary = Utf8.encode(cary);
            this.write(bary,0,bary.length);
        }
    }
    /**
     * @param ch Output character to format encoding
     * @param many Repeat output this many times
     */
    public void nprint(char ch, int many)
        throws java.io.IOException
    {
        if (ch < 0x80)
            this.buf.nwrite((byte)(ch & 0xff),many);
        else {
            char[] cary = new char[]{ch};
            byte[] bary = Utf8.encode(cary);
            for (int cc = 0; cc < many; cc++)
                this.write(bary,0,bary.length);
        }
    }
    /**
     * Write to format UTF-8
     * @param string Output data to format encoding
     */
    public void print(String string)
        throws java.io.IOException
    {
        if (null != string){
            char[] cary = string.toCharArray();
            if (0 < cary.length){
                byte[] bary = Utf8.encode(cary);
                this.write(bary,0,bary.length);
            }
        }
    }
    /**
     * Write to format UTF-8
     * @param indent Indentation level
     * @param string Output data to format encoding
     */
    public void print(int indent, String string)
        throws java.io.IOException
    {
        this.nprint(' ',indent);
        this.print(string);
    }
    /**
     * Write to format UTF-8
     * @param ch Output character to format encoding 
     */
    public void println(char ch)
        throws java.io.IOException
    {
        char[] cary = alto.io.u.Chbuf.cat(ch,CRLF);
        byte[] bary = alto.io.u.Utf8.encode(cary);
        this.write(bary,0,bary.length);
    }
    /**
     * Write to format UTF-8
     * @param string Output data to format encoding with newline
     */
    public void println(String string)
        throws java.io.IOException
    {
        char[] cary = null;
        if (null != string)
            cary = string.toCharArray();
        cary = alto.io.u.Chbuf.cat(cary,CRLF);
        byte[] bary = alto.io.u.Utf8.encode(cary);
        this.write(bary,0,bary.length);
    }
    /**
     * Write to format UTF-8
     * @param indent Indentation level
     * @param string Output data to format encoding
     */
    public void println(int indent, String string)
        throws java.io.IOException
    {
        this.nprint(' ',indent);
        this.println(string);
    }
    public void println()
        throws java.io.IOException
    {
        this.write(CRLF_BYTE,0,2);
    }

    public int copyOutLoop( java.io.OutputStream out) 
        throws java.io.IOException 
    {
        if (null != this.buf)
            return this.buf.copyOutLoop(out);
        else
            throw new java.lang.IllegalStateException("destroyed");
    }

    public int copyOutArray( java.io.OutputStream out) 
        throws java.io.IOException 
    {
        if (null != this.buf)
            return this.buf.copyOutArray(out);
        else
            throw new java.lang.IllegalStateException("destroyed");
    }

    public void copyFrom( java.io.File fi) 
        throws java.io.IOException 
    {
        if (null != this.buf)
            this.buf.readFrom(fi);
        else
            throw new java.lang.IllegalStateException("destroyed");
    }

}
