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

import alto.io.Code;
import alto.io.Check;
import alto.sys.IO;

/**
 * 
 * @author jdp
 * @since 1.5
 */
public class OutputStream
    extends java.io.FilterOutputStream
    implements Buffer,
               alto.sys.Lock.Semaphore,
               alto.sys.IO.Filter.Target,
               alto.io.Output.Http
{

    public final static byte[] CRLF = {'\r','\n'};
    public final static byte[] NIL = {};

    public final static byte[] Ascii (java.lang.String string){
        if (null == string)
            return NIL;
        else {
            char[] src = string.toCharArray();
            int count = src.length;
            if (1 > count)
                return NIL;
            else {
                byte[] dst = new byte[count];
                u8:
                for (int cc = 0; cc < count; cc++){
                    dst[cc] = (byte)(src[cc] & 0xff);
                }
                return dst;
            }
        }
    }
    private final static java.io.OutputStream Ctor(Socket socket)
        throws java.io.IOException
    {
        if (socket.isTcpClient())
            return socket.getOutputStreamTcp();
        else if (socket.isShm())
            return socket.getOutputStreamShm();
        else
            return new Buffer.OutStream();
    }

    private final Trace trace;
    private final Socket socket;
    private final Closeable closeable;
    private final alto.sys.Lock.Semaphore lock;

    private boolean disableTraceOnFlush, useTrace, useTraceWrite;

    /**
     * Buffer
     */
    public OutputStream(){
        super(new Buffer.OutStream());
        this.trace = null;
        this.useTrace = false;
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }
    public OutputStream(Socket socket)
        throws java.io.IOException
    {
        super(Ctor(socket));
        this.useTrace = this.isNotBuffer();
        if (this.useTrace){
            this.disableTraceOnFlush = true;
            this.trace = new Trace();
        }
        else
            this.trace = null;

        this.socket = socket;
        this.closeable = null;
        this.lock = socket;
    }
    public OutputStream(java.io.OutputStream out)
        throws java.io.IOException
    {
        super(out);
        this.useTrace = this.isNotBuffer();
        if (this.useTrace){
            this.disableTraceOnFlush = true;
            this.trace = new Trace();
        }
        else
            this.trace = null;

        this.socket = null;
        this.closeable = null;
        this.lock = socket;
    }
    public OutputStream(java.io.File raw)
        throws java.io.IOException
    {
        super(new java.io.FileOutputStream(raw));
        this.trace = null;
        this.useTrace = false;
        this.socket = null;
        this.closeable = null;
        this.lock = null;
    }


    public IO.Edge getIOEdge(){
        return this.socket;
    }
    public java.io.OutputStream getFilterTarget(){
        return this.out;
    }
    public boolean lockReadEnterTry(){
        if (null != this.lock)
            return this.lock.lockReadEnterTry();
        else
            return true;
    }
    public void lockReadEnter(){
        if (null != this.lock)
            this.lock.lockReadEnter();
    }
    public void lockReadExit(){
        if (null != this.lock)
            this.lock.lockReadExit();
    }
    public boolean lockWriteEnterTry(){
        if (null != this.lock)
            return this.lock.lockWriteEnterTry();
        else
            return true;
    }
    public boolean lockWriteEnterTry(byte cur, byte nex){
        if (null != this.lock)
            return this.lock.lockWriteEnterTry(cur,nex);
        else
            return true;
    }
    public void lockWriteEnter(){
        if (null != this.lock)
            this.lock.lockWriteEnter();
    }
    public boolean lockWriteEnter(byte cas_current, byte cas_next){
        if (null != this.lock)
            return this.lock.lockWriteEnter(cas_current, cas_next);
        else
            return true;
    }
    public void lockWriteExit(){
        if (null != this.lock)
            this.lock.lockWriteExit();
    }
    public Closeable getCloseable(){
        return this.closeable;
    }
    /**
     * Reset trace, or reset underlying buffer.
     */
    public void reset(){
        if (this.useTrace)
            this.trace.reset();
        //
        if (this.out instanceof Buffer.OutStream){
            ((Buffer.OutStream)this.out).reset();
        }
    }
    public void close() throws java.io.IOException {
        Closeable closeable = this.closeable;
        if (null != closeable)
            try {
                closeable.close();
            }
            catch (java.lang.Throwable ignore){
            }
        super.close();
    }
    public void flush() throws java.io.IOException {
        if (this.disableTraceOnFlush)
            this.useTrace = false;

        super.flush();
    }
    public final boolean hasSocket(){
        return (null != this.socket);
    }
    public final Socket getSocket(){
        return this.socket;
    }
    @Code(Check.Review)
    public void startMessage(){
        /*
         * [Review] This is tuned to the HttpMessage #writeMessage
         * process(es) using #println for the status line and
         * #write(byte[]) for headers.
         */
        this.useTraceWrite = false;
    }
    @Code(Check.Review)
    public void startHeaders(){
        this.useTraceWrite = this.useTrace;
    }
    public void endHeaders(){
        this.useTraceWrite = false;
    }
    public void endMessage(){
        this.useTraceWrite = false;
    }
    public final void print(Object object)
        throws java.io.IOException
    {
        if (null != object)
            this.print(object.toString());
    }
    public final void print(java.lang.String string)
        throws java.io.IOException
    {
        byte[] bytes = Ascii(string);
        this.write(bytes);
        if (this.useTraceWrite)
            return;
        else if (this.useTrace){
            this.trace.write(bytes);
        }
    }
    public final void print(char value)
        throws java.io.IOException
    {
        if (0 < value && value < 127)
            this.write(value);
    }
    public final void print(int value)
        throws java.io.IOException
    {
        this.print(java.lang.String.valueOf(value));
    }
    public final void println(java.lang.String string)
        throws java.io.IOException
    {
        this.print(string);
        this.newline();
    }
    public final void println(char value)
        throws java.io.IOException
    {
        this.print(value);
        this.newline();
    }
    public final void println()
        throws java.io.IOException
    {
        this.newline();
    }
    public final void newline()
        throws java.io.IOException
    {
        this.write(CRLF);
        if (this.useTraceWrite)
            return;
        else if (this.useTrace){
            this.trace.write(CRLF);
        }
    }
    public final void write(int ch) 
        throws java.io.IOException 
    {
        super.out.write(ch);
        if (this.useTraceWrite){
            this.trace.write(ch);
        }
    }
    public final void write(byte[] bary) 
        throws java.io.IOException 
    {
        if (null != bary){
            int len = bary.length;
            super.out.write(bary,0,len);

            if (this.useTraceWrite){
                this.trace.write(bary,0,len);
            }
        }
    }
    public final void write(byte[] bary, int ofs, int len) 
        throws java.io.IOException 
    {
        if (null != bary && 0 < len){
            super.out.write(bary,ofs,len);

            if (this.useTraceWrite){
                this.trace.write(bary,0,len);
            }
        }
    }
    public final void writeBuffer(Buffer buffer)
        throws java.io.IOException 
    {
        byte[] bits = buffer.getBuffer();
        int len = buffer.getBufferLength();
        this.write(bits,0,len);
    }
    public final byte[] getTrace(){
        Trace trace = this.trace;
        if (null != trace)
            return trace.getBuffer();
        else
            return this.getBuffer();
    }
    public final int getTraceLength(){
        Trace trace = this.trace;
        if (null != trace)
            return this.trace.length();
        else
            return this.getBufferLength();
    }
    public final boolean isNotBuffer(){
        return (!(this.out instanceof Buffer.OutStream));
    }
    public final boolean isBuffer(){
        return (this.out instanceof Buffer.OutStream);
    }
    public final byte[] getBuffer(){
        if (this.out instanceof Buffer.OutStream){
            Buffer.OutStream bout = (Buffer.OutStream)this.out;
            if (0 < bout.getBufferLength())
                return bout.getBuffer();
        }
        return null;
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
    public final Buffer.OutStream getBufferOutStream(){
        if (this.out instanceof Buffer.OutStream)
            return (Buffer.OutStream)this.out;
        else
            return null;
    }
    public final int getBufferLength(){
        if (this.out instanceof Buffer.OutStream){
            Buffer.OutStream bout = (Buffer.OutStream)this.out;
            return bout.getBufferLength();
        }
        else
            return 0;
    }
    public java.lang.String toString(){
        byte[] buf = this.getBuffer();
        if (null != buf){
            char[] cary = alto.io.u.Utf8.decode(buf);
            return new java.lang.String(cary);
        }
        else
            return "";
    }
}
