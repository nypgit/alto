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

/**
 * <p> A Big Integer subclass for stronger typing caches byte array
 * value.  </p>
 * 
 * @since 1.1
 * @author jdp
 */
public class Value
    extends java.math.BigInteger
    implements Buffer
{
    public final static Value ZERO = new Value("00");
    public final static Value ONE = new Value("01");
    public final static Value TWO = new Value("02");
    public final static Value TEN = new Value("0A");


    private final byte[] value;

    private final java.lang.String valueHex;

    private final int valueHash;


    public Value(int value){
        this(alto.io.u.Bits.Integer(value));
    }
    public Value(long value){
        this(alto.io.u.Bits.Long(value));
    }
    public Value(byte[] value){
        super(value);
        this.value = value;
        this.valueHex = alto.io.u.Hex.encode(value);
        this.valueHash = alto.hash.Function.Xor.Hash32(value);
    }
    /**
     * @param string Number in base 'radix'.
     * @param radix Order of numeric representation.
     */
    public Value(java.lang.String string, int radix){
        super(string,radix);
        byte[] value = super.toByteArray();
        this.value = value;
        this.valueHex = alto.io.u.Hex.encode(value);
        this.valueHash = alto.hash.Function.Xor.Hash32(value);
    }
    /**
     * Base 16, Hex.
     */
    public Value(java.lang.String string){
        this(alto.io.u.Hex.decode(string));
    }
    public Value(Value value){
        this(value.getBuffer());
    }


    protected Value newValue(byte[] value){
        return new Value(value);
    }
    public final Value add(Value value){
        java.math.BigInteger big = super.add(value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Value subtract(Value value){
        java.math.BigInteger big = super.subtract(value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Value multiply(Value value){
        java.math.BigInteger big = super.multiply(value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final Value divide(Value value){
        java.math.BigInteger big = super.divide(value);
        if (this == big)
            return this;
        else if (value == big)
            return value;
        else {
            return this.newValue(big.toByteArray());
        }
    }
    public final byte[] toByteArray(){
        return this.value;
    }
    public final byte[] getBuffer(){
        return this.value;
    }
    public final int getBufferLength(){
        return this.value.length;
    }
    public final int hashCode(){
        return this.valueHash;
    }
    public final boolean equals(java.lang.Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else
            return this.toString().equals(ano.toString());
    }
    /**
     * @return Plain hexidecimal encoding of value
     */
    public final java.lang.String toString(){
        return this.valueHex;
    }
    public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException {
        return this.valueHex;
    }
}
