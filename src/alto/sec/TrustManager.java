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

import alto.sys.FileManager;

import java.util.Enumeration;

import java.security.KeyStore;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

/**
 * 
 * @author jdp
 * @since 1.6
 */
public final class TrustManager
    extends java.lang.Object 
    implements X509TrustManager
{
    public final static TrustManager Instance = new TrustManager();

    public final static javax.net.ssl.X509TrustManager[] Array = new javax.net.ssl.X509TrustManager[]{Instance};

    
    public final static java.security.KeyStore Create(String type, java.io.File file, String pass)
        throws java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException
    {
        java.security.KeyStore keyStore = java.security.KeyStore.getInstance(type);
        try {
            java.io.InputStream in = new java.io.FileInputStream(file);
            try {
                if (null != pass)
                    keyStore.load(in,pass.toCharArray());
                else
                    keyStore.load(in,null);

                return keyStore;
            }
            finally {
                in.close();
            }
        }
        catch (java.io.FileNotFoundException exc){

            if (null != pass)
                keyStore.load(null,pass.toCharArray());
            else
                keyStore.load(null,null);

            return keyStore;
        }
    }
    public final static void Write(java.security.KeyStore store, java.io.File file, java.lang.String pass)
        throws java.io.IOException
    {
        java.io.OutputStream out = new java.io.FileOutputStream(file);
        try {
            if (null != pass)
                store.store(out,pass.toCharArray());
            else
                store.store(out,null);
            return;
        }
        catch (java.security.KeyStoreException exc){
            throw new alto.sys.Error(exc);
        }
        catch (java.security.NoSuchAlgorithmException exc){
            throw new alto.sys.Error(exc);
        }
        catch (java.security.cert.CertificateException exc){
            throw new alto.sys.Error(exc);
        }
        finally {
            out.close();
        }
    }


    private final java.io.File system_file, server_file;

    private KeyStore system;

    private KeyStore server;

    private X509Certificate[] accepted;


    private TrustManager(){
        super();
        this.system_file = new java.io.File( java.lang.System.getProperty("java.home") + "/lib/security/cacerts");
        this.server_file = new java.io.File( FileManager.Instance().getPartition().getFile(), "server.jks");
    }


    public synchronized TrustManager reinit(String server_storepass, String server_keypass)
        throws java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException
    {

        this.system = Create("JKS",this.system_file,null);
        this.server = Create("JKS",this.server_file,server_storepass);

        int system_size = this.system.size();
        int server_size = this.server.size();
        int count = system_size + server_size;
        X509Certificate[] accepted = new X509Certificate[count];
        int idx = 0;
        Enumeration<String> aliases;
        aliases = this.system.aliases();
        while (aliases.hasMoreElements()){
            String alias = aliases.nextElement();
            X509Certificate cert = (X509Certificate)this.system.getCertificate(alias);
            accepted[idx++] = cert;
        }
        aliases = this.server.aliases();
        while (aliases.hasMoreElements()){
            String alias = aliases.nextElement();
            X509Certificate cert = (X509Certificate)this.system.getCertificate(alias);
            accepted[idx++] = cert;
        }
        this.accepted = accepted;

        KeyManager.Instance.reinit(this,server_storepass,server_keypass);

        return this;
    }

    public KeyStore getServer(){
        return this.server;
    }
    public KeyStore getSystem(){
        return this.system;
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
        if (null != chain && 0 < chain.length){
            X509Certificate client = chain[0];
            if (null != client){
                X500Principal principal = client.getSubjectX500Principal();
                if (null != principal){
                    try {
                        X509Certificate known = (X509Certificate)TrustStore.Instance.getCertificateForSubject(principal);
                        if (null == known)
                            throw new CertificateException("Unknown "+principal.getName());

                        else if (client.equals(known))
                            /*
                             * @see java.security.cert.Certficate#equals(java.lang.Object)
                             */
                            return;
                        else
                            throw new CertificateException();
                    }
                    catch (java.security.KeyStoreException exc){
                        throw new CertificateException(exc);
                    }
                }
                else
                    throw new CertificateException();
            }
            else
                throw new CertificateException();
        }
        else
            throw new CertificateException();
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException
    {
        throw new CertificateException("Unimplemented");//TODO
    }

    public X509Certificate[] getAcceptedIssuers(){
        return this.accepted;
    }

    private final static boolean Equal(byte[] a, byte[] b){
        if (null == a)
            return (null == b);
        else if (null == b)
            return false;
        else if (a.length == b.length){
            for (int cc = 0, count = a.length; cc < count; cc++){
                if (a[cc] != b[cc])
                    return false;
            }
            return true;
        }
        else
            return false;
    }
}
