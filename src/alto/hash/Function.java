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
 * Fast hash functions for addressing and indexing.
 *
 * 
 * @author jdp
 * @since 1.1
 */
public interface Function {
    /**
     * 
     */
    public interface Association {
        public boolean hasHashFunction();
        public Function getHashFunction();
    }


    /**
     * Registry of named hash functions defines "djb" (or "djb-32"),
     * "pal" (or "pal-32"), "xor" (or "xor-32"), "sha1" (or "sha1-32")
     * or "md5" (or "md5-32") by default.
     * 
     * <h3>Content Addressing</h3>
     * 
     * When the class {@link alto.lang.http.Type} is first
     * accessed, it loads the mimetypes properties for the host.  It
     * employs this registry to resolve the mapping from mime type
     * string to hash function via a hash function identifier as
     * described immediately above.
     * 
     * @author jdp
     * @see alto.lang.http.Type
     */
    public final static class Registry 
        extends java.lang.Object
    {
        private final static alto.io.u.Objmap Map = new alto.io.u.Objmap();
        /**
         * Static initializer for defining more named hash functions
         * for use by {@link alto.lang.http.Type}
         */
        public final static void SInit(String name, Function function){
            if (null != name && null != function)
                Map.put(name,function);
            else
                throw new java.lang.IllegalArgumentException();
        }
        /**
         * Fast read only lookup 
         * @param name A registered hash function identifer, for
         * example one of "djb" (or "djb-32"), "pal" (or "pal-32"),
         * "xor" (or "xor-32"), "sha1" (or "sha1-32") or "md5" (or
         * "md5-32").
         * @return Null for not found
         */
        public final static Function Lookup(String name){
            return (Function)Map.get(name);
        }
        static {
            SInit(Function.Xor.Name,Function.Xor.Instance);
            SInit(Function.Xor.Short.Name,Function.Xor.Short.Instance);
            SInit(Function.Djb.Name,Function.Djb.Instance);
            SInit(Function.Djb.Short.Name,Function.Djb.Short.Instance);
            SInit(Function.Pal.Name,Function.Pal.Instance);
            SInit(Function.Pal.Short.Name,Function.Pal.Short.Instance);
            SInit(Function.Md5.Name,Function.Md5.Instance);
            SInit(Function.Md5.Short.Name,Function.Md5.Short.Instance);
            SInit(Function.Sha1.Name,Function.Sha1.Instance);
            SInit(Function.Sha1.Short.Name,Function.Sha1.Short.Instance);
        }
    }

    public abstract static class Abstract
        extends alto.io.u.Bits
        implements Function
    {

        public int hashCode(){
            return this.getHashName().hashCode();
        }
        public String toString(){
            return this.getHashName();
        }
        public boolean equals(java.lang.Object ano){
            if (this == ano)
                return true;
            else if (null == ano)
                return false;
            else
                return this.toString().equals(ano.toString());
        }
    }

    /**
     * <code>"djb"</code>
     * 
     * <p> Daniel Bernstein's later function.
     *
     * <pre> hash = (hash * 33 ^ c) </pre>
     * </p>
     * 
     * <p> Sixty four bit default case. </p>
     * 
     * @see http://www.cse.yorku.ca/~oz/hash.html
     * @author jdp
     */
    public static class Djb
        extends Abstract
        implements Function
    {
        public final static String Name = "djb";
        public final static Djb Instance = new Djb();

        public final static long Hash64 ( byte[] b){
            if ( null == b)
                return 0L;
            else {
                long hash = 5381;
                int c;
                for (int cc = 0, count = b.length; cc < count; cc++){
                    c = b[cc] & 0xff;
                    hash = ((hash << 5) + hash) ^ c;
                }
                return hash;
            }
        }
        public final static int Hash32 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                int hash = 5381;
                int c;
                for (int cc = 0, count = b.length; cc < count; cc++){
                    c = b[cc] & 0xff;
                    hash = ((hash << 5) + hash) ^ c;
                }
                return hash;
            }
        }
        /**
         * Thirty two bit default case
         */
        public static class Short
            extends Djb
        {
            public final static String Name = "djb-32";
            public final static Short Instance = new Short();

            public Short(){
                super();
            }

            public String getHashName(){
                return Name;
            }
            public byte[] hash(byte[] bits){
                return Integer(Hash32(bits));
            }
        }

        public Djb(){
            super();
        }

        public String getHashName(){
            return Name;
        }
        public final long hash64 ( byte[] b){
            return Hash64(b);
        }
        public final int hash32 ( byte[] b){
            return Hash32(b);
        }
        public byte[] hash(byte[] bits){
            return Long(Hash64(bits));
        }
    }

    /**
     * <code>"pal"</code>
     * 
     * <p> Per-Aake Larson's "Dynamic Hashing Algorithms", BIT 18
     * (1978).  Used in Ndbm, Sdbm, Berkeley DB. 
     * 
     * <pre> hash = (hash * 65599 + c) </pre>
     * </p>
     * 
     * @see http://www.cse.yorku.ca/~oz/hash.html
     * @author jdp
     */
    public static class Pal
        extends Abstract
        implements Function
    {
        public final static String Name = "pal";
        public final static Pal Instance = new Pal();

        public final static long Hash64 ( byte[] b){
            if ( null == b)
                return 0L;
            else {
                long hash = 0;
                int c;
                for (int cc = 0, count = b.length; cc < count; cc++){
                    c = b[cc] & 0xff;
                    hash = c + (hash << 6) + (hash << 16) - hash;
                }
                return hash;
            }
        }
        public final static int Hash32 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                int hash = 0;
                int c;
                for (int cc = 0, count = b.length; cc < count; cc++){
                    c = b[cc] & 0xff;
                    hash = c + (hash << 6) + (hash << 16) - hash;
                }
                return hash;
            }
        }

        /**
         * Thirty two bit default case
         */
        public static class Short
            extends Pal
        {
            public final static String Name = "pal-32";
            public final static Short Instance = new Short();

            public Short(){
                super();
            }

            public String getHashName(){
                return Name;
            }
            public byte[] hash(byte[] bits){
                return Integer(Hash32(bits));
            }
        }


        public Pal(){
            super();
        }

        public String getHashName(){
            return Name;
        }
        public long hash64 ( byte[] b){
            return Hash64(b);
        }
        public int hash32 ( byte[] b){
            return Hash32(b);
        }
        public byte[] hash ( byte[] b){
            return Long(Hash64(b));
        }
    }

    /**
     * <code>"xor"</code>
     * 
     * <p> XOR folder combines a rotating barrel or shift register
     * from the input value set into an output accumulator with a XOR
     * operation.  </p>
     * 
     * @author jdp
     */
    public static class Xor
        extends Abstract
        implements Function
    {
        public final static String Name = "xor";
        public final static Xor Instance = new Xor();

        public final static long Hash64 ( byte[] b){
            if ( null == b)
                return 0L;
            else {
                long accum = 0, tmp;
                long ch;
                int shift ;
                for ( int c = 0, uc = b.length- 1, uz = b.length; c < uz; c++, uc--){
                    ch = b[c] & 0xff;
                    shift = ((uc % 8) * 8);
                    tmp = (ch << shift);
                    accum ^= tmp;
                }
                return accum;
            }
        }
        public final static int Hash32 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                int accum = 0, tmp;
                int ch;
                int shift ;
                for ( int c = 0, uc = b.length- 1, uz = b.length; c < uz; c++, uc--){
                    ch = b[c] & 0xff;
                    shift = ((uc % 4) * 8);
                    tmp = (ch << shift);
                    accum ^= tmp;
                }
                return accum;
            }
        }
        public final static int Hash8 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                int accum = 0;

                for ( int c = 0, uz = b.length; c < uz; c++){

                    accum ^= (b[c] & 0xff);
                }
                return accum;
            }
        }
        public final static int Hash8 ( int b){
            int ch;
            int accum = 0;

            for ( int c = 0; c < 4; c++){

                ch = ((b >>> (c * 8)) & 0xff);

                accum ^= (ch);
            }
            return accum;
        }

        /**
         * Thirty two bit default case
         */
        public static class Short
            extends Xor
        {
            public final static String Name = "xor-32";
            public final static Short Instance = new Short();

            public Short(){
                super();
            }

            public String getHashName(){
                return Name;
            }
            public byte[] hash(byte[] bits){
                return Integer(Hash32(bits));
            }
        }

        public Xor(){
            super();
        }

        public String getHashName(){
            return Name;
        }
        public long hash64 ( byte[] b){
            return Hash64(b);
        }
        public int hash32 ( byte[] b){
            return Hash32(b);
        }
        public byte[] hash ( byte[] b){
            return Long(Hash64(b));
        }
    }

    /**
     * <code>"md5"</code>
     * 
     * Uses the MD5 hash function to produce a result that is XOR
     * folded into 64 or 32 bits.
     * 
     * 
     * @see MD5
     * @author jdp
     */
    public static class Md5
        extends Abstract
        implements Function
    {
        public final static String Name = "md5";
        public final static Md5 Instance = new Md5();

        public final static long Hash64 ( byte[] b){
            if ( null == b)
                return 0L;
            else {
                MD5 md5 = new MD5();
                md5.update(b);
                byte[] result = md5.hash();
                return Function.Xor.Hash64(result);
            }
        }
        public final static int Hash32 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                MD5 md5 = new MD5();
                md5.update(b);
                byte[] result = md5.hash();
                return Function.Xor.Hash32(result);
            }
        }

        /**
         * Thirty two bit default case
         */
        public static class Short
            extends Md5
        {
            public final static String Name = "md5-32";
            public final static Short Instance = new Short();

            public Short(){
                super();
            }

            public String getHashName(){
                return Name;
            }
            public byte[] hash(byte[] bits){
                return Integer(Hash32(bits));
            }
        }

        public Md5(){
            super();
        }

        public String getHashName(){
            return Name;
        }
        public long hash64 ( byte[] b){
            return Hash64(b);
        }
        public int hash32 ( byte[] b){
            return Hash32(b);
        }
        /**
         * @return Normal MD5 hash (not folded)
         */
        public byte[] hash(byte[] b){
            if ( null == b)
                return null;
            else {
                MD5 md5 = new MD5();
                md5.update(b);
                return md5.hash();
            }
        }
    }

    /**
     * <code>"sha1"</code>
     * 
     * Uses the SHA1 hash function to produce a result that is XOR
     * folded into 64 or 32 bits.
     * 
     * @see SHA1
     * @author jdp
     */
    public static class Sha1
        extends Abstract
        implements Function
    {
        public final static String Name = "sha1";
        public final static Sha1 Instance = new Sha1();

        public final static long Hash64 ( byte[] b){
            if ( null == b)
                return 0L;
            else {
                SHA1 sha1 = new SHA1();
                sha1.update(b);
                byte[] result = sha1.hash();
                return Function.Xor.Hash64(result);
            }
        }
        public final static int Hash32 ( byte[] b){
            if ( null == b) 
                return 0;
            else {
                SHA1 sha1 = new SHA1();
                sha1.update(b);
                byte[] result = sha1.hash();
                return Function.Xor.Hash32(result);
            }
        }

        /**
         * Thirty two bit default case
         */
        public static class Short
            extends Sha1
        {
            public final static String Name = "sha1-32";
            public final static Short Instance = new Short();

            public Short(){
                super();
            }

            public String getHashName(){
                return Name;
            }
            public byte[] hash(byte[] bits){
                return Integer(Hash32(bits));
            }
        }

        public Sha1(){
            super();
        }

        public String getHashName(){
            return Name;
        }
        public long hash64 ( byte[] b){
            return Hash64(b);
        }
        public int hash32 ( byte[] b){
            return Hash32(b);
        }
        /**
         * @return Normal SHA1 hash (not folded)
         */
        public byte[] hash(byte[] b){
            if ( null == b) 
                return null;
            else {
                SHA1 sha1 = new SHA1();
                sha1.update(b);
                return sha1.hash();
            }
        }
    }

    /**
     * @return Hash function identifier, eg, "xor", "djb", "pal",
     * "md5" or "sha1".
     */
    public String getHashName();

    public int hash32(byte[] bits);

    public long hash64(byte[] bits);

    /**
     * @return Default bit length for the subclass
     */
    public byte[] hash(byte[] bits);
}
