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
package alto.lang.component;

import alto.hash.Function;

/**
 * 
 */
public interface Host 
    extends alto.lang.Component.Host
{
    /**
     * 
     */
    public static class Numeric
        extends alto.lang.component.Numeric
        implements Host
    {

        public Numeric(int value){
            super(value);
        }
        public Numeric(long value){
            super(value);
        }
        public Numeric(byte[] bits){
            super(bits);
        }
        public Numeric(java.lang.String string, int radix){
            super(string,radix);
        }
        public Numeric(java.lang.String string){
            super(string);
        }
        public Numeric(Function H, java.lang.String string){
            super(H,string);
        }
        public Numeric(java.math.BigInteger bint){
            super(bint.toByteArray());
        }
        public Numeric(alto.io.Input in)
            throws java.io.IOException
        {
            super(in);
        }


        @Override
        protected alto.lang.component.Host.Numeric newValue(byte[] value){
            return new alto.lang.component.Host.Numeric(value);
        }
        public int getPosition(){
            return Position;
        }
    }
}
