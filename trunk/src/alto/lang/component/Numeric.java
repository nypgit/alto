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
import alto.lang.Component;
import alto.lang.Sio;
import alto.lang.sio.Field;
import alto.io.Input;
import alto.io.Output;
import alto.io.u.Utf8;

import java.io.IOException;

/**
 * Addresses are primarily numeric.
 */
public abstract class Numeric
    extends alto.lang.Value
    implements alto.lang.Component.Numeric
{

    protected Function hashFunction;


    public Numeric(int value){
        super(value);
        this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(long value){
        super(value);
        this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(byte[] value){
        super(value);
        this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(java.lang.String string, int radix){
        super(string,radix);
        this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(java.lang.String string){
        super(string);
        this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(Function H, java.lang.String string){
        this(H.hash(Utf8.encode(string)));
        this.hashFunction = H;
    }
    public Numeric(java.math.BigInteger value){
        this(value.toByteArray());
    }
    public Numeric(Component c){
        super(c.toByteArray());
        if (c.hasHashFunction())
            this.hashFunction = c.getHashFunction();
        else
            this.hashFunction = Function.Xor.Instance;
    }
    public Numeric(alto.io.Input in)
        throws java.io.IOException
    {
        super(Field.Read(in));
        this.hashFunction = Function.Xor.Instance;
    }


    @Override
    protected abstract alto.lang.component.Numeric newValue(byte[] value);

    public final Component.Numeric add(Component.Numeric value){
        java.math.BigInteger big = super.add((java.math.BigInteger)value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Component.Numeric subtract(Component.Numeric value){
        java.math.BigInteger big = super.subtract((java.math.BigInteger)value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Component.Numeric multiply(Component.Numeric value){
        java.math.BigInteger big = super.multiply((java.math.BigInteger)value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Component.Numeric divide(Component.Numeric value){
        java.math.BigInteger big = super.divide((java.math.BigInteger)value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public void sioRead(Input in) 
        throws IOException 
    {
        throw new UnsupportedOperationException();
    }
    public void sioWrite(Output out) 
        throws IOException
    {
        byte[] content = this.toByteArray();
        Field.Write(content,out);
    }
    public boolean hasHashFunction(){
        return (null != this.hashFunction);
    }
    public Function getHashFunction(){
        return this.hashFunction;
    }
    public int compareTo(Component ano){
        if (this == ano)
            return 0;
        else if (null == ano)
            return 1;
        else if (ano instanceof Numeric){
            Numeric that = (Numeric)ano;
            long thisValue = this.longValue();
            long thatValue = that.longValue();
            if (thisValue < thatValue)
                return -1;
            else if (thisValue == thatValue)
                return 0;
            else
                return 1;
        }
        else
            return -1;
    }
}
