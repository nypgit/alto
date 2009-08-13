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

import alto.hash.Function;
import alto.io.Input;
import alto.io.Output;
import alto.io.u.Bits;
import alto.lang.Component;
import alto.lang.Sio;
import alto.sys.Reference;

/**
 * This class includes a command line tool for debuging SIO files.
 */
public abstract class File
    extends Abstract
    implements Sio.Type.TypeClass
{
    public final static class Debug
        extends File
    {

        public final static class AnyType
            extends java.lang.Object
            implements Component.Path
        {
            byte[] data;

            public AnyType(){
                super();
            }

            public boolean hasType(){
                return false;
            }
            public alto.lang.Type getType(){
                return null;
            }
            public void sioRead(Input in) 
                throws java.io.IOException 
            {
                throw new UnsupportedOperationException();
            }
            public void sioWrite(Output out) 
                throws java.io.IOException
            {
                byte[] content = this.toByteArray();
                Field.Write(content,out);
            }
            public byte[] toByteArray(){
                return this.data;
            }
            public Component.Numeric add(Component.Numeric value){
                long thisValue = this.longValue();
                long thatValue = value.longValue();
                return new alto.lang.component.Path.Numeric(Bits.Long(thisValue+thatValue));
            }
            public long longValue(){
                return Bits.Integer(this.data);
            }
            public byte[] getBuffer(){
                return this.data;
            }
            public int getBufferLength(){
                byte[] data = this.data;
                if (null == data)
                    return 0;
                else
                    return data.length;
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
            public int getPosition(){
                return Component.Path.Position;
            }
            public boolean hasHashFunction(){
                return false;
            }
            public Function getHashFunction(){
                return null;
            }
            public int compareTo(Component ano){
                return 0;
            }
            public boolean equals(Object ano){
                return true;
            }
        }

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
            this.sioType = new AnyType();
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


        public static void main(java.lang.String[] argv){
            alto.io.Tools.SInit();
            java.io.File file = new java.io.File(argv[0]);
            alto.io.Input in = null;
            try {
                in = new alto.lang.InputStream(new java.io.FileInputStream(file));
                Debug debug = new Debug(in);
                alto.io.u.Ios out = new alto.io.u.Ios(System.out);
                debug.debugPrint(out);
            }
            catch (java.io.IOException exc){
                exc.printStackTrace();
            }
            finally {
                if (null != in){
                    try {
                        in.close();
                    }
                    catch (java.io.IOException exc){
                    }
                }
            }
            java.lang.System.exit(0);
        }
    }


    protected volatile Component.Path sioType;


    public File(){
        super();
    }
    public File(Input in)
        throws java.io.IOException
    {
        super(in);
    }


    public boolean hasSioType(){
        return (null != this.sioType);
    }
    public boolean hasNotSioType(){
        return (null == this.sioType);
    }
    /**
     * Simple getter is overridden with a lazy fetch from
     * reference in {@link alto.sys.PSioFile} to dynamically get a
     * type from a reference.
     */
    public Component.Path getSioType(){
        Component.Path sioType = this.sioType;
        if (null != sioType)
            return sioType;
        else {
            throw new alto.sys.Error.State("Missing Sio Type");
        }
    }
    protected final boolean setSioTypeFrom(Reference reference)
        throws java.io.IOException
    {
        try {
            this.sioType = Sio.Type.From(reference);
        }
        catch (alto.sys.Error ignore){
        }
        return (null != this.sioType);
    }
    public void sioRead(Input in)
        throws java.io.IOException
    {
        Component.Path type = Sio.Head.Read(in);
        Component.Path thisType = this.getSioType();
        if (thisType.equals(type)){

            if (!thisType.hasType())
                this.sioType = type;

            super.sioRead(in);
        }
        else
            throw new Error(type,"Wrong type");
    }
    public void sioWrite(Output out)
        throws java.io.IOException
    {
        Component.Path type = this.getSioType();
        if (null != type){

            Sio.Head.Write(type,out);

            super.sioWrite(out);
        }
        else
            throw new alto.sys.Error.State("Missing Sio Type");
    }
}
