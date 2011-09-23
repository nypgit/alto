/*
 * Copyright (C) 2007, 2009  John Pritchard and the Alto Project Group.
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
package alto.hash;

import java.io.IOException ; //for `main()'

import java.util.*;

import java.math.BigInteger;

/**
 * This is a port of Steve Reid's SHA-1 code into Java by Chuck
 * McManus, updated by John Pritchard.
 * 
 * <pre>
 *      SHA-1 in C
 *      By Steve Reid (steve@edmweb.com)
 *      100% Public Domain
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
 * </pre>
 * 
 * <p><b>Command line</b>
 * 
 * <p> Calling this class as a command line filter, it will hash data
 * on the standard input.
 * 
 * @author Steve Reid (steve@edmweb.com)
 * @author Chuck McManis (cmcmanis@netcom.com)
 * @author John Pritchard (john@syntelos.com)
 */
public final class SHA1 {

    public final static int SHA_LEN_BITS = 160;

    public final static int SHA_LEN_BYTES = 20;

    /**
     * SHA-1 hash of the empty string, length zero.
     */
    public final static byte[] EMPTY = {
        (byte)0xDA, (byte)0x39, (byte)0xA3, (byte)0xEE, 
        (byte)0x5E, (byte)0x6B, (byte)0x4B, (byte)0x0D, 
        (byte)0x32, (byte)0x55, (byte)0xBF, (byte)0xEF, 
        (byte)0x95, (byte)0x60, (byte)0x18, (byte)0x90, 
        (byte)0xAF, (byte)0xD8, (byte)0x07, (byte)0x09
    };

    private byte hashBits[] = new byte[SHA_LEN_BYTES];
    private boolean hashValid = false;
    private int state[] = new int[5];
    private long count = 0;


    /*
     * The following array forms the basis for the transform
     * buffer. Update puts bytes into this buffer and then
     * transform adds it into the state of the hash.
     */
    private int block[] = new int[16];
    private int blockIndex;

    /**
     * Calls "reset".
     */
    public SHA1() {
        super();

        reset();
    }
    public SHA1(BigInteger b){
        this(Tools.Trim(b));
    }
    public SHA1(byte[] hashBits){
        super();
        if (null != hashBits){
            final int hl = hashBits.length;
            if (hl <= SHA_LEN_BYTES){
                final int x = (SHA_LEN_BYTES-hl);
                System.arraycopy(hashBits,0,this.hashBits,x,hl);
                this.hashValid = true;
            }
            else
                throw new IllegalArgumentException(alto.io.u.Hex.encode(hashBits));
        }
        else
            throw new IllegalArgumentException(alto.io.u.Hex.encode(hashBits));
    }


    public int hashSize(){
        return SHA_LEN_BITS;
    }

    public int hashLength(){
        return SHA_LEN_BYTES;
    }

    public byte[] empty(){ 
        return EMPTY;
    }

    /*
     * These functions are taken out of #defines in Steve's
     * code. Java doesn't have a preprocessor so the first
     * step is to just promote them to real methods.
     * Later we can optimize them out into inline code,
     * note that by making them final some compilers will
     * inline them when given the -O flag.
     */
    final int rol(int value, int bits) {
        int q = (value << bits) | (value >>> (32 - bits));
        return q;
    }

    final int blk0(int i) {
        block[i] = (rol(block[i],24)&0xFF00FF00) | (rol(block[i],8)&0x00FF00FF);
        return block[i];
    }

    final int blk(int i) {
        block[i&15] = rol(block[(i+13)&15]^block[(i+8)&15]^
                          block[(i+2)&15]^block[i&15], 1);
        return (block[i&15]);
    }

    final void R0(int data[], int v, int w, int x , int y, int z, int i) {
        data[z] += ((data[w] & (data[x] ^ data[y] )) ^ data[y]) +
            blk0(i) + 0x5A827999 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R1(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) +
            blk(i) + 0x5A827999 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R2(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) +
            blk(i) + 0x6ED9EBA1 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R3(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) +
            blk(i) + 0x8F1BBCDC + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }

    final void R4(int data[], int v, int w, int x, int y, int z, int i) {
        data[z] += (data[w] ^ data[x] ^ data[y]) +
            blk(i) + 0xCA62C1D6 + rol(data[v] ,5);
        data[w] = rol(data[w], 30);
    }


    /*
     * Steve's original code and comments :
     *
     * blk0() and blk() perform the initial expand.
     * I got the idea of expanding during the round function from SSLeay
     *
     * #define blk0(i) block->l[i]
     * #define blk(i) (block->l[i&15] = rol(block->l[(i+13)&15]^block->l[(i+8)&15] \
     *   ^block->l[(i+2)&15]^block->l[i&15],1))
     *
     * (R0+R1), R2, R3, R4 are the different operations used in SHA1
     * #define R0(v,w,x,y,z,i) z+=((w&(x^y))^y)+blk0(i)+0x5A827999+rol(v,5);w=rol(w,30);
     * #define R1(v,w,x,y,z,i) z+=((w&(x^y))^y)+blk(i)+0x5A827999+rol(v,5);w=rol(w,30);
     * #define R2(v,w,x,y,z,i) z+=(w^x^y)+blk(i)+0x6ED9EBA1+rol(v,5);w=rol(w,30);
     * #define R3(v,w,x,y,z,i) z+=(((w|x)&y)|(w&x))+blk(i)+0x8F1BBCDC+rol(v,5);w=rol(w,30);
     * #define R4(v,w,x,y,z,i) z+=(w^x^y)+blk(i)+0xCA62C1D6+rol(v,5);w=rol(w,30);
     */

    int dd[] = new int[5];

    /**
     * Hash a single 512-bit block. This is the core of the algorithm.
     *
     * Note that working with arrays is very inefficent in Java as it
     * does a class cast check each time you store into the array.
     *
     */

    void transform() {

        /* Copy context->state[] to working vars */
        dd[0] = state[0];
        dd[1] = state[1];
        dd[2] = state[2];
        dd[3] = state[3];
        dd[4] = state[4];
        /* 4 rounds of 20 operations each. Loop unrolled. */
        R0(dd,0,1,2,3,4, 0); R0(dd,4,0,1,2,3, 1); R0(dd,3,4,0,1,2, 2); R0(dd,2,3,4,0,1, 3);
        R0(dd,1,2,3,4,0, 4); R0(dd,0,1,2,3,4, 5); R0(dd,4,0,1,2,3, 6); R0(dd,3,4,0,1,2, 7);
        R0(dd,2,3,4,0,1, 8); R0(dd,1,2,3,4,0, 9); R0(dd,0,1,2,3,4,10); R0(dd,4,0,1,2,3,11);
        R0(dd,3,4,0,1,2,12); R0(dd,2,3,4,0,1,13); R0(dd,1,2,3,4,0,14); R0(dd,0,1,2,3,4,15);
        R1(dd,4,0,1,2,3,16); R1(dd,3,4,0,1,2,17); R1(dd,2,3,4,0,1,18); R1(dd,1,2,3,4,0,19);
        R2(dd,0,1,2,3,4,20); R2(dd,4,0,1,2,3,21); R2(dd,3,4,0,1,2,22); R2(dd,2,3,4,0,1,23);
        R2(dd,1,2,3,4,0,24); R2(dd,0,1,2,3,4,25); R2(dd,4,0,1,2,3,26); R2(dd,3,4,0,1,2,27);
        R2(dd,2,3,4,0,1,28); R2(dd,1,2,3,4,0,29); R2(dd,0,1,2,3,4,30); R2(dd,4,0,1,2,3,31);
        R2(dd,3,4,0,1,2,32); R2(dd,2,3,4,0,1,33); R2(dd,1,2,3,4,0,34); R2(dd,0,1,2,3,4,35);
        R2(dd,4,0,1,2,3,36); R2(dd,3,4,0,1,2,37); R2(dd,2,3,4,0,1,38); R2(dd,1,2,3,4,0,39);
        R3(dd,0,1,2,3,4,40); R3(dd,4,0,1,2,3,41); R3(dd,3,4,0,1,2,42); R3(dd,2,3,4,0,1,43);
        R3(dd,1,2,3,4,0,44); R3(dd,0,1,2,3,4,45); R3(dd,4,0,1,2,3,46); R3(dd,3,4,0,1,2,47);
        R3(dd,2,3,4,0,1,48); R3(dd,1,2,3,4,0,49); R3(dd,0,1,2,3,4,50); R3(dd,4,0,1,2,3,51);
        R3(dd,3,4,0,1,2,52); R3(dd,2,3,4,0,1,53); R3(dd,1,2,3,4,0,54); R3(dd,0,1,2,3,4,55);
        R3(dd,4,0,1,2,3,56); R3(dd,3,4,0,1,2,57); R3(dd,2,3,4,0,1,58); R3(dd,1,2,3,4,0,59);
        R4(dd,0,1,2,3,4,60); R4(dd,4,0,1,2,3,61); R4(dd,3,4,0,1,2,62); R4(dd,2,3,4,0,1,63);
        R4(dd,1,2,3,4,0,64); R4(dd,0,1,2,3,4,65); R4(dd,4,0,1,2,3,66); R4(dd,3,4,0,1,2,67);
        R4(dd,2,3,4,0,1,68); R4(dd,1,2,3,4,0,69); R4(dd,0,1,2,3,4,70); R4(dd,4,0,1,2,3,71);
        R4(dd,3,4,0,1,2,72); R4(dd,2,3,4,0,1,73); R4(dd,1,2,3,4,0,74); R4(dd,0,1,2,3,4,75);
        R4(dd,4,0,1,2,3,76); R4(dd,3,4,0,1,2,77); R4(dd,2,3,4,0,1,78); R4(dd,1,2,3,4,0,79);
        /* Add the working vars back into context.state[] */
        state[0] += dd[0];
        state[1] += dd[1];
        state[2] += dd[2];
        state[3] += dd[3];
        state[4] += dd[4];
    }


    /**
     *
     * SHA1Init - Initialize new context
     */
    public void reset() {
        /* SHA1 initialization constants */
        state[0] = 0x67452301;
        state[1] = 0xEFCDAB89;
        state[2] = 0x98BADCFE;
        state[3] = 0x10325476;
        state[4] = 0xC3D2E1F0;
        count = 0;
        hashBits = new byte[SHA_LEN_BYTES];
        hashValid = false;
        blockIndex = 0;
    }

    /**
     * Add one byte to the hash. When this is implemented
     * all of the abstract class methods end up calling
     * this method for types other than bytes.
     */
    public void update(byte b) {
        int mask = (8 * (blockIndex & 3));

        count += 8;
        block[blockIndex >> 2] &= ~(0xff << mask);
        block[blockIndex >> 2] |= (b & 0xff) << mask;
        blockIndex++;
        if (blockIndex == 64) {
            transform();
            blockIndex = 0;
        }
    }

    /**
     * Add two bytes for hashing
     */
    public void update16(int value16) {

        update((byte)((value16>>> 8)&0xff));
        update((byte)((value16>>> 0)&0xff));
    }

    /**
     * Add four bytes for hashing
     */
    public void update(int value32) {

        update((byte)((value32>>>24)&0xff));
        update((byte)((value32>>>16)&0xff));
        update((byte)((value32>>> 8)&0xff));
        update((byte)((value32>>> 0)&0xff));
    }

    /**
     * Add eight bytes for hashing
     */
    public void update(long value64) {

        update((byte)((value64>>>56)&0xff));
        update((byte)((value64>>>48)&0xff));
        update((byte)((value64>>>40)&0xff));
        update((byte)((value64>>>32)&0xff));

        update((byte)((value64>>>24)&0xff));
        update((byte)((value64>>>16)&0xff));
        update((byte)((value64>>> 8)&0xff));
        update((byte)((value64>>> 0)&0xff));
    }

    /**
     * Add data for hashing
     */
    public void update(BigInteger b) {
        update(b.toByteArray());
    }

    public void update(String string) {
        this.update( alto.io.u.Utf8.encode(string));
    }

    /**
     * Add all bytes to the hash.
     */
    public void update(byte input[]) {
        if ( null != input){
            int len = input.length;
            for (int i = 0; i < len; i++) 
                update(input[i]);
        }
    }

    /**
     * Add a range bytes to the hash.
     */
    public void update(byte input[], int offset, int len) {
        if ( null != input){
            if ( 0 < offset){
                len += offset;
                for (int i = offset; i < len; i++) 
                    update(input[i]);
            }
            else
                for (int i = 0; i < len; i++) 
                    update(input[i]);
        }
    }

    /**
     * Complete processing on the message digest.
     */
    private void finish() {
        if (hashValid)
            return;
        else {
            byte bits[] = new byte[8];
            int i, j;

            for (i = 0; i < 8; i++) {
                bits[i] = (byte)((count >>> (((7 - i) * 8))) & 0xff);
            }

            update((byte) 128);
            while (blockIndex != 56)
                update((byte) 0);
            // This should cause a transform to happen.
            update(bits);
            for (i = 0; i < SHA_LEN_BYTES; i++) {
                hashBits[i] = (byte)
                    ((state[i>>2] >> ((3-(i & 3)) * 8) ) & 0xff);
            }
            hashValid = true;
        }
    }

    public byte[] hash(){
        return hash(false,null,0);
    }
    
    public byte[] hash(boolean reset, byte[] buffer, int offset){

        finish();

        if (null == buffer){

            buffer = new byte[SHA_LEN_BYTES];

            offset = 0;
        }
        else if ( 0 > offset) offset = 0;

        System.arraycopy(hashBits, 0, buffer, offset, SHA_LEN_BYTES);

        if (reset) 
            reset();

        return buffer;
    }

    public String hashKeyname() { return "SHA1"; }

    public BigInteger toInteger(){

        return new BigInteger(1,this.hash());
    }
    public boolean equals(Object that){
        if (that instanceof SHA1)
            return this.equals( (SHA1)that);
        else
            return false;
    }
    public boolean equals(SHA1 that){
        if (this == that)
            return true;
        else if (null == that)
            return false;
        else {
            byte[] ha = this.hash();
            byte[] hb = that.hash();
            return (Tools.NotEmpty(ha)
                    && Tools.Equals(ha,hb));
        }
    }

    

    /**
     * This <code>`main()'</code> method was created to easily test
     * this class.  It hashes whatever's on <code>`System.in'.</code> */
    public static void main(String args[]) {

        int bufl = 1024;

        byte buf[] = new byte[bufl];

        int rc;

        SHA1 md = new SHA1();

        try {
            while ((rc = System.in.read(buf, 0, bufl)) > 0) {

                md.update(buf, 0, rc);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        byte[] out = md.hash();

        System.out.println("sha1: "+alto.io.u.Hex.encode(out));

    }
}
