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
package alto.sys;

import alto.lang.HttpMessage;
import alto.sys.IO;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * 
 * @since 1.2
 */
public class ReferenceInputStream
    extends java.io.FilterInputStream
    implements alto.sys.IO.Filter.Source
{
    public final static int COPY = 0x200;//(512)

    private final Reference reference;
    private final URLConnection connection;
    private final HttpMessage meta;

    private alto.io.u.Bbuf linebuf;


    public ReferenceInputStream(Reference ref, URLConnection connection)
        throws IOException
    {
        super(connection.getInputStream());
        this.reference = ref;
        this.connection = connection;
        this.meta = null;
    }
    public ReferenceInputStream(Reference ref, File file)
        throws IOException
    {
        super(new java.io.FileInputStream(file));
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceInputStream(Reference ref, alto.io.Input in)
        throws IOException
    {
        super((java.io.InputStream)in);
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceInputStream(Reference ref, java.io.InputStream in)
        throws IOException
    {
        super(in);
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceInputStream(Reference ref, HttpMessage meta)
        throws IOException
    {
        super(meta.openInputStream());
        this.reference = ref;
        this.connection = null;
        this.meta = meta;
    }


    public IO.Edge getIOEdge(){
        return this.reference;
    }
    public Reference getReference(){
        return this.reference;
    }
    public final java.io.InputStream getFilterSource(){
        return this.in;
    }
    public final boolean hasConnection(){
        return (null != this.connection);
    }
    public final URLConnection getConnection(){
        return this.connection;
    }
    public final boolean hasMeta(){
        return (null != this.meta);
    }
    public final HttpMessage getMeta(){
        return this.meta;
    }
    private alto.io.u.Bbuf linebuf(){
        alto.io.u.Bbuf linebuf = this.linebuf;
        if (null == linebuf){
            linebuf = new alto.io.u.Bbuf();
            this.linebuf = linebuf;
        }
        return linebuf;
    }
    public final java.lang.String readLine() 
        throws java.io.IOException 
    {
        int ch, drop = 0;
        alto.io.u.Bbuf linebuf = this.linebuf();
        readl:
        while (true) {
            switch (ch = this.read()) {
            case -1:
            case '\n':
                break readl;
            case '\r':
                switch (ch = this.read()){
                case -1:
                    break readl;
                case '\n':
                    break readl;
                default:
                    throw new java.io.IOException("Character carriage-return not followed by line-feed.");
                }
                //break readl;//(unreachable)
            default:
                linebuf.write(ch);
                break;
            }
        }
        byte[] buf = linebuf.toByteArray();
        if (null == buf)
            return null;
        else {
            java.lang.String re = linebuf.toString();
            linebuf.resetall();
            char[] cary = alto.io.u.Utf8.decode(buf);
            return new java.lang.String(cary);
        }
    }
    public byte[] readMany(int length)
        throws java.io.IOException
    {
        byte[] re = new byte[length];
        int r = this.read(re,0,length);
        if (1 > r)
            return null;
        else {
            int count = (length-r);
            int index = r;
            while (0 < count &&(0 < (r = this.read(re,index,count)))){
                if (1 > r)
                    break;
                else {
                    count -= r;
                    index += r;
                }
            }
            if (r < length){
                byte[] copier = new byte[r];
                System.arraycopy(re,0,copier,0,r);
                return copier;
            }
            return re;
        }
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
}
