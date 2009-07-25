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

import alto.sys.IO;

/**
 * 
 * @author jdp
 * @since 1.5
 */
public class InputStream
    extends ConsumptionInputStream
    implements Buffer,
               alto.sys.Lock.Semaphore,
               alto.sys.IO.Filter.Source,
               alto.io.Input
{
    public final static InputStream To(java.io.InputStream in){
        if (in instanceof InputStream)
            return (InputStream)in;
        else
            return new InputStream(in);
    }

    public final static java.lang.String CRLF = "\r\n";
    public final static java.lang.String NIL = "";

    public final static java.lang.String Ascii (byte[] src){
        if (null == src)
            return null;
        else {
            int count = src.length;
            if (1 > count)
                return null;
            else {
                tailtrim:
                for (int cc = (count-1); cc > -1; cc--){
                    switch (src[cc]){
                    case '\r':
                    case '\n':
                        count -= 1;
                        break;
                    default:
                        break tailtrim;
                    }
                }
                if (1 > count)
                    return null;
                else {
                    char[] dst = new char[count];
                    u8:
                    for (int cc = 0; cc < count; cc++){
                        dst[cc] = (char)(src[cc] & 0xff);
                    }
                    return new java.lang.String(dst,0,count);
                }
            }
        }
    }
    /**
     * Normalizes input buffers ({@link java.io.ByteArrayInputStream})
     * to instances of {@link Buffer$InStream}.  Non buffer streams are
     * returned verbatum.
     */
    public final static java.io.InputStream Ctor(java.io.InputStream in){
        if (in instanceof java.io.ByteArrayInputStream){
            if (in instanceof Buffer.InStream)
                return in;
            else {
                throw new IllegalArgumentException("Check: Use Buffer.InStream?");
                //TODO(replace with copy)
            }
        }
        else
            return in;
    }
    public final static java.io.InputStream Ctor(java.lang.String resource)
        throws java.io.IOException
    {
        return new Buffer.InStream(resource);
    }
    private final static java.io.InputStream Ctor(Socket socket)
        throws java.io.IOException
    {
        if (socket.isTcpClient())
            return socket.getInputStreamTcp();
        else if (socket.isShm())
            return socket.getInputStreamShm();
        else
            return new Buffer.InStream();
    }


    private final Socket socket;
    private final Closeable closeable;
    private final alto.sys.Lock.Semaphore lock;

    /**
     * Empty buffer
     */
    public InputStream (){
        super(Buffer.InStream.EMPTY);
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public InputStream (byte[] buf){
        super(new Buffer.InStream(buf));
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public InputStream (java.io.InputStream in){
        super(Ctor(in));
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public InputStream (java.io.File raw)
        throws java.io.IOException
    {
        super(new java.io.FileInputStream(raw));
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public InputStream (java.lang.String resource)
        throws java.io.IOException
    {
        super(Ctor(resource));
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public InputStream (Socket socket)
        throws java.io.IOException
    {
        super(Ctor(socket));
        this.socket = socket;
        this.closeable = null;
        this.lock = socket;
    }


    public IO.Edge getIOEdge(){
        return this.socket;
    }
    public final boolean lockReadEnterTry(){
        if (null != this.lock)
            return this.lock.lockReadEnterTry();
        else
            return true;
    }
    public final void lockReadEnter(){
        if (null != this.lock)
            this.lock.lockReadEnter();
    }
    public final void lockReadExit(){
        if (null != this.lock)
            this.lock.lockReadExit();
    }
    public final boolean lockWriteEnterTry(){
        if (null != this.lock)
            return this.lock.lockWriteEnterTry();
        else
            return true;
    }
    public final boolean lockWriteEnterTry(byte cas_current, byte cas_next){
        if (null != this.lock)
            return this.lock.lockWriteEnterTry(cas_current, cas_next);
        else
            return true;
    }
    public final boolean lockWriteEnter(byte cas_current, byte cas_next){
        if (null != this.lock)
            return this.lock.lockWriteEnter(cas_current, cas_next);
        else
            return true;
    }
    public final void lockWriteEnter(){
        if (null != this.lock)
            this.lock.lockWriteEnter();
    }
    public final void lockWriteExit(){
        if (null != this.lock)
            this.lock.lockWriteExit();
    }
    public final boolean hasSocket(){
        return (null != this.socket);
    }
    public final Socket getSocket(){
        return this.socket;
    }
    /**
     * @return Whether the underlying input stream is a buffer.
     */
    public final boolean isBuffer(){
        return (this.in instanceof Buffer.InStream);
    }
    public final boolean isNotBuffer(){
        return (!(this.in instanceof Buffer.InStream));
    }
    public final Closeable getCloseable(){
        return this.closeable;
    }
    /**
     * Reset trace and entity buffers.
     * @see #readLine()
     * @see #entityBuffer()
     */
    public final void reset() throws java.io.IOException {
        this.trace.reset();
        if (this.isBuffer()){
            Buffer.InStream in = (Buffer.InStream)this.in;
            in.reset();
        }
        this.set(-1L);
    }
    public final void close() throws java.io.IOException {
        Closeable closeable = this.closeable;
        if (null != closeable)
            try {
                closeable.close();
            }
            catch (java.lang.Throwable ignore){
            }
        super.close();
    }
    /**
     * Fills and marks the {@link Trace} buffer with the next line
     * from the underlying input stream.  Lines read are collected
     * into the trace buffer until it is reset by calling this reset
     * method or this entity buffer method.
     * @see #reset()
     * @see #entityBuffer()
     */
    public final java.lang.String readLine()
        throws java.io.IOException
    {
        try {
            this.enterTraceread();
            int ch = 0;
            scan:
            while (-1 < (ch = this.read())){
                switch (ch){
                case '\r':
                    /*
                     * Case "only '\r' line terminals" will fail to be
                     * recognized as line terminals.
                     */
                    continue scan;
                case '\n':
                    /*
                     * Accepted cases '\r\n' and '\n' will succeed.
                     */
                    return Ascii(this.trace.mark());
                default:
                    continue scan;
                }
            }
            if (0 < ch){
                switch (ch){
                case '\r':
                case '\n':
                    return null;
                default:
                    /*
                     * Tail case, without newline
                     */
                    return Ascii(this.trace.mark());
                }
            }
            else
                return null;
        }
        catch (java.lang.RuntimeException exc){
            throw new java.io.IOException(exc);
        }
        finally {
            this.exitTraceread();
        }
    }
    /**
     * @return The contents of the underlying buffer when this is a
     * buffer, or of the {@link Trace} buffer.
     */
    public final byte[] getBuffer(){
        if (this.isBuffer())
            return ((Buffer.InStream)this.in).getBuffer();
        else
            return this.trace.getBuffer();
    }
    /**
     * @return The size of the contents of the underlying buffer when
     * this is a buffer, or of the {@link Trace} buffer.
     */
    public final int getBufferLength(){
        if (this.isBuffer())
            return ((Buffer.InStream)this.in).getBufferLength();
        else
            return this.trace.length();
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
    /**
     * @return The contents of the {@link Trace} buffer, filled by
     * {@link #readLine()} or {@link #entityBuffer(int)}.
     */
    public final byte[] getTrace(){
        return this.trace.getBuffer();
    }
    /**
     * @return The size of the contents of the {@link Trace} buffer,
     * filled by {@link #readLine()} or {@link #entityBuffer(int)}.
     */
    public final int getTraceLength(){
        return this.trace.length();
    }

}
