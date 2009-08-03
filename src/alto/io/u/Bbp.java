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
 * Pipe for <tt>`Bbo'</tt> and <tt>`Bbi'</tt> implements input
 * blocking over <tt>`Bbuf'</tt>.  
 * 
 * <p><b>Usage</b>
 * 
 * <pre>
 * Bbp pipe = new Bbp();
 * OutputStream pipeOut = new Bbo(pipe);
 * InputStream pipeIn = new Bbi(pipe);
 * </pre>
 * 
 * <p><b>Thread safety</b>
 * 
 * <p> This class is multi- thread safe only in the standard I/O API
 * defined here and used by Bbo and Bbi.
 * <i>Note that `Bbuf' is not multi- thread safe.</i>
 * 
 * <p> The mutex locker is accessable via the <tt>`getLocker()'</tt>
 * method.
 * 
 * @author John Pritchard (john@syntelos.org)
 * 
 * @see Bbo
 * @see Bbi
 * @see Dbo
 * @since 1.1
 */
public class Bbp extends Bbuf {

    protected boolean apierrors = true; 

    public Bbp(){
        super();
    }
    /**
     * @param api_exc If true, throw exceptions on I/O methods that
     * shouldn't be used on the pipe.  
     */
    public Bbp( boolean api_exc){
        super();
        this.apierrors = api_exc;
    }

    public Bbp ( int init){
        super(init);
    }

    public Bbp ( byte[] buffer){
        super(buffer);
    }


    public int read() 
        throws java.io.IOException
    {
        while (true){
            if ( 0 < super.available()){

                if ( 0 < super.available())
                    return super.read();
            }
            try {
                synchronized(this){

                    this.wait();

                    continue;
                }
            } catch ( InterruptedException intx){
                return -1;
            }
        }
    }
    public int read(byte b[]) 
        throws java.io.IOException
    {
        return this.read( b, 0, b.length);
    }
    public int read(byte b[], int off, int len) 
        throws java.io.IOException
    {
        while (true){
            if ( 0 < super.available()){

                if ( 0 < super.available())
                    return super.read(b, off, len);
            }
            try {
                synchronized(this){

                    this.wait();

                    continue;
                }
            } catch ( InterruptedException intx){
                return -1;
            }
        }
    }
    public long skip(long n) 
        throws java.io.IOException
    {
        if (apierrors) 
            throw new alto.sys.Error.State("Skip not available on pipe.");
        else
            return 0L;
    }

    public void mark(int readlimit) {}

    public void reset() {
        if (apierrors) throw new alto.sys.Error.State("Reset not available on pipe.");
    }
    public boolean markSupported() {
        return false;
    }

    public synchronized void write(int b) 
        throws java.io.IOException
    {
        super.write(b);
        this.notifyAll();
    }
    public synchronized void write(byte b[])
        throws java.io.IOException
    {
        super.write( b, 0, b.length);
        this.notifyAll();
    }
    public synchronized void write(byte b[], int off, int len) 
        throws java.io.IOException
    {
        super.write( b, off, len);
        this.notifyAll();
    }
    public void flush() 
        throws java.io.IOException
    {
        if (apierrors) throw new alto.sys.Error.State("Flush not available on pipe.");
    }
    public void close() 
        throws java.io.IOException
    {
        if (apierrors) throw new alto.sys.Error.State("Close not available on pipe, will be GC'ed.");
    }
}
