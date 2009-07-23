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

import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

/**
 * {@link RSA} with {@link OAEP} padding.
 * 
 * @author jdp
 */
public class RSAO
    extends RSA
{

    private OAEP pad;
    private int rlen;
    private int hlen;
    private int zlen;


    public RSAO( java.math.BigInteger n, java.math.BigInteger e, java.math.BigInteger d, OAEP A){
        super(n,e,d);
        if (null != A){
            this.pad = A;

            this.rlen = super.blockLengthPlain();
            this.hlen = this.pad.hLength();
            this.zlen = (rlen - hlen);
        }
        else
            throw new IllegalArgumentException();
    }
    public RSAO( byte[] n, byte[] e, byte[] d, OAEP A){
        super(n,e,d);
        if (null != A){
            this.pad = A;

            this.rlen = super.blockLengthPlain();
            this.hlen = this.pad.hLength();
            this.zlen = (rlen - hlen);
        }
        else
            throw new IllegalArgumentException();
    }
    public RSAO( java.math.BigInteger n, java.math.BigInteger e, java.math.BigInteger d)
        throws java.security.NoSuchAlgorithmException
    {
        this(n,e,d, new OAEP());
    }
    public RSAO( byte[] n, byte[] e, byte[] d)
        throws java.security.NoSuchAlgorithmException
    {
        this(n,e,d, new OAEP());
    }
    public RSAO()
        throws java.security.NoSuchAlgorithmException
    {
        super();
        this.pad = new OAEP();
        this.rlen = super.blockLengthPlain();
        this.hlen = this.pad.hLength();
        this.zlen = (rlen - hlen);
    }
    public RSAO(RSAPublicKey U, RSAPrivateKey R)
        throws java.security.NoSuchAlgorithmException
    {
        this(U,R,new OAEP());
    }
    public RSAO(RSAPublicKey U, RSAPrivateKey R, OAEP A)
        throws java.security.NoSuchAlgorithmException
    {
        super(U,R);
        if (null != A){
            this.pad = A;

            this.rlen = super.blockLengthPlain();
            this.hlen = this.pad.hLength();
            this.zlen = (rlen - hlen);
        }
        else
            throw new IllegalArgumentException();
    }
    public RSAO( java.security.KeyPair pair)
        throws java.security.NoSuchAlgorithmException
    {
        this( ((RSAPublicKey)pair.getPublic()), ((RSAPrivateKey)pair.getPrivate()));
    }
    public RSAO( alto.io.Keys keys)
        throws java.security.NoSuchAlgorithmException
    {
        this(keys.getKeyPair("RSA"));
    }


    /**
     * @return Negative one for oversized block
     */
    public int encipherOutputLength( int blk){
        if (this.zlen < blk)
            return -1;
        else
            return super.encipherOutputLength(blk);
    }

    public int encipher ( byte[] ptxt, int inofs, int inlen, byte[] output, int outofs){
        if (this.zlen < inlen)
            throw new IllegalArgumentException("Input length '"+inlen+"' greater than max msg length '"+this.zlen+"'.");
        else {

            int padlen = this.pad.encipherOutputLength(inlen);

            byte[] pad = new byte[padlen];

            padlen = this.pad.encipher(ptxt,inofs,inlen,pad,0);

            padlen = super.encipher(pad,0,padlen,output,outofs);

            return padlen;
        }
    }
    public int decipher ( byte[] ctext, int inofs, int inlen, byte[] output, int outofs){

        int padlen = super.decipherOutputLength(inlen);

        byte[] pad = new byte[padlen];

        padlen = super.decipher(ctext,inofs,inlen,pad,0);

        padlen = this.pad.decipher(pad,0,padlen,output,outofs);

        return padlen;
    }

    public static void main(String[] argv){
        RSAO rsao = null;
        try {
            String msg = argv[0];

            byte[] ptext = alto.io.u.Utf8.encode(msg);

            rsao = new RSAO();

            int ctextLen = rsao.encipherOutputLength(ptext.length);
            if (0 < ctextLen){
                byte[] ctext = new byte[ctextLen];

                int olen = rsao.encipher(ptext,0,ptext.length,ctext,0);

                int ool = rsao.decipherOutputLength(olen);
                if (0 < ool){
                    byte[] ttext = new byte[ool];

                    olen = rsao.decipher(ctext,0,olen,ttext,0);

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
                    throw new IllegalStateException("rsao.decipherOutputLength("+olen+") = "+ool);
            }
            else
                throw new IllegalStateException("rsao.encipherOutputLength("+ptext.length+") = "+ctextLen);
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }
}
