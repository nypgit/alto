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
    extends Sio.File
    implements P,
               Sio.Type.Group,
               alto.sys.Lock.Advanced
{


    protected boolean root;

    protected Reference reference;

    private final alto.sys.Lock.Advanced lock = 
        new alto.sys.lock.Light();


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
    protected final boolean setStorageContent(){
        try {
            return (null != this.reference.setStorageContent(this));
        }
        catch (java.io.IOException exc){
            throw new alto.sys.Error.State(exc);
        }
    }
    /**
     * Implementor calls {@link #authenticateFromRead} after
     * completing the read, in order to verify authentication.
     */
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
            catch (java.io.IOException exc){
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

    public void readMessage()
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
    public void writeMessage()
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

        out = this.reference.openOutput();
        try {
            /*
             * Write container
             */
            this.sioWrite(out);

            out.flush();
            /*
             * Authenticate created meta before closing
             */
            this.authenticateForWrite();
        }
        finally {
            out.close();
        }

        this.setStorageContent();
    }
    protected void authenticateFromRead()
        throws java.io.IOException
    {
    }
    protected void authenticateForWrite()
        throws java.io.IOException
    {
        if (this.reference.hasCreatedMeta())
            this.reference.authenticateCreatedMeta();
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
    public int lockReadLockCount(){
        return this.lock.lockReadLockCount();
    }
    public boolean lockReadEnterTry(){
        return this.lock.lockReadEnterTry();
    }
    public boolean lockReadEnterTry(long millis) throws java.lang.InterruptedException {
        return this.lock.lockReadEnterTry(millis);
    }
    public void lockReadEnter(){
        this.lock.lockReadEnter();
    }
    public void lockReadExit(){
        this.lock.lockReadExit();
    }
    public int lockWriteHoldCount(){
        return this.lock.lockWriteHoldCount();
    }
    public boolean lockWriteEnterTry(){
        return this.lock.lockWriteEnterTry();
    }
    public boolean lockWriteEnterTry(long millis) throws java.lang.InterruptedException {
        return this.lock.lockWriteEnterTry(millis);
    }
    public void lockWriteEnter(){
        this.lock.lockWriteEnter();
    }
    public void lockWriteExit(){
        this.lock.lockWriteExit();
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