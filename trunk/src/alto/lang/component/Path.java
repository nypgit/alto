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
public interface Path 
    extends alto.lang.Component.Path
{
    /**
     * 
     */
    public static class Numeric
        extends alto.lang.component.Numeric
        implements Path
    {
        private alto.lang.Type type;


        public Numeric(String hex){
            super(hex);
        }
        public Numeric(int bits){
            super(bits);
        }
        public Numeric(alto.lang.Type type, int bits){
            super(bits);
            this.type = type;
        }
        public Numeric(alto.lang.Type type, long bits){
            super(bits);
            this.type = type;
        }
        public Numeric(byte[] bits){
            super(bits);
        }
        public Numeric(alto.lang.Type type, byte[] bits){
            super(bits);
            this.type = type;
        }
        public Numeric(alto.lang.Type type, java.lang.String path){
            super(type.getHashFunction(),type.pathTo(path));
            this.type = type;
        }
        public Numeric(alto.lang.Component.Type type, java.lang.String path){
            this(type.getHashFunction(),path);
        }
        /**
         * Used only in the special case of Type addresses.
         */
        public Numeric(Function function, java.lang.String path){
            super(function,path);
        }
        public Numeric(alto.io.Input in)
            throws java.io.IOException
        {
            super(in);
        }


        @Override
        protected alto.lang.component.Path.Numeric newValue(byte[] value){
            return new alto.lang.component.Path.Numeric(value);
        }
        public boolean hasType(){
            return (null != this.type);
        }
        public alto.lang.Type getType(){
            return this.type;
        }
        public int getPosition(){
            return Position;
        }
        public boolean hasHashFunction(){
            if (null != this.hashFunction)
                return true;
            else {
                alto.lang.Type type = this.type;
                if (null != type)
                    return type.hasHashFunction();
                else
                    return false;
            }
        }
        public Function getHashFunction(){
            Function H = this.hashFunction;
            if (null != H)
                return H;
            else {
                alto.lang.Type type = this.type;
                if (null != type)
                    return type.getHashFunction();
                else
                    return null;
            }
        }
    }
}
