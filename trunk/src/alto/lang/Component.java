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

import alto.hash.Function;
import alto.io.Input;
import alto.io.Output;
import alto.io.Uri;
import alto.io.u.Array;
import alto.io.u.Utf8;

import java.io.IOException;

/**
 * <p> An {@link alto.sys.Address} component. </p>
 * 
 * @since 1.1
 * @author jdp
 */

public interface Component 
    extends Buffer, 
            Sio.Type.Field,
            Function.Association
{
    public final static java.lang.Class CLASS = Component.class;

    public abstract static class List {

        public final static Component[] Add(Component[] list, Component c){
            if (null == c)
                return list;
            else 
                return (Component[])Array.set(list,c.getPosition(),c,Component.CLASS);
        }
        public final static Component[] Add(Component[] list, java.lang.String value){
            if (null == value)
                return list;
            else 
                return Component.Tools.For(list,value);
        }
        public final static Component[] Add(Component[] list, byte[] value){
            if (null == value)
                return list;
            else 
                return Component.Tools.For(list,value);
        }
        public final static Component[][] Add(Component[][] list, Component[] c){
            if (null == c)
                return list;
            else if (null == list)
                return new Component[][]{c};
            else {
                int len = list.length;
                Component[][] copier = new Component[len+1][];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
        public final static boolean Equals(Component[] a, Component[] b){
            if (null == a)
                return (null == b);
            else if (null == b)
                return false;
            else if (a.length == b.length){
                Component aa, bb;
                for (int cc = 0, count = a.length; cc < count; cc++){
                    aa = a[cc];
                    bb = b[cc];
                    if (!aa.equals(bb))
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
         * Numeric address processing
         */
        public final static Component[] For(Component[] addr, byte[] value){
            int pos = ((null != addr)?(addr.length):(0));
            switch (pos){
            case Component.Relation.Position:
                return Component.List.Add(addr,new alto.lang.component.Relation.Numeric(value));

            case Component.Host.Position:
                return Component.List.Add(addr,new alto.lang.component.Host.Numeric(value));

            case Component.Type.Position:
                return Component.List.Add(addr,new alto.lang.component.Type.Numeric(value));

            case Component.Path.Position:
                return Component.List.Add(addr,new alto.lang.component.Path.Numeric(value));

            case Component.Version.Position:
                return Component.List.Add(addr,new alto.lang.component.Version.Numeric(value));

            default:
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(pos));
            }
        }
        /**
         * Numeric address processing
         */
        public final static Component.Numeric For(int pos, byte[] value){
            switch (pos){
            case Component.Relation.Position:
                return (new alto.lang.component.Relation.Numeric(value));

            case Component.Host.Position:
                return (new alto.lang.component.Host.Numeric(value));

            case Component.Type.Position:
                return (new alto.lang.component.Type.Numeric(value));

            case Component.Path.Position:
                return (new alto.lang.component.Path.Numeric(value));

            case Component.Version.Position:
                return (new alto.lang.component.Version.Numeric(value));

            default:
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(pos));
            }
        }
        /**
         * Numeric address parsing
         */
        public final static Component[] For(Component[] addr, java.lang.String string){
            int pos = ((null != addr)?(addr.length):(0));
            switch (pos){
            case Component.Relation.Position:
                return Component.List.Add(addr,Component.Relation.Tools.ValueOf(string));

            case Component.Host.Position:
                return Component.List.Add(addr,Component.Host.Tools.ValueOf(string));

            case Component.Type.Position:
                return Component.List.Add(addr,Component.Type.Tools.ValueOf(string));

            case Component.Path.Position:
                return Component.List.Add(addr,Component.Path.Tools.ValueOf(string));

            case Component.Version.Position:
                return Component.List.Add(addr,Component.Version.Tools.ValueOf(string));

            default:
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(pos));
            }
        }
        /**
         * Symbolic location parsing
         */
        public final static Component[] For(Component[] addr, alto.lang.Type type, java.lang.String string){
            int pos = ((null != addr)?(addr.length):(0));
            switch (pos){
            case Component.Relation.Position:
                return Component.List.Add(addr,Component.Relation.Tools.ValueOf(string));

            case Component.Host.Position:
                return Component.List.Add(addr,Component.Host.Tools.ValueOf(type,string));

            case Component.Type.Position:
                return Component.List.Add(addr,Component.Type.Tools.ValueOf(type));

            case Component.Path.Position:
                return Component.List.Add(addr,Component.Path.Tools.ValueOf(type,string));

            case Component.Version.Position:
                return Component.List.Add(addr,Component.Version.Tools.ValueOf(string));

            default:
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(pos));
            }
        }
        /**
         * Symbolic location parsing
         */
        public final static Component[] For(Component[] addr, alto.lang.Type type, Uri parser){
            int pos = ((null != addr)?(addr.length):(0));
            switch (pos){
            case Component.Relation.Position:{
                java.lang.String relation = parser.getQuery("relation");
                if (null != relation)
                    return Component.List.Add(addr,Component.Relation.Tools.ValueOf(relation));
                else
                    return Component.List.Add(addr,Component.Relation.U);
            }
            case Component.Host.Position:{
                java.lang.String string = parser.getHostName();
                if (null != string)
                    return Component.List.Add(addr,Component.Host.Tools.ValueOf(type,string));
                else
                    throw new java.lang.IllegalArgumentException("Missing host name");
            }
            case Component.Type.Position:{
                if (null != type)
                    return Component.List.Add(addr,Component.Type.Tools.ValueOf(type));
                else {
                    java.lang.String path = parser.getPath();
                    if (null != path){
                        type = alto.lang.Type.Tools.Of(path);
                        if (null != type)
                            return Component.List.Add(addr,type.hashAddressComponent());
                        else
                            throw new java.lang.IllegalArgumentException("Missing type");
                    }
                    else
                        throw new java.lang.IllegalArgumentException("Missing path");
                }
            }
            case Component.Path.Position:{
                java.lang.String path = parser.getPath();
                if (null != path){
                    if (null != type)
                        return Component.List.Add(addr,Component.Path.Tools.ValueOf(type,path));
                    else {
                        type = alto.lang.Type.Tools.Of(path);
                        if (null != type)
                            return Component.List.Add(addr,Component.Path.Tools.ValueOf(type,path));
                        else
                            throw new java.lang.IllegalArgumentException("Missing type");
                    }
                }
                else
                    throw new java.lang.IllegalArgumentException("Missing path");
            }
            case Component.Version.Position:{
                java.lang.String version = parser.getQuery("version");
                if (null != version)
                    return Component.List.Add(addr,Component.Version.Tools.ValueOf(version));
                else
                    return Component.List.Add(addr,Component.Version.Current);
            }
            default:
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(pos));
            }
        }
        public final static boolean IsComplete(Component[] address){
            return (null != address && Component.Version.Position < address.length);
        }
        public final static Component[] AddressNumericFrom(Uri parser){
            int count = parser.countPath();
            if (Component.Version.Position < count){
                try {
                    Component[] addr = null;
                    java.lang.String p;
                    for (int idx = 0, term = (count-1); idx < count; idx++){
                        p = parser.getPath(idx);
                        if (null != p)
                            addr = For(addr,p);
                    }
                    if (IsComplete(addr))
                        return addr;
                    else
                        return null;
                }
                catch (java.lang.RuntimeException exc){
                }
            }
            return null;
        }
        public final static Component[] AddressSymbolicFrom(Uri parser){
            try {
                Component[] address = new Component[]{Component.Relation.U};
                java.lang.String path = parser.getPath();
                alto.lang.Type type = alto.lang.Type.Tools.Of(path);

                address = Component.Tools.For(address,type,parser);
                address = Component.Tools.For(address,type,parser);
                address = Component.Tools.For(address,type,parser);
                address = Component.Tools.For(address,type,parser);

                if (IsComplete(address))
                    return address;
                else
                    return null;
            }
            catch (java.lang.RuntimeException exc){
                return null;
            }
        }
        public final static Component[] AddressSymbolicFrom(Component[] address, Uri parser){
            try {
                java.lang.String path = parser.getPath();
                if (Component.Tools.IsPath(true,path)){
                    alto.lang.Type type = alto.lang.Type.Tools.Of(path);
                    if (null != type){
                        address = Component.Tools.For(address,type,parser);
                        if (IsComplete(address))
                            return address;
                        else {
                            address = Component.Tools.For(address,type,parser);
                            if (IsComplete(address))
                                return address;
                            else {
                                address = Component.Tools.For(address,type,parser);
                                if (IsComplete(address))
                                    return address;
                                else {
                                    address = Component.Tools.For(address,type,parser);
                                    if (IsComplete(address))
                                        return address;
                                }
                            }
                        }
                    }
                }
                return null;
            }
            catch (java.lang.RuntimeException exc){
                if (IsComplete(address))
                    return address;
                else
                    return null;
            }
        }
        /**
         * @param path A string
         * @return A string with leading '/' but no trailing '/'.
         */
        public final static java.lang.String Path(java.lang.String path){
            if (null == path)
                return null;
            else {
                if ('/' != path.charAt(0))
                    path = '/'+path;
                //
                int term = path.length()-1;
                if (0 < term){
                    if ('/' == path.charAt(term)){
                        path = path.substring(0,term);
                        if (1 > path.length())
                            return null;
                    }
                    return path;
                }
                else
                    return path;
            }
        }
        public final static byte[] Cat(Component[] address){
            byte[] value = null;
            for (int cc = 0, count = address.length; cc < count; cc++){
                Component comp = address[cc];
                value = alto.io.u.Bbuf.cat(value,comp.toByteArray());
            }
            return value;
        }
        public final static java.lang.String PathStorageFor(Component[] address){
            if (null != address){
                StringBuilder string = new StringBuilder("/");
                string.append(address[0].toString());
                for (int cc = 1, count = address.length; cc < count; cc++){
                    string.append('/');
                    string.append(address[cc].toString());
                }
                return string.toString();
            }
            else
                throw new IllegalArgumentException();
        }
        public final static java.lang.String Clean(java.lang.String string){
            int term = (string.length()-1);
            if (0 < term){
                while ('/' == string.charAt(0)){
                    string = string.substring(1);
                    term = (string.length()-1);
                    if (0 > term)
                        return null;
                }
                while ('/' == string.charAt(term)){
                    string = string.substring(0,term);
                    term = (string.length()-1);
                    if (0 > term)
                        return null;
                }
                return string;
            }
            else
                return null;
        }
        public final static java.lang.String CleanTail(java.lang.String string){
            int term = (string.length()-1);
            if (-1 < term){
                while ('/' == string.charAt(term)){
                    string = string.substring(0,term);
                    term = (string.length()-1);
                    if (0 > term)
                        return null;
                }
                return string;
            }
            else
                return null;
        }
        public final static boolean IsNotName(java.lang.String identifier){
            if (null == identifier)
                return true;
            else
                return (!IsName(identifier));
        }
        public final static boolean IsName(java.lang.String identifier){
            char[] scan = identifier.toCharArray();
            int chars = 0, dotX = -1;

            for (int cc = 0, count = scan.length; cc < count; cc++){
                switch(scan[cc]){
                case '-':
                    if (0 == chars)
                        return false;
                    else {
                        break;
                    }
                case '.':
                    if (0 == chars)
                        return false;
                    else if (dotX == (cc-1))
                        return false;
                    else {
                        dotX = cc;
                        break;
                    }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (0 == chars)
                        return false;
                    else {
                        break;
                    }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    chars += 1;
                    break;
                default:
                    return false;
                }
            }
            return (0 < chars);
        }
        public final static boolean IsPath(boolean sep, java.lang.String identifier){
            char[] scan = identifier.toCharArray();
            int chars = 0, seps = 0;

            for (int cc = 0, count = scan.length; cc < count; cc++){
                switch(scan[cc]){
                case '-':
                    if (0 == chars)
                        return false;
                    else {
                        break;
                    }
                case '.':
                    if (0 == chars)
                        return false;
                    else {
                        break;
                    }
                case '/':
                    seps += 1;
                    break;
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    chars += 1;
                    break;
                default:
                    return false;
                }
            }
            if (sep)
                return (0 < chars && 0 < seps);
            else
                return (0 < chars);
        }
    }

    /**
     * Implementor is always a subclass of {@link java.math.BigInteger}
     * 
     */
    public interface Numeric
        extends Component
    {
        /**
         * @return New instance with arithmetic value
         */
        public Numeric add(Numeric num);

        public long longValue();
    }

    /**
     * The last or terminal component of an address is an item
     * version.  This component of an address may be a {@link
     * #IsValid(int) name string}, in which case it will be
     * represented by an instance of this class.
     */
    public interface Named 
        extends Component
    {
        /**
         * @return Name string
         */
        public String toString();
    }


    /**
     * <p> Storage address roots.  All addresses begin with one of
     * these roots. </p>
     */
    public interface Relation 
        extends Component.Named
    {
        public final static int Position = 0;
        public final static int LengthWith = (Position+1);

        /**
         * SDFS node affinity in relation <code>'/a/'</code>.
         */
        public final static Component.Relation A = new alto.lang.component.Relation.Named("a");
        public final static Component.Relation B = new alto.lang.component.Relation.Named("b");
        public final static Component.Relation C = new alto.lang.component.Relation.Named("c");
        public final static Component.Relation D = new alto.lang.component.Relation.Named("d");
        public final static Component.Relation E = new alto.lang.component.Relation.Named("e");
        public final static Component.Relation F = new alto.lang.component.Relation.Named("f");
        public final static Component.Relation G = new alto.lang.component.Relation.Named("g");
        public final static Component.Relation H = new alto.lang.component.Relation.Named("h");
        public final static Component.Relation I = new alto.lang.component.Relation.Named("i");
        public final static Component.Relation J = new alto.lang.component.Relation.Named("j");
        public final static Component.Relation K = new alto.lang.component.Relation.Named("k");
        public final static Component.Relation L = new alto.lang.component.Relation.Named("l");
        public final static Component.Relation M = new alto.lang.component.Relation.Named("m");
        public final static Component.Relation N = new alto.lang.component.Relation.Named("n");
        public final static Component.Relation O = new alto.lang.component.Relation.Named("o");
        public final static Component.Relation P = new alto.lang.component.Relation.Named("p");
        public final static Component.Relation Q = new alto.lang.component.Relation.Named("q");
        public final static Component.Relation R = new alto.lang.component.Relation.Named("r");
        public final static Component.Relation S = new alto.lang.component.Relation.Named("s");
        public final static Component.Relation T = new alto.lang.component.Relation.Named("t");
        /**
         * Normal user space content store is relation 'U' or
         * <code>'/u/'</code>.  Message objects under 'U' have Request
         * Line Path expressions in user space, not address space.  
         */
        public final static Component.Relation U = new alto.lang.component.Relation.Named("u");
        /**
         * Systemic relations on user space are put into 'V'.  For
         * example the Sio Type relation on Types has no
         * representation in user space, and is therefore in 'V'.
         */
        public final static Component.Relation V = new alto.lang.component.Relation.Named("v");
        public final static Component.Relation W = new alto.lang.component.Relation.Named("w");
        public final static Component.Relation X = new alto.lang.component.Relation.Named("x");
        public final static Component.Relation Y = new alto.lang.component.Relation.Named("y");
        public final static Component.Relation Z = new alto.lang.component.Relation.Named("z");

        public final static Component[] Base = new Component[]{U};

        public final static Component.Relation Nil = new alto.lang.component.Relation.Numeric(0);
        public final static Component.Relation ZERO = Nil;
        public final static Component.Relation ONE = new alto.lang.component.Relation.Numeric(1);

        public final static class Tools 
            extends Component.Tools
        {
            public final static Component.Relation From(Component[] addr){
                if (null == addr)
                    throw new java.lang.IllegalArgumentException();
                else if (Position < addr.length)
                    return (Component.Relation)addr[Position];
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static java.lang.String PathStorageFor(Component root){
                if (root instanceof Component.Relation)
                    return root.toString();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Relation ValueOf(java.lang.String name){
                name = Clean(name);
                if (null != name){
                    if (IsName(name))
                        return new alto.lang.component.Relation.Named(name);
                }
                return null;
            }
        }
    }
    /**
     * <p> The second component of every address. </p>
     * 
     * <p> The XOR64 hash of a hostname, or one of two special 8 bit
     * constants zero for global or one for local. </p>
     */
    public interface Host
        extends Component.Numeric
    {
        public final static int Position = 1;
        public final static int LengthWith = (Position+1);

        public final static Component.Host Local = new alto.lang.component.Host.Numeric(0);
        public final static Component.Host Global = new alto.lang.component.Host.Numeric(1);
        public final static Component[] Base = new Component[]{Global};

        public final static class Tools
            extends Component.Tools
        {
            public final static Component.Host From(Component[] addr){
                if (null == addr)
                    throw new java.lang.IllegalArgumentException();
                else if (Position < addr.length)
                    return (Component.Host)addr[Position];
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static java.lang.String PathStorageFor(Component root){
                if (root instanceof Component.Host)
                    return root.toString();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Host ValueOf(java.lang.String name){
                name = Clean(name);
                if (null != name){
                    if (IsName(name))
                        return null;//.sys.Error.Bug("Numeric address processing.")//
                    else
                        return new alto.lang.component.Host.Numeric(name);
                }
                else
                    return Host.Local;
            }
            public final static Component.Host ValueOf(alto.lang.Type type, java.lang.String name){
                name = Clean(name);
                if (null != name){
                    if (IsName(name)){
                        if (null != type)
                            return type.hostOf(name);
                        else
                            return new alto.lang.component.Host.Numeric(Function.Xor.Instance,name);
                    }
                    else
                        return new alto.lang.component.Host.Numeric(name);
                }
                else
                    return Host.Local;
            }
            public final static Component.Host ValueOf(byte[] value){
                if (null != value)
                    return new alto.lang.component.Host.Numeric(value);
                else
                    return null;
            }
        }
    }
    /**
     * <p> The third component of every address.  A DJB32 hash of a
     * mimetype string. </p>
     */
    public interface Type
        extends Component.Numeric
    {
        public final static int Position = 2;
        public final static int LengthWith = (Position+1);

        public final static class Tools
            extends Component.Tools
        {
            public final static Component.Type From(Component[] addr){
                if (null == addr)
                    throw new IllegalArgumentException();
                else if (Position < addr.length)
                    return (Component.Type)addr[Position];
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component[][] For(Component.Host host, alto.lang.Type.List typeList){
                Component[][] list = null;
                for (int cc = 0, count = typeList.length(); cc < count; cc++){
                    alto.lang.Type type = typeList.get(cc);
                    Component componentType = type.hashAddressComponent();
                    Component[] address = new Component[]{Component.Relation.U,host,componentType};
                    list = Component.List.Add(list,address);
                }
                return list;
            }
            public final static Component[] For(java.lang.String mimetype){
                Component.Type atyp = alto.lang.component.Type.Numeric.MimeType.Instance;
                return new Component[]{
                    Component.Relation.U,//(U)//(common)
                    Component.Host.Global,
                    atyp,
                    new alto.lang.component.Path.Numeric(atyp,mimetype),
                    Component.Version.Current
                };
            }
            public final static Component[] ForR(java.lang.String fext){
                Component.Type atyp = alto.lang.component.Type.Numeric.Address.Instance;
                return new Component[]{
                    Component.Relation.V,//(V)//(distinct)
                    Component.Host.Global,
                    atyp,
                    new alto.lang.component.Path.Numeric(atyp,fext),
                    Component.Version.Current
                };
            }
            public final static Component[] For(alto.lang.Component.Type addr){
                Component.Type atyp = 
                    alto.lang.component.Type.Numeric.MimeType.Instance;
                return new Component[]{
                    Component.Relation.U,//(U)//(common)
                    Component.Host.Global,
                    atyp,
                    new alto.lang.component.Path.Numeric(atyp.getType(),addr.toByteArray()),
                    Component.Version.Current
                };
            }
            public final static java.lang.String PathStorageFor(Component root){
                if (root instanceof Component.Type)
                    return root.toString();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Type ValueOf(alto.lang.Type type){
                if (null != type)
                    return (Component.Type)type.hashAddressComponent();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Type ValueOf(java.lang.String string){
                string = Clean(string);
                if (null != string)
                    return new alto.lang.component.Type.Numeric(string);
                else
                    return null;
            }
            public final static Component.Type ValueOf(byte[] value){
                if (null != value)
                    return new alto.lang.component.Type.Numeric(value);
                else
                    return null;
            }
            /**
             * @see alto.lang.Type#getTypeClass()
             */
            public final static java.lang.Class ClassFor(alto.sys.Reference contentReference)
                throws java.io.IOException
            {
                alto.lang.Type type = alto.lang.Type.Tools.Dereference(ReferenceTo(contentReference));
                if (null != type)
                    return type.getTypeClass();
                else
                    return null;
            }
            public final static alto.sys.Reference ReferenceToR(java.lang.String fext){
                return alto.sys.Reference.Tools.Create(new Address(ForR(fext)));
            }
            public final static alto.sys.Reference ReferenceTo(java.lang.String mimetype){
                return alto.sys.Reference.Tools.Create(new Address(For(mimetype)));
            }
            public final static alto.sys.Reference ReferenceTo(alto.sys.Reference contentReference){
                return contentReference.toAddressClass(alto.lang.component.Type.Numeric.MimeType.Instance);
            }
        }

        public boolean hasType();

        public alto.lang.Type getType();

    }
    /**
     * <p> The fourth component of every address.  The hash of the
     * HTTP request line path expression is hashed with a function
     * described by the type.  The default hash function for this
     * address component is XOR64, while some types employ the
     * DJB32. </p>
     */
    public interface Path
        extends Component.Numeric
    {
        public final static int Position = 3;
        public final static int LengthWith = (Position+1);

        public final static Component.Path ZERO = new alto.lang.component.Path.Numeric(0);
        public final static Component.Path ONE = new alto.lang.component.Path.Numeric(1);

        public final static class Tools
            extends Component.Tools
        {
            public final static Component.Path From(Component[] addr){
                if (null == addr)
                    throw new java.lang.IllegalArgumentException();
                else if (Position < addr.length)
                    return (Component.Path)addr[Position];
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static java.lang.String PathStorageFor(Component root){
                if (root instanceof Component.Path)
                    return root.toString();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Path ValueOf(alto.lang.Type type, int value){

                return new alto.lang.component.Path.Numeric(type,value);
            }
            public final static Component.Path ValueOf(alto.lang.Type type, long value){

                return new alto.lang.component.Path.Numeric(type,value);
            }
            public final static Component.Path ValueOf(alto.lang.Type type, byte[] value){

                return new alto.lang.component.Path.Numeric(type,value);
            }
            public final static Component.Path ValueOf(alto.lang.Type type, java.lang.String path){
                path = Path(path);
                if (null != path)
                    return new alto.lang.component.Path.Numeric(type,path);
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Path ValueOf(java.lang.String string){
                string = Clean(string);
                if (null != string){
                    try {
                        byte[] value = alto.io.u.Hex.decode(string);
                        return new alto.lang.component.Path.Numeric(value);
                    }
                    catch (java.lang.RuntimeException notnumeric){
                        return null;
                    }
                }
                else
                    return null;
            }
            /**
             * @param path A string
             * @return A string with leading '/' but no trailing '/'.
             */
            public final static java.lang.String Normalize(java.lang.String path){
                if (null == path)
                    return null;
                else {
                    if ('/' != path.charAt(0))
                        path = '/'+path;
                    //
                    int term = path.length()-1;
                    if (0 < term){
                        if ('/' == path.charAt(term)){
                            path = path.substring(0,term);
                            if (1 > path.length())
                                return null;
                        }
                        return path;
                    }
                    else
                        return path;
                }
            }
        }

        public boolean hasType();

        public alto.lang.Type getType();

    }
    /**
     * <p> The last component of every address. </p>
     * 
     * <p> Version components may be named or numeric. </p>
     */
    public interface Version
        extends Component
    {
        public final static int Position = 4;
        public final static int LengthWith = (Position+1);

        public final static Component.Version ZERO = new alto.lang.component.Version.Numeric(0);
        public final static Component.Version ONE = new alto.lang.component.Version.Numeric(1);

        public final static java.lang.String CurrentName = "current";
        /**
         * Storage item version current file name
         * <code>"current"</code>.
         */
        public final static Component.Version Current = new alto.lang.component.Version.Named(CurrentName);
            
        public final static java.lang.String TempName = "temporary";
        /**
         * Storage item version transaction temporary file name
         * <code>"temporary"</code>.
         */
        public final static Component.Version Temp = new alto.lang.component.Version.Named(TempName);

        public final static class Tools
            extends Component.Tools
        {
            public final static Component.Version From(Component[] addr){
                if (null == addr)
                    throw new java.lang.IllegalArgumentException();
                else if (Position < addr.length)
                    return (Component.Version)addr[Position];
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static java.lang.String PathStorageFor(Component root){
                if (root instanceof Component.Version)
                    return root.toString();
                else
                    throw new java.lang.IllegalArgumentException();
            }
            public final static Component.Version ValueOf(java.lang.String string){
                string = Clean(string);
                if (null != string){
                    try {
                        byte[] value = alto.io.u.Hex.decode(string);
                        return new alto.lang.component.Version.Numeric(value);
                    }
                    catch (java.lang.RuntimeException notnumeric){

                        return new alto.lang.component.Version.Named(string);
                    }
                }
                else
                    throw new java.lang.IllegalArgumentException(string);
            }
        }
    }

    public byte[] toByteArray();

    public int getPosition();

    public int compareTo(Component ano);

}
