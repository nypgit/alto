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
import alto.lang.Address;
import alto.lang.Buffer;
import alto.lang.Caller;
import alto.lang.Component;
import alto.lang.Date;
import alto.lang.HttpMessage;
import alto.lang.Socket;


/**
 * <p> This class caches file system state and content for "unique
 * instance usage" to minimize stats and reads to the underlying file
 * system.  </p>
 * 
 * <p> A unique instance of this class in the runtime is able to employ a
 * reckoning strategy over the state of the underlying file system.
 * All file system changes including reads and writes pass through the
 * unique instance, giving it knowledge of when the underlying state
 * changes.  </p>
 * 
 * <h3>Usage</h3>
 * 
 * <p> Application code employs {@link alto.sys.Reference}.  Only the
 * system code employs this class.  </p>
 * 
 * 
 * @author jdp
 * @since 1.6
 */
public abstract class File 
    extends java.io.File
    implements alto.sys.Destroy
{

    public final static String ETag(java.io.File file){

        return ETag(file.length(),file.lastModified());
    }
    public final static String ETag(long length, long lastmod){
        /*
         * Communicable precision in HTTP timestamps is seconds.
         * ETags are more useful when consistent across HTTP copies,
         * therefore they need to be constructed with no more
         * precision than seconds.
         */
        long mod = (lastmod/1000L);
        long tag = (length ^ mod);
        return alto.io.u.Hex.encode(tag);
    }
    /**
     * @param dir Call <code>"dir.mkdirs()"</code> if dir does not
     * exist.
     */
    protected final static void EnsureContainer(java.io.File dir)
        throws java.io.IOException
    {
        if (null != dir && (!dir.exists()))
            dir.mkdirs();
    }
    /**
     * @param file For file parent dir, call
     * <code>"dir.mkdirs()"</code> if file (and parent directory) does
     * not exist.
     */
    protected final static void EnsureContainerOf(java.io.File file)
        throws java.io.IOException
    {
        if (file.exists())
            return;
        else {
            java.io.File parent = file.getParentFile();
            if (null != parent){
                EnsureContainer(parent);
            }
        }
    }
    protected final static boolean Delete(java.io.File file){
        try {
            /*
             * Since there is no good way to see if a directory
             * contains children, just try to delete it and see if
             * that fails.  
             */
            if (file.delete()){
                java.io.File parent = file.getParentFile();
                if (null != parent){
                    Delete(parent);
                }
                return true;
            }
            else
                return false;
        }
        catch (Throwable t){
            t.printStackTrace();
            return false;
        }
    }
    protected final static boolean Delete(File file){
        try {
            /*
             * Since there is no good way to see if a directory
             * contains children, just try to delete it and see if
             * that fails.  
             */
            if (file.resolveDelete()){
                java.io.File parent = file.getParentFile();
                if (null != parent){
                    Delete(parent);
                }
                return true;
            }
            else
                return false;
        }
        catch (Throwable t){
            t.printStackTrace();
            return false;
        }
    }
    protected final static void Commit(java.nio.channels.WritableByteChannel dstc)
        throws java.io.IOException
    {
        if (dstc instanceof java.io.OutputStream){
            ((java.io.OutputStream)dstc).flush();
        }
    }


    protected final alto.sys.Lock.Advanced lock = 
        new alto.sys.lock.Light();

    protected final Partition partition;

    protected final Address address;

    protected volatile boolean statExists, statIsfile, dirty;

    protected volatile long statLast, statLength; 

    protected volatile String statLastString, statLastTag;

    protected volatile boolean cached, cachedInit, hasPolicy;

    protected volatile FileTransaction transaction;

    protected volatile HttpMessage message;

    protected volatile Object content;


    /**
     * 
     */
    private File(Partition partition, Address address, String fsloc){
        super(fsloc);
        this.partition = partition;
        this.address = address;
        this.stat(true);
    }
    /**
     * Used by {@link FileCache}, exclusively.  
     */
    public File(Partition parent, Address address){
        this(parent,address,parent.pathTo(address));
    }


    protected abstract HttpMessage newHttpMessage();

    @Code(Check.Locking)
    public HttpMessage read()
        throws java.io.IOException
    {
        HttpMessage message = this.message;
        if (null != message)
            return message;
        else if (this.isFile()){
            this.lockReadEnter();
            try {
                /*
                 * Read message from storage
                 */
                message = this.newHttpMessage();
                synchronized(this){
                    if (null != this.message){
                        this.message = message;
                    }
                    else {
                        this.message = message;
                        alto.io.Input in = this.openMessageInput();
                        message.readMessage(in);
                        message = this.message;
                        return message;
                    }
                }
                return null;
            }
            finally {
                this.lockReadExit();
            }
        }
        else
            return null;
    }
    @Code(Check.Locking)
    public HttpMessage write()
        throws java.io.IOException
    {
        HttpMessage message = this.message;
        if (null != message)
            return message.cloneHttpMessage();

        else {
            message = this.newHttpMessage();
            this.message = message;
            return message.cloneHttpMessage();
        }
    }
    @Code(Check.Locking)
    public boolean write(HttpMessage message)
        throws java.io.IOException
    {
        if (this.isValidForWrite(message)){
            if (this.isNotPersistent()){
                synchronized(this){
                    this.message = message;
                    this.stat(message.getContentLength(),message.getLastModified());
                }
            }
            else {
                /*
                 * [TODO] Asynchronous Cache Writes 
                 */
                alto.io.Output out = this.openMessageOutput(message);
                try {
                    message.writeMessage(out);
                }
                finally {
                    out.close();
                }
                synchronized(this){
                    this.message = message;
                }
                FileManager.DFSNotifyPUT(this);
            }
            return true;
        }
        else
            return false;
    }
    @Code(Check.Locking)
    public boolean copyTo(HttpMessage dst)
        throws java.io.IOException
    {
        HttpMessage message = this.read();
        if (null != message)
            return message.copyTo(dst);
        else
            return false;
    }
    @Code(Check.Locking)
    public boolean copyTo(File dst)
        throws java.io.IOException
    {
        HttpMessage message = this.read();
        if (null != message){
            dst.write(message);
            return true;
        }
        else
            return false;
    }
    @Code(Check.Locking)
    @Override
    public boolean renameTo(java.io.File dst){
        this.lockWriteEnter();
        try {
            if (this.hasFileTransaction()){
                FileTransaction transaction = this.getFileTransaction();
                if (transaction.isHeldBy()){

                    if (Caller.IsNotIn(FileTransaction.class))
                        transaction.release();

                    if (super.renameTo(dst)){
                        this.drop();
                        return true;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            else if (super.renameTo(dst)){
                this.drop();
                return true;
            }
            else
                return false;
        }
        finally {
            this.lockWriteExit();
        }
    }
    @Code(Check.Locking)
    public boolean revert()
        throws java.io.IOException
    {
        Principal by = Thread.GetPrincipal(true);
        this.lockWriteEnter();
        try {
            FileTransaction transaction = this.getCreateFileTransaction(by);
            if (transaction.isHeldBy()){

                if (transaction.revert()){

                    this.stat(true);

                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }
        finally {
            this.lockWriteExit();
        }
    }
    @Code(Check.Locking)
    public boolean delete(){
        this.lockWriteEnter();
        try {
            if (this.hasFileTransaction()){
                FileTransaction transaction = this.getFileTransaction();
                if (transaction.isHeldBy()){

                    transaction.release();

                    if (Delete(this)){
                        this.dropBuffer();
                        return true;
                    }
                    else
                        return false;
                }
                else
                    return false;
            }
            else if (Delete(this)){
                this.dropBuffer();
                return true;
            }
            else
                return false;
        }
        finally {
            this.lockWriteExit();
        }
    }
    /**
     * Alias for {@link #dropBuffer()}. 
     */
    public void flush(){
        this.dropBuffer();
    }
    public boolean hasSubjectPolicy(){
        return this.hasPolicy;
    }
    public Object getContent(){
        return this.content;
    }
    /**
     * @return Argument on success
     */
    public Object setContent(Object content){

        return (this.content = content);
    }
    public Object dropContent(){
        Object content = this.content;
        this.content = null;
        this.hasPolicy = false;
        return content;
    }
    public alto.io.Uri getUri(){
        return this.address.getUri();
    }
    public String getETag(){
        HttpMessage message = this.message;
        if (null != message)
            return message.getETag();
        else {
            String statLastTag = this.statLastTag;
            if (null == statLastTag){
                statLastTag = ETag(this);
                this.statLastTag = statLastTag;
            }
            return statLastTag;
        }
    }
    public Address getAddress(){
        return this.address;
    }
    public boolean isAddressToCurrent(){
        return this.address.isAddressToCurrent();
    }
    /**
     * @return Writes are to "temporary", and then moved to named
     * version
     */
    public boolean isTransactional(){
        return this.address.isTransactional();
    }
    /**
     * @return Writes directly to target file
     */
    public boolean isNotTransactional(){
        return this.address.isNotTransactional();
    }
    /**
     * @return Writes into disk cache
     */
    public boolean isPersistent(){
        return this.address.isPersistent();
    }
    /**
     * @return Writes into memory
     */
    public boolean isNotPersistent(){
        return this.address.isNotPersistent();
    }
    /**
     * DFS copy-in from remote only proceeds on file objects marked
     * dirty.  Ensures "copy in once" for multiple DFS responses.
     */
    public void markDirty(){
        this.dirty = true;
    }
    public boolean isDirty(){
        return this.dirty;
    }
    public abstract boolean isCached();

    public boolean exists(){
        return (this.statExists);
    }
    public boolean canRead(){
        return (this.statExists);
    }
    public boolean canWrite(){
        return (true);
    }
    public boolean isDirectory(){
        if (this.statExists)
            return (!this.statIsfile);
        else
            return false;
    }
    /**
     * Local GET test
     */
    @Code(Check.Review)
    public boolean isFile(){
        if (this.statExists)
            return this.statIsfile;

//         else if (this.dirty){
//             if (FileManager.DFSRequestGET(this)){
//                 if (0L < dfs_timeout)
//                     this.waitforDFS(DFS_TIMEOUT);
//                 else
//                     throw new alto.sys.Error.Argument("Invalid network timeout '"+dfs_timeout+"'.");
//             }
//             return this.isFile();
//         }
        else
            return false;
    }
    public synchronized void notifyDFS(){
        this.notifyAll();
    }
    public synchronized void waitforDFS(long timeout)
        throws java.lang.InterruptedException
    {
        this.wait(timeout);
    }
    public long getLastModified(){
        HttpMessage message = this.message;
        if (null != message)
            return message.getLastModified();
        else
            return this.statLast;
    }
    public java.lang.String getLastModifiedString(){
        HttpMessage message = this.message;
        if (null != message)
            return message.getLastModifiedString();
        else
            return this.lastModifiedString();
    }
    public long lastModified(){
        return this.statLast;
    }
    public java.lang.String lastModifiedString(){
        java.lang.String statLastString = this.statLastString;
        if (null == statLastString){
            long statLast = this.statLast;
            if (0L < statLast){
                statLastString = Date.ToString(statLast);
                this.statLastString = statLastString;
            }
        }
        return statLastString;
    }
    public long length(){
        return this.statLength;
    }
    public long statSize(){
        if (null != this.message)
            return this.statLength;
        else
            return 0;
    }
    /**
     * Required headers
     * <pre>
     * Location
     * Content-Type
     * Content-Length
     * ETag
     * Last-Modified
     * Content-MD5
     * </pre>
     * 
     * @return True for message authenticated and having required headers.
     * @exception alto.sys.UnauthorizedException For an
     * authenticated message with required headers but not meeting the
     * access control assertion.
     */
    public abstract boolean isValidForWrite(HttpMessage message)
        throws java.io.IOException,
               alto.sys.UnauthorizedException;

    /**
     * Used internally to drop the buffer on writing to the underlying
     * file.
     */
    public void dropBuffer(){
        this.message = null;
    }
    public void ensureContainer()
        throws java.io.IOException
    {
        EnsureContainerOf(this);
    }
    /**
     * Read Http Message.
     */
    @Code(Check.Locking)
    private FileInputStream openMessageInput()
        throws java.io.IOException
    {
        this.lockReadEnter();
        try {
            return new FileInputStream(this);
        }
        catch (java.io.FileNotFoundException exc){
            throw new NotFoundException(exc,"Not found");
        }
        finally {
            this.lockReadExit();
        }
    }
    /**
     * Write Http Message. 
     * 
     * @exception java.io.IOException 
     * @exception java.lang.SecurityException A unique output conflict
     * is represented by a {@link alto.sys.ConflictException}.
     */
    @Code(Check.Locking)
    private FileOutputStream openMessageOutput(HttpMessage message)
        throws java.io.IOException, java.lang.SecurityException
    {
        if (this.isNotPersistent())
            throw new alto.sys.Error.Bug();

        else if (this.isNotTransactional()){

            this.ensureContainer();

            return new FileOutputStream(this,message);
        }
        else {
            Principal by = Thread.GetPrincipal(true);
            this.lockWriteEnter();
            try {
                FileTransaction transaction = this.getCreateFileTransaction(by);

                return transaction.getFileOutputStream(message);
            }
            finally {
                this.lockWriteExit();
            }
        }
    }
    public FileTransaction getFileTransaction(){
        return this.transaction;
    }
    public boolean networkLockAcquire(long timeout)
        throws java.io.IOException
    {
        Principal by = Thread.GetPrincipal(true);
        FileTransaction flock = this.getCreateFileTransaction(by,timeout);
        return (flock.isHeldBy());
    }
    public boolean networkLockRelease()
        throws java.io.IOException
    {
        return this.releaseFileTransaction();
    }
    /**
     * Reference network lock release
     */
    public boolean releaseFileTransaction(){
        this.lockWriteEnter();
        try {
            FileTransaction transaction = this.transaction;
            if (null == transaction)
                return false;
            else
                return transaction.release();
        }
        finally {
            this.lockWriteExit();
        }
    }
    public void closeFileTransaction(FileTransaction transaction, HttpMessage message){
        this.lockWriteEnter();
        try {
            if (transaction == this.transaction){
                this.transaction = null;
                this.stat(message);
                return;
            }
            else
                throw new ConflictException();
        }
        finally {
            this.lockWriteExit();
        }
    }
    public void setFileTransaction(FileTransaction transaction){
        if (null != this.transaction){
            if (null == transaction)
                this.transaction = null;
            else if (transaction == this.transaction)
                return;
            else
                throw new alto.sys.ConflictException();
        }
        else
            this.transaction = transaction;
    }
    /**
     * Have an open {@link FileTransaction}
     */
    public boolean hasFileTransaction(){
        return (null != this.transaction);
    }
    public boolean hasNotFileTransaction(){
        return (null == this.transaction);
    }
    /**
     * Have a valid file lock (in a {@link FileTransaction}).
     */
    public boolean isFileTransactionHeld(){
        FileTransaction transaction = this.getFileTransaction();
        if (null != transaction)
            return transaction.isHeld();
        else
            return false;
    }
    public boolean isNotFileTransactionHeld(){
        FileTransaction transaction = this.getFileTransaction();
        if (null != transaction)
            return transaction.isNotHeld();
        else
            return true;
    }
    public boolean isFileTransactionHeldBy(){
        FileTransaction transaction = this.getFileTransaction();
        if (null != transaction)
            return transaction.isHeldBy();
        else
            return false;
    }
    public Principal getFileTransactionHeldBy(){
        FileTransaction transaction = this.getFileTransaction();
        if (null != transaction)
            return transaction.getHeldBy();
        else
            return null;
    }
    public Address[] listItems(){
        Address address = this.address;
        if (address.isAddressToClass()){
            String[] flist = this.list();
            if (null != flist){
                Component relation = address.getComponentRelation();
                Component container = address.getComponentContainer();
                Component typeclass = address.getComponentClass();
                Component version = Component.Version.Current;
                Component[] base = {
                    relation,
                    container,
                    typeclass
                }, component;
                Address[] list = null;
                for (int cc = 0, count = flist.length; cc < count; cc++){
                    component = Component.List.Add(base,flist[cc]);
                    component = Component.List.Add(component,version);
                    list = Address.List.Add(list,new Address(component));
                }
                return list;
            }
            else
                return null;
        }
        else
            throw new alto.sys.Error.Bug();
    }
    public Address[] listVersions(){
        Address address = this.address;
        if (address.isAddressBasePath()){
            String[] flist = this.list();
            if (null != flist){
                Address[] list = null;
                for (int cc = 0, count = flist.length; cc < count; cc++){
                    String name = flist[cc];
                    Address add = new Address(address,name);
                    list = Address.List.Add(list,add);
                }
                return list;
            }
            else
                return null;
        }
        else if (address.hasComponentTerminal()){
            Address base = address.getAddressBasePath();
            File basef = FileManager.Instance().getStorage(base);
            return basef.listVersions();
        }
        else
            throw new alto.sys.Error.Bug();
    }
    public Component.Numeric lastVersion(){
        Address address = this.address;
        if (address.isAddressBasePath()){
            String[] flist = this.list();
            if (null != flist){
                Component.Numeric last = null;
                for (int cc = 0, count = flist.length; cc < count; cc++){
                    String name = flist[cc];
                    Component.Version test = Component.Version.Tools.ValueOf(name);
                    if (test instanceof Component.Numeric){
                        if (null == last)
                            last = (Component.Numeric)test;
                        else if (1 == test.compareTo( (Component)last))
                            last = (Component.Numeric)test;
                    }
                }
                return last;
            }
            else
                return null;
        }
        else if (address.hasComponentTerminal()){
            Address base = address.getAddressBasePath();
            File basef = FileManager.Instance().getStorage(base);
            return basef.lastVersion();
        }
        else
            throw new alto.sys.Error.Bug();
    }
    public Address lastVersionAddress(){
        Component.Numeric lastVersion = this.lastVersion();
        return new Address(this.address.getAddressPath(),lastVersion);
    }
    public File lastVersionFile(){
        FileManager fm = (FileManager)FileManager.Instance();
        Address lastVersion = this.lastVersionAddress();
        return fm.getStorage(lastVersion);
    }
    public Component.Numeric newVersion(){
        Component.Numeric last = this.lastVersion();
        if (null == last)
            return (Component.Numeric)Component.Version.ONE;
        else
            return last.add((Component.Numeric)Component.Version.ONE);
    }
    public Address newVersionAddress(){
        Component.Numeric newVersion = this.newVersion();
        return new Address(this.address.getAddressPath(),newVersion);
    }
    public File newVersionFile(){
        FileManager fm = (FileManager)FileManager.Instance();
        Address newVersion = this.newVersionAddress();
        return fm.getStorage(newVersion);
    }
    public Component tempVersion(){
        return Component.Version.Temp;
    }
    public Address tempVersionAddress(){
        Component tempVersion = this.tempVersion();
        return new Address(this.address.getAddressPath(),tempVersion);
    }
    public File tempVersionFile(){
        FileManager fm = (FileManager)FileManager.Instance();
        Address tempVersion = this.tempVersionAddress();
        return fm.getStorage(tempVersion);
    }
    public Address currentVersionAddress(){
        Address address = this.address;
        if (address.isAddressToCurrent())
            return address;
        else {
            return new Address(this.address.getAddressPath(),Component.Version.Current);
        }
    }
    public File currentVersionFile(){
        Address currentVersion = this.currentVersionAddress();
        if (currentVersion == this.address)
            return this;
        else {
            FileManager fm = (FileManager)FileManager.Instance();
            return fm.getStorage(currentVersion);
        }
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
    public int hashCode(){
        return this.address.hashCode();
    }
    public String toString(){
        return this.address.toString();
    }
    public boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano instanceof File){
            File that = (File)ano;
            return this.address.equals(that.getAddress());
        }
        else
            return this.toString().equals(ano.toString());
    }
    public void destroy(){
        this.transaction = null;
        this.message = null;
        this.content = null;
        this.statExists = false;
        this.statLast = 0L;
        this.statLastString = null;
        this.statLastTag = null;
        this.statLength = 0L;
    }
    protected void stat(boolean test){
        if (test){
            this.message = null;
            this.dirty = false;
            this.statExists = super.exists();
            this.statIsfile = super.isFile();
            this.statLast = this.statUpdateLastModified();
            this.statLastString = null;
            this.statLastTag = null;
            this.statLength = super.length();
        }
        else {
            this.message = null;
            this.statExists = false;
            this.statIsfile = false;
            this.statLast = 0L;
            this.statLastString = null;
            this.statLastTag = null;
            this.statLength = 0L;
        }
    }
    protected long statUpdateLastModified(){
        HttpMessage message = this.message;
        if (null != message){
            long lastmod = message.getLastModified();
            super.setLastModified(lastmod);
            return lastmod;
        }
        else
            return super.lastModified();
    }
    protected void stat(HttpMessage message){
        if (null != message)
            this.stat(message.getLastModified(),message.getContentLength());
        else
            this.stat(true);
    }
    private void stat(long last, long length){
        boolean isfile = (this.isPersistent() && super.isFile());
        this.statExists = isfile;
        this.statIsfile = isfile;
        this.statLength = length;
        this.message = null;
        this.dirty = false;
        if (0L < last){
            this.setLastModified(last);
        }
        else {
            this.statLast = super.lastModified();
            this.statLastString = null;
            this.statLastTag = null;
        }
    }
    /**
     * Drop from cache occurs on move source and delete target.
     */
    protected abstract void drop();

    protected abstract boolean resolveDelete();

    protected abstract FileTransaction getCreateFileTransaction(Principal by)
        throws java.io.IOException;

    protected abstract FileTransaction getCreateFileTransaction(Principal by, long timeout)
        throws java.io.IOException;

}
