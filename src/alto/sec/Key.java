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
package alto.sec;

import alto.hash.FOAM;
import alto.hash.RSA;
import alto.hash.RSAO;
import alto.hash.SHA1;
import alto.hash.Tools;
import alto.io.Code;
import alto.io.Check;
import alto.io.Input;
import alto.io.Output;
import alto.io.u.B64;
import alto.lang.Address;
import alto.lang.Date;
import alto.lang.Message;
import alto.lang.Sio;
import alto.sec.pkcs.PKCS12;
import alto.sec.x509.AlgorithmId;
import alto.sec.x509.CertificateAlgorithmId;
import alto.sec.x509.CertificateExtensions;
import alto.sec.x509.CertificateIssuerName;
import alto.sec.x509.CertificateSerialNumber;
import alto.sec.x509.CertificateSubjectName;
import alto.sec.x509.CertificateValidity;
import alto.sec.x509.CertificateVersion;
import alto.sec.x509.CertificateX509Key;
import alto.sec.x509.KeyIdentifier;
import alto.sec.x509.SubjectKeyIdentifierExtension;
import alto.sec.x509.X500Name;
import alto.sec.x509.X500Signer;
import alto.sec.x509.X509Certificate;
import alto.sec.x509.X509CertInfo;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * A key group contains three fields: algorithm identifier string,
 * public key and private key.
 */
public final class Key
    extends Sio.Group
    implements alto.sys.Destroy
{
    public final static KeyFactory KeyFactoryRSA ;
    static {
        try {
            KeyFactoryRSA = KeyFactory.getInstance("RSA");
        }
        catch (java.security.NoSuchAlgorithmException exc){
            throw new alto.sys.Error.State("RSA",exc);
        }
    }
    /**
     * Parse binary
     */
    public final static RSAPublicKey KeyPublicRSA(byte[] key)
        throws java.security.spec.InvalidKeySpecException
    {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
        return (RSAPublicKey)KeyFactoryRSA.generatePublic(spec);
    }
    /**
     * Parse binary
     */
    public final static RSAPrivateKey KeyPrivateRSA(byte[] key)
        throws java.security.spec.InvalidKeySpecException
    {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(key);
            return (RSAPrivateKey)KeyFactoryRSA.generatePrivate(spec);
        }
        catch (java.security.spec.InvalidKeySpecException exc){

            X509EncodedKeySpec spec = new X509EncodedKeySpec(key);
            return (RSAPrivateKey)KeyFactoryRSA.generatePrivate(spec);
        }
    }
    /**
     * Decode from Base64
     */
    public final static RSAPublicKey KeyPublicRSADecode(String string)
        throws java.security.spec.InvalidKeySpecException
    {
        return KeyPublicRSA(B64.decode(string));
    }
    /**
     * Decode from Base64
     */
    public final static RSAPrivateKey KeyPrivateRSADecode(String string)
        throws java.security.spec.InvalidKeySpecException
    {
        return KeyPrivateRSA(B64.decode(string));
    }
    /**
     * Encode to Base64
     */
    public final static byte[] KeyPublicRSAEncode(RSAPublicKey key)
        throws java.security.spec.InvalidKeySpecException
    {
        return B64.encode(key.getEncoded());
    }
    /**
     * Encode to Base64
     */
    public final static byte[] KeyPrivateRSAEncode(RSAPrivateKey key)
        throws java.security.spec.InvalidKeySpecException
    {
        return B64.encode(key.getEncoded());
    }

    public static class List {
        public final static Key[] Add(Key[] list, Key c){
            if (null == c)
                return list;
            else if (null == list)
                return new Key[]{c};
            else {
                int len = list.length;
                Key[] copier = new Key[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
    }


    private Keys keys;

    private String alg, sig;

    private PublicKey keyPublic;

    private PrivateKey keyPrivate;

    private X509Certificate certificate;

    private KeyFactory keyFactory;

    private KeyPair keyPair;

    private X500Signer signer;


    public Key(Keys keys, Input in)
        throws java.io.IOException
    {
        super();
        this.keys = keys;
        this.sioRead(in);
    }
    public Key(Keys keys){
        super();
        this.keys = keys;
    }


    public void destroy(){
        this.keys = null;
        this.alg = null;
        this.keyPublic = null;
        this.keyPrivate = null;
        this.keyFactory = null;
    }
    public boolean generate(String alg){
        if ("RSA".equalsIgnoreCase(alg))
            return this.generateRSA();
        else
            return false;
    }
    public boolean generateRSA(){
        try {
            RSA rsa = new RSA();
            this.keyPublic = rsa.getPublicKey();
            this.keyPrivate = rsa.getPrivateKey();
            this.alg = this.keyPublic.getAlgorithm();
            this.sig = "SHA1With"+this.alg;
            try {
                Key protector = this.keys.getProtectorRSA();
                if (null != protector)
                    this.generateCertificateRSA(protector);
                else
                    this.generateCertificateRSA(this);
            }
            catch (CertificateException exc){
                throw new alto.sys.Error.State(exc);
            }
            return true;
        }
        catch (java.security.NoSuchAlgorithmException unexpected){
            throw new alto.sys.Error.State(unexpected);
        }
    }
    private void generateCertificateRSA(Key issuer)
        throws CertificateException
    {
        try {
            java.util.Date begin = new java.util.Date();
            java.util.Date end = Date.Future(Date.Years);

            X509CertInfo info = new X509CertInfo();
            info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber((int)(begin.getTime()/1000L)));

            X500Signer signer = issuer.getSigner(); 
            AlgorithmId alg = signer.getAlgorithmId();
            info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(alg));
            info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(this.getName()));
            info.set(X509CertInfo.KEY, new CertificateX509Key(this.keyPublic));
            info.set(X509CertInfo.VALIDITY, new CertificateValidity(begin,end));
            info.set(X509CertInfo.ISSUER, new CertificateIssuerName(issuer.getName()));
            {
                //(skid)
                CertificateExtensions ext = new CertificateExtensions();
                ext.set(SubjectKeyIdentifierExtension.NAME, new SubjectKeyIdentifierExtension(KeyIdentifier.Encode(this.keyPublic)));
                info.set(X509CertInfo.EXTENSIONS, ext);
            }
            X509Certificate certificate = new X509Certificate(info);
            certificate.sign(this.keyPrivate, signer);

            this.certificate = certificate;
        }
        catch (java.io.IOException exc){
            throw new CertificateException(exc);
        }
        catch (NoSuchAlgorithmException exc){
            throw new CertificateException(exc);
        }
        catch (NoSuchProviderException exc){
            throw new CertificateException(exc);
        }
        catch (InvalidKeyException exc){
            throw new CertificateException(exc);
        }
        catch (SignatureException exc){
            throw new CertificateException(exc);
        }
    }
    public String getAlgorithm(){
        return this.alg;
    }
    public void setAlgorithm(String alg){
        if (null != alg){
            this.alg = alg;
            this.keyFactory = null;
        }
        else
            throw new IllegalArgumentException();
    }
    public String getSignatureAlgorithm(){
        return this.sig;
    }
    public String getUID()
        throws java.io.IOException
    {
        return this.getName().getUID();
    }
    public X500Name getName()
        throws java.io.IOException
    {
        return this.keys.getX500Name();
    }
    public PublicKey getKeyPublic(){
        return this.keyPublic;
    }
    private PrivateKey getKeyPrivate(){
        return this.keyPrivate;
    }
    public KeyFactory getKeyFactory()
        throws NoSuchAlgorithmException
    {
        KeyFactory keyfactory = this.keyFactory;
        if (null == keyFactory){
            String alg = this.alg;
            if (null != alg){
                if ("RSA".equals(alg)){
                    keyFactory = KeyFactoryRSA;
                    this.keyFactory = keyFactory;
                }
                else {
                    keyFactory = KeyFactory.getInstance(alg);
                    this.keyFactory = keyFactory;
                }
            }
        }
        return keyFactory;
    }
    public KeyPair getKeyPair(){
        KeyPair keyPair = this.keyPair;
        if (null == keyPair){
            PublicKey pub = this.getKeyPublic();
            PrivateKey pri = this.getKeyPrivate();
            keyPair = new KeyPair(pub,pri);
            this.keyPair = keyPair;
        }
        return keyPair;
    }
    public X509Certificate getCertificate(){
        return this.certificate;
    }
    public X500Signer getSigner()
        throws java.io.IOException, InvalidKeyException, NoSuchAlgorithmException
    {
        X500Signer signer = this.signer;
        if (null == signer){
            Signature signature = Signature.getInstance(this.sig);
            signature.initSign(this.keyPrivate);
            X500Name name = this.getName();
            signer = new X500Signer(signature, name);
            this.signer = signer;
        }
        return signer;
    }
    public boolean canCreateKeyRSA(){
        return (this.keyPrivate instanceof RSAPrivateKey && this.keyPublic instanceof RSAPublicKey);
    }
    /**
     * Use "SHA1 with RSAO" to get the symmetric key for the input
     * address (version independent) when this is an RSA key.  The key
     * returned is always the same for this key and the input address,
     * and in this sense is "protected" by this key.
     * 
     * @param address Protected resource location
     * 
     * @return Private symmetric key for address protected by this
     * key.  
     */
    public byte[] createKeyRSA(Address address){
        if (null != address){
            PrivateKey keyPrivate = this.keyPrivate;
            PublicKey keyPublic = this.keyPublic;
            if (keyPrivate instanceof RSAPrivateKey && keyPublic instanceof RSAPublicKey){

                RSAPrivateKey R = (RSAPrivateKey)keyPrivate;
                RSAPublicKey U = (RSAPublicKey)keyPublic;

                SHA1 sha = new SHA1();

                sha.update(U.getModulus());

                sha.update(U.getPublicExponent());

                sha.update(R.getPrivateExponent());

                sha.update(address.getAddressPathBytes());

                byte[] hash = sha.hash();
                try {
                    RSAO rsao = new RSAO(U,R);

                    byte[] sig = new byte[rsao.decipherOutputLength(hash.length)];

                    int r = rsao.decipher(hash,0,hash.length,sig,0);

                    if (r < sig.length)
                        return Tools.Trim(sig,0,r);
                    else
                        return sig;
                }
                catch (java.security.NoSuchAlgorithmException unexpected){
                    throw new alto.sys.Error.State(unexpected);
                }
            }
            else
                throw new alto.sys.Error.State(this.alg);
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public byte[] signSHA1WithRSAO(byte[] msg){
        if (null != msg){
            PrivateKey keyPrivate = this.keyPrivate;
            PublicKey keyPublic = this.keyPublic;
            if (keyPrivate instanceof RSAPrivateKey && keyPublic instanceof RSAPublicKey){

                RSAPrivateKey R = (RSAPrivateKey)keyPrivate;
                RSAPublicKey U = (RSAPublicKey)keyPublic;

                SHA1 sha = new SHA1();

                sha.update(msg);

                byte[] hash = sha.hash();
                try {
                    RSAO rsao = new RSAO(U,R);

                    byte[] sig = new byte[rsao.decipherOutputLength(hash.length)];

                    rsao.decipher(hash,0,hash.length,sig,0);

                    int r = rsao.decipher(hash,0,hash.length,sig,0);

                    if (r < sig.length)
                        return Tools.Trim(sig,0,r);
                    else
                        return sig;
                }
                catch (java.security.NoSuchAlgorithmException unexpected){
                    throw new alto.sys.Error.State(unexpected);
                }
            }
            else
                throw new alto.sys.Error.State(this.alg);
        }
        else
            return null;
    }
    public byte[] signWithRSAO(SHA1 sha){
        if (null != sha){
            PrivateKey keyPrivate = this.keyPrivate;
            PublicKey keyPublic = this.keyPublic;
            if (keyPrivate instanceof RSAPrivateKey && keyPublic instanceof RSAPublicKey){
                RSAPrivateKey R = (RSAPrivateKey)keyPrivate;
                RSAPublicKey U = (RSAPublicKey)keyPublic;
                try {
                    RSAO rsao = new RSAO(U,R);
                    return rsao.sign(sha);
                }
                catch (java.security.NoSuchAlgorithmException unexpected){
                    throw new alto.sys.Error.State(unexpected);
                }
            }
            else
                throw new alto.sys.Error.State(this.alg);
        }
        else
            return null;
    }
    public byte[] signWithRSA(SHA1 sha){
        if (null != sha){
            PrivateKey keyPrivate = this.keyPrivate;
            PublicKey keyPublic = this.keyPublic;
            if (keyPrivate instanceof RSAPrivateKey && keyPublic instanceof RSAPublicKey){
                RSAPrivateKey R = (RSAPrivateKey)keyPrivate;
                RSAPublicKey U = (RSAPublicKey)keyPublic;
                RSA rsa = new RSA(U,R);
                return rsa.sign(sha);
            }
            else
                throw new alto.sys.Error.State(this.alg);
        }
        else
            return null;
    }
    public FOAM createFoamRSA(Address nonce){
        try {
            return new FOAM(this.createKeyRSA(nonce));
        }
        catch (java.security.NoSuchAlgorithmException unexpected){
            throw new alto.sys.Error.State(unexpected);
        }
    }
    public byte[] enfoamRSA(Address nonce, byte[] msg){
        if (null == msg)
            return null;
        else {
            FOAM foam = this.createFoamRSA(nonce);
            int ilen = msg.length;
            int olen = foam.encipherOutputLength(ilen);
            byte[] out = new byte[olen];
            foam.encipher(msg,0,ilen,out,0);
            return out;
        }
    }
    public byte[] defoamRSA(Address nonce, byte[] msg){
        if (null == msg)
            return null;
        else {
            FOAM foam = this.createFoamRSA(nonce);
            int ilen = msg.length;
            int olen = foam.decipherOutputLength(ilen);
            byte[] out = new byte[olen];
            foam.decipher(msg,0,ilen,out,0);
            return out;
        }
    }
    public byte[] protectRSA(byte[] msg){
        return this.keys.protectRSA(msg);
    }
    public byte[] unprotectRSA( byte[] msg){
        return this.keys.unprotectRSA(msg);
    }
    public PKCS12 createPfx(String passwd)
        throws java.io.IOException, 
               CertificateException
    {
        String alias = this.getUID();
        if (null != alias){
            PrivateKey pkcs8 = this.getKeyPrivate();
            if (null != pkcs8){
                X509Certificate cert = this.getCertificate();
                if (null != cert){
                    return new PKCS12(alias,pkcs8,passwd,cert);
                }
                else
                    throw new CertificateException("Missing certificate");
            }
            else
                throw new CertificateException("Missing private key");
        }
        else
            throw new CertificateException("Missing UID");
    }
    public void sioRead(Input in) 
        throws java.io.IOException
    {
        /*
         * read container
         */
        super.sioRead(in);
        /*
         * read contents
         */
        in = super.openInput();
        try {
            this.setAlgorithm(Sio.Field.ReadUtf8(in));
            try {
                KeyFactory keyFactory = this.getKeyFactory();

                byte[] keyPublic = Sio.Field.Read(in);
                try {
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyPublic);
                    this.keyPublic = this.keyFactory.generatePublic(spec);
                }
                catch (java.security.spec.InvalidKeySpecException exc){
                    try {
                        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyPublic);
                        this.keyPublic = this.keyFactory.generatePublic(spec);
                    }
                    catch (java.security.spec.InvalidKeySpecException exc2){
                        throw new alto.sys.Error.State(this.toString(),exc2);
                    }
                }

                byte[] keyPrivate = this.unprotectRSA(Sio.Field.Read(in));
                try {
                    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyPrivate);
                    this.keyPrivate = this.keyFactory.generatePrivate(spec);
                }
                catch (java.security.spec.InvalidKeySpecException exc){
                    try {
                        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyPrivate);
                        this.keyPrivate = this.keyFactory.generatePrivate(spec);
                    }
                    catch (java.security.spec.InvalidKeySpecException exc2){
                        throw new alto.sys.Error.State(this.toString(),exc2);
                    }
                }

                this.sig = Sio.Field.ReadUtf8(in);

                byte[] certBits = Sio.Field.Read(in);
                try {
                    X509Certificate certificate = new X509Certificate(certBits);
                    this.certificate = certificate;
                }
                catch (CertificateException exc){
                    throw new alto.sys.Error.State(this.toString(),exc);
                }
            }
            catch (java.security.NoSuchAlgorithmException exc){
                throw new alto.sys.Error.State(this.toString(),exc);
            }
        }
        finally {
            in.close();
        }
    }
    public void sioWrite(Output out) 
        throws java.io.IOException
    {
        /*
         * write contents
         */
        Output buf = super.openOutput();
        try {
            Sio.Field.WriteUtf8(this.alg,buf);

            java.security.Key key;

            key = this.keyPublic;
            if (null != key)
                Sio.Field.Write(key.getEncoded(),buf);
            else
                Sio.Field.Write(null,buf);

            key = this.keyPrivate;
            if (null != key)
                Sio.Field.Write(this.protectRSA(key.getEncoded()),buf);
            else
                Sio.Field.Write(null,buf);

            Sio.Field.WriteUtf8(this.sig,buf);

            X509Certificate certificate = this.certificate;
            if (null != key)
                try {
                    Sio.Field.Write(certificate.getEncodedInternal(),buf);
                }
                catch (CertificateEncodingException exc){
                    throw new alto.sys.Error.State(this.toString(),exc);
                }
            else
                Sio.Field.Write(null,buf);

            buf.flush();
        }
        finally {
            buf.close();
        }
        /*
         * write container
         */
        super.sioWrite(out);
    }
}
