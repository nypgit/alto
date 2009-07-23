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

import alto.lang.HttpRequest;
import alto.sec.x509.X500Name;
import alto.sec.x509.X509Certificate;
import alto.sys.IO;
import alto.sys.Reference;
import alto.sys.Thread;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.security.Key;
import java.security.KeyException;
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

import java.util.Date;
import java.util.Enumeration;

/**
 * Infinite store for certificate entries exclusively.
 * 
 * @author jdp
 * @since 1.6
 */
public final class TrustStoreSpi
    extends KeyStoreSpi
{

    public final static TrustStoreSpi Instance = new TrustStoreSpi();


    private TrustStoreSpi(){
        super();
    }


    public Certificate[] engineGetCertificateChain(java.lang.String alias){
        Certificate cert = this.engineGetCertificate(alias);
        if (null == cert)
            return null;
        else
            return new Certificate[]{cert};
    }
    public Certificate engineGetCertificate(java.lang.String alias){
//         IO.Context request = Thread.Context();
//         if (null != request){
//             Reference reference_subject = TrustStore.SubjectFor(request,alias);
//             String subject_dn = reference_subject.readString();
//             if (null != subject_dn){
//                 Reference reference_cert = TrustStore.CertificateFor(request,subject_dn);
//                 return reference_cert.readBary();
//             }
//             else
                return null;
//         }
//         else
//             throw new alto.sys.Error.Bug();
    }
    public Certificate engineGetCertificateForSubject(java.lang.String subject_dn){
//         IO.Context request = Thread.Context();
//         if (null != request){
//             Reference reference_cert = TrustStore.CertificateFor(request,subject_dn);
//             return reference_cert.getReadParsedX509Certificate();
//         }
//         else
            throw new alto.sys.Error.Bug();
    }
    public Date engineGetCreationDate(java.lang.String alias){
//         IO.Context request = Thread.Context();
//         if (null != request){
//             Reference reference_subject = TrustStore.SubjectFor(request,alias);
//             /*
//              * [TODO] Check DFS protocol
//              */
//             long last = reference_subject.getStorage().lastModified();
//             if (0L < last)
//                 return new Date(last);
//             else
                return null;
//         }
//         else
//             throw new alto.sys.Error.Bug();
    }
    public void engineSetCertificateEntry(java.lang.String alias,
                                          Certificate cert)
        throws KeyStoreException
    {
//         /*
//          * Currently it's up to the network layer (s.POST) to ensure
//          * that this op is sane wrt identities and ownership.
//          */
//         if (null != cert && null != alias && cert instanceof X509Certificate){
//             X509Certificate x509cert = (X509Certificate)cert;
//             /*
//              * [TODO] Check alias for stale DN
//              */
//             Principal subject = x509cert.getSubjectDN();
//             if (null != subject){
//                 IO.Context request = Thread.Context();
//                 if (null != request){
//                     Reference reference_subject = TrustStore.SubjectFor(request,alias);
//                     Reference reference_alias = TrustStore.AliasFor(request,subject);
//                     Reference reference_cert = TrustStore.CertificateFor(request,subject);
//                     try {
//                         reference_cert.setWriteParsedX509Certificate(x509cert);
//                         reference_subject.setWriteParsedString(subject.getName());
//                         reference_alias.setWriteParsedString(alias);
//                         return;
//                     }
//                     catch (java.io.IOException exc){
//                         throw new KeyStoreException(exc);
//                     }
//                 }
//                 else
//                     throw new alto.sys.Error.Bug();
//             }
//             else
//                 throw new KeyStoreException();
//         }
//         else
//             throw new KeyStoreException();
    }
    public void engineDeleteEntry(String alias)
        throws KeyStoreException
    {

    }
    public boolean engineContainsAlias(java.lang.String alias){
//         if (null != alias){
//             IO.Context request = Thread.Context();
//             if (null != request){
//                 Reference reference = TrustStore.SubjectFor(request,alias);
//                 try {
//                     return (null != reference.readString());
//                 }
//                 catch (java.io.IOException exc){
//                     throw new alto.sys.Error.State(exc);
//                 }
//             }
//             else
                throw new alto.sys.Error.Bug();
//         }
//         else
//             throw new alto.sys.Error.Argument();
    }
    public java.lang.String engineGetCertificateAlias(Certificate cert){
//         if (null != cert && cert instanceof X509Certificate){
//             X509Certificate x509cert = (X509Certificate)cert;
//             Principal subject = x509cert.getSubjectDN();
//             if (null != subject){
//                 IO.Context request = Thread.Context();
//                 if (null != request){
//                     Reference reference = TrustStore.AliasFor(request,subject);
//                     try {
//                         return reference.readString();
//                     }
//                     catch (java.io.IOException exc){
//                         throw new alto.sys.Error.State(exc);
//                     }
//                 }
//                 else
                    throw new alto.sys.Error.Bug();
//             }
//             else
//                 throw new alto.sys.Error.State();
//         }
//         else
//             throw new alto.sys.Error.Argument();
    }
    public Key engineGetKey(java.lang.String alias, char[] password)
        throws NoSuchAlgorithmException, UnrecoverableKeyException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public void engineSetKeyEntry(String alias, Key key,
                                  char[] password,
                                  Certificate[] chain)
        throws KeyStoreException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public void engineSetKeyEntry(String alias, byte[] key,
                                  Certificate[] chain)
        throws KeyStoreException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public Enumeration<java.lang.String> engineAliases(){
        throw new java.lang.UnsupportedOperationException();
    }
    public int engineSize(){
        throw new java.lang.UnsupportedOperationException();
    }
    public boolean engineIsKeyEntry(java.lang.String alias){
        return false;
    }
    public boolean engineIsCertificateEntry(java.lang.String alias){
        return this.engineContainsAlias(alias);
    }
    public void engineStore(OutputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public void engineStore(KeyStore.LoadStoreParameter param)
		throws IOException, NoSuchAlgorithmException,
               CertificateException 
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public void engineLoad(InputStream stream, char[] password)
        throws IOException, NoSuchAlgorithmException, CertificateException
    {
        throw new java.lang.UnsupportedOperationException();
    }
    public void engineLoad(KeyStore.LoadStoreParameter param)
		throws IOException, NoSuchAlgorithmException,
               CertificateException 
    {
        return;//@see TrustStore#TrustStore() initialization
    }

}
