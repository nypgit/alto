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

import alto.hash.SHA1;
import alto.io.Input;
import alto.io.Output;
import alto.lang.Sio;
import alto.lang.sio.Field;

import java.util.Set;

/**
 * Foreign roles shared with a principal.
 * 
 * @see Protected
 */
public final class Role
    extends Protected
    implements alto.sys.Destroy
{

    public static class List {
        public final static Role[] Add(Role[] list, Role c){
            if (null == c)
                return list;
            else if (null == list)
                return new Role[]{c};
            else {
                int len = list.length;
                Role[] copier = new Role[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
    }


    private String uid;

    private byte[] nonce, signature;

    private boolean authenticated;

    private alto.io.Role cacheRole;

    /**
     * @see Protected
     */
    public Role(Keys keys, Input in)
        throws java.io.IOException
    {
        super(keys,in);
    }
    /**
     * Add role signed by keys
     */
    public Role(Keys role){
        super();
        this.uid = role.getUID();

        /*
         * [Review] Technically this should be done in authenticate
         */
        this.cacheRole = alto.io.Role.Lookup(this.uid);

        this.nonce = alto.io.u.Prng.RandLongBits();
        {
            SHA1 sha = new SHA1();
            sha.update(uid);
            sha.update(nonce);
            this.signature = role.signWithRSA(sha);
        }
    }


    public void destroy(){
    }
    public java.lang.String getUID(){
        return this.uid;
    }
    public boolean isUID(alto.io.Keys role){
        return this.isUID(role.getUID());
    }
    public boolean isUID(java.lang.String uid){
        return (uid.equals(this.uid));
    }
    public alto.io.Role toRole(){
        return this.cacheRole;
    }
    public boolean memberOf(Set<alto.io.Role> set){
        if (null != set)
            return set.contains(this.cacheRole);
        else
            return false;
    }
    /**
     * @return False for failure, True for success.
     */
    public boolean authenticate(alto.io.Keys role){
        if (this.authenticated)
            return this.authenticated;
        else {
            /*
             * If authentication fails, permit retries
             */
            SHA1 sha = new SHA1();
            sha.update(this.uid);
            sha.update(this.nonce);
            byte[] authentication = role.signWithRSA(sha);
            this.authenticated = alto.io.u.Bbuf.equals(this.signature,authentication);
            return this.authenticated;
        }
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
            this.uid = Field.ReadUtf8(in);

            /*
             * [Review] Technically this should be done in authenticate
             */
            this.cacheRole = alto.io.Role.Lookup(this.uid);

            this.nonce = Field.Read(in);
            this.signature = Field.Read(in);
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
            Field.WriteUtf8(this.uid,out);
            Field.Write(this.nonce,out);
            Field.Write(this.signature,out);

            out.flush();
        }
        finally {
            out.close();
        }
    }
}
