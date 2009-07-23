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
 * <p> FOAM is {@link OTP} with {@link OAEP}.  </p>
 *
 * <p><b>Application Notes</b>
 * 
 * <ol>
 *
 * <li> Never use a key for more than one plaintext. </li>
 * 
 * <li> The OAEP Hash and Generator functions must be unrelated to the
 * Key stream generator. </li>
 * 
 * <li> For a general (private) communication stream, the FOAM key
 * would be itself generated from yet another secure PRNG, seeded from
 * the hash of a shared secret.  In this way, two parties communicate
 * over a network link after first possessing or establishing both a
 * shared secret, and the usage of FOAM in terms of the algorithms of
 * its requisite functions. </li>
 * 
 * </ol> </p>
 *
 * 
 * @author John Pritchard
 * @since 1.2
 */
public final class FOAM 
    extends OTP
    implements Cipher 
{

    protected final OAEP pad;


    public FOAM (byte[] key)
        throws java.security.NoSuchAlgorithmException
    {
        this("SHA1PRNG", key);
    }
    public FOAM (String KN, byte[] key)
        throws java.security.NoSuchAlgorithmException
    {
        this(KN,java.security.MessageDigest.getInstance("SHA1"), key, new OAEP());
    }
    /**
     * @param KN Secure Random Name
     *
     * @param KH Key hash function for seeding the the key stream
     * generator.
     *
     * @param key Shared secret for initializing the key stream
     * generator -- must be the same on both ends of the
     * communications link.
     * 
     * @param pad Initialized AONT
     */
    public FOAM( String KN, java.security.MessageDigest KH, byte[] key, OAEP pad)
        throws java.security.NoSuchAlgorithmException
    {
        super(KN,KH,key);
        this.pad = pad;
    }


    public final int encipherOutputLength( int blk){

        return this.pad.encipherOutputLength(blk);
    }

    public final int decipherOutputLength( int blk){

        return this.pad.decipherOutputLength(blk);
    }

    public int encipher ( byte[] ptxt, int inofs, int inlen, byte[] output, int outofs){

        int padlen = this.pad.encipherOutputLength(inlen);

        byte[] pad = new byte[padlen];

        padlen = this.pad.encipher(ptxt,inofs,inlen,pad,0);

        padlen = super.encipher(pad,0,padlen,output,outofs);

        return padlen;
    }
    public int decipher ( byte[] ctext, int inofs, int inlen, byte[] output, int outofs){

        int padlen = super.decipherOutputLength(inlen);

        byte[] pad = new byte[padlen];

        padlen = super.decipher(ctext,inofs,inlen,pad,0);

        padlen = this.pad.decipher(pad,0,padlen,output,outofs);

        return padlen;
    }



    private final static byte[] main_test_key = {
        'h','e','l','l','o'
    };
    public static void main(String[] argv){
        FOAM foam = null;
        try {
            String msg = argv[0];

            byte[] ptext = alto.io.u.Utf8.encode(msg);

            foam = new FOAM(main_test_key);

            int ctextLen = foam.encipherOutputLength(ptext.length);
            if (0 < ctextLen){
                byte[] ctext = new byte[ctextLen];

                int olen = foam.encipher(ptext,0,ptext.length,ctext,0);

                foam.reset(main_test_key);

                int ool = foam.decipherOutputLength(olen);
                if (0 < ool){
                    byte[] ttext = new byte[ool];

                    olen = foam.decipher(ctext,0,olen,ttext,0);

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
                    throw new IllegalStateException("foam.decipherOutputLength("+olen+") = "+ool);
            }
            else
                throw new IllegalStateException("foam.encipherOutputLength("+ptext.length+") = "+ctextLen);
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
