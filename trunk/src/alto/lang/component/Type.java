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
import alto.io.Code;
import alto.io.Check;

/**
 * 
 */
public interface Type 
    extends alto.lang.Component.Type
{
    /**
     * 
     */
    public static class Numeric
        extends alto.lang.component.Numeric
        implements Type
    {
        /**
         * 
         */
        public final static class MimeType
            extends alto.lang.component.Type.Numeric 
        {
            public final static MimeType Instance = new MimeType();

            private MimeType(){
                super(Function.Djb.Short.Instance,"application/x-syntelos-mime-type");
            }
        }
        /**
         * 
         */
        public final static class Address
            extends alto.lang.component.Type.Numeric 
        {
            public final static Address Instance = new Address();

            private Address(){
                super(Function.Djb.Short.Instance,"application/x-syntelos-address");
            }
        }


        private alto.lang.Type type;

        public Numeric(String hex){
            super(hex);
        }
        public Numeric(int bits){
            super(bits);
        }
        public Numeric(byte[] bits){
            super(bits);
        }
        public Numeric(alto.lang.Type type){
            super(type.hashAddress());
            this.type = type;
        }
        public Numeric(alto.io.Input in)
            throws java.io.IOException
        {
            super(in);
        }
        /**
         * @see alto.lang.component.Type$MimeType
         */
        protected Numeric(Function function, java.lang.String path){
            super(function,path);
        }


        @Override
        protected alto.lang.component.Type.Numeric newValue(byte[] value){
            return new alto.lang.component.Type.Numeric(value);
        }
        public boolean hasType(){
            return (null != this.type);
        }
        /**
         * @see alto.lang.type.Type#MimeType
         */
        @Code(Check.Bootstrap)
        public void setType(alto.lang.Type type){
            this.type = type;
        }
        public alto.lang.Type getType(){
            alto.lang.Type type = this.type;
            if (null != type)
                return type;
            else {
                type = alto.lang.Type.Tools.For(this);
                this.type = type;
                return type;
            }
        }
        public int getPosition(){
            return Position;
        }
    }
}
