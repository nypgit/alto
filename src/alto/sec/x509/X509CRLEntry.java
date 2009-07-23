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
/*
 * Copyright 1997-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package alto.sec.x509;

import alto.io.Code;
import alto.io.Check;
import alto.sec.util.*;
import alto.io.u.Bbuf;
import alto.io.u.Hex;
import alto.hash.Function;

import java.io.InputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;


/**
 * <p>Abstract class for a revoked certificate in a CRL.
 * This class is for each entry in the <code>revokedCertificates</code>,
 * so it deals with the inner <em>SEQUENCE</em>.
 * The ASN.1 definition for this is:
 * <pre>
 * revokedCertificates    SEQUENCE OF SEQUENCE  {
 *     userCertificate    CertificateSerialNumber,
 *     revocationDate     ChoiceOfTime,
 *     crlEntryExtensions Extensions OPTIONAL
 *                        -- if present, must be v2
 * }  OPTIONAL
 *
 * CertificateSerialNumber  ::=  INTEGER
 *
 * Extensions  ::=  SEQUENCE SIZE (1..MAX) OF Extension
 *
 * Extension  ::=  SEQUENCE  {
 *     extnId        OBJECT IDENTIFIER,
 *     critical      BOOLEAN DEFAULT FALSE,
 *     extnValue     OCTET STRING
 *                   -- contains a DER encoding of a value
 *                   -- of the type registered for use with
 *                   -- the extnId object identifier value
 * }
 * </pre>
 *
 * @author Hemma Prafullchandra
 */
public class X509CRLEntry 
    extends java.lang.Object
    implements java.security.cert.X509Extension
{

    private final static boolean isExplicit = false;
    private final static long YR_2050 = 2524636800000L;

    private SerialNumber serialNumber = null;
    private Date revocationDate = null;
    private CRLExtensions extensions = null;
    private byte[] revokedCert = null;
    private X500Name certIssuer;

    /**
     * Constructs a revoked certificate entry using the given
     * serial number and revocation date.
     *
     * @param num the serial number of the revoked certificate.
     * @param date the Date on which revocation took place.
     */
    public X509CRLEntry(BigInteger num, Date date) {
        super();
        this.serialNumber = new SerialNumber(num);
        this.revocationDate = date;
    }

    /**
     * Constructs a revoked certificate entry using the given
     * serial number, revocation date and the entry
     * extensions.
     *
     * @param num the serial number of the revoked certificate.
     * @param date the Date on which revocation took place.
     * @param crlEntryExts the extensions for this entry.
     */
    public X509CRLEntry(BigInteger num, Date date,
                        CRLExtensions crlEntryExts) 
    {
        super();
        this.serialNumber = new SerialNumber(num);
        this.revocationDate = date;
        this.extensions = crlEntryExts;
    }

    /**
     * Unmarshals a revoked certificate from its encoded form.
     *
     * @param revokedCert the encoded bytes.
     * @exception CRLException on parsing errors.
     */
    public X509CRLEntry(byte[] revokedCert) 
        throws CRLException 
    {
        super();
        try {
            this.parse(new DerValue(revokedCert));
        }
        catch (IOException e) {
            this.revokedCert = null;
            throw new CRLException("Parsing",e);
        }
    }

    /**
     * Unmarshals a revoked certificate from its encoded form.
     *
     * @param derVal the DER value containing the revoked certificate.
     * @exception CRLException on parsing errors.
     */
    public X509CRLEntry(DerValue derValue) 
        throws CRLException 
    {
        super();
        try {
            this.parse(derValue);
        } catch (IOException e) {
            revokedCert = null;
            throw new CRLException("Parsing", e);
        }
    }

    /**
     * Returns true if this revoked certificate entry has
     * extensions, otherwise false.
     *
     * @return true if this CRL entry has extensions, otherwise
     * false.
     */
    public boolean hasExtensions() {
        return (null != this.extensions);
    }

    /**
     * Encodes the revoked certificate to an output stream.
     *
     * @param outStrm an output stream to which the encoded revoked
     * certificate is written.
     * @exception CRLException on encoding errors.
     */
    public void encode(DerOutputStream outStrm) 
        throws CRLException 
    {
        try {
            if (null == this.revokedCert) {
                DerOutputStream tmp = new DerOutputStream();
                // sequence { serialNumber, revocationDate, extensions }
                this.serialNumber.encode(tmp);

                if (this.revocationDate.getTime() < YR_2050) {
                    tmp.putUTCTime(this.revocationDate);
                } else {
                    tmp.putGeneralizedTime(this.revocationDate);
                }

                if (null != this.extensions)
                    this.extensions.encode(tmp, isExplicit);

                DerOutputStream seq = new DerOutputStream();
                seq.write(DerValue.tag_Sequence, tmp);

                this.revokedCert = seq.toByteArray();
            }
            outStrm.write(revokedCert);
        } 
        catch (IOException e) {
            throw new CRLException("Encoding",e);
        }
    }

    /**
     * Returns the ASN.1 DER-encoded form of this CRL Entry,
     * which corresponds to the inner SEQUENCE.
     *
     * @exception CRLException if an encoding error occurs.
     */
    public byte[] getEncoded() throws CRLException {
        if (null == this.revokedCert)
            this.encode(new DerOutputStream());
        
        return this.revokedCert.clone();
    }

    protected byte[] getEncodedInternal() throws CRLException {
        if (null == this.revokedCert)
            this.encode(new DerOutputStream());
        
        return this.revokedCert;
    }

    public X500Name getCertificateIssuer() {
        return this.certIssuer;
    }

    void setCertificateIssuer(X500Name crlIssuer, X500Name certIssuer) {
        if (crlIssuer.equals(certIssuer)) {
            this.certIssuer = null;
        } else {
            this.certIssuer = certIssuer;
        }
    }

    /**
     * Gets the serial number from this X509CRLEntry,
     * i.e. the <em>userCertificate</em>.
     *
     * @return the serial number.
     */
    public BigInteger getSerialNumber() {
        return this.serialNumber.getNumber();
    }

    /**
     * Gets the revocation date from this X509CRLEntry,
     * the <em>revocationDate</em>.
     *
     * @return the revocation date.
     */
    public Date getRevocationDate() {
        return new Date(this.revocationDate.getTime());
    }

    /**
     * get Reason Code from CRL entry.
     *
     * @returns Integer or null, if no such extension
     * @throws IOException on error
     */
    public Integer getReasonCode() throws IOException {
        Object obj = this.getExtension(PKIXExtensions.ReasonCode_Id);
        if (obj == null)
            return null;
        else {
            CRLReasonCodeExtension reasonCode = (CRLReasonCodeExtension)obj;
            return (Integer)(reasonCode.get(reasonCode.REASON));
        }
    }

    /**
     * Returns a printable string of this revoked certificate.
     *
     * @return value of this revoked certificate in a printable form.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.serialNumber.toString());
        sb.append("  On: " + this.revocationDate.toString());
        if (certIssuer != null) {
            sb.append("\n    Certificate issuer: " + this.certIssuer);
        }
        if (this.extensions != null) {
            Collection allEntryExts = this.extensions.getAllExtensions();
            Object[] objs = allEntryExts.toArray();

            sb.append("\n    CRL Entry Extensions: " + objs.length);
            for (int i = 0; i < objs.length; i++) {
                sb.append("\n    [" + (i+1) + "]: ");
                Extension ext = (Extension)objs[i];
                try {
                    if (OIDMap.getClass(ext.getExtensionId()) == null) {
                        sb.append(ext.toString());
                        byte[] extValue = ext.getExtensionValue();
                        if (extValue != null) {
                            extValue = DerValue.Encode.OctetString(extValue);

                            sb.append("Extension unknown: DER encoded OCTET string =\n"
                                      + Hex.encode(extValue) + "\n");
                        }
                    } else
                        sb.append(ext.toString()); //sub-class exists
                }
                catch (Exception e) {
                    sb.append(", Error parsing this extension");
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    /**
     * Return true if a critical extension is found that is
     * not supported, otherwise return false.
     */
    public boolean hasUnsupportedCriticalExtension() {
        CRLExtensions extensions = this.extensions;
        if (null == extensions)
            return false;
        else
            return extensions.hasUnsupportedCriticalExtension();
    }

    /**
     * Gets a Set of the extension(s) marked CRITICAL in this
     * X509CRLEntry.  In the returned set, each extension is
     * represented by its OID string.
     *
     * @return a set of the extension oid strings in the
     * Object that are marked critical.
     */
    @Code(Check.Builds)
    public Set<String> getCriticalExtensionOIDs() {
        CRLExtensions extensions = this.extensions;
        if (null == extensions)
            return null;
        else {
            Set<String> extSet = new HashSet<String>();
            for (Extension ex : extensions.getAllExtensions()) {
                if (ex.isCritical()) {
                    extSet.add(ex.getExtensionId().toString());
                }
            }
            return extSet;
        }
    }

    /**
     * Gets a Set of the extension(s) marked NON-CRITICAL in this
     * X509CRLEntry. In the returned set, each extension is
     * represented by its OID string.
     *
     * @return a set of the extension oid strings in the
     * Object that are marked critical.
     */
    @Code(Check.Builds)
    public Set<String> getNonCriticalExtensionOIDs() {
        CRLExtensions extensions = this.extensions;
        if (extensions == null)
            return null;
        else {
            Set<String> extSet = new HashSet<String>();
            for (Extension ex : extensions.getAllExtensions()) {
                if (!ex.isCritical()) {
                    extSet.add(ex.getExtensionId().toString());
                }
            }
            return extSet;
        }
    }

    /**
     * Gets the DER encoded OCTET string for the extension value
     * (<em>extnValue</em>) identified by the passed in oid String.
     * The <code>oid</code> string is
     * represented by a set of positive whole number separated
     * by ".", that means,<br>
     * &lt;positive whole number&gt;.&lt;positive whole number&gt;.&lt;positive
     * whole number&gt;.&lt;...&gt;
     *
     * @param oid the Object Identifier value for the extension.
     * @return the DER encoded octet string of the extension value.
     */
    public byte[] getExtensionValue(String oid) {
        CRLExtensions extensions = this.extensions;
        if (extensions == null)
            return null;
        else {
            try {
                ObjectIdentifier findOID = new ObjectIdentifier(oid);
                String extAlias = OIDMap.getName(findOID);
                Extension crlExt = null;
                if (extAlias == null) { // may be unknown
                    Extension ex = null;
                    ObjectIdentifier inCertOID;
                    Enumeration<Extension> e = extensions.getElements();
                    while (e.hasMoreElements()) {
                        ex = e.nextElement();
                        inCertOID = ex.getExtensionId();
                        if (inCertOID.equals(findOID)) {
                            crlExt = ex;
                            break;
                        }
                    }
                }
                else
                    crlExt = extensions.get(extAlias);

                if (crlExt == null)
                    return null;
                else {
                    byte[] extData = crlExt.getExtensionValue();
                    if (null == extData)
                        return null;
                    else
                        return DerValue.Encode.OctetString(extData);
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * get an extension
     *
     * @param oid ObjectIdentifier of extension desired
     * @returns Extension of type <extension> or null, if not found
     */
    @Code(Check.SecNoCopy)
    public Extension getExtension(ObjectIdentifier oid) {
        CRLExtensions extensions = this.extensions;
        if (null == extensions)
            return null;
        else {
            return extensions.get(OIDMap.getName(oid));
        }
    }

    private void parse(DerValue derVal)
        throws CRLException, IOException 
    {

        if (DerValue.tag_Sequence != derVal.tag) {
            throw new CRLException("Invalid encoded RevokedCertificate, starting sequence tag missing.");
        }
        else if (0 == derVal.data.available())
            throw new CRLException("No data encoded for RevokedCertificates");
        else {
            this.revokedCert = derVal.toByteArray();
            {
                DerInputStream in = derVal.toDerInputStream();
                DerValue val = in.getDerValue();
                this.serialNumber = new SerialNumber(val);
            }
            switch(derVal.data.peekByte()){
            case DerValue.tag_UtcTime:
                this.revocationDate = derVal.data.getUTCTime();
                break;
            case DerValue.tag_GeneralizedTime:
                this.revocationDate = derVal.data.getGeneralizedTime();
                break;
            default:
                throw new CRLException("Invalid encoding for revocation date");
            }

            if (1 > derVal.data.available())
                return;  // no extensions
            else 
                this.extensions = new CRLExtensions(derVal.toDerInputStream());
        }
    }

    /**
     * Returns the CertificateIssuerExtension
     *
     * @return the CertificateIssuerExtension, or null if it does not exist
     */
    CertificateIssuerExtension getCertificateIssuerExtension() {
        return (CertificateIssuerExtension)
            this.getExtension(PKIXExtensions.CertificateIssuer_Id);
    }

    public int hashCode(){
        try {
            return (Function.Xor.Hash32(this.getEncodedInternal()));
        }
        catch (CRLException exc){
            return 0;
        }
    }
    public boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (ano instanceof X509CRLEntry){
            X509CRLEntry that = (X509CRLEntry)ano;
            try {
                return Bbuf.equals(this.getEncodedInternal(),that.getEncodedInternal());
            }
            catch (CRLException exc){
                return false;
            }
        }
        else
            return false;
    }
}
