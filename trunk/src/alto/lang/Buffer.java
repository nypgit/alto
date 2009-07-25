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
 * <p> Interface for anything containing a singular byte array.  A
 * buffer returned on this interface is not copied, if at all
 * possible.  This permits may accesses to the {@link #getBuffer()}
 * method without the cost of copying. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public interface Buffer {
    /**
     * <p> A buffer implementation of {@link
     * alto.sys.IO}. </p>
     * 
     * <p> <i>N.B.</i> Not MT SAFE, read and write position states are
     * internal to this buffer. </p>
     */
    public static class IOB 
        extends alto.io.u.Bbuf
        implements alto.sys.IO.Edge,
                   Buffer
    {
        protected final static alto.io.Uri UriDefault = new alto.io.u.Uri("buffer:default");

        protected long last;

        protected java.lang.String lastString;

        protected Type format;

        protected Buffer.InStream in;

        protected Buffer.OutStream out;


        public IOB(){
            super();
        }
        public IOB(byte[] content){
            super(content);
        }


        public alto.io.Uri getUri(){
            return UriDefault;
        }
        public final void setFormat(Type format){
            this.format = format;
        }
        public final Type getFormat(){
            return this.format;
        }
        public final void setFormatSio(){
            this.format = Type.Tools.Of("sio");
        }
        public final boolean isFormatSio(){
            return (Type.Tools.Of("sio") == this.format);
        }

        public java.nio.channels.ReadableByteChannel openChannelReadable(){
            return null;
        }
        public java.nio.channels.ReadableByteChannel getChannelReadable(){
            return null;
        }
        public long lastModified(){
            return last;
        }
        public java.lang.String lastModifiedString(){
            java.lang.String lastString = this.lastString;
            if (null == lastString){
                long last = this.last;
                if (0L < last){
                    lastString = Date.ToString(last);
                    this.lastString = lastString;
                }
            }
            return lastString;
        }
        public long getLastModified(){
            return last;
        }
        public java.lang.String getLastModifiedString(){
            return this.lastModifiedString();
        }
        public java.io.InputStream openInputStream()
            throws java.io.IOException
        {
            return (this.in = new Buffer.InStream(this));
        }
        public alto.io.Input openInput()
            throws java.io.IOException
        {
            return (this.in = new Buffer.InStream(this));
        }
        public java.io.InputStream getInputStream()
            throws java.io.IOException
        {
            Buffer.InStream in = this.in;
            if (null == in){
                in = new Buffer.InStream(this);
                this.in = in;
            }
            return in;
        }
        public alto.io.Input getInput()
            throws java.io.IOException
        {
            Buffer.InStream in = this.in;
            if (null == in){
                in = new Buffer.InStream(this);
                this.in = in;
            }
            return in;
        }
        public java.nio.channels.WritableByteChannel openChannelWritable(){
            return null;
        }
        public java.nio.channels.WritableByteChannel getChannelWritable(){
            return null;
        }
        public boolean setLastModified(long last){
            this.last = last;
            return true;
        }
        public java.io.OutputStream openOutputStream()
            throws java.io.IOException
        {
            return (this.out = new Buffer.OutStream(this));
        }
        public alto.io.Output openOutput()
            throws java.io.IOException
        {
            return (this.out = new Buffer.OutStream(this));
        }
        public java.io.OutputStream getOutputStream()
            throws java.io.IOException
        {
            Buffer.OutStream out = this.out;
            if (null == out){
                out = new Buffer.OutStream(this);
                this.out = out;
            }
            return out;
        }
        public alto.io.Output getOutput()
            throws java.io.IOException
        {
            Buffer.OutStream out = this.out;
            if (null == out){
                out = new Buffer.OutStream(this);
                this.out = out;
            }
            return out;
        }
        public byte[] getBuffer(){
            try {
                return this.toByteArray();
            }
            catch (java.io.IOException notreached){
                throw new IllegalStateException();
            }
        }
        public int getBufferLength(){
            try {
                return this.length();
            }
            catch (java.io.IOException notreached){
                throw new IllegalStateException();
            }
        }
        public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
            byte[] bits = this.getBuffer();
            if (null == bits)
                return null;
            else {
                char[] cary = alto.io.u.Utf8.decode(bits);
                return new String(cary,0,cary.length);
            }
        }
    }
    /**
     * Extends {@link java.io.ByteArrayInputStream} with {@link
     * Buffer} for use by {@link InputStream}.
     */
    public static class InStream
        extends alto.io.u.Bbi
        implements Buffer,
                   alto.io.Input
    {
        private final static byte[] nil = new byte[0];

        public final static int COPY = 0x200;//(512)
        public final static InStream EMPTY = new InStream();

        public final static byte[] Ctor(java.lang.String resource)
            throws java.io.IOException
        {
            java.lang.ClassLoader cl = InStream.class.getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream(resource);
            if (null == in)
                return nil;
            else {
                try {
                    alto.io.u.Bbuf bb = new alto.io.u.Bbuf(in);
                    return bb.toByteArray();
                }
                finally {
                    in.close();
                }
            }
        }


        /**
         * Empty
         */
        public InStream(){
            super(nil);
        }
        public InStream(IOB iob){
            super(iob);
            iob.reset_read();
        }
        public InStream(byte[] buf){
            super(buf);
        }
        public InStream(java.lang.String resource)
            throws java.io.IOException
        {
            this(Ctor(resource));
        }


        public int copyTo(alto.io.Output out)
            throws java.io.IOException 
        {
            int total = 0;
            byte[] iobuf = new byte[COPY];
            int count;
            while(0 < (count = this.read(iobuf,0,COPY))){
                out.write(iobuf,0,count);
                total += count;
            }
            return total;
        }
        public byte[] getBuffer(){
            try {
                return this.toByteArray();
            }
            catch (java.io.IOException notreached){
                throw new IllegalStateException();
            }
        }
        public int getBufferLength(){
            try {
                return this.length();
            }
            catch (java.io.IOException notreached){
                throw new IllegalStateException();
            }
        }
        public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
            byte[] bits = this.getBuffer();
            if (null == bits)
                return null;
            else {
                char[] cary = alto.io.u.Utf8.decode(bits);
                return new String(cary,0,cary.length);
            }
        }
    }
    /**
     * Extends {@link java.io.ByteArrayOutputStream} with {@link
     * Buffer} for use by {@link OutputStream}.  
     */
    public static class OutStream
        extends alto.io.u.Bbo
        implements Buffer,
                   alto.io.Output
    {
        public static class Writer
            extends java.io.OutputStreamWriter
            implements Buffer
        {

            public Writer(){
                super(new OutStream());
            }
            public Writer(IOB iob){
                super(new OutStream(iob));
                iob.reset_write();
            }


            public OutStream getOutputStream(){
                return (OutStream)super.lock;
            }
            public byte[] getBuffer(){
                try {
                    return this.getOutputStream().toByteArray();
                }
                catch (java.io.IOException notreached){
                    throw new IllegalStateException();
                }
            }
            public int getBufferLength(){
                try {
                    return this.getOutputStream().length();
                }
                catch (java.io.IOException notreached){
                    throw new IllegalStateException();
                }
            }
            public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
                byte[] bits = this.getBuffer();
                if (null == bits)
                    return null;
                else {
                    char[] cary = alto.io.u.Utf8.decode(bits);
                    return new String(cary,0,cary.length);
                }
            }
        }

        /**
         * Fillable
         */
        public OutStream(){
            super();
        }
        public OutStream(IOB iob){
            super(iob);
            iob.reset_write();
        }


        public byte[] getBuffer(){
            try {
                return this.toByteArray();
            }
            catch (java.io.IOException ignore){
                throw new IllegalStateException();
            }
        }
        public int getBufferLength(){
            try {
                return this.length();
            }
            catch (java.io.IOException ignore){
                throw new IllegalStateException();
            }
        }
        public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
            byte[] bits = this.getBuffer();
            if (null == bits)
                return null;
            else {
                char[] cary = alto.io.u.Utf8.decode(bits);
                return new String(cary,0,cary.length);
            }
        }
    }

    /**
     * An immutable array.  Only copy as necessary for immutablility.
     * @return Null for length zero.
     */
    public byte[] getBuffer();

    public int getBufferLength();
    /**
     * Interface compatible with javax tools file object.  Convert
     * buffer to string via UTF8.
     */
    public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException;
}
