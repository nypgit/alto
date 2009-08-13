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

/**
 * Subclasses and their containers implement Sio reading and writing
 * slightly differently.
 * 
 * @see Auth
 * @see Role
 */
public abstract class Protected
    extends alto.lang.sio.Group
{


    protected Protected(Keys keys, Input in)
        throws java.io.IOException
    {
        super();
        this.sioRead(keys,in);
    }
    protected Protected(){
        super();
    }


    public void sioRead(Keys keys, Input in) 
        throws java.io.IOException
    {
        /*
         * read container
         */
        super.sioRead(in);

        byte[] ciphertext = this.toByteArray();
        this.resetall();
        byte[] plaintext = keys.unprotectRSA(ciphertext);
        this.write(plaintext);
        /*
         * read contents
         */
        this.sioRead(super.openInput());
    }
    public void sioWrite(Keys keys, Output out) 
        throws java.io.IOException
    {
        /*
         * write contents
         */
        Output buf = super.openOutput();
        try {
            this.sioWrite(buf);

            buf.flush();
        }
        finally {
            buf.close();
        }
        byte[] plaintext = this.toByteArray();
        this.resetall();
        byte[] ciphertext = keys.protectRSA(plaintext);
        this.write(ciphertext);
        /*
         * write container
         */
        super.sioWrite(out);
    }
}
