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
package alto.lang.buffer;

import alto.lang.Buffer;

/**
 * Extends {@link java.io.ByteArrayInputStream} with {@link
 * Buffer} for use by {@link InputStream}.
 */
public class InputStream
    extends alto.io.u.Bbi
    implements Buffer,
               alto.io.Input
{
    private final static byte[] nil = new byte[0];

    public final static int COPY = 0x200;//(512)
    public final static InputStream EMPTY = new InputStream();

    public final static byte[] Ctor(java.lang.String resource)
        throws java.io.IOException
    {
        java.lang.ClassLoader cl = InputStream.class.getClassLoader();
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
    public InputStream(){
        super(nil);
    }
    public InputStream(Abstract iob){
        super(iob);
        iob.reset_read();
    }
    public InputStream(byte[] buf){
        super(buf);
    }
    public InputStream(java.lang.String resource)
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
            throw new alto.sys.Error.State();
        }
    }
    public int getBufferLength(){
        try {
            return this.length();
        }
        catch (java.io.IOException notreached){
            throw new alto.sys.Error.State();
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
