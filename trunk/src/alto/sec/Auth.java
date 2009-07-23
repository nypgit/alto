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
package alto.sec;

import alto.io.Authentication;
import alto.io.Input;
import alto.io.Output;
import alto.io.u.Array;
import alto.io.u.Bits;
import alto.io.u.Chbuf;
import alto.lang.Sio;

/**
 * Foreign authentication credentials associated with a principal.
 * 
 * @see Protected
 */
public final class Auth
    extends Protected
    implements alto.sys.Destroy
{

    public static class List {
        public final static Auth[] Add(Auth[] list, Auth c){
            if (null == c)
                return list;
            else if (null == list)
                return new Auth[]{c};
            else {
                int len = list.length;
                Auth[] copier = new Auth[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
    }

    public final static int APIKey = 0;
    public final static int APISecret = 1;



    private Authentication type;

    private int nelements;
    /**
     * In cases having an API identifier and secret, the API
     * Identifier is [0] and the API Secret is [1].
     */
    private String[] elements;

    private java.math.BigInteger cacheKey, cacheSecret;


    /**
     * @see Protected
     */
    public Auth(Keys keys, Input in)
        throws java.io.IOException
    {
        super(keys,in);
    }
    /**
     * Add auth
     */
    public Auth(Authentication type){
        super();
        if (null != type)
            this.type = type;
        else
            throw new alto.sys.Error.Bug();
    }
    public Auth(Authentication type, java.lang.String key, java.lang.String secret){
        super();
        if (null != type){
            this.type = type;
            if (type.hasAuthKeyAndSecret()){
                this.set(APIKey,key);
                this.set(APISecret,secret);
            }
            else
                throw new alto.sys.Error.Bug();
        }
        else
            throw new alto.sys.Error.Bug();
    }


    public void destroy(){
    }
    public int count(){
        return this.nelements;
    }
    public Authentication getType(){
        return this.type;
    }
    public boolean isType(Authentication type){
        return (type == this.type);
    }
    public java.lang.String get(int idx){
        if (-1 < idx && idx < this.nelements)
            return this.elements[idx];
        else
            return null;
    }
    public synchronized void set(int idx, java.lang.String value){
        this.cacheKey = null;
        this.cacheSecret = null;
        if (-1 < idx && idx <= this.count() && null != value){
            this.elements = Array.set(this.elements,idx,value);
            this.nelements = this.elements.length;
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public boolean hasKeyAndSecret(){
        Authentication type = this.type;
        return (null != type && type.hasAuthKeyAndSecret());
    }
    public boolean equalsKeyAndSecret(java.lang.String key, java.lang.String secret){
        String inKey = this.getAPIKey();
        if (null == inKey || (!inKey.equals(key)))
            return false;
        else {
            String inSecret = this.getAPISecret();
            return (null != inSecret && inSecret.equals(secret));
        }
    }
    public void setKeyAndSecret(java.lang.String key, java.lang.String secret){
        this.set(APIKey,key);
        this.set(APISecret,secret);
    }
    public java.lang.String getAPIKey(){
        if (this.type.hasAuthKeyAndSecret())
            return this.get(APIKey);
        else
            throw new alto.sys.Error.Bug();
    }
    public java.math.BigInteger getAPIKeyHex(){
        java.math.BigInteger cacheKey = this.cacheKey;
        if (null == cacheKey){
            java.lang.String hex = this.getAPIKey();
            byte[] bytes = alto.io.u.Hex.decode(hex);
            cacheKey = new java.math.BigInteger(bytes);
            synchronized(this){
                this.cacheKey = cacheKey;
            }
        }
        return cacheKey;
    }
    public java.lang.String getAPISecret(){
        if (this.type.hasAuthKeyAndSecret())
            return this.get(APISecret);
        else
            throw new alto.sys.Error.Bug();
    }
    public java.math.BigInteger getAPISecretHex(){
        java.math.BigInteger cacheSecret = this.cacheSecret;
        if (null == cacheSecret){
            java.lang.String hex = this.getAPISecret();
            byte[] bytes = alto.io.u.Hex.decode(hex);
            cacheSecret = new java.math.BigInteger(bytes);
            synchronized(this){
                this.cacheSecret = cacheSecret;
            }
        }
        return cacheSecret;
    }

    /**
     * @see Protected
     */
    public void sioRead(Input in) 
        throws java.io.IOException
    {
        /*
         * protected ('in' is buf)
         */
        try {
            int authtype = Sio.Field.ReadInt(in);
            this.type = Authentication.Lookup(authtype);

            int count = Sio.Field.ReadInt(in);
            this.nelements = count;

            java.lang.String elements[] = null, element;
            for (int cc = 0; cc < count; cc++){
                element = Sio.Field.ReadUtf8(in);
                elements = Array.add(elements,element);
            }
            this.elements = elements;
        }
        finally {
            in.close();
        }
    }
    /**
     * @see Protected
     */
    public void sioWrite(Output out) 
        throws java.io.IOException
    {
        /*
         * protected ('out' is buf)
         */
        try {
            Sio.Field.WriteInt(this.type.constant,out);

            java.lang.String elements[] = this.elements, element;
            int count = ((null != elements)?(elements.length):(0));
            this.nelements = count;

            Sio.Field.WriteInt(count,out);

            for (int cc = 0; cc < count; cc++){
                element = elements[cc];
                Sio.Field.WriteUtf8(element,out);
            }
            out.flush();
        }
        finally {
            out.close();
        }
    }
}
