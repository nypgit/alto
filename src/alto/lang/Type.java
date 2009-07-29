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
package alto.lang;

import alto.sys.Reference;

import java.util.Set;
import javax.tools.JavaFileObject.Kind;

/**
 * An algebraic term for a mimetype, as a description from persistent
 * store (in headers format), or an element of network communication
 * as on HTTP.
 * 
 * @author jdp
 */
public interface Type
    extends alto.sys.PHeaders,
            alto.hash.Function,
            alto.hash.Function.Association
{

    public final static Type Nil = null;

    public abstract static class Tools {

        protected static Tools Instance;

        /**
         * @see alto.io.Tools
         */
        public final static void SInit(Tools instance){
            if (null == Instance){
                Component.Type[] bootstrap = new Component.Type[]{
                    alto.lang.component.Type.Numeric.MimeType.Instance,
                    alto.lang.component.Type.Numeric.Address.Instance
                };
                instance.bootstrap(bootstrap);

                Instance = instance;
                Instance = (Tools)alto.sys.Init.Tools.Init(instance);
            }
            else
                throw new alto.sys.Error.State.Init("alto.io.Tools already initialized");
        }

        public final static Type ForMimeType(){
            return For(alto.lang.component.Type.Numeric.MimeType.Instance);
        }
        public final static Type ForAddress(){
            return For(alto.lang.component.Type.Numeric.Address.Instance);
        }
        public final static Type For(String mimetype){
            if (null != Instance)
                try {
                    return Instance.dereference(Reference.Tools.Create(Component.Type.Tools.For(mimetype)));
                }
                catch (java.io.IOException exc){
                    return null;
                }
            else
                throw new alto.sys.Error.State.Init("alto.io.Tools not initialized");
        }
        public final static Type For(Component.Type type){
            if (null != Instance)
                try {
                    return Instance.dereference(Reference.Tools.Create(Component.Type.Tools.For(type)));
                }
                catch (java.io.IOException exc){
                    return null;
                }
            else
                throw new alto.sys.Error.State.Init("alto.io.Tools not initialized");
        }
        public final static Type.List For(Set<Kind> kinds){
            Kind[] kindsSet = new Kind[kinds.size()];
            kindsSet = kinds.toArray(kindsSet);
            Type.List list = new Type.List();
            for (int cc = 0, count = kindsSet.length; cc < count; cc++){
                Kind kind = kindsSet[cc];
                if (Kind.SOURCE == kind)
                    list.add(Type.Tools.Of("java"));
                else if (Kind.CLASS == kind)
                    list.add(Type.Tools.Of("class"));
                else if (Kind.HTML == kind)
                    list.add(Type.Tools.Of("html"));
            }
            return list;
        }
        public final static Type Of(String fext){
            if (null != Instance)
                try {
                    return Instance.dereference(Reference.Tools.Create(Component.Type.Tools.ForR(FextI(fext))));
                }
                catch (java.io.IOException exc){
                    return null;
                }
            else
                throw new alto.sys.Error.State.Init("alto.io.Tools not initialized");
        }
        public final static Type Dereference(Reference ref)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.dereference(ref);
            else
                throw new alto.sys.Error.State.Init("alto.io.Tools not initialized");
        }
        public final static Reference ReferenceToMimetype(String identifier){
            return Reference.Tools.Create(Component.Type.Tools.For(identifier));
        }
        public final static Reference ReferenceToFext(String identifier){
            return Reference.Tools.Create(Component.Type.Tools.ForR(identifier));
        }

        /**
         * File type operator accepts <code>"path/file.(type)"<code> and
         * <code>"path/file.(type).other"</code> (as in
         * <code>"path/file.(type).xml"</code> and
         * <code>"path/file.(type).txt"</code>) conventions.
         * 
         * @param path File path string
         * @return File type string
         */
        public final static java.lang.String FextX(java.lang.String path){
            if (null == path || 1 > path.length())
                return null;
            else {
                java.lang.String p = path;
                int idx = p.lastIndexOf('/');
                if (-1 < idx)
                    /*
                     * (trim)
                     */
                    p = p.substring(idx+1);

                int i0 = p.lastIndexOf('.');
                if (-1 < i0){
                    int i1 = p.lastIndexOf('.',(i0-1));
                    if (-1 < i1 && i1 < i0)
                        /*
                         * file.(type).xml
                         */
                        return p.substring(i1+1,i0);
                    else
                        /*
                         * file.(type)
                         */
                        return p.substring(i0+1);
                }
                else
                    return null;
            }
        }
        /**
         * File type operator accepts <code>"path/file.(type)"<code> and
         * <code>"path/file.(type).other"</code> (as in
         * <code>"path/file.(type).xml"</code> and
         * <code>"path/file.(type).txt"</code>) conventions.
         * 
         * @param path File path string
         * @return File type string
         */
        public final static java.lang.String FextI(java.lang.String path){
            if (null == path || 1 > path.length())
                return null;
            else {
                java.lang.String p = path;
                int idx = p.lastIndexOf('/');
                if (-1 < idx)
                    /*
                     * (trim)
                     */
                    p = p.substring(idx+1);

                int i0 = p.lastIndexOf('.');
                if (-1 < i0){
                    int i1 = p.lastIndexOf('.',(i0-1));
                    if (-1 < i1 && i1 < i0)
                        /*
                         * file.(type).xml
                         */
                        return p.substring(i1+1,i0);
                    else
                        /*
                         * file.(type)
                         */
                        return p.substring(i0+1);
                }
                else
                    /*
                     * (punt)
                     */
                    return p;
            }
        }
        /**
         * Fext operator of last resort takes the right hand side terminal
         * of any dot concatenated sequence.
         * 
         * @param name Any string
         * @return A filename extension or the input string.
         */
        public final static java.lang.String FextR(java.lang.String name){
            if (null != name){
                int idx = IndexOfFext(name);
                if (0 < idx)
                    name = name.substring(idx+1);
                //
                return name.toLowerCase();
            }
            else
                return null;
        }
        /**
         * Single pass scan for fext delimiter with safety check with
         * respect to string ordering of path delimiters.
         */
        public final static int IndexOfFext(java.lang.String path){
            if (null == path)
                return -1;
            else {
                if (java.io.File.pathSeparatorChar != '/'){
                    path = path.replace(java.io.File.pathSeparatorChar,'/');
                }
                char[] cary = path.toCharArray();
                for (int cc = (cary.length-1); -1 < cc; cc--){
                    switch (cary[cc]){
                    case '/':
                        return -1;
                    case '.':
                        return cc;
                    default:
                        break;
                    }
                }
                return -1;
            }
        }

        /**
         * Called before optional {@link Init} with the builtin types
         * required to bootstrap the store. When this method (or
         * optional, subsequent init) returns, these instances must
         * have valid {@link Type} objects assigned.  Currently these
         * are {@link
         * alto.lang.component.Type.Numeric.MimeType#Instance} and
         * {@link alto.lang.component.Type.Numeric.Address#Instance}.
         */
        public abstract void bootstrap(Component.Type[] type);
        /**
         * Needs to dereference a type from a reference to type or a
         * reference to an address to type.  The former has address
         * class component {@link
         * alto.lang.component.Type.Numeric.MimeType#Instance} and the
         * latter has address class component {@link
         * alto.lang.component.Type.Numeric.Address#Instance}.
         */
        public abstract Type dereference(Reference ref)
            throws java.io.IOException;
    }

    /**
     * List of {@link Type}
     */
    public static class List 
        extends java.lang.Object
        implements java.lang.Iterable<Type>
    {
        /** 
         * Default application components' types.
         */
        public static class Default 
            extends Type.List
        {
            public final static Default Instance = new Default();

            public Default(){
                super();
                this.add(Type.Tools.Of("txt"));
                this.add(Type.Tools.Of("html"));
                this.add(Type.Tools.Of("xml"));
                this.add(Type.Tools.Of("java"));
                this.add(Type.Tools.Of("sp"));
                this.add(Type.Tools.Of("properties"));
                this.add(Type.Tools.Of("jpg"));
                this.add(Type.Tools.Of("png"));
                this.add(Type.Tools.Of("gif"));
                this.add(Type.Tools.Of("jar"));
                this.add(Type.Tools.Of("sh"));
                this.add(Type.Tools.Of("js"));
                this.add(Type.Tools.Of("css"));
                this.add(Type.Tools.Of("jnlp"));
            }
        }
        /**
         * Type list snapshot iterator.
         */
        public final static class Iterator
            extends java.lang.Object
            implements java.util.Iterator<Type>
        {
            private int index;
            private final int count;
            private final Type[] list;

            public Iterator (Type.List list){
                super();
                this.list = list.list;
                this.count = list.length();
            }
            public boolean hasNext(){
                return (this.index < this.count);
            }
            public Type next(){
                if (this.index < this.count)
                    return this.list[this.index++];
                else
                    throw new java.util.NoSuchElementException("Index "+String.valueOf(this.index));
            }
            /**
             * Not supported
             */
            public void remove(){
                throw new UnsupportedOperationException();
            }
        }


        protected Type[] list;


        public List(){
            super();
        }
        public List(Type head){
            super();
            if (null != head)
                this.add(head);
            else
                throw new IllegalArgumentException();
        }


        public void add(Type type){
            if (null != type){
                Type[] list = this.list;
                if (null == list){
                    this.list = new Type[]{type};
                }
                else {
                    int len = list.length;
                    Type[] copier = new Type[len+1];
                    System.arraycopy(list,0,copier,0,len);
                    copier[len] = type;
                    this.list = copier;
                }
            }
        }
        public int length(){
            Type[] list = this.list;
            if (null == list)
                return 0;
            else
                return list.length;
        }
        public int size(){
            Type[] list = this.list;
            if (null == list)
                return 0;
            else
                return list.length;
        }
        public Type get(int idx){
            Type[] list = this.list;
            if (null == list)
                throw new IllegalArgumentException(java.lang.String.valueOf(idx));
            else if (-1 < idx && idx < list.length)
                return list[idx];
            else
                throw new IllegalArgumentException(java.lang.String.valueOf(idx));
        }
        /**
         * @return First best (practical) matching type
         */
        public Type best(){
            Type[] list = this.list;
            if (null == list)
                return null;
            else {
                int count = list.length;
                if (1 == count)
                    return list[0];
                else {
                    Type best = null;
                    for (int cc = 0; cc < count; cc++){
                        Type test = list[cc];
                        if (null == best)
                            best = test;
                        else if (test.getQ() > best.getQ())//(in-order preference when eq(q))
                            best = test;
                        else if (test.isPractical() && best.isNotPractical())
                            best = test;
                    }
                    if (best.isPractical())
                        return best;
                    else
                        return null;
                }
            }
        }
        /**
         * @return First best (practical) matching type
         */
        public Type best(Type compareto){
            Type[] list = this.list;
            if (null == list)
                return null;
            else {
                int count = list.length;
                if (1 == count)
                    return list[0];
                else {
                    Type best = null;
                    for (int cc = 0; cc < count; cc++){
                        Type test = list[cc];
                        if (test.equals(compareto) || compareto.equals(test)){
                            if (null == best)
                                best = test;
                            else if (test.getQ() > best.getQ())//(in-order preference when eq(q))
                                best = test;
                            else if (test.isPractical() && best.isNotPractical())
                                best = test;
                        }
                    }
                    if (best.isPractical())
                        return best;
                    else
                        return null;
                }
            }
        }
        public java.util.Iterator<Type> iterator(){
            return new Iterator(this);
        }
    }


    public boolean hasMimeType();

    public boolean hasNotMimeType();

    public java.lang.String getMimeType();

    public void setMimeType(java.lang.String value);

    public boolean hasFext();

    public boolean hasNotFext();

    public java.lang.String getFext();

    public java.lang.String[] listFext();

    public void setFext(java.lang.String value);

    public boolean hasHashFunctionName();

    public boolean hasNotHashFunctionName();

    public java.lang.String getHashFunctionName();

    public void setHashFunctionName(java.lang.String name);

    public boolean hasTypeClass();

    public boolean hasNotTypeClass();

    public java.lang.Class getTypeClass();

    public void setTypeClass(java.lang.Class value);

    public void setTypeClass(java.lang.String value);

    public boolean hasFioClass();

    public boolean hasNotFioClass();

    public java.lang.Class getFioClass();

    public void setFioClass(java.lang.Class value);

    public void setFioClass(java.lang.String value);

    public boolean hasPClass();

    public boolean hasNotPClass();

    public java.lang.Class getPClass();

    public void setPClass(java.lang.Class value);

    public void setPClass(java.lang.String value);

    public boolean isTypeMeta();

    public boolean isNotTypeMeta();

    public boolean hasTypeMeta();

    public boolean hasNotTypeMeta();

    public java.lang.Boolean getTypeMeta();

    public void setTypeMeta(java.lang.Boolean value);

    public boolean isTypePersistent();

    public boolean hasTypePersistent();

    public boolean hasNotTypePersistent();

    public java.lang.Boolean getTypePersistent();

    public void setTypePersistent(java.lang.Boolean value);

    public boolean isTypeTransactional();

    public boolean hasTypeTransactional();

    public boolean hasNotTypeTransactional();

    public java.lang.Boolean getTypeTransactional();

    public void setTypeTransactional(java.lang.Boolean value);

    public java.lang.String getKey();

    public byte[] hashAddress();

    public Component.Numeric hashAddressComponent();

    public java.lang.String hashAddressString();
    /**
     * @return This type expression is "* / *".
     */
    public boolean isAny();
    /**
     * @return This type expression includes wildcards.
     */
    public boolean isNotPractical();
    /**
     * @return This type expression has no wildcards "*".  When true,
     * this.toString() is a usable mimetype.  May include type
     * parameters.
     */
    public boolean isPractical();
    /**
     * @return Whether this.toString() includes a type parameter.  The
     * special type parameter named "q" (used by HTTP Accept) will
     * never be present as a "normal" type parameter.
     */
    public boolean hasParameter();
    /**
     * @return Type parameter (name "=" value) string.
     */
    public java.lang.String getParameter();

    public java.lang.String getName();
    /**
     * @return Accept preference factor, default 1.0
     */
    public float getQ();

    public boolean hasHashFunction();

    public boolean hasNotHashFunction();

    public alto.hash.Function getHashFunction();

    public void setHashFunction(alto.hash.Function h);

    public void setHashFunction(java.lang.String hn);

    public java.lang.String getHashName();

    public int hash32(byte[] bits);

    public long hash64(byte[] bits);

    public byte[] hash(byte[] bits);

    public int hash32(java.lang.String string);

    public long hash64(java.lang.String string);

    /**
     * Path hash for address.
     * @param item Path string
     * @return Null for null input
     */
    public Component.Path hash(java.lang.String item);

    /**
     * Optional Type specific path transform
     */
    public java.lang.String pathTo(java.lang.String path);

    /**
     * Optional Type specific host transform
     */
    public Component.Host hostOf(java.lang.String host);

    /**
     * Type specific reference transform
     */
    public alto.sys.Reference referenceTo(alto.sys.Reference contentReference);

    /**
     * Type specific reference producer
     */
    public alto.sys.Reference referenceTo(HttpRequest request);

    /**
     * Type specific reference producer for types with static hosts
     * like {@link Component$Host#Local) or {@link
     * Component$Host#Global}.
     */
    public alto.sys.Reference referenceTo(java.lang.String path);

    public boolean hasFio();

    public alto.lang.Fio getFio();

    public alto.lang.Fio getCreateFio()
        throws java.io.IOException,
               java.lang.ClassNotFoundException;

}
