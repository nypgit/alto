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
package alto.io.u;

/**
 * <p> An instance of objmap that includes a Lock and API for external
 * users.  No locking is performed internally, excepting the calls
 * made by the users of one of these objects to its Lock API. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public class Lobjmap
    extends Objmap
    implements alto.sys.Lock.Advanced
{

    private alto.sys.Lock.Advanced lock =
        new alto.sys.lock.Light();


    public Lobjmap(){
        super();
    }


    public final Lobjmap cloneLobjmap(){
        return this.cloneLobjmap(true);
    }
    public final Lobjmap cloneLobjmap(boolean newlock){
        Lobjmap clone = (Lobjmap)super.cloneObjmap();
        if (newlock)
            clone.lock = new alto.sys.lock.Light();
        return clone;
    }
    public final int lockReadLockCount(){
        return this.lock.lockReadLockCount();
    }
    public final boolean lockReadEnterTry(){
        return this.lock.lockReadEnterTry();
    }
    public final boolean lockReadEnterTry(long millis) 
        throws java.lang.InterruptedException 
    {
        return this.lock.lockReadEnterTry();
    }
    public final void lockReadEnter(){
        this.lock.lockReadEnter();
    }
    public final void lockReadExit(){
        this.lock.lockReadExit();
    }
    public final int lockWriteHoldCount(){
        return this.lock.lockWriteHoldCount();
    }
    public final boolean lockWriteEnterTry(){
        return this.lock.lockWriteEnterTry();
    }
    public final boolean lockWriteEnterTry(long millis) 
        throws java.lang.InterruptedException 
    {
        return this.lock.lockWriteEnterTry(millis);
    }
    public final void lockWriteEnter(){
        this.lock.lockWriteEnter();
    }
    public final void lockWriteExit(){
        this.lock.lockWriteExit();
    }
}
