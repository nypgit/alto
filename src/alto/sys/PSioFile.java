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

import alto.io.Input;
import alto.io.Output;
import alto.io.u.Chbuf;
import alto.lang.Address;
import alto.lang.Component;
import alto.lang.Sio;
import alto.lang.Type;


/**
 * 
 */
public abstract class PSioFile
    extends alto.lang.sio.File
    implements P,
               Sio.Type.Group
{


    protected boolean root;

    protected Reference reference;


    /**
     * Sio group constructor
     */
    public PSioFile(Reference reference)
        throws java.io.IOException
    {
        super();
        this.setReference(reference);
    }
    /**
     * Sio record constructor
     * @see Address
     */
    protected PSioFile(alto.io.Input sio)
        throws java.io.IOException
    {
        super(sio);
    }
    /**
     * Sio record constructor
     * @see Address
     */
    protected PSioFile(){
        super();
    }


    /**
     * A "root" message has a "host local" address.  A root key
     * protected by human system administrators.  User and host keys
     * are protected by root keys.
     */
    public boolean isRoot(){
        return this.root;
    }
    /**
     * A "user" message is not in a "host local" address.
     */
    public boolean isUser(){
        return (!this.root);
    }
    public boolean isTransient(){
        return false;
    }
    public boolean isPersistent(){
        return true;
    }
    public abstract void readMessage(Input in)
        throws java.io.IOException;

    public abstract void writeMessage(Output out)
        throws java.io.IOException;

    protected boolean isInstanceEmpty(){
        return (this.hasNotSioTag() && this.existsStorage());
    }
    public final alto.lang.Message init(){
        if (this.isInstanceEmpty()){
            try {
                this.readMessage();
            }
            catch (Exception exc){
                throw new alto.sys.Error.State(this.getReferenceString(),exc);
            }
        }
        return this;
    }
    /**
     */
    public void formatMessage()
        throws java.io.IOException
    {}

    public final void readMessage()
        throws java.io.IOException
    {
        Input in = this.reference.openInput();
        try {
            /*
             * Read container
             */
            this.sioRead(in);
        }
        finally {
            in.close();
        }

        in = this.openInput();
        try {
            /*
             * Read buffer content
             */
            this.readMessage(in);
        }
        finally {
            in.close();
        }
    }
    public final void writeMessage()
        throws java.io.IOException
    {
        this.resetall();

        Output out = this.openOutput();
        try {
            /*
             * Write buffer content
             */
            this.writeMessage(out);
        }
        finally {
            out.close();
        }

        out = this.reference.openOutput(this);
        try {
            /*
             * Write container
             */
            this.sioWrite(out);
        }
        finally {
            out.close();
        }
    }

    @Override
    public Component.Path getSioType(){
        Component.Path sioType = this.sioType;
        if (null != sioType)
            return sioType;
        else {
            try {
                sioType = Sio.Type.From(this.reference);
                if (null != sioType){
                    this.sioType = sioType;
                    return sioType;
                }
                else
                    throw new alto.sys.Error.State("Missing Sio Type");
            }
            catch (java.io.IOException exc){
                throw new alto.sys.Error.State(this.reference.toString(),exc);
            }
            catch (alto.sys.Error exc){
                throw new alto.sys.Error.State(this.reference.toString(),exc);
            }
        }
    }

    public final Reference getReference(){
        return this.reference;
    }
    public final void setReference(Reference reference)
        throws java.io.IOException
    {
        if (null != reference){
            this.reference = reference;
            this.root = reference.inAddressContainer(ROOT);
            this.setSioTypeFrom(reference);
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public final Address getAddress(){
        return this.reference.getAddress();
    }
    public final java.lang.String getReferenceString(){
        return this.reference.toString();
    }
    public final boolean existsStorage(){
        return this.reference.existsStorage();
    }
    public final boolean existsNotStorage(){
        return this.reference.existsNotStorage();
    }
    public final long getStorageLastModified(){
        return this.reference.getStorageLastModified();
    }
    public final java.lang.String getStorageLastModifiedString(){
        return this.reference.getStorageLastModifiedString();
    }
    public final long getStorageLength(){
        return this.reference.getStorageLength();
    }

    public java.lang.String toString(){
        return this.reference.toString();
    }
    public int hashCode(){
        return this.reference.hashCode();
    }
    /**
     * Strong object class and reference string equivalence.  
     * @see alto.sec.Keys
     */
    public boolean equals(java.lang.Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano.getClass().equals(this.getClass()))
            return this.reference.equals(((PSioFile)ano).getReference());
        else
            return false;
    }
}
