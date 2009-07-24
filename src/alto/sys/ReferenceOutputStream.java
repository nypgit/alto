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

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @since 1.2
 */
public class ReferenceOutputStream
    extends java.io.FilterOutputStream
    implements alto.sys.IO.Filter.Target
{
    protected final static char[] CRLF = {
        '\r',
        '\n'
    };
    protected final static byte[] CRLF2 = {
        '\r',
        '\n'
    };

    private final Reference reference; 
    private final URLConnection connection;
    private final HttpMessage meta;


    public ReferenceOutputStream(Reference ref, URLConnection connection)
        throws IOException
    {
        super(connection.getOutputStream());
        this.reference = ref;
        this.connection = connection;
        this.meta = null;
    }
    public ReferenceOutputStream(Reference ref, File file)
        throws IOException
    {
        super(new java.io.FileOutputStream(file));
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceOutputStream(Reference ref, alto.io.Output out)
        throws IOException
    {
        super((java.io.OutputStream)out);
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceOutputStream(Reference ref, java.io.OutputStream out)
        throws IOException
    {
        super(out);
        this.reference = ref;
        this.connection = null;
        this.meta = null;
    }
    public ReferenceOutputStream(Reference ref, HttpMessage meta)
        throws IOException
    {
        super(meta.openOutputStream());
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
    public java.io.OutputStream getFilterTarget(){
        return this.out;
    }
    public final boolean hasConnection(){
        return (null != this.connection);
    }
    public final URLConnection getConnection(){
        return this.connection;
    }
    public final boolean isMetaOutput(){
        return (null != this.meta);
    }
    public final boolean hasMeta(){
        return (null != this.meta);
    }
    public final HttpMessage getMeta(){
        return this.meta;
    }
    public void print(char ch)
        throws java.io.IOException
    {
        if (ch < 0x80)
            this.write(ch);
        else {
            char[] cary = new char[]{ch};
            byte[] bary = alto.io.u.Utf8.encode(cary);
            this.write(bary,0,bary.length);
        }
    }
    public void print(String string)
        throws java.io.IOException
    {
        if (null != string){
            char[] cary = string.toCharArray();
            if (0 < cary.length){
                byte[] bary = alto.io.u.Utf8.encode(cary);
                this.write(bary,0,bary.length);
            }
        }
    }
    public void println(char ch)
        throws java.io.IOException
    {
        char[] cary = alto.io.u.Chbuf.cat(ch,CRLF);
        byte[] bary = alto.io.u.Utf8.encode(cary);
        this.write(bary,0,bary.length);
    }
    public void println()
        throws java.io.IOException
    {
        byte[] bary = CRLF2;
        this.write(bary,0,2);
    }
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
    public void close()
        throws java.io.IOException
    {
        try {
            /*
             * In the "meta" case, this is closing the buffer to the
             * message body.
             */
            super.close();
        }
        finally {
            /*
             * In the "meta" case, subsequent to closing the message
             * body, authenticate and write the message to storage.
             */
            if (null != this.meta){
                Reference reference = this.reference;
                reference.close();
            }
        }
    }
}
