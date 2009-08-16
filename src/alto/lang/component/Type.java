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
public class Type
    extends alto.lang.component.Numeric
    implements alto.lang.Component.Type
{
    /**
     * Type of mimetype (a headers formatted object).
     */
    public final static class MimeType
        extends alto.lang.component.Type
    {
        public final static MimeType Instance = new MimeType();

        private MimeType(){
            super(Function.Xor.Instance,Strings.MimeType);
        }
    }
    /**
     * Type of addresss (a sio formatted object).
     */
    public final static class Address
        extends alto.lang.component.Type
    {
        public final static Address Instance = new Address();

        private Address(){
            super(Function.Xor.Instance,Strings.Address);
        }
    }
    /**
     * The empty type for references to runtime objects having no
     * I/O representation.
     */
    public final static class Nil
        extends alto.lang.component.Type
    {
        public final static Nil Instance = new Nil();

        private Nil(){
            super(Function.Xor.Instance,Strings.Nil);
        }
    }


    private alto.lang.Type type;

    public Type(String hex){
        super(hex);
    }
    public Type(int bits){
        super(bits);
    }
    public Type(byte[] bits){
        super(bits);
    }
    public Type(alto.lang.Type type){
        super(type.getAddress().getComponentPath());
        this.type = type;
        this.hashFunction = type.getHashFunction();
    }
    public Type(alto.io.Input in)
        throws java.io.IOException
    {
        super(in);
    }
    public Type(Function function, java.lang.String path){
        super(function,path);
    }


    @Override
    protected alto.lang.component.Numeric newValue(byte[] value){
        return new alto.lang.component.Type(value);
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
            try {
                type = alto.lang.Type.Tools.For(this);
                this.type = type;
                return type;
            }
            catch (alto.sys.Error.State.Init bootstrap){
                return null;
            }
        }
    }
    public int getPosition(){
        return Position;
    }
}
