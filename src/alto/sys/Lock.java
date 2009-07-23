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

/**
 * <p> Locking abstraction permits producers and consumers to specify
 * and employ locking semantics and operations independently.  </p>
 * 
 * @author jdp
 * @since 1.1
 */
public interface Lock {
    /**
     * More features not available for all implementors.
     */
    public interface Advanced
        extends Lock
    {

        public int lockReadLockCount();

        /**
         * @param millis Timeout
         */
        public boolean lockReadEnterTry(long millis) throws java.lang.InterruptedException ;

        public int lockWriteHoldCount();

        /**
         * @param millis Timeout
         */
        public boolean lockWriteEnterTry(long millis) throws java.lang.InterruptedException ;
    }
    /**
     * <p> A CAS Semaphore lock.  Acquire the write lock, then compare
     * and set the semaphore.  If the semaphore CAS operation was
     * successful, remain in the write lock and return true.
     * Otherwise exit the write lock and return false.  </p>
     * 
     * <p> This interface is used in the neighborhood of {@link
     * alto.net.shm.Connection SHM} to denote its Request-
     * Response process locking, however in those cases the normal
     * lock write enter method without arguments is used. </p>
     */
    public interface Semaphore
        extends Lock
    {
        /**
         * @return Entered lock on successful CAS set.
         */
        public boolean lockWriteEnter(byte cas_current, byte cas_next);
        /**
         * @return Entered lock on successful CAS set.
         */
        public boolean lockWriteEnterTry(byte cas_current, byte cas_next);
    }
    /**
     * <p> An inert lock used to simplify performant blocks.
     * </p>
     * 
     * @author jdp
     */
    public final class Nil 
        extends java.lang.Object
        implements Lock.Semaphore
    {
        public final static Nil Instance = new Nil();

        public Nil(){
            super();
        }

        public final boolean lockReadEnterTry(){
            return true;
        }
        public final void lockReadEnter(){
            return;
        }
        public final void lockReadExit(){
            return;
        }
        public final boolean lockWriteEnterTry(){
            return true;
        }
        public final boolean lockWriteEnterTry(byte cas_current, byte cas_next){
            return true;
        }
        public final boolean lockWriteEnter(byte cas_current, byte cas_next){
            return true;
        }
        public final void lockWriteEnter(){
            return;
        }
        public final void lockWriteExit(){
            return;
        }
    }

    public boolean lockReadEnterTry();

    public void lockReadEnter();

    public void lockReadExit();

    public boolean lockWriteEnterTry();

    public void lockWriteEnter();

    public void lockWriteExit();

}
