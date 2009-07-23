/* 
 *  Syntelos IOU 
 *  Copyright (C) 2006 John Pritchard and the Alto Project Group.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307 USA
 */
package alto.io.u;

/**
 * 
 *
 * @author John Pritchard
 */
public abstract class Prng 
    extends Bits
{


    public final static java.util.Random Instance = new java.util.Random();

    /**
     * Eight bytes of random bits
     */
    public final static long RandLong(){

        return Instance.nextLong();
    }

    /**
     * Eight bytes of random bits
     */
    public final static byte[] RandLongBits(){

        return Long(Instance.nextLong());
    }

    /**
     * Eight bytes of random bits in a hexadecimal string value.
     */
    public final static String RandLongStringHex(){

        return Hex.encode(Long(Instance.nextLong()));
    }

    /**
     * Eight bytes of random bits in a hexadecimal string value.
     */
    public final static byte[] RandLongAsciiHex(){

        return Hex.encode2(Long(Instance.nextLong()));
    }

    /**
     * Eight bytes of random bits in a base64 string value.
     */
    public final static String RandLongStringB64(){

        return new java.lang.String(B64.encode(Long(Instance.nextLong())));
    }

    /**
     * Eight bytes of random bits in a base64 string value.
     */
    public final static byte[] RandLongAsciiB64(){

        return B64.encode(Long(Instance.nextLong()));
    }

    /**
     * Fast and efficient checksum: XOR- fold input bits into output
     * in big- endian order.
     * 
     * <p> Eight bytes representing a big endian long integer will
     * produce the value of the integer.
     */
    public final static long LongXor ( byte[] b){
        if ( null == b) throw new IllegalArgumentException("Null argument for hash function.");

        long accum = 0, tmp;

        int shift ;

        for ( int c = 0, uc = b.length- 1; c < b.length; c++, uc--){

            shift = ((uc % 8)<<3);

            tmp = (b[c]&0xff);

            tmp <<= shift;

            accum ^= tmp;
        }
        return accum;
    }

    /**
     * Fast and efficient checksum: XOR- fold input bits into output
     * in big- endian order.
     * 
     * <p> Four bytes representing a big endian integer will produce
     * the value of the integer.
     */
    public final static int IntegerXor ( byte[] b){
        if ( null == b) throw new IllegalArgumentException("Null argument for hash function.");

        int accum = 0, tmp;

        int shift ;

        for ( int c = 0, uc = b.length- 1; c < b.length; c++, uc--){

            shift = ((uc % 4)<<2);

            tmp = (b[c]&0xff);

            tmp <<= shift;

            accum ^= tmp;
        }
        return accum;
    }

    /**
     * Fast and efficient checksum: XOR- fold input bits into output
     * in big- endian order.
     * 
     * <p> Two bytes representing a big endian short integer will
     * produce the value of the integer.
     */
    public final static short ShortXor ( byte[] b){
        if ( null == b) throw new IllegalArgumentException("Null argument for hash function.");

        short accum = 0, tmp;

        int shift ;

        for ( int c = 0, uc = b.length- 1; c < b.length; c++, uc--){

            shift = ((uc % 2)<<1);

            tmp = (short)(b[c]&0xff);

            tmp <<= shift;

            accum ^= tmp;
        }
        return accum;
    }

}
