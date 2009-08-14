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
package alto.lang.buffer;

import alto.lang.Buffer;

/**
 * Extends {@link java.io.ByteArrayOutputStream} with {@link
 * Buffer} for use by {@link OutputStream}.  
 */
public class OutputStream
    extends alto.io.u.Bbo
    implements Buffer,
               alto.io.Output
{
    public static class Writer
        extends java.io.OutputStreamWriter
        implements Buffer
    {

        public Writer(){
            super(new OutputStream());
        }
        public Writer(Abstract iob){
            super(new OutputStream(iob));
            iob.reset_write();
        }


        public OutputStream getOutputStream(){
            return (OutputStream)super.lock;
        }
        public byte[] getBuffer(){
            try {
                return this.getOutputStream().toByteArray();
            }
            catch (java.io.IOException notreached){
                throw new alto.sys.Error.State();
            }
        }
        public int getBufferLength(){
            try {
                return this.getOutputStream().length();
            }
            catch (java.io.IOException notreached){
                throw new alto.sys.Error.State();
            }
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
    }

    /**
     * Fillable
     */
    public OutputStream(){
        super();
    }
    public OutputStream(Abstract iob){
        super(iob);
        iob.reset_write();
    }


    public byte[] getBuffer(){
        try {
            return this.toByteArray();
        }
        catch (java.io.IOException ignore){
            throw new alto.sys.Error.State();
        }
    }
    public int getBufferLength(){
        try {
            return this.length();
        }
        catch (java.io.IOException ignore){
            throw new alto.sys.Error.State();
        }
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
}
