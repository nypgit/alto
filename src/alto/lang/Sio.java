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
import alto.io.u.Bbuf;
import alto.io.u.Bits;
import alto.io.u.Utf8;
import alto.io.Input;
import alto.io.Output;
import alto.sys.Reference;

/**
 * SIO is a tagged message format for lists and trees.  Like XML, SIO
 * is a container for defining subformats.  SIO is lighter than XML,
 * but heavier than something like 9P messages.  
 * 
 * SIO is generally comparable to Google Protobuf.  In comparison to
 * protobuf SIO is not externally defined, and has an integrated
 * binding identification.
 * 
 * <h3>Message format</h3>
 * 
 * <pre>
 *   S V U ( T L D )
 * </pre>
 * 
 * The SIO format head contains the components S, V and U in eight
 * bytes.  The SIO format body contains the components T, L and D in
 * two or more bytes.
 * 
 * <h4>Message head</h4>
 * 
 * String S is a three byte validator and identifier having value
 * "sio" in ASCII / UTF8 code.
 * 
 * Byte "V" file format version has value one (0x01).  
 * 
 * Value "U" is a four byte integer Sio Type Class.  See {@link
 * Sio$Type}.
 * 
 * <h4>Message body</h4>
 * 
 * Byte "T" tag value is one of 0x1D group tag, or 0x1E record tag, or
 * 0x1C field tag.
 * 
 * Value "L" is the length of "D" in one, two or three bytes.  Each
 * byte of L is seven bits of a zero positive integer value, in
 * network byte order.  When the high bit of an encoded byte is on,
 * another byte of L bits follows.
 * 
 * Bytes "D" are opaque data and may contain <i>(T L D)</i> sequences
 * (when T is group or record).
 * 
 * <h3>Message structure</h3>
 * 
 * Generally, Sio messages are organized into groups containing
 * records, and records containing fields.  However, a Sio message may
 * start with a group, record or field.
 * 
 * A java object class marks its starting point in terms of group,
 * record or field using the interfaces declared under {@link
 * Sio$Type} including {@link Sio$Type$Group}, {@link Sio$Type$Record}
 * and {@link Sio$Type$Field}.  For example, the {@link
 * alto.sys.Address Address} class extends {@link Sio$File}
 * and implements {@link Sio$Type$Record} to denote that the sio type
 * class is structured as a record of fields.
 * 
 * Groups may contain fields, records or groups.  Records contain
 * fields, exclusively.  And fields are opaque payload data.  Within
 * this definition a class may define any structure and
 * interpretation.
 * 
 * A class may employ uniform record structures within groups like a
 * relational table, or a class may define irregular sets of records
 * within groups like an XML file.  
 * 
 * <h3>Objects and structures</h3>
 * 
 * A first impression for object orientation may be that Groups
 * contain Records and Records contain fields, exclusively.  Following
 * this rule strictly produces "too many objects".  It's practical for
 * a Group to contain a mixture of Fields and Groups.  
 * 
 * A programming language object class is typically a Group, and a
 * list of objects would be represented by a field containing an
 * integer followed by zero or more groups in the list.  And this
 * sequence may be followed as Field, Groups, Field, Groups without
 * extra and unneccessary containment when the sequence is static for
 * a class.
 * 
 * 
 * @author jdp
 */
public interface Sio {
    
    /**
     * 
     */
    public final static class Error 
        extends java.lang.RuntimeException
    {
        public final static byte[] Ctor(int[] value){
            if (null == value)
                return null;
            else {
                int len = value.length;
                byte[] re = new byte[len];
                for (int cc = 0; cc < len; cc++)
                    re[cc] = (byte)(value[cc] & 0xff);
                return re;
            }
        }
        public final static int IndexOfEOF(byte[] value){
            if (null == value)
                return -1;
            else {
                int len = value.length;
                for (int cc = 0; cc < len; cc++){
                    if (-1 == value[cc])
                        return cc;
                }
                return -1;
            }
        }

        private final byte[] value;

        private boolean initEOF;
        private int indexOfEOF;


        public Error(int value, java.lang.String msg){
            super(msg);
            this.value = new byte[]{ (byte)(value&0xff)};
        }
        public Error(byte[] value, java.lang.String msg){
            super(msg);
            this.value = value;
        }
        public Error(int[] value, java.lang.String msg){
            super(msg);
            this.value = Ctor(value);
        }
        public Error(Component.Path value, java.lang.String msg){
            super(msg);
            this.value = Bbuf.cat(Sio.Head.Value,value.toByteArray());
        }


        /**
         * @return Discarded input in order, may be unread.
         */
        public byte[] getValue(){
            return this.value;
        }
        public int indexOfEOF(){
            int indexOfEOF = this.indexOfEOF;
            if (!this.initEOF){
                this.initEOF = true;
                indexOfEOF = IndexOfEOF(this.value);
                this.indexOfEOF = indexOfEOF;
            }
            return indexOfEOF;
        }
        public boolean hasEOF(){
            return (-1 < this.indexOfEOF());
        }
    }
    /**
     * 
     */
    public final static class Head {

        public final static byte Version = 0x01;

        public final static byte[] Value = {
            's', 'i', 'o', Version
        };

        public final static Component.Path Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            if (a == Value[0]){
                int b = in.read();
                if (b == Value[1]){
                    int c = in.read();
                    if (c == Value[2]){
                        int d = in.read();
                        if (d == Value[3])
                            return Sio.Type.Read(in);
                        else
                            throw new Error(new int[]{a,b,c,d},"Format error version '0x"+Integer.toHexString(d)+"'.");
                    }
                    else
                        throw new Error(new int[]{a,b,c},"Format error head identifier [2]");
                }
                else
                    throw new Error(new int[]{a,b},"Format error head identifier [1]");
            }
            else
                throw new Error(a,"Format error head identifier [0]");
        }
        public final static void Write(Component.Path type, Output out)
            throws java.io.IOException
        {
            out.write(Value,0,4);
            Sio.Type.Write(type,out);
        }
    }
    public final static class Tag {

        public final static int Group  = 0x1D;
        public final static int Record = 0x1E;
        public final static int Field  = 0x1C;
        public final static int Unit   = 0x1F;

        public final static boolean IsValid(int value){
            switch(value){
            case Field:
            case Group:
            case Record:
            case Unit:
                return true;
            default:
                return false;
            }
        }

        public final static java.lang.String Name(int value){
            switch(value){
            case Field:
                return "Field";
            case Group:
                return "Group";
            case Record:
                return "Record";
            case Unit:
                return "Unit";
            default:
                return null;
            }
        }
    }
    /**
     * The superclass {@link Buffer$IOB} buffer contains payload for
     * this object.
     */
    public abstract static class SIOB
        extends Buffer.IOB
    {
        protected final static java.lang.Object[] Try(Input in)
            throws java.io.IOException
        {
            Object[] contents = null;
            alto.io.u.Bbi inb = (alto.io.u.Bbi)in;
            try {
                while (true){
                    int tag = inb.read();
                    if (-1 < tag){
                        inb.unread();
                        switch(tag){
                        case Sio.Tag.Field:
                            Sio.Field.Debug field = new Sio.Field.Debug(in);
                            contents = alto.io.u.Array.add(contents,field);
                            break;
                        case Sio.Tag.Group:
                            Sio.Group.Debug group = new Sio.Group.Debug(in);
                            contents = alto.io.u.Array.add(contents,group);
                            break;
                        case Sio.Tag.Record:
                            Sio.Record.Debug record = new Sio.Record.Debug(in);
                            contents = alto.io.u.Array.add(contents,record);
                            break;
                        default:
                            Object data = inb.toByteArray();
                            if (null != data)
                                contents = alto.io.u.Array.add(contents,data);
                            return contents;
                        }
                    }
                    else
                        return contents;
                }
            }
            catch (java.lang.RuntimeException end){
                Object data = inb.toByteArray();
                if (null != data)
                    contents = alto.io.u.Array.add(contents,data);
                return contents;
            }
            finally {
                in.close();
            }
        }

        protected int sioTag;


        public SIOB(){
            super();
        }
        public SIOB(Input in)
            throws java.io.IOException
        {
            this();
            this.sioRead(in);
        }


        public java.lang.Object[] debugContents(){
            return null;
        }
        public final void debugPrint(alto.io.u.Ios out)
            throws java.io.IOException
        {
            java.lang.String name = Sio.Tag.Name(this.getSioTag());
            out.indentln(name+" {");
            java.lang.Object[] contents = this.debugContents();
            if (null != contents){
                out.push();
                for (int cc = 0, count = contents.length; cc < count; cc++){
                    Object content = contents[cc];
                    if (content instanceof SIOB)
                        ((SIOB)content).debugPrint(out);
                    else if (content instanceof byte[]){
                        byte[] data = (byte[])content;
                        out.dataln(data);
                    }
                }
                out.pop();
            }
            out.indentln("}");
        }

        public final boolean hasSioTag(){
            return Tag.IsValid(this.sioTag);
        }
        public final boolean hasNotSioTag(){
            return (!Tag.IsValid(this.sioTag));
        }
        public final int getSioTag(){
            int sioTag = this.sioTag;
            if (Tag.IsValid(sioTag))
                return sioTag;

            else if (this instanceof Sio.Type.Field){
                sioTag = Sio.Tag.Field;
                this.sioTag = sioTag;
                return sioTag;
            }
            else if (this instanceof Sio.Type.Group){
                sioTag = Sio.Tag.Group;
                this.sioTag = sioTag;
                return sioTag;
            }
            else if (this instanceof Sio.Type.Record){
                sioTag = Sio.Tag.Record;
                this.sioTag = sioTag;
                return sioTag;
            }
            else
                throw new alto.sys.Error.State("Missing SIO tag.");
        }
        public final boolean isSioGroup(){
            return (Sio.Tag.Group == this.sioTag);
        }
        public final boolean isSioRecord(){
            return (Sio.Tag.Record == this.sioTag);
        }
        public final boolean isSioField(){
            return (Sio.Tag.Field == this.sioTag);
        }
        public final void sioRead(byte[] buf)
            throws java.io.IOException
        {
            Input in = new Buffer.InStream(buf);
            this.sioRead(in);
        }
        public void sioRead(Input in)
            throws java.io.IOException
        {
            int sioTag = in.read();
            if (Tag.IsValid(sioTag)){
                this.sioTag = sioTag;
                int len = Sio.Length.Read(in);
                this.readFrom(in,len);
            }
            else
                throw new Error(sioTag,"Bad SIO tag value.");
        }
        public void sioWrite(Output out)
            throws java.io.IOException
        {
            switch(this.getSioTag()){
            case Sio.Tag.Field:
                Sio.Field.Write(this.getBuffer(),out);
                return;
            case Sio.Tag.Group:
                Sio.Group.Write(this.getBuffer(),out);
                return;
            case Sio.Tag.Record:
                Sio.Record.Write(this.getBuffer(),out);
                return;
            default:
                throw new java.io.IOException("Invalid sio tag for writing '0x"+Integer.toHexString(this.sioTag)+"'.");
            }
        }
    }
    /**
     * This class includes a command line tool for debuging SIO files.
     */
    public abstract static class File
        extends SIOB
        implements Sio.Type.TypeClass
    {

        public final static class Debug
            extends Sio.File
        {

            public final static class AnyType
                extends java.lang.Object
                implements Component.Path
            {
                byte[] data;

                public AnyType(){
                    super();
                }

                public boolean hasType(){
                    return false;
                }
                public alto.lang.Type getType(){
                    return null;
                }
                public void sioRead(Input in) 
                    throws java.io.IOException 
                {
                    throw new UnsupportedOperationException();
                }
                public void sioWrite(Output out) 
                    throws java.io.IOException
                {
                    byte[] content = this.toByteArray();
                    Sio.Field.Write(content,out);
                }
                public byte[] toByteArray(){
                    return this.data;
                }
                public Component.Numeric add(Component.Numeric value){
                    long thisValue = this.longValue();
                    long thatValue = value.longValue();
                    return new alto.lang.component.Path.Numeric(Bits.Long(thisValue+thatValue));
                }
                public long longValue(){
                    return Bits.Integer(this.data);
                }
                public byte[] getBuffer(){
                    return this.data;
                }
                public int getBufferLength(){
                    byte[] data = this.data;
                    if (null == data)
                        return 0;
                    else
                        return data.length;
                }
                public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
                    byte[] bits = this.getBuffer();
                    if (null == bits)
                        return null;
                    else {
                        char[] cary = alto.io.u.Utf8.decode(bits);
                        return new String(cary,0,cary.length);
                    }
                }
                public int getPosition(){
                    return Component.Path.Position;
                }
                public boolean hasHashFunction(){
                    return false;
                }
                public Function getHashFunction(){
                    return null;
                }
                public int compareTo(Component ano){
                    return 0;
                }
                public boolean equals(Object ano){
                    return true;
                }
            }

            public Object[] contents;


            public Debug(Input in)
                throws java.io.IOException
            {
                super(in);
            }


            public java.lang.Object[] debugContents(){
                return this.contents;
            }
            public void sioRead(Input in)
                throws java.io.IOException
            {
                this.sioType = new AnyType();
                /*
                 * Read container
                 */
                super.sioRead(in);
                /*
                 * Read buffer
                 */
                in = this.openInput();
                try {
                    this.contents = Try(in);
                }
                finally {
                    in.close();
                }
            }


            public static void main(java.lang.String[] argv){
                java.io.File file = new java.io.File(argv[0]);
                alto.io.Input in = null;
                try {
                    in = new alto.lang.InputStream(new java.io.FileInputStream(file));
                    Debug debug = new Debug(in);
                    alto.io.u.Ios out = new alto.io.u.Ios(System.out);
                    debug.debugPrint(out);
                }
                catch (java.io.IOException exc){
                    exc.printStackTrace();
                }
                finally {
                    if (null != in){
                        try {
                            in.close();
                        }
                        catch (java.io.IOException exc){
                        }
                    }
                }
                java.lang.System.exit(0);
            }
        }


        protected volatile Component.Path sioType;


        public File(){
            super();
        }
        public File(Input in)
            throws java.io.IOException
        {
            super(in);
        }


        public boolean hasSioType(){
            return (null != this.sioType);
        }
        public boolean hasNotSioType(){
            return (null == this.sioType);
        }
        /**
         * Simple getter is overridden with a lazy fetch from
         * reference in {@link alto.sys.PSioFile} to dynamically get a
         * type from a reference.
         */
        public Component.Path getSioType(){
            Component.Path sioType = this.sioType;
            if (null != sioType)
                return sioType;
            else {
                throw new alto.sys.Error.State("Missing Sio Type");
            }
        }
        protected final boolean setSioTypeFrom(Reference reference)
            throws java.io.IOException
        {
            try {
                this.sioType = Sio.Type.From(reference);
            }
            catch (alto.sys.Error ignore){
            }
            return (null != this.sioType);
        }
        public void sioRead(Input in)
            throws java.io.IOException
        {
            Component.Path type = Sio.Head.Read(in);
            Component.Path thisType = this.getSioType();
            if (thisType.equals(type)){

                if (!thisType.hasType())
                    this.sioType = type;

                super.sioRead(in);
            }
            else
                throw new Error(type,"Wrong type");
        }
        public void sioWrite(Output out)
            throws java.io.IOException
        {
            Component.Path type = this.getSioType();
            if (null != type)
                Sio.Head.Write(type,out);
            else
                throw new alto.sys.Error.State("Missing Sio Type");
            super.sioWrite(out);
        }
    }
    /**
     * 
     */
    public abstract static class Group 
        extends SIOB
        implements Sio.Type.Group
    {

        public final static byte Tag = Sio.Tag.Group;

        public final static byte[] Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            if (Group.Tag == a){
                int len = Sio.Length.Read(in);
                if (0 < len){
                    byte[] buf = new byte[len];
                    int ofs = 0, read;
                    while (0 < len && 0 < (read = in.read(buf,ofs,len))){
                        ofs += read;
                        len -= read;
                    }
                    return buf;
                }
                else
                    return null;
            }
            else
                throw new Error(a,"Format error group tag");
        }
        public final static void Write(byte[] d, Output out)
            throws java.io.IOException
        {
            out.write(Sio.Group.Tag);
            int len = ((null != d)?(d.length):(0));
            if (0 < len){
                Sio.Length.Write(len,out);
                out.write(d,0,len);
            }
            else {
                out.write(0);
            }
        }

        public final static class Debug
            extends Sio.Group
        {
            public Object[] contents;


            public Debug(Input in)
                throws java.io.IOException
            {
                super(in);
            }


            public java.lang.Object[] debugContents(){
                return this.contents;
            }
            public void sioRead(Input in)
                throws java.io.IOException
            {
                /*
                 * Read container
                 */
                super.sioRead(in);
                /*
                 * Read buffer
                 */
                in = this.openInput();
                try {
                    this.contents = Try(in);
                }
                finally {
                    in.close();
                }
            }
        }


        public Group(){
            super();
        }
        public Group(Input in)
            throws java.io.IOException
        {
            super(in);
        }
    }
    /**
     * 
     */
    public abstract static class Record 
        extends SIOB
        implements Sio.Type.Record
    {
        public final static byte Tag = Sio.Tag.Record;

        public final static byte[] Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            if (Record.Tag == a){
                int len = Sio.Length.Read(in);
                if (0 < len){
                    byte[] buf = new byte[len];
                    int ofs = 0, read;
                    while (0 < len && 0 < (read = in.read(buf,ofs,len))){
                        ofs += read;
                        len -= read;
                    }
                    return buf;
                }
                else
                    return null;
            }
            else
                throw new Error(a,"Format error record tag");
        }
        public final static void Write(byte[] d, Output out)
            throws java.io.IOException
        {
            out.write(Sio.Record.Tag);
            int len = ((null != d)?(d.length):(0));
            if (0 < len){
                Sio.Length.Write(len,out);
                out.write(d,0,len);
            }
            else {
                out.write(0);
            }
        }

        public final static class Debug
            extends Sio.Record
        {
            public Object[] contents;


            public Debug(Input in)
                throws java.io.IOException
            {
                super(in);
            }


            public java.lang.Object[] debugContents(){
                return this.contents;
            }
            public void sioRead(Input in)
                throws java.io.IOException
            {
                /*
                 * Read container
                 */
                super.sioRead(in);
                /*
                 * Read buffer
                 */
                in = this.openInput();
                try {
                    this.contents = Try(in);
                }
                finally {
                    in.close();
                }
            }
        }


        public Record(){
            super();
        }
        public Record(Input in)
            throws java.io.IOException
        {
            super(in);
        }
    }
    /**
     * 
     */
    public abstract static class Field 
        extends SIOB
        implements Sio.Type.Field
    {
        public final static byte Tag = Sio.Tag.Field;

        public final static byte[] Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            if (Field.Tag == a){
                int len = Sio.Length.Read(in);
                if (0 < len){
                    byte[] buf = new byte[len];
                    int ofs = 0, read;
                    while (0 < len && 0 < (read = in.read(buf,ofs,len))){
                        ofs += read;
                        len -= read;
                    }
                    return buf;
                }
                else
                    return null;
            }
            else
                throw new Error(a,"Format error field tag");
        }
        public final static java.lang.String ReadUtf8(Input in)
            throws java.io.IOException
        {
            byte[] field = Field.Read(in);
            char[] string = Utf8.decode(field);
            if (null != string)
                return new java.lang.String(string,0,string.length);
            else
                return null;
        }
        public final static int ReadInt(Input in)
            throws java.io.IOException
        {
            byte[] field = Field.Read(in);
            return Bits.Integer(field);
        }
        public final static void Write(byte[] d, Output out)
            throws java.io.IOException
        {
            out.write(Sio.Field.Tag);
            int len = ((null != d)?(d.length):(0));
            if (0 < len){
                Sio.Length.Write(len,out);
                out.write(d,0,len);
            }
            else {
                out.write(0);
            }
        }
        public final static void WriteInt(int value, Output out)
            throws java.io.IOException
        {
            byte[] d = Bits.Integer(value);
            Field.Write(d,out);
        }
        public final static void WriteUtf8(java.lang.String string, Output out)
            throws java.io.IOException
        {
            Write(Utf8.encode(string),out);
        }

        public final static class Debug
            extends Sio.Field
        {
            public Object[] contents;


            public Debug(Input in)
                throws java.io.IOException
            {
                super(in);
            }


            public java.lang.Object[] debugContents(){
                return this.contents;
            }
            public void sioRead(Input in)
                throws java.io.IOException
            {
                /*
                 * Read container
                 */
                super.sioRead(in);
                /*
                 * Read buffer
                 */
                in = this.openInput();
                try {
                    this.contents = Try(in);
                }
                finally {
                    in.close();
                }
            }
        }


        public Field(){
            super();
        }
        public Field(Input in)
            throws java.io.IOException
        {
            super(in);
        }
    }
    /**
     * 
     */
    public abstract static class Length {

        public final static int Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            if (0 < (a & 0x80)){
                int b = in.read();
                if (0 < (b & 0x80)){
                    int c = in.read();

                    return (((a & 0x7f) << 14)|((b & 0x7f) << 7) | (c & 0x7f));
                }
                else
                    return (((a & 0x7f) << 7)|(b & 0x7f));
            }
            else
                return (a & 0x7f);
        }
        public final static void Write(int len, Output out)
            throws java.io.IOException
        {
            int a = ((len >>> 14) & 0x7f);
            int b = ((len >>> 7 ) & 0x7f);
            int c = (len & 0x7f);
            if (0 < a){
                out.write(a | 0x80);
                out.write(b | 0x80);
                out.write(c);
            }
            else if (0 < b){
                out.write(b | 0x80);
                out.write(c);
            }
            else
                out.write(c);
        }
    }
    /**
     * A sio type is a four byte address component.
     */
    public abstract static class Type {

        /**
         * A top level sio object has a sio type.
         * 
         * @see Sio$File
         */
        public interface TypeClass
            extends Sio
        {
            public boolean hasSioType();
            public boolean hasNotSioType();
            /**
             * @return Non null value or throw an illegal state
             * exception
             */
            public Component.Path getSioType();
        }

        /**
         * Notational marker interface denotes group
         */
        public interface Group
            extends Sio
        {}
        /**
         * Notational marker interface denotes record
         */
        public interface Record
            extends Sio
        {}
        /**
         * Notational marker interface denotes field
         */
        public interface Field
            extends Sio
        {}

        private final static int ValidTrue = 7;
        private final static int ValidInverse = (~7);
        public final static boolean IsValid(Component.Path type){
            if (null != type){
                byte[] bits = type.toByteArray();
                int bitsl = bits.length;
                return (0 < (bitsl & ValidTrue))&&(0 == (bitsl & ValidInverse));
            }
            else
                return false;
        }
        public final static Component.Path From(alto.lang.Type contentType){
            if (null != contentType){
                Component.Type addr = contentType.getAddressComponent();
                return new alto.lang.component.Path.Numeric(Function.Djb.Hash32(addr.toByteArray()));
            }
            else
                throw new alto.sys.Error.Argument("Missing type.");
        }
        public final static Component.Path From(Reference contentReference)
            throws java.io.IOException
        {
            if (null != contentReference)
                return From(contentReference.getContentType());
            else
                throw new alto.sys.Error.Argument("Missing reference.");
        }
        public final static Component.Path Read(Input in)
            throws java.io.IOException
        {
            int a = in.read();
            int b = in.read();
            int c = in.read();
            int d = in.read();
            byte[] bits = new byte[]{
                (byte)a,
                (byte)b,
                (byte)c,
                (byte)d
            };
            return Component.Path.Tools.ValueOf(alto.lang.Type.Tools.Instances.Sio(),bits);
        }
        public final static void Write(Component.Path type, Output out)
            throws java.io.IOException
        {
            byte[] bits = type.toByteArray();
            switch (bits.length){
            case 4:
                out.write(bits,0,4);
                return;
            case 3:
                out.write(0);
                out.write(bits,0,3);
                return;
            case 2:
                out.write(0);
                out.write(0);
                out.write(bits,0,2);
                return;
            case 1:
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(bits[0]);
                return;
            default:
                throw new alto.sys.Error.Argument(java.lang.String.valueOf(bits.length));
            }
        }
    }

    public abstract static class Abstract 
        extends java.lang.Object
        implements Sio
    {

        public Abstract(){
            super();
        }


        public final void sioRead(byte[] buf)
            throws java.io.IOException
        {
            Input in = new Buffer.InStream(buf);
            this.sioRead(in);
        }
    }


    public void sioRead(Input in) throws java.io.IOException;

    public void sioWrite(Output out) throws java.io.IOException;


}
