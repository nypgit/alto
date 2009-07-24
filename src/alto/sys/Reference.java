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

import alto.io.Principal;
import alto.io.Check;
import alto.io.Code;
import alto.io.Uri;
import alto.lang.Address;
import alto.lang.Component;
import alto.lang.HttpMessage;
import alto.lang.HttpRequest;
import alto.lang.Type;
import alto.lang.Value;

import java.net.URL;
import java.net.URLConnection;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.Modifier;
import static javax.tools.JavaFileObject.Kind;

/**
 * The reference class is the primary user interface for handling URLs
 * in source and target programming relative to storage {@link Address
 * Addresses}.  
 * 
 * A reference can pull a resource from a URL, and then store it to an
 * internal location expressing that same URL.  Typically, however,
 * references are used in one mode or the other in the scope of the
 * stack frame lifetime of an instance.
 * 
 * <h3>Operation<h3>
 * 
 * The reference may be employed on a URL or Address.  To employ a
 * reference as a URL the <tt>toRemote()</tt> method must be called on
 * the reference.  Otherwise calling the <tt>toStorage()</tt> method
 * employs the reference in the default mode as operating on the
 * content storage subsystem.
 * 
 * @author jdp
 * @see Address
 * @since 1.6
 */
public abstract class Reference
    extends java.lang.Object
    implements IO.Edge,
               IO.FileObject,
               alto.io.Uri
{

    public abstract static class List {

        public final static Reference[] Add(Reference[] list, Reference c){
            if (null == c)
                return list;
            else if (null == list)
                return new Reference[]{c};
            else {
                int len = list.length;
                Reference[] copier = new Reference[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
        public final static Reference[] Add(Reference[] list, Reference[] c){
            if (null == c)
                return list;
            else if (null == list)
                return c;
            else {
                int len = list.length;
                int clen = c.length;
                Reference[] copier = new Reference[len+clen];
                System.arraycopy(list,0,copier,0,len);
                System.arraycopy(c,0,copier,len,clen);
                return copier;
            }
        }
        public final static boolean Equals(Reference[] a, Reference[] b){
            if (null == a)
                return (null == b);
            else if (null == b)
                return false;
            else if (a.length == b.length){
                for (int cc = 0, count = a.length; cc < count; cc++){
                    if (!a.equals(b))
                        return false;
                }
                return true;
            }
            else
                return false;
        }
    }
    public abstract static class Tools {
        /**
         * @param path A string
         * @return A string with no leading or trailing '/'.
         */
        public final static String Clean(String path){
            if (null == path)
                return null;
            else {
                int term = path.length()-1;
                if (0 < term){
                    if ('/' == path.charAt(term)){
                        path = path.substring(0,term);
                        if (1 > path.length())
                            return null;
                    }
                    if ('/' == path.charAt(0)){
                        path = path.substring(1);
                        if (1 > path.length())
                            return null;
                    }
                    return path;
                }
                else if (0 > term || '/' == path.charAt(0))
                    return null;
                else
                    return path;
            }
        }
        public final static String Parent(String path){
            if (null == path || 1 > path.length())
                return null;
            else {
                int ix1 = path.lastIndexOf('/');
                if (-1 < ix1){
                    boolean isfilep = (-1 < path.indexOf('.',ix1));
                    if (isfilep)
                        return path.substring(0,ix1);
                    else
                        return path;
                }
                else 
                    return null;
            }
        }
        public final static String Parent(Reference r){
            if (null == r)
                return null;
            else
                return Parent(r.toString());
        }
        /**
         * Scan once for <code>"/(src|bin)/"</code>.
         * 
         * @param path String containing infix "/src/" or "/bin/".
         * @return Index of first character (path separator) in one of
         * "/src/" or "/bin/", or negative one for not found.
         */
        public final static int IndexOfSrcBin(String path){
            if (null == path)
                return -1;
            else {
                if (File.pathSeparatorChar != '/'){
                    path = path.replace(File.pathSeparatorChar,'/');
                }
                char[] cary = path.toCharArray();

                int start = -1;
                boolean scan = false;
                for (int cc = 0, count = cary.length; cc < count; cc++){

                    switch (cary[cc]){
                    case '/':
                        /*
                         * First or last char is '/'
                         */
                        if (-1 < start && start == (cc-4))
                            return start;
                        else {
                            scan = true;
                            start = cc;
                        }
                        break;
                    case 's':
                    case 'b':
                        /*
                         * Second in "/(src|bin)/"
                         */
                        if (scan)
                            scan = (-1 < start && start == (cc-1));
                        break;
                    case 'i':
                    case 'r':
                        /*
                         * Third char in "/(src|bin)/"
                         */
                        if (scan)
                            scan = (-1 < start && start == (cc-2));
                        break;

                    case 'c':
                    case 'n':
                        /*
                         * Fourth char in "/(src|bin)/"
                         */
                        if (scan)
                            scan = (-1 < start && start == (cc-3));
                        break;
                    default:
                        scan = false;
                        break;
                    }
                }
                return -1;
            }
        }

        protected static Tools Instance;

        /**
         * @see alto.io.Tools
         */
        public final static void SInit(Tools instance){
            if (null == Instance){
                Instance = instance;
                if (instance instanceof alto.sys.Init){
                    ((alto.sys.Init)instance).init();
                }
            }
            else
                throw new Error.State();
        }

        public final static Reference Create(String uri){
            if (null != Instance)
                return Instance.create(uri);
            else
                throw new Error.Bug();
        }
        public final static Reference Create(String uri, Address addr){
            if (null != Instance)
                return Instance.create(uri,addr);
            else
                throw new Error.Bug();
        }
        public final static Reference Create(Address addr){
            if (null != Instance)
                return Instance.create(addr);
            else
                throw new Error.Bug();
        }
        public final static Reference Create(String hostname, Type type, String path){
            if (null != Instance){
                String uri = "http://"+alto.io.u.Chbuf.fcat(hostname,path);
                Address addr = new Address(Component.Host.Tools.ValueOf(hostname),Component.Type.Tools.ValueOf(type),Component.Path.Tools.ValueOf(path));
                return Instance.create(uri,addr);
            }
            else
                throw new Error.Bug();
        }
        public final static Reference Create(Component container, Component type, Component path){
            return Create(new Address(container,type,path));
        }
        public final static Reference Create(Component relation, Component container, Component type, Component path){
            return Create(new Address(relation,container,type,path));
        }
        public final static Reference Create(Component relation, Component container, Component type, Component path, Component version){
            return Create(new Address(relation,container,type,path,version));
        }
        public final static Reference Create(Component[] address){
            return Create(new Address(address));
        }
        public final static Reference Create(Component[] prefix, Component version){
            return Create(new Address(prefix,version));
        }

        public abstract Reference create(String uri);

        public abstract Reference create(String uri, Address addr);

        public abstract Reference create(Address addr);

    }


    protected final String string;

    protected Address address;

    protected HttpMessage createMeta;

    protected URL url;

    protected File storage;

    protected String path_request, fext;

    protected Type fextType;

    protected Uri parser;


    protected Reference(){
        super();
        this.string = null;
    }
    protected Reference(String string){
        super();
        this.string = string;
        this.address = new Address(string);
        this.parser = address.getUri();
    }
    protected Reference(Address address){
        super();
        this.address = address;
        this.string = address.getAddressReference();
        this.parser = address.getUri();
    }


    /**
     * This must be called before a reference will operate on remote
     * resources.  To invert the effect, call {@link #toStorage()}.
     */
    public URL toRemote()
        throws java.net.MalformedURLException
    {
        URL url = this.url;
        if (null == url){
            url = new URL(this.string);
            this.url = url;
            this.storage = null;
        }
        return url;
    }
    public alto.sys.File toStorage(){
        alto.sys.File file = this.storage;
        if (null == file){
            this.url = null;
            return this.getStorage();
        }
        return file;
    }
    public alto.sys.File getStorage(){
        alto.sys.File storage = this.storage;
        if (null == storage){
            Address address = this.getAddress();
            if (null != address){
                FileManager fm = FileManager.Instance();
                if (null != fm){
                    storage = fm.getStorage(address);
                    this.storage = storage;
                }
            }
        }
        return this.storage;
    }
    public alto.sys.File dropStorage(){
        alto.sys.File storage = this.storage;
        this.storage = null;
        Address address = this.getAddress();
        if (null != address){
            FileManager fm = FileManager.Instance();
            if (null != fm){
                storage = fm.dropStorage(address);
            }
        }
        return storage;
    }
    public boolean hasAddress(){
        return (null != this.address);
    }
    public boolean hasNotAddress(){
        return (null == this.address);
    }
    public Address getAddress(){
        return this.address;
    }
    public void setAddress(Address address){
        this.address = address;
    }
    public Reference toAddressRelation(Component.Relation relation){
        if (this.inAddressRelation(relation))
            return this;
        else
            return Reference.Tools.Create(this.getAddress().toRelation(relation));
    }
    public Component getAddressRelation(){
        Address address = this.getAddress();
        if (null != address)
            return address.getComponentRelation();
        else
            return null;
    }
    public boolean isAddressContainerLocal(){
        Component host = getAddressContainer();
        if (null != host)
            return Component.Host.Local.equals(host);
        else
            return false;
    }
    public boolean isAddressContainerGlobal(){
        Component host = getAddressContainer();
        if (null != host)
            return Component.Host.Global.equals(host);
        else
            return false;
    }
    public Component getAddressContainer(){
        Address address = this.getAddress();
        if (null != address)
            return address.getComponentContainer();
        else
            return null;
    }
    public Component getAddressClass(){
        Address address = this.getAddress();
        if (null != address)
            return address.getComponentClass();
        else
            return null;
    }
    public Reference toAddressClass(Component.Type type){
        if (this.inAddressClass(type))
            return this;
        else
            return Reference.Tools.Create(this.getAddress().toAddressClass(type));
    }
    public Component getAddressPath(){
        Address address = this.getAddress();
        if (null != address)
            return address.getComponentPath();
        else
            return null;
    }
    public Component getAddressTerminal(){
        Address address = this.getAddress();
        if (null != address)
            return address.getComponentTerminal();
        else
            return null;
    }
    public boolean inAddressRelation(Component type){
        Address address = this.getAddress();
        if (null != address)
            return address.inRelation(type);
        else
            return false;
    }
    public boolean inAddressContainer(Component type){
        Address address = this.getAddress();
        if (null != address)
            return address.inContainer(type);
        else
            return false;
    }
    public boolean inAddressContainerOf(Reference that){
        if (null != that){
            Address thisAddress = this.getAddress();
            if (null != thisAddress){
                Address thatAddress = that.getAddress();
                if (null != thatAddress)
                    return thisAddress.inContainerOf(thatAddress);
            }
        }
        return false;
    }
    public boolean inAddressClass(Component type){
        Address address = this.getAddress();
        if (null != address)
            return address.inClass(type);
        else
            return false;
    }
    public boolean inAddressClass(Type type){
        Address address = this.getAddress();
        if (null != address)
            return address.inClass(type);
        else
            return false;
    }
    public boolean isAddressMeta(){
        Address address = this.getAddress();
        if (null != address)
            return address.isMeta();
        else
            return false;
    }
    public boolean isNotAddressMeta(){
        Address address = this.getAddress();
        if (null != address)
            return address.isNotMeta();
        else
            return false;
    }
    public boolean isAddressPersistent(){
        Address address = this.getAddress();
        if (null != address)
            return address.isPersistent();
        else
            return false;
    }
    public boolean isAddressTransient(){
        Address address = this.getAddress();
        if (null != address)
            return address.isTransient();
        else
            return false;
    }
    public boolean isAddressTransactional(){
        Address address = this.getAddress();
        if (null != address)
            return address.isTransactional();
        else
            return false;
    }
    public boolean isNotAddressTransactional(){
        Address address = this.getAddress();
        if (null != address)
            return address.isNotTransactional();
        else
            return false;
    }
    public boolean isAddressToCurrent(){
        Address address = this.getAddress();
        if (null != address)
            return address.isAddressToCurrent();
        else
            return false;
    }
    public boolean isNotAddressToCurrent(){
        Address address = this.getAddress();
        if (null != address)
            return address.isNotAddressToCurrent();
        else
            return false;
    }
    public boolean nocache(){
        Address address = this.address;
        if (null != address)
            return address.nocache();
        else
            return false;
    }
    public long hashAddress(){
        Address address = this.address;
        if (null != address)
            return address.getHashAddress();
        else
            throw new alto.sys.Error.Bug();
    }
    public boolean isStoreValid(){
        return this.getAddress().isStoreValid();
    }
    public String getRequestPath(){
        String path = this.path_request;
        if (null == path){
            if (null != this.storage)
                path = this.getStoragePath();
            else 
                path = alto.io.u.Chbuf.fcat( "/", this.getPath());

            this.path_request = path;
        }
        return path;
    }
    /**
     * Read from storage
     */
    public HttpMessage read()
        throws java.io.IOException
    {
        File storage = this.getStorage();
        if (null != storage){
            try {
                return storage.read();
            }
            catch (alto.sys.UnauthorizedException exc){
                throw new alto.sys.UnauthorizedException(this.toString(),exc);
            }
        }
        else
            return null;
    }
    public boolean hasCreatedMeta(){
        return (null != this.createMeta);
    }
    public HttpMessage getCreatedMeta(){
        return this.createMeta;
    }

    protected abstract HttpMessage newHttpMessage(boolean store);

    /**
     * Write to createMeta buffer 
     * @see #close
     */
    public HttpMessage write()
        throws java.io.IOException
    {
        HttpMessage createMeta = this.createMeta;
        if (null == createMeta){
            /*
             * Write content (to storage or network)
             */
            createMeta = this.newHttpMessage(true);
            createMeta.setPathCompleteWithDefaults(this);
            File storage = this.getStorage();
            if (null != storage){
                storage.copyTo(createMeta);
                this.createMeta = createMeta;
            }
        }
        return createMeta;
    }
    /**
     * @return Success setting thread context from this reference.
     */
    public boolean enterThreadContextTry(){
        try {
            HttpMessage meta = this.read();
            return Thread.MaySetContext(meta);
        }
        catch (java.io.IOException exc){
            return false;
        }
    }
    /**
     * If there is an open created meta, and the current thread has no
     * context, then set the current thread context to the created
     * meta.  This permits the meta to authenticate and write, for
     * client or bootstrap operations.
     * 
     * This method assumes neither client side nor server side.  
     * 
     * @see alto.sec.Keys#authenticate()
     */
    public boolean enterThreadContextFromCreatedMetaTry(){
        HttpMessage createMeta = this.createMeta;
        if (null != createMeta)
            return Thread.MaySetContext(createMeta);
        else
            return false;
    }
    /**
     * Authenticate the created method from the {@link Thread}
     * context principal.
     * 
     * @see alto.io.Message#writeMessage()
     * @see PSioFile#authenticate()
     */
    @Code(Check.Locking)
    public void authenticateCreatedMeta()
        throws java.io.IOException
    {
        Principal principal = Thread.GetPrincipal(true);
        if (principal instanceof Principal.Authentic)
            this.authenticateCreatedMeta( (Principal.Authentic)principal);
        else if (null != principal)
            throw new alto.sys.Error.Bug(principal.getClass().getName());
        else 
            throw new alto.sys.Error.Bug("See 'sys/Reference#enterThreadContextFromCreatedMetaTry()'.");
    }
    /**
     * This method permits the created meta to be authenticated with
     * the argument principal when called before closing the created
     * meta.
     * 
     * @see alto.sec.Keys#authenticate()
     * @see alto.lang.HttpMessage#authenticateCreateCredentials(alto.io.Principal$Authentic)
     */
    public void authenticateCreatedMeta(Principal.Authentic principal)
        throws java.io.IOException
    {
        HttpMessage createMeta = this.createMeta;
        if (null != createMeta)
            createMeta.authenticateCreateCredentials(principal);
        else
            throw new alto.sys.Error.Bug();
    }
    @Code(Check.TODO)
    public void close()
        throws java.io.IOException
    {
        HttpMessage createMeta = this.createMeta;
        if (null != createMeta){
            try {
                URL url = this.url;
                if (null != url){
                    URLConnection connection = url.openConnection();
                    connection.setDoOutput(true);
                    if (connection instanceof alto.net.Connection){
                        alto.net.Connection nc = (alto.net.Connection)connection;
                        nc.setReference(this);
                        /////////////////////////[TODO]
                        //nc.write(createMeta);//[TODO]
                        /////////////////////////[TODO]
                        throw new alto.sys.Error.Bug(this.toString());
                        /////////////////////////[TODO]
                    }
                    else
                        throw new alto.sys.Error.Bug(this.toString());
                }
                File storage = this.getStorage();
                if (null != storage){

                    createMeta.authenticate();

                    if (storage.write(createMeta))
                        return;
                    else
                        throw new alto.sys.Error.State("Invalid Write");
                }
                else
                    throw new alto.sys.Error.Bug(this.toString());
            }
            finally {
                this.createMeta = null;
            }
        }
    }
    public Uri getUri(){
        return this;
    }
    public Uri getCreateParser(boolean meta){
        Uri parser = this.parser;
        if (null == parser){
            /*
             * (check existing)
             */
            Address address = this.address;
            if (null != address && address.hasHostPath()){
                parser = address.getCreateParser();
                this.parser = parser;
                return parser;
            }
            /*
             * (reach for meta)
             */
            if (meta){
                try {
                    String string = this.getContentLocation();
                    if (null != string){
                        parser = new alto.io.u.Uri(string);
                        this.parser = parser;
                    }
                }
                catch (java.io.IOException ignore){
                }
            }
            /*
             * (default)
             */
            String string = this.string;
            if (null != string){
                parser = new alto.io.u.Uri(string);
                this.parser = parser;
            }
        }
        return parser;
    }
    /**
     * @return If address is not null, return address storage path.
     */
    public String getStoragePath(){
        Address address = this.getAddress();
        if (null != address)
            return address.getPathStorage();
        else
            return null;
    }
    public Object getStorageContent()
        throws java.io.IOException
    {
        File storage = this.getStorage();
        if (null != storage)
            return storage.getContent();
        else
            return null;
    }
    /**
     * @return Argument on success
     */
    public Object setStorageContent(Object content)
        throws java.io.IOException
    {
        File storage = this.getStorage();
        if (null != storage)
            return storage.setContent(content);
        else
            return null;
    }
    public Object dropStorageContent()
        throws java.io.IOException
    {
        File storage = this.getStorage();
        if (null != storage)
            return storage.dropContent();
        else
            return null;
    }
    public boolean hasStorage(){
        return (null != this.getStorage());
    }
    public boolean hasNotStorage(){
        return (null == this.getStorage());
    }
    public boolean existsStorage(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.exists();
        else
            return false;
    }
    public boolean existsNotStorage(){
        File storage = this.getStorage();
        if (null != storage)
            return (!storage.exists());
        else
            return true;
    }
    public boolean isStorageFile(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.isFile();
        else
            return false;
    }
    public boolean isNotStorageFile(){
        File storage = this.getStorage();
        if (null != storage)
            return (!storage.isFile());
        else
            return false;
    }
    public long getStorageLastModified(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lastModified();
        else
            return 0L;
    }
    public java.lang.String getStorageLastModifiedString(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lastModifiedString();
        else
            return null;
    }
    public long getStorageLength(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.length();
        else
            return 0L;
    }
    public byte[] getBuffer()
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.getBuffer();
        else
            return null;
    }
    public int getBufferLength()
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.getBufferLength();
        else
            return 0;
    }
    public boolean get(HttpMessage response)
        throws java.io.IOException
    {
        return this.copyTo(response);
    }
    public boolean get(Reference target)
        throws java.io.IOException
    {
        return this.copyTo(target);
    }
    public boolean head(HttpMessage response)
        throws java.io.IOException
    {
        return this.headTo(response);
    }
    public boolean put(HttpMessage request)
        throws java.io.IOException
    {
        return this.copyFrom(request);
    }
    public boolean put(Reference source)
        throws java.io.IOException
    {
        source.copyTo(this);
        return true;
    }
    public boolean delete(){

        File storage = this.getStorage();
        if (null != storage)
            return storage.delete();
        else
            return false;
    }
    public boolean revert()
        throws java.io.IOException
    {
        File storage = this.getStorage();
        if (null != storage)
            return storage.revert();
        else
            return false;
    }
    public boolean copyFrom(HttpMessage request)
        throws java.io.IOException
    {
        if (null != request){
            if (request.hasNotLocation() && request instanceof HttpRequest)
                ((HttpRequest)request).setLocation();
            //
            try {
                this.createMeta = request;
                return true;
            }
            finally {
                this.close();
            }
        }
        else
            return false;
    }
    /**
     * @see alto.sx.methods.Copy
     * @return True for response Created, False for response Not
     * Found.
     */
    public boolean copyTo(Reference target)
        throws java.io.IOException
    {
        if (null != this.url){
            alto.io.Input in = this.openInput();
            try {
                alto.io.Output out = target.openOutput();
                try {
                    if (null != in && null != out){
                        int r = in.copyTo(out);
                        return (0 < r);
                    }
                }
                finally {
                    if (null != out)
                        out.close();
                }
            }
            finally {
                if (null != in)
                    in.close();
            }
            return false;
        }
        File storage = this.getStorage();
        if (null != storage){
            HttpMessage source = this.read();
            if (null != source)
                return target.copyFrom(source);
            else
                return false;
        }
        else
            throw new alto.sys.Error.Bug(this.string);
    }
    public boolean copyTo(HttpMessage response)
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.copyTo(response);
        else
            return false;
    }
    public boolean headTo(HttpMessage response)
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.headTo(response);
        else
            return false;
    }
    public boolean moveTo(Reference dst)
        throws java.io.IOException
    {
        File srcStorage = this.getStorage();
        if (null != srcStorage){
            File dstStorage = dst.getStorage();
            if (null != dstStorage)
                return srcStorage.renameTo(dstStorage);
        }
        return false;
    }
    public org.w3c.dom.Document readDocument()
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return Xml.Tools.ReadDocument(null,storage);
        else
            return null;
    }
    public void writeDocument(org.w3c.dom.Document doc)
        throws java.io.IOException
    {
        HttpMessage storage = this.write();
        try {
            Xml.Tools.WriteDocument(null,doc,storage);
        }
        finally {
            this.close();
        }
    }
    public Kind getKind(){
        try {
            Type type = this.getContentType();
            if (Type.Tools.Of("java") == type)
                return Kind.SOURCE;
            else if (Type.Tools.Of("class") == type)
                return Kind.CLASS;
            else if (Type.Tools.Of("html") == type)
                return Kind.HTML;
            else
                return Kind.OTHER;
        }
        catch (java.io.IOException exc){
            return Kind.OTHER;
        }
    }
    public boolean isNameCompatible(String simpleName, Kind kind){
        return true;
    }
    public NestingKind getNestingKind(){
        return null;
    }
    public Modifier getAccessLevel(){
        return null;
    }
    /**
     * Called from {@link alto.sx.methods.Lock}
     */
    public boolean networkLockAcquire(long timeout)
        throws java.io.IOException
    {
        File file = this.getStorage();
        if (null != file)
            return file.networkLockAcquire(timeout);
        else
            throw new alto.sys.Error.State(this.toString());
    }
    /**
     * Called from {@link alto.sx.methods.Unlock}
     */
    public boolean networkLockRelease()
        throws java.io.IOException
    {
        File file = this.getStorage();
        if (null != file)
            return file.networkLockRelease();
        else
            return false;
    }
    public int lockReadLockCount(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockReadLockCount();
        else
            return 0;
    }
    public boolean lockReadEnterTry(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockReadEnterTry();
        else
            return true;
    }
    public boolean lockReadEnterTry(long millis) throws java.lang.InterruptedException {
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockReadEnterTry(millis);
        else
            return true;
    }
    public void lockReadEnter(){
        File storage = this.getStorage();
        if (null != storage)
            storage.lockReadEnter();
    }
    public void lockReadExit(){
        File storage = this.getStorage();
        if (null != storage)
            storage.lockReadExit();
    }
    public int lockWriteHoldCount(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockWriteHoldCount();
        else
            return 0;
    }
    public boolean lockWriteEnterTry(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockWriteEnterTry();
        else
            return true;
    }
    public boolean lockWriteEnterTry(long millis) throws java.lang.InterruptedException {
        File storage = this.getStorage();
        if (null != storage)
            return storage.lockWriteEnterTry(millis);
        else
            return true;
    }
    public void lockWriteEnter(){
        File storage = this.getStorage();
        if (null != storage)
            storage.lockWriteEnter();
    }
    public void lockWriteExit(){
        File storage = this.getStorage();
        if (null != storage)
            storage.lockWriteExit();
    }
    protected Reference getReferenceIn(char select){
        String path = this.string;
        int idx = Reference.Tools.IndexOfSrcBin(path);
        if (-1 < idx){
            int idx4 = (idx+4);
            String test = path.substring(idx,idx4);
            if (select == test.charAt(1))
                return this;
            else {
                String newpath;
                switch (select){
                case 'b':
                    newpath = path.substring(0,idx)+"/bin/"+path.substring(idx4);
                    return Reference.Tools.Create(newpath);

                case 's':
                    newpath = path.substring(0,idx)+"/src/"+path.substring(idx4);
                    return Reference.Tools.Create(newpath);

                default:
                    throw new Error.Bug(path);
                }
            }
        }
        else
            throw new Error.State(path);
    }
    /**
     * For reference into a subpath of '/src/' or '/bin/'
     * @return A reference into the corresponding subpath of '/bin/'.
     */
    public Reference getReferenceInBin(){
        return this.getReferenceIn('b');
    }
    /**
     * For reference into a subpath of '/src/' or '/bin/'
     * @return A reference into the corresponding subpath of '/src/'.
     */
    public Reference getReferenceInSrc(){
        return this.getReferenceIn('s');
    }
    /**
     * For a reference having URL host and path components
     * @return A correct and complete reference to the argument
     * filename extension.
     */
    public Reference getReferenceInFext(String fext){
        if (null != fext){
            String string = this.string;
            int idx = Type.Tools.IndexOfFext(string);
            if (-1 < idx){
                String nstring = string.substring(0,(idx+1))+fext;
                return Reference.Tools.Create(nstring);
            }
            else
                throw new Error.State(string);
        }
        else
            throw new Error.Argument("Missing 'String fext'.");
    }

    public String getPathNameExt(){
        String fext = this.fext;
        if (null == fext){
            String path = this.getPathTail();
            if (null != path){
                fext = Type.Tools.FextX(path);
                this.fext = fext;
            }
        }
        return fext;
    }
    public String getPathWithoutNameExt(){
        String fext = this.getPathNameExt();
        if (null == fext)
            return this.getPath();
        else {
            String path = this.getPath();
            int term = (path.length()-(fext.length()+1));
            return path.substring(0,term);
        }
    }
    public String getPathNameExtMeta(){
        String fext = this.fext;
        if (null == fext){
            String path = this.getPathTailMeta();
            if (null != path){
                fext = Type.Tools.FextX(path);
                this.fext = fext;
            }
        }
        return fext;
    }
    public Type getPathNameType(){
        Type fextType = this.fextType;
        if (null == fextType){
            String fext = this.getPathNameExt();
            if (null != fext){
                fextType = Type.Tools.Of(fext);
                this.fextType = fextType;
            }
        }
        return fextType;
    }
    public Type getPathNameTypeMeta(){
        Type fextType = this.fextType;
        if (null == fextType){
            String fext = this.getPathNameExtMeta();
            if (null != fext){
                fextType = Type.Tools.Of(fext);
                this.fextType = fextType;
            }
        }
        return fextType;
    }
    public Type getContentType()
        throws java.io.IOException
    {
        Type type = this.getPathNameType();
        if (null != type)
            return type;
        else {
            HttpMessage meta = this.read();
            if (null != meta){
                type = meta.getContentType();
                if (null != type)
                    return type;
            }
            Address address = this.getAddress();
            if (null != address){
                Component.Type typeComponent = address.getComponentClass();
                if (null != typeComponent)
                    return Type.Tools.For(typeComponent);
            }
            return null;
        }
    }
    public String getContentLocation()
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.getLocation();
        else
            return this.getLocationMeta();
    }
    public String getETag()
        throws java.io.IOException
    {
        HttpMessage storage = this.read();
        if (null != storage)
            return storage.getETag();
        else
            return null;
    }
    public boolean hasLocation(){
        return (null != this.getHostName())&&(null != this.getPath());
    }
    public String getLocation(){
        String string = this.string;
        if (null != string && string.startsWith("http://"))
            return string;
        else {
            String host = this.getHostName();
            if (null != host){
                String path = this.getPath();
                if (null != path)
                    return "http://"+alto.io.u.Chbuf.fcat(host,path);
            }
        }
        return null;
    }
    /**
     * For constructing meta data, don't pull on meta data.
     */
    public String getLocationMeta(){
        String string = this.string;
        if (null != string && string.startsWith("http://"))
            return string;
        else {
            Uri parser = this.getCreateParser(false);
            String host = parser.getHostName();
            if (null != host){
                String path = parser.getPath();
                if (null != path)
                    return "http://"+alto.io.u.Chbuf.fcat(host,path);
            }
        }
        return null;
    }
    public long lastModified(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lastModified();
        else
            return 0L;
    }
    public java.lang.String lastModifiedString(){
        File storage = this.getStorage();
        if (null != storage)
            return storage.lastModifiedString();
        else
            return null;
    }
    public boolean setLastModified(long last){
        File storage = this.getStorage();
        if (null != storage){
            storage.setLastModified(last);
            return true;
        }
        else
            return false;
    }
    public long getLastModified(){
        try {
            HttpMessage storage = this.read();
            if (null != storage)
                return storage.getLastModified();
        }
        catch (java.io.IOException exc){
        }
        return this.lastModified();
    }
    public java.lang.String getLastModifiedString(){
        try {
            HttpMessage storage = this.read();
            if (null != storage)
                return storage.getLastModifiedString();
        }
        catch (java.io.IOException exc){
        }
        return this.lastModifiedString();
    }
    /**
     * @see IO$FileObject
     */
    public java.lang.String getName(){
        return this.getPathTail();
    }

    public boolean isRelative(){
        return this.getCreateParser(true).isRelative();
    }
    public boolean isAbsolute(){
        return this.getCreateParser(true).isAbsolute();
    }
    public java.lang.String getScheme(){
        return this.getCreateParser(true).getScheme();
    }
    public boolean hasScheme(){
        return this.getCreateParser(true).hasScheme();
    }
    public int countScheme(){
        return this.getCreateParser(true).countScheme();
    }
    public java.lang.String getScheme(int idx){
        return this.getCreateParser(true).getScheme(idx);
    }
    public java.lang.String getSchemeTail(){
        return this.getCreateParser(true).getSchemeTail();
    }
    public java.lang.String getSchemeHead(){
        return this.getCreateParser(true).getSchemeHead();
    }
    public java.lang.String getHostUser(){
        return this.getCreateParser(true).getHostUser();
    }
    public java.lang.String getHostPass(){
        return this.getCreateParser(true).getHostPass();
    }
    public java.lang.String getHostName(){
        return this.getHostName(true);
    }
    public java.lang.String getHostNameMeta(){
        return this.getHostName(false);
    }
    public java.lang.String getHostName(boolean meta){
        URL url = this.url;
        if (null != url){
            String hostname = url.getHost();
            if (null != hostname)
                return hostname;
        }
        Address address = this.address;
        if (null != address){
            String hostname = address.getHostName();
            if (null != hostname)
                return hostname;
        }
        Uri parser = this.getCreateParser(meta);
        if (null != parser){
            return parser.getHostName();
        }
        else
            return null;
    }
    public java.lang.String getHostPort(){
        return this.getCreateParser(true).getHostPort();
    }
    public boolean inPath(String searchPath, boolean recurse){
        if (null == searchPath)
            return true;
        else {
            String thisPath = this.getPath();
            if (null != thisPath){
                int idx = thisPath.indexOf(searchPath);
                if (-1 < idx){
                    if (recurse){
                        if ('/' == searchPath.charAt(0))
                            return (0 == idx);
                        else
                            return true;
                    }
                    else {
                        int term = thisPath.lastIndexOf('/');
                        if (0 > term)
                            return true;
                        else {
                            int sl = searchPath.length();
                            int valid = idx+sl;
                            if ('/' == searchPath.charAt(sl-1))
                                return (term == valid);
                            else
                                return ((term-1) == valid);
                        }
                    }
                }
            }
            return false;
        }
    }
    public java.lang.String getPath(){
        return this.getPath(true);
    }
    public java.lang.String getPathMeta(){
        return this.getPath(false);
    }
    public java.lang.String getPath(boolean meta){
        if (null != this.parser)
            return this.parser.getPath();
        else {
            URL url = this.url;
            if (null != url){
                String path = url.getPath();
                if (null != path)
                    return path;
            }
            Address address = this.address;
            if (null != address){
                String path = address.getPath();
                if (null != path)
                    return path;
            }
            Uri parser = this.getCreateParser(meta);
            if (null != parser){
                return parser.getPath();
            }
            return null;
        }
    }
    public boolean hasPath(){
        return this.getCreateParser(true).hasPath();
    }
    public boolean hasPathMeta(){
        return this.getCreateParser(false).hasPath();
    }
    public int countPath(){
        return this.getCreateParser(true).countPath();
    }
    public int countPathMeta(){
        return this.getCreateParser(false).countPath();
    }
    public java.lang.String getPath(int idx){
        return this.getCreateParser(true).getPath(idx);
    }
    public java.lang.String getPathMeta(int idx){
        return this.getCreateParser(false).getPath(idx);
    }
    public java.lang.String getPathTail(){
        return this.getCreateParser(true).getPathTail();
    }
    public java.lang.String getPathTailMeta(){
        return this.getCreateParser(false).getPathTail();
    }
    public java.lang.String getPathHead(){
        return this.getCreateParser(true).getPathHead();
    }
    public java.lang.String getPathHeadMeta(){
        return this.getCreateParser(false).getPathHead();
    }
    public java.lang.String getPathParent(){
        return this.getCreateParser(true).getPathParent();
    }
    public java.lang.String getPathParentMeta(){
        return this.getCreateParser(false).getPathParent();
    }
    public boolean hasIntern(){
        return this.getCreateParser(true).hasIntern();
    }
    public int countIntern(){
        return this.getCreateParser(true).countIntern();
    }
    public java.lang.String getIntern(){
        return this.getCreateParser(true).getIntern();
    }
    public java.lang.String getIntern(int idx){
        return this.getCreateParser(true).getIntern(idx);
    }
    public java.lang.String getInternTail(){
        return this.getCreateParser(true).getInternTail();
    }
    public java.lang.String getInternHead(){
        return this.getCreateParser(true).getInternHead();
    }
    public boolean hasQuery(){
        return this.getCreateParser(true).hasQuery();
    }
    public int countQuery(){
        return this.getCreateParser(true).countQuery();
    }
    public java.lang.String getQuery(){
        return this.getCreateParser(true).getQuery();
    }
    public java.lang.String getQuery(int idx){
        return this.getCreateParser(true).getQuery(idx);
    }
    public java.lang.String[] getQueryKeys(){
        return this.getCreateParser(true).getQueryKeys();
    }
    public java.lang.String getQuery(java.lang.String key){
        return this.getCreateParser(true).getQuery(key);
    }
    public boolean hasQuery(java.lang.String key){
        return this.getCreateParser(true).hasQuery(key);
    }
    public boolean hasFragment(){
        return this.getCreateParser(true).hasFragment();
    }
    public int countFragment(){
        return this.getCreateParser(true).countFragment();
    }
    public java.lang.String getFragment(){
        return this.getCreateParser(true).getFragment();
    }
    public java.lang.String getFragment(int idx){
        return this.getCreateParser(true).getFragment(idx);
    }
    public java.lang.String getFragmentHead(){
        return this.getCreateParser(true).getFragmentHead();
    }
    public java.lang.String getFragmentTail(){
        return this.getCreateParser(true).getFragmentTail();
    }
    public java.lang.String getTerminal(){
        return this.getCreateParser(true).getTerminal();
    }
    public boolean hasTerminal(){
        return this.getCreateParser(true).hasTerminal();
    }
    public int countTerminal(){
        return this.getCreateParser(true).countTerminal();
    }
    public java.lang.String getTerminal(int idx){
        return this.getCreateParser(true).getTerminal(idx);
    }
    public java.lang.String getTerminalHead(){
        return this.getCreateParser(true).getTerminalHead();
    }
    public java.lang.String getTerminalTail(){
        return this.getCreateParser(true).getTerminalTail();
    }
    public java.lang.String[] getTerminalKeys(){
        return this.getCreateParser(true).getTerminalKeys();
    }
    public java.lang.String getTerminalLookup(java.lang.String key){
        return this.getCreateParser(true).getTerminalLookup(key);
    }
    /*
     */
    public String toString(){
        return this.string;
    }
    public int hashCode(){
        return this.string.hashCode();
    }
    public boolean equals(Object ano){
        if (ano == this)
            return true;
        else if (ano instanceof String)
            return this.toString().equals( (String)ano);
        else
            return this.toString().equals( ano.toString());
    }
}
