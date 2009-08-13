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
package alto.lang.sio;

import alto.io.Input;
import alto.io.Output;
import alto.io.u.Bits;
import alto.io.u.Utf8;
import alto.lang.Component;
import alto.lang.Sio;
import alto.lang.sio.Field;

/**
 * 
 */
public abstract class Field 
    extends Abstract
    implements Sio.Type.Field
{
    public final static byte Tag = Sio.Tag.Field;

    public final static byte[] Read(Input in)
        throws java.io.IOException
    {
        int a = in.read();
        if (Field.Tag == a){
            int len = Sio.Length.Read(in);
            if (0 < len){
                byte[] buf = new byte[len];
                int ofs = 0, read;
                while (0 < len && 0 < (read = in.read(buf,ofs,len))){
                    ofs += read;
                    len -= read;
                }
                return buf;
            }
            else
                return null;
        }
        else
            throw new Error(a,"Format error field tag");
    }
    public final static java.lang.String ReadUtf8(Input in)
        throws java.io.IOException
    {
        byte[] field = Field.Read(in);
        char[] string = Utf8.decode(field);
        if (null != string)
            return new java.lang.String(string,0,string.length);
        else
            return null;
    }
    public final static int ReadInt(Input in)
        throws java.io.IOException
    {
        byte[] field = Field.Read(in);
        return Bits.Integer(field);
    }
    public final static void Write(byte[] d, Output out)
        throws java.io.IOException
    {
        out.write(Field.Tag);
        int len = ((null != d)?(d.length):(0));
        if (0 < len){
            Sio.Length.Write(len,out);
            out.write(d,0,len);
        }
        else {
            out.write(0);
        }
    }
    public final static void WriteInt(int value, Output out)
        throws java.io.IOException
    {
        byte[] d = Bits.Integer(value);
        Field.Write(d,out);
    }
    public final static void WriteUtf8(java.lang.String string, Output out)
        throws java.io.IOException
    {
        Write(Utf8.encode(string),out);
    }

    public final static class Debug
        extends Field
    {
        public Object[] contents;


        public Debug(Input in)
            throws java.io.IOException
        {
            super(in);
        }


        public java.lang.Object[] debugContents(){
            return this.contents;
        }
        public void sioRead(Input in)
            throws java.io.IOException
        {
            /*
             * Read container
             */
            super.sioRead(in);
            /*
             * Read buffer
             */
            in = this.openInput();
            try {
                this.contents = Try(in);
            }
            finally {
                in.close();
            }
        }
    }


    public Field(){
        super();
    }
    public Field(Input in)
        throws java.io.IOException
    {
        super(in);
    }
}
