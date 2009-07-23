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

/**
 * <p> The L. Blum, M. Blum and M. Shub secure PRNG (1986).  "Quadratic
 * residue generator".  Very good and very slow.  Typical application:
 * key generation.  Implemented from Schneier, "Applied Cryptography",
 * 2nd Ed., pg 417. </p>
 * 
 * @author John Pritchard
 * @since 1.2
 */
public class BBS extends java.util.Random {

    /**
     * Determined in testing against Carmichael numbers.  Eleven is
     * too low.  Twelve produced no false positives.
     */
    public final static int PRIME_CERT_EXP = 12;

    /**
     * Default Blum integer is 2048 bits.
     */
    public final static int DEFAULT_BLUM_SIZE = 2048;



    private int blumsz = DEFAULT_BLUM_SIZE;

    /**
     * Large Blum integer, public key.  <i>N = P * Q</i>, for special P and Q.
     */
    protected java.math.BigInteger N, P, Q; 

    /**
     * Seed, private key.
     */
    protected java.math.BigInteger X; 

    /**
     * Construct a new 2048 bit BBS, generating new public and private
     * keys using the default secure random PRNG from Sun (SHA-1 hash
     * based).  This usage is compatible with Java runtimes since
     * version 1.1.
     * 
     * @since 1.1
     */
    public BBS(){
        this( DEFAULT_BLUM_SIZE, new java.security.SecureRandom()); 
    }

    /**
     * Construct a new BBS, generating new public and private keys.
     * 
     * @param blumsz Blum Size: number of bits in N, the Blum integer.  
     *
     * @param rng Random bit generator for creating large primes for seed.
     */
    public BBS( int blumsz, java.util.Random rng){
        super();

        if (1 > this.blumsz)
            throw new IllegalArgumentException("Require positive number of bits for integer.");
        else
            this.blumsz = blumsz;

        _init_gen(rng);
    }

    /**
     * Construct a new BBS, using an existing public key in the form
     * of a Blum integer `N'.
     * 
     * @param N Public key is a Blum Integer.
     *
     * @param rng Random bit generator for creating large primes for seed.
     */
    public BBS( java.math.BigInteger N, java.util.Random rng){
        super();

        if (null == N)
            throw new IllegalArgumentException("Null public seed, 'N'.");
        else
            this.N = N;
	
        this.blumsz = N.bitLength();

        if (null == rng)
            throw new IllegalArgumentException("Null RNG.");
        else
            this._init_gen_seed(rng);
    }

    /**
     * Construct a new BBS, using an existing public key in the form
     * of a Blum integer `N', and a secret initial seed, `X'.
     * 
     * @param N Public key is a Blum Integer.
     * 
     * @param rng Random bit generator for creating large primes for seed.
     *
     * @param X Secret initial seed.
     */
    public BBS( java.math.BigInteger N, java.util.Random rng, java.math.BigInteger X){
        super();

        if (null == N)
            throw new IllegalArgumentException("Null public seed, 'N'.");
        else {
            this.N = N;

            this.blumsz = N.bitLength();
        }
        if (null == X)
            throw new IllegalArgumentException("Null secret initial seed, 'X'.");
        else
            this.X = X;
    }

    /**
     * Construct a new BBS, using an existing public key in the form
     * of the Blum integer factors, `P' and `Q'.
     * 
     * @param P Blum integer component.
     * 
     * @param Q Blum integer component.
     *
     * @param rng Random bit generator for creating large primes for seed.
     */
    public BBS( java.math.BigInteger P, java.math.BigInteger Q, java.util.Random rng){
        super();

        if (null == P)
            throw new IllegalArgumentException("Null Blum integer component,  'P'.");
        else
            this.P = P;
        if (null == Q)
            throw new IllegalArgumentException("Null Blum integer component,  'Q'.");
        else
            this.Q = Q;

        /*
         * Blum integer
         */
        this.N = P.multiply(Q);

        this.blumsz = this.N.bitLength();

        if (null == rng)
            throw new IllegalArgumentException("Null RNG.");
        else
            this._init_gen_seed(rng);
    }

    /**
     * Construct a new BBS, using existing keys in the form of the
     * Blum integer factors, `P' and `Q', and the secret (initial)
     * seed `X'.
     * 
     * @param P Blum integer component.
     * 
     * @param Q Blum integer component.
     *
     * @param X Secret initial seed.
     *
     * @param rng Random bit generator for creating large primes for seed.
     */
    public BBS( java.math.BigInteger P, java.math.BigInteger Q, java.math.BigInteger X){
        super();

        if (null == P)
            throw new IllegalArgumentException("Null public seed component, 'P'.");
        else
            this.P = P;

        if (null == Q)
            throw new IllegalArgumentException("Null public seed component, 'Q'.");
        else
            this.Q = Q;

        /*
         * Blum integer
         */
        this.N = P.multiply(Q);

        this.blumsz = this.N.bitLength();

        /*
         * Initial Seed
         */
        if (null == X)
            throw new IllegalArgumentException("Null private seed component, 'X'.");
        else
            this.X = X;
    }

    private final void _init_gen( java.util.Random rng){

        int sz2 = (this.blumsz>>1);

        java.math.BigInteger p, q;

        while (true){

            p = new java.math.BigInteger(sz2,PRIME_CERT_EXP,rng);

            if ( 3 == p.mod(Tools.FOUR).intValue())
                break;
        }
        this.P = p;

        while (true){

            q = new java.math.BigInteger(sz2,PRIME_CERT_EXP,rng);

            if ( 3 == q.mod(Tools.FOUR).intValue())
                break;
        }
        this.Q = q;

        /*
         * Blum integer
         */
        this.N = p.multiply(q);

        this.blumsz = this.N.bitLength();

        _init_gen_seed(rng);
    }

    private final void _init_gen_seed( java.util.Random rng){

        int xsz = this.blumsz-1; // highest power of two less than N

        while (true){

            this.X = new java.math.BigInteger(xsz,rng);

            if ( 1 == this.X.gcd(this.N).intValue())
                break;
        }
        this.X = this.X.modPow(Tools.TWO,this.N);
    }

    public int bitLength(){ return blumsz;}

    /**
     * Conventional BBS taking one bit per seed iteration, as opposed
     * to <i>log<sub>2</sub>X.bitLength</t> bits per iteration as is
     * possible [Schneier pg 418].
     */
    protected int next( int numbits){

        int acc = 0, bit;

        for ( int cc = 0; cc < numbits; cc++){

            if (this.X.testBit(0))

                acc |= (1<<cc);

            this.X = this.X.modPow( Tools.TWO, this.N);
        }
        return acc;
    }

    /**
     * Set the seed (private key) as a random number relatively prime
     * to `N' (public key).  This is useful for periodically changing
     * the seed (and this PRNG output) for a given `N'.
     * 
     * @param seed A random integer relatively prime to `N'.
     */
    public void setSeed( java.math.BigInteger seed){

        if (1 != seed.gcd(this.N).intValue())
            throw new IllegalArgumentException("Bad seed is not relatively prime to `N'.");
        else
            this.X = seed;
    }

    /**
     * <p> This method defined here does nothing, to prevent the
     * superclass from seeding from its constructor.</p>
     */
    public void setSeed(long seed){
    }

}
