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

import alto.io.Code;
import alto.io.Check;
import alto.io.Principal;
import alto.lang.HttpMessage;
import alto.lang.Type;

/**
 * A {@link File} write (including delete) transaction is a file lock
 * constructed on a named version, typically <code>"current"</code>.
 * It provides a transactional output stream to {@link File} for
 * {@link Reference}, and the versioning methods commit, discard and
 * revert.
 * 
 * <h3>Operation</h3>
 * 
 * The transaction writes to a version named "temporary", which is a
 * locked file.  When the write to "temporary" is complete, the
 * transaction commits to the constructed named version, typically
 * "current", copying a preexisting "current" into a new numeric
 * version.  
 * 
 * <h3>Locking</h3>
 * 
 * The file transaction is also a key software device the resource
 * locking system.
 * 
 * <h3>Timeout</h3>
 * 
 * A file transaction is constructed and started as a thread in every
 * use case.  The thread times- out after a default or requested-
 * acceptable number of seconds, typically less than ten.  On timeout,
 * an open transaction is discarded.
 * 
 * <h3>Notes</h3>
 * 
 * Of course, use of a constructed named version not "current" would
 * create an unusual and undesireable set of versions in a space also
 * having a named "current".  Or in other words, writes (transactions)
 * to named versions other than "current" is not intended.
 * 
 * <h3>UI</h3>
 * 
 * On topic, user interfaces in areas with many frequent writes should
 * delete intermediate versions in a user session.  Numeric versions
 * are accessible by numeric address, exclusively.
 * 
 * 
 * @author jdp
 */
public interface FileTransaction
    extends java.lang.Runnable
{

    /**
     * @return Has file output stream.  Otherwise released.
     */
    public boolean isOpen();

    public boolean isCurrent();

    public File getFileCurrent();

    public File getFileNamed();

    public File getFileVersion();

    public File getFileTemp();

    public FileOutputStream getFileOutputStream(HttpMessage message)
        throws java.io.FileNotFoundException;

    /**
     * @return Has lock and lock is valid.  Otherwise released.
     */
    public boolean isHeld();

    public boolean isNotHeld();

    public Principal getHeldBy();

    public boolean isHeldBy();

    public boolean isHeldBy(Principal p);

    public boolean setLastModified(long last);

    /**
     * Wait for the transaction to complete.
     */
    public void waitfor()
        throws java.lang.InterruptedException;

    /**
     * Wait as many as 'to' milliseconds for the transaction to
     * complete.
     */
    public void waitfor(long to)
        throws java.lang.InterruptedException;

    /**
     * Called from reference network lock release and {@link
     * FileOutputStream#close()} to call File Output Stream (close).
     * 
     * @return Once for lock
     */
    public boolean release();

    /**
     * Rename "named" to "version", and "temporary" to "named".
     * Anything that fails attempts to discard "temporary".  If the
     * second operation (rename temporary to named) fails then the
     * commit process attempts to undo the rename of named to version
     * by renaming version to named -- before discarding the temporary
     * file.
     * 
     * There is a network level race between the rename of named to
     * version, and the rename of temporary to named.  An unprotected
     * operation on named (e.g. GET) in this race window may fail.  
     */
    public void commit()
        throws java.io.IOException;

    /**
     * Rename current to version, and named to current.  Naturally, if
     * named is current do nothing.  If the second operation (rename
     * named to current) fails, then this proceedure attempts to undo
     * the rename of current to version by renaming version to
     * current.
     * @see #discard()
     */
    public boolean revert()
        throws java.io.IOException;

    /**
     * Invoked from an API call to {@link FileOutputStream#discard()} or otherwise indirectly
     */
    public void discard()
        throws java.io.IOException;

}
