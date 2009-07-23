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

import alto.sec.x509.X500Name;
import alto.sec.x509.X509Certificate;

import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;

import javax.net.ssl.HandshakeClientHello;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;

/**
 * 
 * @author jdp
 * @since 1.6
 */
public final class KeyManager
    extends javax.net.ssl.X509ExtendedKeyManager
    implements javax.net.ssl.X509ServerKeyManager
{
    public final static KeyManager Instance = new KeyManager();
    
    public final static javax.net.ssl.X509KeyManager[] Array = new javax.net.ssl.X509KeyManager[]{Instance};

    public final static String ServerName(HandshakeClientHello hello){
        if (hello instanceof HandshakeClientHello.ServerName)
            return ((HandshakeClientHello.ServerName)hello).getHelloServerName();
        else
            return null;
    }


    private KeyStore server;

    private KeyStore.PasswordProtection server_keypass;


    private KeyManager(){
        super();
    }


    public KeyManager reinit(TrustManager trustmanager, String server_storepass, String server_keypass)
        throws java.io.IOException, java.security.KeyStoreException, java.security.NoSuchAlgorithmException, java.security.cert.CertificateException
    {
        KeyStore server = trustmanager.getServer();
        if (null != server){
            this.server = server;
            if (null != server_keypass)
                this.server_keypass = new KeyStore.PasswordProtection(server_keypass.toCharArray());
            else
                this.server_keypass = new KeyStore.PasswordProtection(null);
            return this;
        }
        else
            throw new alto.sys.Error.Bug();
    }
    public String[] getClientAliases(String keyType, Principal[] issuers){
        return null;
    }
    public String chooseClientAlias(String[] keyType, Principal[] issuers, java.net.Socket tcp){
        return null;
    }
    public String[] getServerAliases(String keyType, Principal[] issuers){
        return null;
    }
    public String chooseServerAlias(String keyType, Principal[] issuers, java.net.Socket tcp){
        return null;
    }
    public String chooseServerAlias(String keyType, SSLSocket socket, HandshakeClientHello hello){

        String server_name = ServerName(hello);
        return server_name;
    }
    public String chooseServerAlias(String keyType, SSLEngine engine, HandshakeClientHello hello){
        return null;
    }
    public java.security.cert.X509Certificate[] getCertificateChain(String alias){
        throw new java.lang.UnsupportedOperationException("stub/to be rewritten");
    }
    public X509Certificate[] getCertificateChain2(String alias){
        KeyStore server = this.server;
        if (null != server)
            try {
                return (X509Certificate[])server.getCertificateChain(alias);
            }
            catch (java.security.KeyStoreException exc){
                throw new alto.sys.Error.State(exc);
            }
        else
            throw new alto.sys.Error.State("uninitialized");
    }
    public PrivateKey getPrivateKey(String alias){
        KeyStore server = this.server;
        if (null != server)
            try {
                return (PrivateKey)server.getKey(alias,this.server_keypass.getPassword());
            }
            catch (java.security.KeyStoreException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.security.NoSuchAlgorithmException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.security.UnrecoverableKeyException exc){
                throw new alto.sys.Error.State(exc);
            }
        else
            throw new alto.sys.Error.State("uninitialized");
    }
    /**
     * @return Alias for key entry for the client side of a connection.
     */
    public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine)
    {
        return null;
    }
    /**
     * @return Alias for key entry for the server side of a connection.
     */
    public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine)
    {
        return null;
    }

}
