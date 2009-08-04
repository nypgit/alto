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

import alto.io.u.Hex;

import java.net.URL;

/**
 * <p> Element of {@link Headers} list. </p>
 * 
 * @see Headers
 * @author jdp
 * @since 1.5
 */
public class Header
    extends java.lang.Object
{


    /**
     * Header classes implementing this interface will have this
     * method called immediately before toString on writing headers to
     * output.  Implementors reconstruct stale strings.
     */
    public interface Update {

        public void update();
    }

    /**
     * Authentication moved into lang/Headers so that any Headers
     * object could be employed as a client authentication context.
     */
    public static class SAuth
        extends Header
    {

        public final static String Version   = "SAuth";
        public final static String UID       = "SAuth-UID";
        public final static String Nonce     = "SAuth-Nonce";
        public final static String Signature = "SAuth-Signature";

        public interface Value {
            /**
             * Client request for authentication via SAuth.  Inclusion
             * of this header is standard HTTP, but not required for
             * authentication via SAuth.
             */
            public final static Header.SAuth Authorization = new Header.SAuth("Authorization","SAuth");
            /**
             * Client request for authentication
             */
            public final static Header.SAuth Version = new Header.SAuth("SAuth","1.0 RSA SHA-1");
        }

        public SAuth(String n, String v){
            super(n,v);
        }
    }
    public static class SDFS
        extends Header
    {

        public final static String Version    = "SDFS";
        public final static String MID        = "SDFS-MID";
        public final static String TTL        = "SDFS-TTL";
        public final static String Affinity   = "SDFS-Affinity";
        public final static String Method     = "SDFS-Method";
        public final static String MSig       = "SDFS-MSig";
        public final static String Notify     = "SDFS-Notify";

        public interface Value {
            public final static Header.SDFS Version = new Header.SDFS("SDFS","1.0");
        }

        public SDFS(String n, String v){
            super(n,v);
        }
    }
    public static class Http
        extends Header
    {

        public final static String Date              = "Date";
        public final static String Location          = "Location";
        public final static String Host              = "Host";
        public final static String UserAgent         = "User-Agent";
        public final static String Server            = "Server";
        public final static String Method            = "Method";
        public final static String Connection        = "Connection";

        /**
         * Request to authenticate
         */
        public final static String Authorization     = "Authorization";
        /**
         * Response header for authentication failure.
         */
        public final static String WWWAuthenticate   = "WWW-Authenticate";

        public final static String ContentType       = "Content-Type";
        public final static String ContentLength     = "Content-Length";
        public final static String ContentEncoding   = "Content-Encoding";
        public final static String ContentLocation   = "Content-Location";
        public final static String ContentRange      = "Content-Range";
        public final static String ContentMD5        = "Content-MD5";
        public final static String ETag              = "ETag";
        public final static String LastModified      = "Last-Modified";
        public final static String IfModifiedSince   = "If-Modified-Since";
        public final static String IfUnmodifiedSince = "If-Unmodified-Since";
        public final static String IfNoneMatch       = "If-None-Match";
        public final static String Expires           = "Expires";

        public final static String Destination       = "Destination";
        public final static String VersionTag        = "X-Version-Tag";
        public final static String XPrincipal        = "X-Principal";

        /**
         * Copy essential content description headers from 'from' to 'to'.
         */
        public final static void Update(alto.lang.HttpMessage to, alto.lang.HttpMessage from){
            if (null != to && null != from){
                to.setHeader(from.getLocationHeader());
                to.setHeader(from.getContentTypeHeader());
                to.setHeader(from.getContentLengthHeader());
                to.setHeader(from.getContentEncodingHeader());
                to.setHeader(from.getContentRangeHeader());
                to.setHeader(from.getContentLocationHeader());
                to.setHeader(from.getETagHeader());
                to.setHeader(from.getLastModifiedHeader());
                to.setHeader(from.getExpiresHeader());
                to.setHeader(from.getContentMD5Header());
            }
            else
                throw new alto.sys.Error.Bug();
        }

        public interface Value {
            public static class Method 
                extends Header.Http
                implements Header.Http.Value
            {
                public final static String Name = "X-Method";

                public final static Method SX = new Method("SX/1.0");
                public final static Method SXU = new Method("SX/1.0 U/1.0");


                public Method(String value){
                    super(Name,value);
                }
            }
            public static class Connection 
                extends Header.Http
                implements Header.Http.Value
            {
                public final static Connection KeepAlive = new Connection("Connection","keep-alive");
                public final static Connection Close = new Connection("Connection","close");
                public final static Connection Upgrade = new Connection("Connection","Upgrade");


                public Connection(String name, String value){
                    super(name,value);
                }
            }
            public static class ContentType 
                extends Header.Http
                implements Header.Http.Value
            {
                public final static ContentType Text = new ContentType("Content-Type",Type.Tools.Of("txt"));
                public final static ContentType Html = new ContentType("Content-Type",Type.Tools.Of("html"));
                public final static ContentType Xml = new ContentType("Content-Type",Type.Tools.Of("xml"));
                public final static ContentType ApplicationXml = new ContentType("Content-Type",Type.Tools.Of("axml"));
                public final static ContentType JavaSource = new ContentType("Content-Type",Type.Tools.Of("java"));
                public final static ContentType JavaClass = new ContentType("Content-Type",Type.Tools.Of("class"));

                public final static ContentType Multipart = new ContentType("Content-Type",Type.Tools.Of("multipart"));
                public final static ContentType MessageHttp = new ContentType("Content-Type",Type.Tools.Of("http"));
                public final static ContentType UrlEncoded = new ContentType("Content-Type",Type.Tools.Of("www"));

                public final static ContentType Jar = new ContentType("Content-Type",Type.Tools.Of("jar"));
                public final static ContentType Zip = new ContentType("Content-Type",Type.Tools.Of("zip"));


                public ContentType(String name, alto.lang.Type value){
                    super(name,value.toString(),value);
                    if (null == value)
                        throw new alto.sys.Error.Bug(name);
                }
            }
        }

        public Http(String n, String v){
            super(n,v);
        }
        public Http(String n, String v, Object p){
            super(n,v,p);
        }
    }
    /**
     * 
     */
    public static class Malformed
        extends alto.sys.Error.State
    {
        public Malformed(String line){
            super(line);
        }
        public Malformed(alto.sys.Reference ref, Malformed exc){
            super(ref.toString(),exc);
        }
    }


    public final String name;

    protected String string;

    protected String value;

    protected Object parsed;

    private String bufString;
    private byte[] bufBytes;


    public Header(String line){
        super();
        if (null != line){
            line = line.trim();
            if (0 < line.length()){
                int idx = line.indexOf(':');
                if (0 < idx){
                    this.name = line.substring(0,idx).trim();
                    this.value = line.substring(idx+1).trim();
                    this.string = this.name+": "+this.value;
                    return;
                }
            }
        }
        throw new Malformed(line);
    }
    public Header(String name, java.lang.Object value){
        this(name,((null != value)?(value.toString()):(null)),value);
    }
    public Header(String name, String value){
        this(name,value,null);
    }
    public Header(String name, String value, Object parsed){
        super();
        if (null != name){
            this.name = name.trim();
            if (null != value){
                this.value = value.trim();
                this.string = this.name+": "+this.value;
            }
            else if (null != parsed){
                value = parsed.toString();
                this.value = value.trim();
                this.string = this.name+": "+this.value;
            }
            this.parsed = parsed;
        }
        else
            throw new alto.sys.Error.Argument();
    }


    public String getName(){
        return this.name;
    }
    public String getValue(){
        return this.value;
    }
    public boolean hasParsed(){
        return (null != this.parsed);
    }
    public Object getParsed(){
        return this.parsed;
    }
    public String[] getParsedStringArray(){
        Object parsed = this.parsed;
        if (parsed instanceof String[])
            return (String[])parsed;
        else {
            String value = this.value;
            if (null != value){
                int valuelen = value.length();
                if (0 < valuelen){
                    String[] array = null;
                    java.util.StringTokenizer strtok = new java.util.StringTokenizer(value,";");
                    while (strtok.hasMoreTokens()){
                        String subs = strtok.nextToken();
                        if (null == array)
                            array = new String[]{subs.trim()};
                        else {
                            int len = array.length;
                            String[] copier = new String[len+1];
                            System.arraycopy(array,0,copier,0,len);
                            copier[len] = subs.trim();
                        }
                    }
                    this.parsed = array;
                    return array;
                }
            }
            return null;
        }
    }
    public Boolean getParsedBoolean(){
        Object parsed = this.parsed;
        if (parsed instanceof Boolean)
            return (Boolean)parsed;
        else {
            Boolean p = new Boolean(this.value);
            this.parsed = p;
            return p;
        }
    }
    public Number getParsedNumber(){
        Object parsed = this.getParsed();
        if (parsed instanceof java.lang.Number)
            return (java.lang.Number)parsed;
        else {
            try {
                Integer p = new Integer(this.value);
                this.parsed = p;
                return p;
            }
            catch (NumberFormatException exc){
                try {
                    Long p = new Long(this.value);
                    this.parsed = p;
                    return p;
                }
                catch (NumberFormatException exc2){
                    return null;
                }
            }
        }
    }
    public Integer getParsedInteger(){
        Object parsed = this.getParsed();
        if (parsed instanceof Integer)
            return (Integer)parsed;
        else if (parsed instanceof java.lang.Number)
            return new Integer(((java.lang.Number)parsed).intValue());

        else {
            try {
                Integer p = new Integer(this.value);
                this.parsed = p;
                return p;
            }
            catch (NumberFormatException exc){
                return null;
            }
        }
    }
    public Float getParsedFloat(){
        Object parsed = this.getParsed();
        if (parsed instanceof Float)
            return (Float)parsed;
        else if (parsed instanceof java.lang.Number)
            return new Float(((java.lang.Number)parsed).floatValue());

        else {
            try {
                Float p = new Float(this.value);
                this.parsed = p;
                return p;
            }
            catch (NumberFormatException exc){
                return null;
            }
        }
    }
    public Long getParsedLong(){
        Object parsed = this.getParsed();
        if (parsed instanceof Long)
            return (Long)parsed;
        else if (parsed instanceof java.lang.Number)
            return new Long(((java.lang.Number)parsed).longValue());

        else {
            try {
                Long p = new Long(this.value);
                this.parsed = p;
                return p;
            }
            catch (NumberFormatException exc){
                return null;
            }
        }
    }
    public java.math.BigInteger getParsedHex(){
        Object parsed = this.getParsed();
        if (parsed instanceof java.math.BigInteger)
            return (java.math.BigInteger)parsed;
        else {
            byte[] value = Hex.decode(this.value);
            try {
                java.math.BigInteger p = new java.math.BigInteger(value);
                this.parsed = p;
                return p;
            }
            catch (NumberFormatException exc){
                return null;
            }
        }
    }
    public URL getParsedURL(){
        Object parsed = this.parsed;
        if (parsed instanceof URL)
            return (URL)parsed;
        else {
            try {
                URL p = new URL(this.value);
                this.parsed = p;
                return p;
            }
            catch (java.net.MalformedURLException exc){
                return null;
            }
        }
    }
    public java.lang.Class getParsedClass(){
        Object parsed = this.parsed;
        if (parsed instanceof java.lang.Class)
            return (java.lang.Class)parsed;
        else {
            try {
                java.lang.Class p = java.lang.Class.forName(this.value);
                this.parsed = p;
                return p;
            }
            catch (java.lang.ClassNotFoundException exc){
                return null;
            }
        }
    }
    public alto.lang.Type getParsedType(){
        Object parsed = this.parsed;
        if (parsed instanceof alto.lang.Type)
            return (alto.lang.Type)parsed;
        else {
            alto.lang.Type p = alto.lang.Type.Tools.For(this.value);
            this.parsed = p;
            return p;
        }
    }
    public alto.sys.Reference getParsedReference(){
        Object parsed = this.parsed;
        if (parsed instanceof alto.sys.Reference)
            return (alto.sys.Reference)parsed;
        else {
            alto.sys.Reference p = new alto.sys.Reference(this.value);
            this.parsed = p;
            return p;
        }
    }
    public void setParsed(Object p){
        this.parsed = p;
    }
    public long getDate(){
        Object parsed = this.getParsed();
        if (parsed instanceof java.lang.Number)
            return ((java.lang.Number)parsed).longValue();
        else {
            long re = alto.lang.Date.Parse(this.value);
            this.parsed = new java.lang.Long(re);
            return re;
        }
    }
    public final void writeln(alto.io.Output out)
        throws java.io.IOException
    {
        byte[] bytes = this.byteln();
        
        out.write(bytes,0,bytes.length);
    }
    protected final byte[] byteln(){
        String string = this.string;
        String bufString = this.bufString;
        byte[] bufBytes = this.bufBytes;
        if (null != bufBytes && bufString == string)
            return bufBytes;
        else {
            if (null == string)
                string = this.toString();

            bufBytes = alto.io.u.Utf8.encode(string+"\r\n");

            synchronized(this){
                this.bufString = string;
                this.bufBytes = bufBytes;
            }
            return bufBytes;
        }
    }
    public String toString(){
        String string = this.string;
        if (null != string)
            return string;
        else { 
            String name = this.name;
            String value = this.value;
            if (null != value)
                string = (name+": "+value);
            else
                string = (name+": ");

            this.string = string;
            return string;
        }
    }
    /**
     * <p> Header line equivalence, hash of name and value in header
     * line format. </p>
     */
    public int hashCode(){
        return this.toString().hashCode();
    }
    /**
     * <p> Case sensitive header line string equivalence. </p>
     */
    public boolean equals(java.lang.Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else 
            return this.toString().equals(ano.toString());
    }
    public boolean equals(String name, String value){
        if (null != name && null != value){
            if (this.name.equals(name)){
                String thisvalue = this.value;
                return (null != thisvalue && thisvalue.equals(value));
            }
        }
        return false;
    }
}
