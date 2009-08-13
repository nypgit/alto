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
import alto.lang.Component;
import alto.lang.Sio;

/**
 * The superclass {@link Buffer$IOB} buffer contains payload for
 * this object.
 */
public abstract class SIOB
    extends alto.lang.Buffer.IOB
    implements Sio
{
    protected final static java.lang.Object[] Try(Input in)
        throws java.io.IOException
    {
        Object[] contents = null;
        alto.io.u.Bbi inb = (alto.io.u.Bbi)in;
        try {
            while (true){
                int tag = inb.read();
                if (-1 < tag){
                    inb.unread();
                    switch(tag){
                    case Sio.Tag.Field:
                        Field.Debug field = new Field.Debug(in);
                        contents = alto.io.u.Array.add(contents,field);
                        break;
                    case Sio.Tag.Group:
                        Group.Debug group = new Group.Debug(in);
                        contents = alto.io.u.Array.add(contents,group);
                        break;
                    case Sio.Tag.Record:
                        Record.Debug record = new Record.Debug(in);
                        contents = alto.io.u.Array.add(contents,record);
                        break;
                    default:
                        Object data = inb.toByteArray();
                        if (null != data)
                            contents = alto.io.u.Array.add(contents,data);
                        return contents;
                    }
                }
                else
                    return contents;
            }
        }
        catch (java.lang.RuntimeException end){
            Object data = inb.toByteArray();
            if (null != data)
                contents = alto.io.u.Array.add(contents,data);
            return contents;
        }
        finally {
            in.close();
        }
    }

    protected int sioTag;


    public SIOB(){
        super();
    }
    public SIOB(Input in)
        throws java.io.IOException
    {
        this();
        this.sioRead(in);
    }


    public java.lang.Object[] debugContents(){
        return null;
    }
    public final void debugPrint(alto.io.u.Ios out)
        throws java.io.IOException
    {
        java.lang.String name = Sio.Tag.Name(this.getSioTag());
        out.indentln(name+" {");
        java.lang.Object[] contents = this.debugContents();
        if (null != contents){
            out.push();
            for (int cc = 0, count = contents.length; cc < count; cc++){
                Object content = contents[cc];
                if (content instanceof SIOB)
                    ((SIOB)content).debugPrint(out);
                else if (content instanceof byte[]){
                    byte[] data = (byte[])content;
                    out.dataln(data);
                }
            }
            out.pop();
        }
        out.indentln("}");
    }

    public final boolean hasSioTag(){
        return Tag.IsValid(this.sioTag);
    }
    public final boolean hasNotSioTag(){
        return (!Tag.IsValid(this.sioTag));
    }
    public final int getSioTag(){
        int sioTag = this.sioTag;
        if (Tag.IsValid(sioTag))
            return sioTag;

        else if (this instanceof Sio.Type.Field){
            sioTag = Sio.Tag.Field;
            this.sioTag = sioTag;
            return sioTag;
        }
        else if (this instanceof Sio.Type.Group){
            sioTag = Sio.Tag.Group;
            this.sioTag = sioTag;
            return sioTag;
        }
        else if (this instanceof Sio.Type.Record){
            sioTag = Sio.Tag.Record;
            this.sioTag = sioTag;
            return sioTag;
        }
        else
            throw new alto.sys.Error.State("Missing SIO tag.");
    }
    public final boolean isSioGroup(){
        return (Sio.Tag.Group == this.sioTag);
    }
    public final boolean isSioRecord(){
        return (Sio.Tag.Record == this.sioTag);
    }
    public final boolean isSioField(){
        return (Sio.Tag.Field == this.sioTag);
    }
    public final void sioRead(byte[] buf)
        throws java.io.IOException
    {
        Input in = new alto.lang.Buffer.InStream(buf);
        this.sioRead(in);
    }
    public void sioRead(Input in)
        throws java.io.IOException
    {
        int sioTag = in.read();
        if (Tag.IsValid(sioTag)){
            this.sioTag = sioTag;
            int len = Sio.Length.Read(in);
            this.readFrom(in,len);
        }
        else
            throw new Error(sioTag,"Bad SIO tag value.");
    }
    public void sioWrite(Output out)
        throws java.io.IOException
    {
        switch(this.getSioTag()){
        case Sio.Tag.Field:
            Field.Write(this.getBuffer(),out);
            return;
        case Sio.Tag.Group:
            Group.Write(this.getBuffer(),out);
            return;
        case Sio.Tag.Record:
            Record.Write(this.getBuffer(),out);
            return;
        default:
            throw new java.io.IOException("Invalid sio tag for writing '0x"+Integer.toHexString(this.sioTag)+"'.");
        }
    }
}
