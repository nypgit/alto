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

import alto.lang.Type;
import alto.sys.IO;
import alto.sys.Reference;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.Provider;
import java.security.UnrecoverableKeyException;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import java.util.Date;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

/**
 * 
 * @author jdp
 * @since 1.6
 */
public final class TrustStore
    extends KeyStore
{
    public final static Type CertAlias = Type.Tools.Of("cert-alias");
    public final static Type CertSubject = Type.Tools.Of("cert-subject");

    public final static TrustStore Instance = new TrustStore();


    public final static String HostName(IO.Context request){
        /////////////////////////////////////////////////////////
        throw new java.lang.UnsupportedOperationException("TODO");
        /////////////////////////////////////////////////////////
    }
    public final static Reference CertificateFor(IO.Context request, Principal principal){
        String host = HostName(request);
        if (null != host){
            String path = principal.getName();
            if (null != path)
                return new Reference(host,Type.Tools.Of("der"),path);
        }
        throw new alto.sys.Error.Argument();
    }
    public final static Reference CertificateFor(IO.Context request, String subject){
        String host = HostName(request);
        if (null != host){
            if (null != subject)
                return new Reference(host,Type.Tools.Of("der"),subject);
        }
        throw new alto.sys.Error.Argument();
    }
    public final static Reference AliasFor(IO.Context request, Principal principal){
        String host = HostName(request);
        if (null != host){
            String path = principal.getName();
            if (null != path)
                return new Reference(host,CertAlias,path);
        }
        throw new alto.sys.Error.Argument();
    }
    public final static Reference AliasFor(IO.Context request, String subject){
        String host = HostName(request);
        if (null != host){
            if (null != subject)
                return new Reference(host,CertAlias,subject);
        }
        throw new alto.sys.Error.Argument();
    }
    public final static Reference SubjectFor(IO.Context request, String alias){
        String host = HostName(request);
        if (null != host){
            if (null != alias)
                return new Reference(host,CertSubject,alias);
        }
        throw new alto.sys.Error.Argument();
    }

    /**
     * 
     */
    public final static class TrustStoreProvider
        extends Provider
    {
        public final static Provider Instance = new TrustStoreProvider();

        private TrustStoreProvider(){
            super("Syntelos",1.0,"Syntelos Store");
        }
    }


    TrustStore(){
        super(TrustStoreSpi.Instance,TrustStoreProvider.Instance,"Syntelos Store");
        try {
            /*
             * initialize
             */
            this.load(null);
        }
        catch (Exception notreached){
        }
    }


    public final Certificate getCertificateForSubject(X500Principal principal)
        throws KeyStoreException
    {
        return this.getCertificateForSubject(principal.getName());
    }
    public final Certificate getCertificateForSubject(String subject_dn)
        throws KeyStoreException
    {
        return TrustStoreSpi.Instance.engineGetCertificateForSubject(subject_dn);
    }

}
