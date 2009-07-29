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
import alto.io.Uri;
import alto.io.u.Chbuf;
import alto.lang.Address;
import alto.lang.Component;
import alto.lang.HttpMessage;
import alto.lang.HttpRequest;
import alto.lang.HttpResponse;
import alto.lang.Type;
import alto.sec.Keys;
import alto.sec.x509.X500Name;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import static javax.tools.JavaFileObject.Kind;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * A file manager with reference based features.
 * 
 * @since 1.5
 */
public abstract class FileManager
    extends DFS
{
    public static FileManager Instance(){
        java.lang.ClassLoader test = java.lang.Thread.currentThread().getContextClassLoader();
        if (test instanceof FileManager)
            return (FileManager)test;
        else {
            throw new Error.State("File manager not initialized.");
        }
    }
    public final static void SInit(FileManager fm){
        fm = (FileManager)alto.sys.Init.Tools.Init(fm);
        Thread.currentThread().setContextClassLoader(fm);
    }

    /**
     * {@link alto.sx.methods.List LIST method} semantics for
     * request line query parameters "query" and "recurse".
     */
    public final static class List {

        public final static Type.List Type(Reference ref){
            if (null == ref)
                return null;
            else {
                String qv = ref.getQuery("type");
                if (null != qv)
                    return Type(qv);
                else
                    return Type(ref.getPathNameExt());
            }
        }
        public final static Type.List Type(Uri ref){
            if (null == ref)
                return null;
            else {
                String qv = ref.getQuery("type");
                if (null != qv)
                    return Type(qv);
                else
                    return Type(ref.getPathTail());
            }
        }
        /**
         * @param value Value of a query parameter named "type" 
         * @return True for null or equal to "true", otherwise false.
         */
        public final static Type.List Type(String value){
            if (null == value)
                return null;
            else {
                String[] set = Chbuf.split(value,',');
                Type.List list = new Type.List();
                for (int cc = 0, count = set.length; cc < count; cc++){
                    String fext = set[cc];
                    Type type = Type.Tools.Of(fext);
                    if (null != type)
                        list.add(type);
                }
                return list;
            }
        }
        public final static boolean Recurse(Uri ref){
            if (null == ref)
                return true;
            else
                return Recurse(ref.getQuery("recurse"));
        }
        /**
         * @param bool Value of a query parameter named "recurse" 
         * @return True for null or equal to "true", otherwise false.
         */
        public final static boolean Recurse(String bool){
            if (null == bool || ("true".equalsIgnoreCase(bool)))
                return true;
            else 
                return false;
        }

        public final static Iterable<JavaFileObject> Wrap(Reference[] alist){

            java.util.List<JavaFileObject> rlist = new ArrayList<JavaFileObject>();
            for (int cc = 0, count = ((null != alist)?(alist.length):(0)); cc < count; cc++){
                rlist.add(alist[cc]);
            }
            return rlist;
        }
        public final static int From(String string){
            if (null == string)
                return 0;
            else 
                return Integer.parseInt(string);
        }
        public final static int From(Uri ref){
            if (null == ref)
                return 0;
            else
                return From(ref.getQuery("from"));
        }
        public final static int Count(String string){
            if (null == string)
                return Integer.MAX_VALUE;
            else 
                return Integer.parseInt(string);
        }
        public final static int Count(Uri ref){
            if (null == ref)
                return Integer.MAX_VALUE;
            else
                return Count(ref.getQuery("count"));
        }
        public final static int Count(Reference ref){
            if (null == ref)
                return Integer.MAX_VALUE;
            else
                return Count(ref.getQuery("count"));
        }
    }


    /**
     */
    public final static class Ctor {
        /**
         */
        public final static class Location
            extends java.lang.Object
            implements IO.Location
        {
            public final static Location Default = new Location("");

            /**
             */
            public final static class Parsed {

                public final static Parsed Nil = new Parsed();

                public final String name, tld, dom, apn, pkg, dn;

                private Parsed(){
                    super();
                    this.name = null;
                    this.tld = null;
                    this.dom = null;
                    this.apn = null;
                    this.pkg = null;
                    this.dn = null;
                }
                public Parsed(JavaFileManager.Location location){
                    this(location.getName());
                }
                public Parsed(String name){
                    super();
                    String tld = null, dom = null, apn = null, pkg = null, dn = null;
                    if (null != name && 0 < name.length()){
                        /*
                         * Convert a hostname "apn.dom.tld" to a package name
                         * "tld.dom.apn" or "tld.dom".
                         */
                        java.util.StringTokenizer strtok = new java.util.StringTokenizer(name,".");
                        while (strtok.hasMoreTokens()){
                            apn = dom;
                            dom = tld;
                            tld = strtok.nextToken();
                        }
                        if (null != dom && null != tld){
                            if (null != apn)
                                pkg = (tld+'.'+dom+'.'+apn);
                            else
                                pkg = (tld+'.'+dom);

                            dn = (dom+'.'+tld);
                        }
                    }
                    this.name = name;
                    this.tld = tld;
                    this.dom = dom;
                    this.apn = apn;
                    this.pkg = pkg;
                    this.dn = dn;
                }


                public java.lang.String getDN(){
                    return this.dn;
                }
                public X500Name createX500Name()
                    throws java.io.IOException
                {
                    String dn = this.dn;
                    return new X500Name("CN="+dn+", UID="+dn+", DC="+this.dom+", DNQ="+this.tld);
                }
            }


            public final java.lang.String locationName;
            public final boolean locationWriteable;


            public Location(java.lang.String locationName){
                super();
                this.locationName = locationName;
                this.locationWriteable = (IsLocationWriteable(locationName));
            }
            public Location(IO.Location.Transient location){
                super();
                java.lang.String locationName = location.getName();
                this.locationName = locationName;
                this.locationWriteable = (null != locationName && location.isOutputLocation());
            }


            public java.lang.String getName(){
                return this.locationName;
            }
            public boolean isOutputLocation(){
                return this.locationWriteable;
            }
            public String toString(){
                return this.locationName;
            }
            public int hashCode(){
                return this.locationName.hashCode();
            }
            public boolean equals(java.lang.Object ano){
                if (this == ano)
                    return true;
                else if (null == ano)
                    return false;
                else if (ano instanceof IO.Location){
                    IO.Location anol = (IO.Location)ano;
                    return this.locationName.equals(anol.getName());
                }
                else
                    return this.locationName.equals(ano.toString());
            }
        }
        public final static boolean IsLocationWriteable(String locationName){
            if (null == locationName || 1 > locationName.length())
                return false;
            else
                return (0 > locationName.indexOf(".jar"));
        }
        public final static java.lang.String Classpath(java.lang.String a){
            return a;
        }
        public final static java.lang.String Classpath(java.lang.String a, java.lang.String b){
            if (null == a)
                return b;
            else if (null == b)
                return a;
            else {
                return Chbuf.cat(a,File.pathSeparatorChar,b);
            }
        }
    }


    protected static Partition partition;

    protected final JavaFileManager.Location location;

    protected final Ctor.Location.Parsed locationParsed;

    protected Reference keysReference;


    /**
     * Default constructor
     */
    public FileManager(){
        super();
        this.location = Ctor.Location.Default;
        this.locationParsed = Ctor.Location.Parsed.Nil;
    }
    /**
     * Application storage constructor
     */
    protected FileManager(java.lang.ClassLoader parentCl, JavaFileManager.Location location){
        super(parentCl);
        if (null != location){
            if (location instanceof IO.Location.Transient)
                this.location = new Ctor.Location((IO.Location.Transient)location);
            else
                this.location = location;

            this.locationParsed = new Ctor.Location.Parsed(location);
        }
        else
            throw new IllegalArgumentException("Null argument 'Location'.");
    }


    public final boolean isInitialized(){
        return (null != partition);
    }
    public abstract boolean isDefault();

    /**
     * Default init looks for conventional <i>dev</i> and <i>prod</i>
     * store directories, <code>"/www/syntelos"</code> and
     * <code>"/www"</code>, respectively.
     */
    @Code(Check.Locked)
    public final FileManager init(){
        java.io.File dir = null;
        if (null == partition){
            dir = new java.io.File("/www/syntelos");
            if (dir.isDirectory()){

                return this.init(dir);
            }
            else {
                dir = new java.io.File("/www");
                if (dir.isDirectory()){

                    return this.init(dir);
                }
            }
        }
        return this;
    }
    @Code(Check.Locked)
    protected final FileManager init(java.io.File dir){
        if (null == partition){

            if (!dir.exists()){
                if (!dir.mkdirs()){

                    throw new IllegalArgumentException("Failed to create directory '"+dir+"'.");
                }
            }
            else if (!dir.isDirectory()){

                throw new IllegalArgumentException("Not directory '"+dir+"'.");
            }

            partition = new Partition(dir);
        }

        try {
            this.dfsInit();
            return this;
        }
        catch (java.io.IOException exc){
            throw new Error.State(dir.getPath(),exc);
        }
    }
    public Partition getPartition(){
        return partition;
    }
    public abstract Component.Host getComponent();

    public abstract Component.Host getComponentDN();

    @Code(Check.Locking)
    public Reference getKeysReference() 
        throws java.io.IOException
    {
        Reference keysReference = this.keysReference;
        if (null == keysReference){
            keysReference = Keys.Tools.ReferenceTo(this);
            this.keysReference = keysReference;
        }
        return keysReference;
    }
    @Code(Check.Locking)
    public abstract File deleteStorage(Address address);

    @Code(Check.Locking)
    public abstract File getStorage(Address address);

    @Code(Check.Locking)
    public abstract File dropStorage(Address address);

    @Code(Check.Locking)
    public abstract Reference[] listLocationOf(Address[] items, String path, boolean recurse)
        throws java.io.IOException;

    @Code(Check.Locking)
    public abstract Reference[] list(String location, Type.List types, String path, boolean recurse)
        throws java.io.IOException;

    @Code(Check.Locking)
    public final Address[] listItems(Address base){
        File dir = this.getStorage(base);
        return dir.listItems();
    }
    @Code(Check.Locking)
    public final Address[] listVersions(Reference ref){
        return this.listVersions(ref.getAddress());
    }
    @Code(Check.Locking)
    public final Address[] listVersions(Address address){
        Address path = address.getAddressBasePath();
        File dir = this.getStorage(path);
        return dir.listVersions();
    }
    /**
     * Classes are loaded from locations (output true) via a storage
     * reference of the form
     * <code>'//Location/Package/Class.class'</code>, following java
     * conventions for all classpath resource types including jar
     * files and applets.
     */
    @Code(Check.Locking)
    protected abstract java.lang.Class findClass(java.lang.String bname) 
        throws java.lang.ClassNotFoundException;

    /**
     * @param path A file path expression
     * @return A reference to a file in this application location
     */
    public abstract Reference createReference(String path);

    public final JavaFileManager.Location getLocation(){
        return this.location;
    }
    public final boolean isLocation(java.lang.String name){
        if (null != name)
            return (this.getName().equals(location));
        else
            throw new Error.Argument();
    }
    public final boolean isLocation(JavaFileManager.Location location){
        if (null != location)
            return (this.location.equals(location));
        else
            return (this.isDefault());
    }
    /**
     * @return An empty string for the default file manager, or the
     * hostname of this application context.
     * @see #isDefault()
     */
    public final java.lang.String getName(){
        return this.location.getName();
    }
    public final boolean isOutputLocation(){
        return this.location.isOutputLocation();
    }
    public X500Name createX500Name()
        throws java.io.IOException
    {
        return this.locationParsed.createX500Name();
    }
    public final String getPackageName(){
        return this.locationParsed.pkg;
    }
    /**
     * @return Location domain name (hierarchically under TLD)
     */
    public final String getDC(){
        return this.locationParsed.dom;
    }
    /**
     * @return Location host name TLD
     */
    public final String getDNQ(){
        return this.locationParsed.tld;
    }
    /**
     * @return Location Domain Name, dot, TLD
     */
    public final String getDN(){
        return this.locationParsed.dn;
    }
    public final String packageName(String name){
        String pkg = this.getPackageName();
        if (null != pkg)
            return pkg+'.'+name;
        else
            return null;
    }
    @Code(Check.Locking)
    public final Object create(String classname, Class memberof){
        try {
            java.lang.Class jclass = this.loadClass(classname);
            if (memberof.isAssignableFrom(jclass)){
                return jclass.newInstance();
            }
        }
        catch (java.lang.IllegalAccessException exc){
            throw new alto.sys.Error(exc);
        }
        catch (java.lang.InstantiationException exc){
            throw new alto.sys.Error(exc);
        }
        catch (java.lang.ClassNotFoundException ignore){
        }
        return null;
    }
    @Code(Check.Locking)
    public abstract FileManager lookupClassLoader(JavaFileManager.Location location);

    @Code(Check.Locking)
    @Override
    public abstract FileManager getClassLoader(JavaFileManager.Location location);

    public int isSupportedOption(String current){
        return -1;
    }
    public boolean handleOption(String current, Iterator<String> remaining){
        return false;
    }
    public boolean hasLocation(JavaFileManager.Location location){
        if (this.isLocation(location))
            return true;
        else
            return (null != this.lookupClassLoader(location));
    }
    public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file){
        if (file instanceof Reference){
            Reference ref = (Reference)file;
            return ref.getReferenceInBin().getReferenceInFext("class").toString();
        }
        else
            throw new UnsupportedOperationException();
    }
    public boolean isSameFile(FileObject a, FileObject b){
        if (a instanceof Reference && b instanceof Reference){
            Reference ra = (Reference)a;
            Reference rb = (Reference)b;
            Address aa = ra.getAddress();
            Address ab = rb.getAddress();
            if (null != aa && null != ab)
                return (aa.equals(ab));
            else
                return false;
        }
        else
            return (a.equals(b));
    }
    public final JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String classname, Kind kind)
        throws IOException
    {
        return this.referenceTo(location,classname,kind);
    }
    public final JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String classname, Kind kind,
                                               FileObject sibling)
        throws IOException
    {
        return this.referenceTo(location,classname,kind);
    }
    public final FileObject getFileForInput(JavaFileManager.Location location, String packagename, String relativename)
        throws IOException
    {
        return this.referenceTo(location,packagename,relativename);
    }
    public final FileObject getFileForOutput(JavaFileManager.Location location, String packagename, String relativename,
                                       FileObject sibling)
        throws IOException
    {
        return this.referenceTo(location,packagename,relativename);
    }
    public void flush() 
        throws IOException
    {
    }
    public void close() 
        throws IOException
    {
    }

    protected abstract JavaFileObject referenceTo(JavaFileManager.Location location, String packagename, String relativename)
        throws IOException;

    protected abstract JavaFileObject referenceTo(JavaFileManager.Location location, String classname, Kind kind)
        throws IOException;

    /**
     * List files under location and type sharing packagename.
     */
    public final java.lang.Iterable<JavaFileObject> list(JavaFileManager.Location location, String packagename,
                                                   Set<Kind> kinds, boolean recurse)
        throws IOException
    {
        String path = packagename.replace('.','/');
        return List.Wrap( this.list(location.getName(),Type.Tools.For(kinds),path,recurse));
    }
    /**
     * List files under location and type sharing packagename.
     */
    public final Reference[] list(String path, Type type, boolean recurse)
        throws IOException
    {
        return this.list(this.getName(),(new Type.List(type)),path,recurse);
    }
    /**
     * List files under location and type sharing packagename.
     */
    public final Reference[] list(String path, Type.List types, boolean recurse)
        throws IOException
    {
        return this.list(this.getName(),types,path,recurse);
    }
    public abstract Reference[] list(Reference ref)
        throws IOException;

    public abstract Reference[] list(Uri ref)
        throws IOException;

    public abstract java.lang.Iterable<JavaFileObject> compileList(HttpRequest request, HttpResponse response)
        throws IOException;

    public abstract Reference[] fragmentDereference(IO.Source.Fio request, Type fioType)
        throws java.io.IOException;

    public final java.lang.String toString(){
        return this.getName();
    }
    public final int hashCode(){
        return this.toString().hashCode();
    }
    public final boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano instanceof String)
            return this.toString().equals(ano);
        else if (ano instanceof Location)
            return this.isLocation((Location)ano);
        else 
            return this.toString().equals(ano.toString());
    }
}
