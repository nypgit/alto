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
import alto.sys.Reference;

/**
 * This class reads and writes SIO file format.
 */
public abstract class File
    extends Abstract
    implements Sio.Type.TypeClass
{

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
