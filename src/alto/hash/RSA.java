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
package alto.hash;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * <p> Rivest, Shamir and Adleman's 1978 asymmetric cipher from
 * Schneier, <i>Applied Cryptography</i>, 2nd Ed., pp 466-474, and the
 * RSA Specification, RSA Laboratories. </p>
 * 
 * <h3>Usage Notes</h3>
 * 
 * <p> Following these rules will ensure the integrity of a system
 * using this software, and RSA generally.
 * 
 * <ol>
 *
 * <li>Pad plaintext with random values or use the OAEP transform on
 * plaintext.
 * 
 * <li>Sign plaintext hashes, and never sign foreign (remote user)
 * plaintexts.  (Refer to the "ISO 9796 block format" for a solution
 * to the chosen plaintext attack [Schneier pg 472].)
 * 
 * <li>Use key sizes of 1024 (or greater).
 *
 * <li>Never sign ciphertext. (Sign the hash of any text.)
 * 
 * <li>Always ensure that the block (bit) length is (the largest power
 * of two) less than the public key N component bit length. 
 * 
 * </ol> </p>
 * 
 * <h3>Implementation Notes</h3>
 * 
 * <dl>
 * 
 * <dt>Weak keys
 * 
 * <dd>Carmichael numbers [Schneier pg 471] are non- prime numbers
 * often reported as primes due to the nature of probabilistic
 * primality tests.  See these <a
 * href="ftp://ftp.dpmms.cam.ac.uk/pub/Carmichael/">Carmichael
 * resources</a>.<br> Testing Java's "Big Integer" over the 246,683
 * Carmichael Numbers from 561 to 1e16, I found that a primality test
 * certainty (exponent) of 12 (P[prime] = 1-(1/2)^12, or 0.999755859)
 * is enough to filter out these false primes that will produce weak
 * keys.  Eleven is too low and produced one false positive as did
 * ten.  Twelve produced no false positives.  The sourcecode can be
 * modified for higher values.  <br> Note also that due to the
 * implementation of testing in the Java math package, the certainty
 * exponent is a minimum value, not an exclusive value.
 * 
 * </dl>
 *
 * @author John Pritchard
 * @since 1.2
 */
public class RSA 
    extends Tools
    implements Cipher 
{
    private static KeyFactory RSAKeyFactory;

    public final static KeyFactory GetKeyFactory(){
        if (null == RSAKeyFactory){
            try {
                RSAKeyFactory = KeyFactory.getInstance("RSA");
            }
            catch (java.security.NoSuchAlgorithmException unexpected){
                throw new IllegalStateException(unexpected);
            }
        }
        return RSAKeyFactory;
    }

    /**
     * Determined in testing against Carmichael numbers.  Eleven is
     * too low.  Twelve produced no false positives.
     */
    public final static int PRIME_CERT_EXP = 12;

    /**
     * Small public key component choice for key generation function,
     * recommended by PEM and PKCS.
     */
    public final static int E_SMALL = 0;
    /**
     * Medium sized public key component choice for key generation
     * function.
     */
    public final static int E_MEDIUM = 1;
    /**
     * Large public key component choice for key generation function,
     * recommended by X.509 and PKCS.
     */
    public final static int E_LARGE = 2;
    /**
     * Random public key component choice for key generation function.
     * Performance can be slow -- a random "e" has no particular
     * advantages, and can be less secure than one of the constants,
     * including the "small e".
     */
    public final static int E_RANDOM = 3;

    /**
     * Recommended public key "e" values [Schneier pg 469].
     */
    public final static java.math.BigInteger E[] = {
        THREE, java.math.BigInteger.valueOf(17), java.math.BigInteger.valueOf(65537)
    };

    public final static int KEYS_PUB_N = 0;
    public final static int KEYS_PUB_E = 1;
    public final static int KEYS_PRI_D = 2;


    /**
     * Produce keys choosing an "e" public key component as small,
     * medium, large or random.
     * 
     * @param keysz Key size in bits that is greater than the intended
     * block size.
     * 
     * @param ec Choice of public key "e" values using the constants:
     * <tt>`E_SMALL'</tt> (value zero), <tt>`E_MEDIUM'</tt> (value
     * one), <tt>`E_LARGE'</tt> (value two), or a number of bits
     * (greater than two!) in a random "e".
     * 
     * @param rng A random number generator (subclass of Random).
     * 
     * @returns An array containing the two components of the public
     * key, "n" and "e", with the private key "d", which should be
     * accessed using the constants <tt>`KEYS_PUB_N'</tt>,
     * <tt>`KEYS_PUB_E'</tt> and <tt>`KEYS_PRI_D'</tt>, respectively.
     */
    public final static java.math.BigInteger[] keys( int keysz, int ec, java.util.Random rng){
        java.math.BigInteger p, q, n, e = null, d, P;

        switch(ec){
        case E_SMALL:
            e = E[E_SMALL];
            break;
        case E_MEDIUM:
            e = E[E_MEDIUM];
            break;
        case E_LARGE:
            e = E[E_LARGE];
            break;
        default:

            // e = null
            break;
        }

        int sz = (keysz>>1);

        while(true){

            p = new java.math.BigInteger(sz,PRIME_CERT_EXP,rng);

            q = new java.math.BigInteger(sz,PRIME_CERT_EXP,rng);

            n = p.multiply(q);

            P = p.subtract(ONE).multiply(q.subtract(ONE));

            if ( null == e){

                // 
                java.math.BigInteger n1 = n.subtract(ONE);
                int comp = java.math.BigInteger.valueOf(ec).compareTo(n1);
                if (/* ec >= (n-1) */ -1 != comp)
                    ec = n1.intValue();
                //

                java.math.BigInteger p1 = p.subtract(ONE);

                java.math.BigInteger q1 = q.subtract(ONE);

                while (true){

                    e = new java.math.BigInteger(ec,PRIME_CERT_EXP,rng);

                    if ( 1 == p1.gcd(e).intValue() && 1 == q1.gcd(e).intValue())
                        break;
                }
            }

            d = e.modInverse(P);

            if ( 1 == d.gcd(n).intValue())
                break;
        }

        java.math.BigInteger keys[] = new java.math.BigInteger[3];

        keys[KEYS_PUB_N] = n;

        keys[KEYS_PUB_E] = e;

        keys[KEYS_PRI_D] = d;

        return keys;
    }



    private java.math.BigInteger n = null, e = null, d = null;//, _n1;

    private int blklen_plain, blklen_crypt;

    private RSAPublicKey keyPublic;
    private RSAPrivateKey keyPrivate;


    public RSA()
        throws java.security.NoSuchAlgorithmException
    {
        this(1024,E_LARGE,java.security.SecureRandom.getInstance("SHA1PRNG"));
    }
    /**
     * Create new keys choosing an "e" public key component as small,
     * medium, large or random.
     * 
     * @param keysz Key size in bits that is greater than the intended
     * block size.
     * 
     * @param ec Choice of public key "e" values using the constants:
     * <tt>`E_SMALL'</tt> (value zero), <tt>`E_MEDIUM'</tt> (value
     * one), <tt>`E_LARGE'</tt> (value two), or a number of bits
     * (greater than two!) in a random "e".
     * 
     * @param rng A random number generator (can override
     * functions in the Random class).
     */
    public RSA( int keysz, int ec, java.util.Random rng){
        this(keys(keysz,ec,rng));
    }
    /**
     * At least one of "d" and "n", or "e" and "n", are required for a
     * private or public key, respectively. 
     *
     * @param n Public key N component.
     * @param e Public key E component.
     * @param d Private key.
     */
    public RSA( java.math.BigInteger n, java.math.BigInteger e, java.math.BigInteger d){
        super();

        if ( null == n)
            throw new java.lang.IllegalArgumentException("Incomplete key is missing the `n' component.");
        else if ( null == e && null == d)
            throw new java.lang.IllegalArgumentException("Incomplete key.");
        else {
            this.n = n;
            this.e = e;
            this.d = d;
            this._init();
        }
    }
    public RSA( RSAPublicKey U, RSAPrivateKey R){
        this(U.getModulus(),U.getPublicExponent(),R.getPrivateExponent());
        this.keyPublic = U;
        this.keyPrivate = R;
    }
    public RSA( java.security.KeyPair pair){
        this( ((RSAPublicKey)pair.getPublic()), ((RSAPrivateKey)pair.getPrivate()));
    }
    public RSA( alto.io.Keys keys){
        this(keys.getKeyPair("RSA"));
    }
    /**
     * At least one of "d" and "n", or "e" and "n", are required for a
     * private or public key, respectively.
     *
     * <p> This constructor checks for null arguments, but not for
     * empty (length zero) arrays.  Length zero argument arrays can
     * lead to unexpected results!
     *
     * @param n Public key N component.
     * @param e Public key E component.
     * @param d Private key.
     */
    public RSA( byte[] n, byte[] e, byte[] d){
        super();
        if ( null == n)
            throw new java.lang.IllegalArgumentException("Incomplete key is missing the `n' component.");
        else if ( null == e || null == d){

            if ( null == e && null == d)
                throw new java.lang.IllegalArgumentException("Incomplete key.");
            else if ( null != e)
                this.e = new java.math.BigInteger(1,e);
            else if ( null != d)
                this.d = new java.math.BigInteger(1,d);
        }
        else {
            this.e = new java.math.BigInteger(1,e);
            this.d = new java.math.BigInteger(1,d);
        }
        this.n = new java.math.BigInteger(1,n);

        this._init();
    }
    /**
     * @param keys An array of two or three elements: if two, the two
     * components of the public key; if three, both the public and
     * private keys.
     */
    public RSA( java.math.BigInteger[] keys){
        super();
        if ( null == keys)
            throw new java.lang.IllegalArgumentException("Null keys array argument.");
        else {
            int len = keys.length;
            if ( 0 == len || 1 == len)

                throw new java.lang.IllegalArgumentException("Empty or insufficient keys array argument.");

            else if ( 2 == len){

                this.n = keys[KEYS_PUB_N];
                this.e = keys[KEYS_PUB_E];
            }
            else if ( 3 == len){

                this.n = keys[KEYS_PUB_N];
                this.e = keys[KEYS_PUB_E];
                this.d = keys[KEYS_PRI_D];
            }
            else
                throw new java.lang.IllegalArgumentException("Too many elements in keys array argument.");

            this._init();
        }
    }

    private final void _init(){

        int b = this.n.bitLength();

        this.blklen_plain = ((b + 7) >> 3);

        this.blklen_crypt = this.blklen_plain;
    }


    public RSAPublicKey getPublicKey(){
        RSAPublicKey keyPublic = this.keyPublic;
        if (null == keyPublic){
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(this.n,this.e);
            try {
                keyPublic = (RSAPublicKey)GetKeyFactory().generatePublic(keySpec);
                this.keyPublic = keyPublic;
            }
            catch (java.security.spec.InvalidKeySpecException unexpected){
                throw new IllegalStateException(unexpected);
            }
        }
        return keyPublic;
    }
    public RSAPrivateKey getPrivateKey(){
        RSAPrivateKey keyPrivate = this.keyPrivate;
        if (null == keyPrivate){
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(this.n,this.d);
            try {
                keyPrivate = (RSAPrivateKey)GetKeyFactory().generatePrivate(keySpec);
                this.keyPrivate = keyPrivate;
            }
            catch (java.security.spec.InvalidKeySpecException unexpected){
                throw new IllegalStateException(unexpected);
            }
        }
        return keyPrivate;
    }
    public void reset( byte[] key)
        throws java.security.NoSuchAlgorithmException
    {
        throw new java.lang.UnsupportedOperationException();
    }

    public int blockLengthPlain(){return this.blklen_plain;}

    public int blockLengthCipher(){return this.blklen_crypt;}

    public int encipherOutputLength( int blk){
        int blklen_plain = this.blklen_plain;
        if (blklen_plain < blk)
            return -1;
        else
            return blklen_plain;
    }
    public int decipherOutputLength( int blk){
        int blklen_crypt = this.blklen_crypt;
        if (blklen_crypt < blk)
            return -1;
        else
            return blklen_crypt;
    }
    public boolean canPad(int inlen){
        return (inlen < this.blklen_plain);
    }
    /**
     * If can pad and this returns null, then no pad necessary.
     */
    public byte[] pad(byte[] in, int inofs, int inlen){
        return Prepad(this.blklen_plain, in, inofs, inlen);
    }
    /**
     * Cipher interface to encrypt the plaintext block using the
     * public key.  Accepts short blocks.  See {@link #canPad(int)}
     * and {@link #pad(byte[],int,int)}.
     *
     * @param plaintext Block (its length must be the proper block size).
     *
     * @param inofs Offset into plaintext for block to process.
     *
     * @param inlen Length of plaintext block must be identical to
     * "block length" when set.
     *
     * @param output Destination buffer (not the input buffer because
     * the output size will vary).
     *
     * @param outofs Index into the destination buffer.
     */
    public int encipher ( byte[] plaintext, int inofs, int inlen, byte[] output, int outofs){
        int blkl = this.blklen_plain;
        if ( blkl < inlen)
            throw new java.lang.IllegalArgumentException("Invalid input block size '"+inlen+"'.");
        else if ( null == this.e || null == this.n)
            throw new java.lang.IllegalArgumentException("Public key is not available.");
        else
            return this.encipher( this.e, this.n, plaintext, inofs, inlen, output, outofs);
    }

    /**
     * Encrypt the plaintext block using the public key, starting at
     * plaintext buffer index "offset" and with "length" many bytes in
     * the plaintext block.
     *
     * @param e Public key "e" component
     *
     * @param n Public key "n" component
     *
     * @param plaintext Block buffer
     *
     * @param offset Buffer offset index to block
     *
     * @param length Buffer block length
     * 
     * @param output Destination buffer.
     *
     * @param outofs Destination buffer index.
     * 
     * @returns Number of bytes in output.
     */
    protected final static int encipher ( java.math.BigInteger e, java.math.BigInteger n, byte[] plaintext, int offset, int length, byte[] output, int outofs){

        byte[] block = Trim(plaintext,offset,length);

        java.math.BigInteger m = new java.math.BigInteger(1,block);

        if (0 <= m.compareTo(n))
            throw new IllegalArgumentException("Plain text block too large");
        else {
            java.math.BigInteger c = m.modPow( e, n);

            byte[] result = Trim(c);

            int reslen = result.length;
            int outlen = (output.length - outofs);
            if (reslen > outlen)
                throw new IllegalArgumentException("Output block too small, require '"+reslen+"' found '"+outlen+"'.");
            else {
                java.lang.System.arraycopy(result,0,output,outofs,reslen);
                return reslen;
            }
        }
    }
    /**
     * Cipher interface to decrypt the ciphertext block using the
     * private key.
     *
     * @param ciphertext Block (the array contains one block!)
     *
     * @param offset Index into the cipher text for the current block.
     *
     * @param len Length of the ciphertext block in bytes.
     *
     * @param output Destination buffer.
     *
     * @param outofs Offset into the destination buffer.
     *
     * @returns Number of bytes written to the output buffer.
     */
    public int decipher ( byte[] ciphertext, int offset, int len, byte[] output, int outofs){
        if ( null == this.d || null == this.n)
            throw new java.lang.IllegalArgumentException("Private key is not available.");
        else
            return this.decipher( n, d, ciphertext, offset, len, output, outofs);
    }
    public byte[] sign(SHA1 sha){
        byte[] hash = sha.hash();
        byte[] sig = new byte[this.decipherOutputLength(hash.length)];
        int r = this.decipher(hash,0,hash.length,sig,0);
        if (r < sig.length)
            return Trim(sig,0,r);
        else
            return sig;
    }
    /**
     * Decrypt the ciphertext block using the private key, starting at
     * the ciphertext buffer index "offset" and with "length" many
     * bytes in the ciphertext block.
     *
     * @param n Public key "n" component
     *
     * @param d Private key ("d")
     *
     * @param ciphertext Block buffer
     *
     * @param offset Buffer offset index to block
     *
     * @param length Buffer block length
     *
     * @param output Destination buffer.
     *
     * @param outofs Offset into the destination buffer.
     *
     * @returns Number of bytes written to the output buffer.
     */
    public final static int decipher ( java.math.BigInteger n, java.math.BigInteger d, byte[] ciphertext, int offset, int length, byte[] output, int outofs){

        byte[] block = Trim(ciphertext,offset,length);

        java.math.BigInteger c = new java.math.BigInteger(1,block);

        java.math.BigInteger m = c.modPow( d, n);

        byte[] result = Trim(m);

        int reslen = result.length;

        int outlen = (output.length - outofs);
        if (reslen > outlen)
            throw new IllegalArgumentException("Output block too small, require '"+reslen+"' found '"+outlen+"'.");
        else {
            java.lang.System.arraycopy(result,0,output,outofs,reslen);
            return reslen;
        }
    }


    public static void main(String[] argv){
        RSA rsa = null;
        try {
            String msg = argv[0];

            byte[] ptext = alto.io.u.Utf8.encode(msg);

            rsa = new RSA();

            int ctextLen = rsa.encipherOutputLength(ptext.length);
            if (0 < ctextLen){
                byte[] ctext = new byte[ctextLen];

                int olen = rsa.encipher(ptext,0,ptext.length,ctext,0);

                int ool = rsa.decipherOutputLength(olen);
                if (0 < ool){
                    byte[] ttext = new byte[ool];

                    olen = rsa.decipher(ctext,0,olen,ttext,0);

                    ttext = Trim(ttext,olen);

                    try {
                        String validateMsg = new String(alto.io.u.Utf8.decode(ttext));
                        System.out.println(validateMsg);
                        System.exit(0);
                    }
                    catch (IllegalStateException decoderr){

                        ComparisonPrint(ptext,ttext,System.err);

                        System.exit(1);
                    }
                }
                else
                    throw new IllegalStateException("rsa.decipherOutputLength("+olen+") = "+ool);
            }
            else
                throw new IllegalStateException("rsa.encipherOutputLength("+ptext.length+") = "+ctextLen);
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
