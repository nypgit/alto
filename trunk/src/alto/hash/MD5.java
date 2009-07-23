/*
 * Copyright (c) 1996 Santeri Paavolainen, Helsinki Finland 
 * Copyright (C) 2002, 2005 Timothy W Macinta
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


/**
 * <p> Fast implementation of RSA's MD5 hash generator in Java JDK
 * Beta-2 or higher.  This Java class has been derived from the RSA
 * Data Security, Inc. MD5 Message-Digest Algorithm and its reference
 * implementation given in RFC 1321. </p>
 * 
 * <p> Originally written by Santeri Paavolainen, Helsinki Finland
 * 1996.  </p>
 * 
 * <p> Many optimizations and some bug fixes by Timothy W Macinta.
 * </p>
 * 
 * <p> Cleanup and reorg by <code>jdp@syntelos</code>.  Dropped
 * "native" option.  </p>
 *
 *
 * @author Santeri Paavolainen (sjpaavol@cc.helsinki.fi)
 * @author Timothy W Macinta (twm@alum.mit.edu) 
 * @author jdp@syntelos 
 */
public class MD5 {

    /**
     * <p> {@link MD5} internal state structure permits easy
     * reinitialization.  </p>
     *
     * @author Santeri Paavolainen <sjpaavol@cc.helsinki.fi>
     * @author Timothy W Macinta (twm@alum.mit.edu) 
     * @author jdp@syntelos 
     */
    private final static class MD5State {
        /**
         * 128-bit state 
         */
        int	state[];
  
        /**
         * 64-bit character count
         */
        long count;
  
        /**
         * 64-byte buffer (512 bits) for storing to-be-hashed characters
         */
        byte	buffer[];

        public MD5State() {
            super();
            buffer = new byte[64];
            count = 0;
            state = new int[4];
    
            state[0] = 0x67452301;
            state[1] = 0xefcdab89;
            state[2] = 0x98badcfe;
            state[3] = 0x10325476;

        }

        /** Create this State as a copy of another state */
        public MD5State (MD5State from) {
            this();
    
            int i;
    
            for (i = 0; i < buffer.length; i++)
                this.buffer[i] = from.buffer[i];
    
            for (i = 0; i < state.length; i++)
                this.state[i] = from.state[i];
    
            this.count = from.count;
        }
    };

    /**
     * MD5 state
     */
    MD5State	state;
 
    /**
     * if hash() has been called, finals is set to the current finals
     * state.  Any update() causes this to be set to null.
     */
    MD5State 	finals;

  
    /**
     * Initialize MD5.
     */
    public MD5 () {
        super();
        this.init();
    }

    /**
     * Initialize and update MD5.
     * 
     * @param string Update hash after initialization.
     */
    public MD5 (String string) {
        this();
        this.update(string);
    }

    /**
     * Initialize MD5 internal state (object can be reused just by
     * calling init() after every hash()
     */
    public void init () {
        this.state = new MD5State();
        this.finals = null;
    }

    public void update (byte buffer[], int offset, int length) {
        this.finals = null;
        Update(this.state, buffer, offset, length);
    }
    public void update (byte buffer[], int length) {
        this.finals = null;
        Update(this.state, buffer, 0, length);
    }
    public void update (byte buffer[]) {
        this.update(buffer, 0, buffer.length);
    }
    public void update (byte b) {
        byte buffer[] = new byte[1];
        buffer[0] = b;

        this.update(buffer, 1);
    }
  
    /**
     * Update buffer with given string.  Note that because the version of
     * the s.getBytes() method without parameters is used to convert the
     * string to a byte array, the results of this method may be different
     * on different platforms.  The s.getBytes() method converts the string
     * into a byte array using the current platform's default character set
     * and may therefore have different results on platforms with different
     * default character sets.  If a version that works consistently
     * across platforms with different default character sets is desired,
     * use the overloaded version of the Update() method which takes a
     * string and a character encoding.
     *
     * @param s		String to be update to hash (is used as
     *		       	s.getBytes())
     */
    public void update (String s) {
        byte chars[] = s.getBytes();
        this.update(chars, chars.length);
    }

    /**
     * Update buffer with given string using the given encoding.  If the
     * given encoding is null, the encoding "ISO8859_1" is used.
     *
     * @param s		String to be update to hash (is used as
     *		       	s.getBytes(charset_name))
     * @param charset_name The character set to use to convert s to a
     *                    byte array, or null if the "ISO8859_1"
     *                    character set is desired.
     * @exception         java.io.UnsupportedEncodingException If the named
     *                    charset is not supported.
     */
    public void update (String s, String charset_name) throws java.io.UnsupportedEncodingException {
        if (charset_name == null) charset_name = "ISO8859_1";
        byte chars[] = s.getBytes(charset_name);
        this.update(chars, chars.length);
    }

    /**
     * Update buffer with a single integer (only & 0xff part is used,
     * as a byte)
     *
     * @param i		Integer value, which is then converted to 
     *			byte as i & 0xff
     */
    public void update (int i) {
        this.update((byte) (i & 0xff));
    }

    /**
     * Returns array of bytes (16 bytes) representing hash as of the
     * current state of this object. Note: getting a hash does not
     * invalidate the hash object, it only creates a copy of the real
     * state which is finalized. 
     *
     * @return	Array of 16 bytes, the hash of all updated bytes
     */
    public byte[] hash() {
        if (null == this.finals) {
            MD5State fin = new MD5State(this.state);

            int[] count_ints = {(int) (fin.count << 3), (int) (fin.count >> 29)};
            byte[] bits = Encode(count_ints, 8);
    
            int index = (int) (fin.count & 0x3f);
            int padlen = (index < 56) ? (56 - index) : (120 - index);

            Update(fin, PADDING, 0, padlen);
            Update(fin, bits, 0, 8);	

            /* this.update() sets finals to null 
             */
            this.finals = fin;
        } 
        return Encode(this.finals.state, 16);
    }    

    /**
     * Returns 32-character hex representation of this objects hash
     *
     * @return String of this object's hash
     */
    public String hashHex () {
        return asHex(this.hash());
    }

    /**	
     * Updates hash with the bytebuffer given (using at maximum length bytes from
     * that buffer)
     *
     * @param stat	Which state is updated
     * @param buffer	Array of bytes to be hashed
     * @param offset	Offset to buffer array
     * @param length	Use at maximum `length' bytes (absolute
     *			maximum is buffer.length)
     */
    private static void Update (MD5State stat, byte buffer[], int offset, int length) {
        int	index, partlen, i, start;

        /* Length can be told to be shorter, but not inter */
        if ((length - offset)> buffer.length)
            length = buffer.length - offset;

        /* compute number of bytes mod 64 */

        index = (int) (stat.count & 0x3f);
        stat.count += length;
    
        partlen = 64 - index;

        if (length >= partlen) {
            int[] decode_buf = new int[16];
            if (partlen == 64) {
                partlen = 0;
            } else {
                for (i = 0; i < partlen; i++)
                    stat.buffer[i + index] = buffer[i + offset];
                Transform(stat, stat.buffer, 0, decode_buf);
            }
            for (i = partlen; (i + 63) < length; i+= 64) {
                Transform(stat, buffer, i + offset, decode_buf);
            }
            index = 0;
        } 
        else 
            i = 0;

        /* buffer remaining input 
         */
        if (i < length) {
            start = i;
            for (; i < length; i++) {
                stat.buffer[index + i - start] = buffer[i + offset];
            }
        }
    }

    private static void Decode (byte buffer[], int offset, int[] out) {
        /*len += offset;
          for (int i = 0; offset < len; i++, offset += 4) {
          out[i] = ((int) (buffer[offset] & 0xff)) |
          (((int) (buffer[offset + 1] & 0xff)) << 8) |
          (((int) (buffer[offset + 2] & 0xff)) << 16) |
          (((int)  buffer[offset + 3]) << 24);
          }*/

        // unrolled loop (original loop shown above)

        out[0] = ((int) (buffer[offset] & 0xff)) |
            (((int) (buffer[offset + 1] & 0xff)) << 8) |
            (((int) (buffer[offset + 2] & 0xff)) << 16) |
            (((int)  buffer[offset + 3]) << 24);
        out[1] = ((int) (buffer[offset + 4] & 0xff)) |
            (((int) (buffer[offset + 5] & 0xff)) << 8) |
            (((int) (buffer[offset + 6] & 0xff)) << 16) |
            (((int)  buffer[offset + 7]) << 24);
        out[2] = ((int) (buffer[offset + 8] & 0xff)) |
            (((int) (buffer[offset + 9] & 0xff)) << 8) |
            (((int) (buffer[offset + 10] & 0xff)) << 16) |
            (((int)  buffer[offset + 11]) << 24);
        out[3] = ((int) (buffer[offset + 12] & 0xff)) |
            (((int) (buffer[offset + 13] & 0xff)) << 8) |
            (((int) (buffer[offset + 14] & 0xff)) << 16) |
            (((int)  buffer[offset + 15]) << 24);
        out[4] = ((int) (buffer[offset + 16] & 0xff)) |
            (((int) (buffer[offset + 17] & 0xff)) << 8) |
            (((int) (buffer[offset + 18] & 0xff)) << 16) |
            (((int)  buffer[offset + 19]) << 24);
        out[5] = ((int) (buffer[offset + 20] & 0xff)) |
            (((int) (buffer[offset + 21] & 0xff)) << 8) |
            (((int) (buffer[offset + 22] & 0xff)) << 16) |
            (((int)  buffer[offset + 23]) << 24);
        out[6] = ((int) (buffer[offset + 24] & 0xff)) |
            (((int) (buffer[offset + 25] & 0xff)) << 8) |
            (((int) (buffer[offset + 26] & 0xff)) << 16) |
            (((int)  buffer[offset + 27]) << 24);
        out[7] = ((int) (buffer[offset + 28] & 0xff)) |
            (((int) (buffer[offset + 29] & 0xff)) << 8) |
            (((int) (buffer[offset + 30] & 0xff)) << 16) |
            (((int)  buffer[offset + 31]) << 24);
        out[8] = ((int) (buffer[offset + 32] & 0xff)) |
            (((int) (buffer[offset + 33] & 0xff)) << 8) |
            (((int) (buffer[offset + 34] & 0xff)) << 16) |
            (((int)  buffer[offset + 35]) << 24);
        out[9] = ((int) (buffer[offset + 36] & 0xff)) |
            (((int) (buffer[offset + 37] & 0xff)) << 8) |
            (((int) (buffer[offset + 38] & 0xff)) << 16) |
            (((int)  buffer[offset + 39]) << 24);
        out[10] = ((int) (buffer[offset + 40] & 0xff)) |
            (((int) (buffer[offset + 41] & 0xff)) << 8) |
            (((int) (buffer[offset + 42] & 0xff)) << 16) |
            (((int)  buffer[offset + 43]) << 24);
        out[11] = ((int) (buffer[offset + 44] & 0xff)) |
            (((int) (buffer[offset + 45] & 0xff)) << 8) |
            (((int) (buffer[offset + 46] & 0xff)) << 16) |
            (((int)  buffer[offset + 47]) << 24);
        out[12] = ((int) (buffer[offset + 48] & 0xff)) |
            (((int) (buffer[offset + 49] & 0xff)) << 8) |
            (((int) (buffer[offset + 50] & 0xff)) << 16) |
            (((int)  buffer[offset + 51]) << 24);
        out[13] = ((int) (buffer[offset + 52] & 0xff)) |
            (((int) (buffer[offset + 53] & 0xff)) << 8) |
            (((int) (buffer[offset + 54] & 0xff)) << 16) |
            (((int)  buffer[offset + 55]) << 24);
        out[14] = ((int) (buffer[offset + 56] & 0xff)) |
            (((int) (buffer[offset + 57] & 0xff)) << 8) |
            (((int) (buffer[offset + 58] & 0xff)) << 16) |
            (((int)  buffer[offset + 59]) << 24);
        out[15] = ((int) (buffer[offset + 60] & 0xff)) |
            (((int) (buffer[offset + 61] & 0xff)) << 8) |
            (((int) (buffer[offset + 62] & 0xff)) << 16) |
            (((int)  buffer[offset + 63]) << 24);
    }

    private static void Transform (MD5State state, byte buffer[], int offset, int[] decode_buf) {
        int	
            a = state.state[0],
            b = state.state[1],
            c = state.state[2],
            d = state.state[3],
            x[] = decode_buf;

        Decode(buffer, offset, decode_buf);
    
        /* Round 1
         */
        a += ((b & c) | (~b & d)) + x[ 0] + 0xd76aa478; /* 1 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[ 1] + 0xe8c7b756; /* 2 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[ 2] + 0x242070db; /* 3 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[ 3] + 0xc1bdceee; /* 4 */
        b = ((b << 22) | (b >>> 10)) + c;

        a += ((b & c) | (~b & d)) + x[ 4] + 0xf57c0faf; /* 5 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[ 5] + 0x4787c62a; /* 6 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[ 6] + 0xa8304613; /* 7 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[ 7] + 0xfd469501; /* 8 */
        b = ((b << 22) | (b >>> 10)) + c;

        a += ((b & c) | (~b & d)) + x[ 8] + 0x698098d8; /* 9 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[ 9] + 0x8b44f7af; /* 10 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[10] + 0xffff5bb1; /* 11 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[11] + 0x895cd7be; /* 12 */
        b = ((b << 22) | (b >>> 10)) + c;

        a += ((b & c) | (~b & d)) + x[12] + 0x6b901122; /* 13 */
        a = ((a << 7) | (a >>> 25)) + b;
        d += ((a & b) | (~a & c)) + x[13] + 0xfd987193; /* 14 */
        d = ((d << 12) | (d >>> 20)) + a;
        c += ((d & a) | (~d & b)) + x[14] + 0xa679438e; /* 15 */
        c = ((c << 17) | (c >>> 15)) + d;
        b += ((c & d) | (~c & a)) + x[15] + 0x49b40821; /* 16 */
        b = ((b << 22) | (b >>> 10)) + c;
    
    
        /* Round 2
         */
        a += ((b & d) | (c & ~d)) + x[ 1] + 0xf61e2562; /* 17 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[ 6] + 0xc040b340; /* 18 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[11] + 0x265e5a51; /* 19 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[ 0] + 0xe9b6c7aa; /* 20 */
        b = ((b << 20) | (b >>> 12)) + c;

        a += ((b & d) | (c & ~d)) + x[ 5] + 0xd62f105d; /* 21 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[10] + 0x02441453; /* 22 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[15] + 0xd8a1e681; /* 23 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[ 4] + 0xe7d3fbc8; /* 24 */
        b = ((b << 20) | (b >>> 12)) + c;

        a += ((b & d) | (c & ~d)) + x[ 9] + 0x21e1cde6; /* 25 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[14] + 0xc33707d6; /* 26 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[ 3] + 0xf4d50d87; /* 27 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[ 8] + 0x455a14ed; /* 28 */
        b = ((b << 20) | (b >>> 12)) + c;

        a += ((b & d) | (c & ~d)) + x[13] + 0xa9e3e905; /* 29 */
        a = ((a << 5) | (a >>> 27)) + b;
        d += ((a & c) | (b & ~c)) + x[ 2] + 0xfcefa3f8; /* 30 */
        d = ((d << 9) | (d >>> 23)) + a;
        c += ((d & b) | (a & ~b)) + x[ 7] + 0x676f02d9; /* 31 */
        c = ((c << 14) | (c >>> 18)) + d;
        b += ((c & a) | (d & ~a)) + x[12] + 0x8d2a4c8a; /* 32 */
        b = ((b << 20) | (b >>> 12)) + c;
    
    
        /* Round 3
         */
        a += (b ^ c ^ d) + x[ 5] + 0xfffa3942;      /* 33 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[ 8] + 0x8771f681;      /* 34 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[11] + 0x6d9d6122;      /* 35 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[14] + 0xfde5380c;      /* 36 */
        b = ((b << 23) | (b >>> 9)) + c;
    
        a += (b ^ c ^ d) + x[ 1] + 0xa4beea44;      /* 37 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[ 4] + 0x4bdecfa9;      /* 38 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[ 7] + 0xf6bb4b60;      /* 39 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[10] + 0xbebfbc70;      /* 40 */
        b = ((b << 23) | (b >>> 9)) + c;
    
        a += (b ^ c ^ d) + x[13] + 0x289b7ec6;      /* 41 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[ 0] + 0xeaa127fa;      /* 42 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[ 3] + 0xd4ef3085;      /* 43 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[ 6] + 0x04881d05;      /* 44 */
        b = ((b << 23) | (b >>> 9)) + c;
    
        a += (b ^ c ^ d) + x[ 9] + 0xd9d4d039;      /* 33 */
        a = ((a << 4) | (a >>> 28)) + b;
        d += (a ^ b ^ c) + x[12] + 0xe6db99e5;      /* 34 */
        d = ((d << 11) | (d >>> 21)) + a;
        c += (d ^ a ^ b) + x[15] + 0x1fa27cf8;      /* 35 */
        c = ((c << 16) | (c >>> 16)) + d;
        b += (c ^ d ^ a) + x[ 2] + 0xc4ac5665;      /* 36 */
        b = ((b << 23) | (b >>> 9)) + c;
    

        /* Round 4
         */
        a += (c ^ (b | ~d)) + x[ 0] + 0xf4292244; /* 49 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[ 7] + 0x432aff97; /* 50 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[14] + 0xab9423a7; /* 51 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[ 5] + 0xfc93a039; /* 52 */
        b = ((b << 21) | (b >>> 11)) + c;

        a += (c ^ (b | ~d)) + x[12] + 0x655b59c3; /* 53 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[ 3] + 0x8f0ccc92; /* 54 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[10] + 0xffeff47d; /* 55 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[ 1] + 0x85845dd1; /* 56 */
        b = ((b << 21) | (b >>> 11)) + c;

        a += (c ^ (b | ~d)) + x[ 8] + 0x6fa87e4f; /* 57 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[15] + 0xfe2ce6e0; /* 58 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[ 6] + 0xa3014314; /* 59 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[13] + 0x4e0811a1; /* 60 */
        b = ((b << 21) | (b >>> 11)) + c;

        a += (c ^ (b | ~d)) + x[ 4] + 0xf7537e82; /* 61 */
        a = ((a << 6) | (a >>> 26)) + b;
        d += (b ^ (a | ~c)) + x[11] + 0xbd3af235; /* 62 */
        d = ((d << 10) | (d >>> 22)) + a;
        c += (a ^ (d | ~b)) + x[ 2] + 0x2ad7d2bb; /* 63 */
        c = ((c << 15) | (c >>> 17)) + d;
        b += (d ^ (c | ~a)) + x[ 9] + 0xeb86d391; /* 64 */
        b = ((b << 21) | (b >>> 11)) + c;

        state.state[0] += a;
        state.state[1] += b;
        state.state[2] += c;
        state.state[3] += d;
    }

    private static byte[] Encode (int input[], int len) {
        int		i, j;
        byte	out[];

        out = new byte[len];

        for (i = j = 0; j  < len; i++, j += 4) {
            out[j] = (byte) (input[i] & 0xff);
            out[j + 1] = (byte) ((input[i] >>> 8) & 0xff);
            out[j + 2] = (byte) ((input[i] >>> 16) & 0xff);
            out[j + 3] = (byte) ((input[i] >>> 24) & 0xff);
        }

        return out;
    }

    private final static char[] HEX_CHARS = {'0', '1', '2', '3',
                                             '4', '5', '6', '7',
                                             '8', '9', 'a', 'b',
                                             'c', 'd', 'e', 'f',};

    /**
     * Turns array of bytes into string representing each byte as
     * unsigned hex number.
     * 
     * @param hash	Array of bytes to convert to hex-string
     * @return	Generated hex string
     */
    public static String asHex (byte hash[]) {
        char buf[] = new char[hash.length * 2];
        for (int i = 0, x = 0; i < hash.length; i++) {
            buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
            buf[x++] = HEX_CHARS[hash[i] & 0xf];
        }
        return new String(buf);
    }

    /** 
     * Padding for hash
     */
    private final static byte PADDING[] = {
        (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

}
