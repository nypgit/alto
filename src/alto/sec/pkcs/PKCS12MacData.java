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
 * Copyright 1999-2007 Sun Microsystems, Inc.  All Rights Reserved.
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

package alto.sec.pkcs;

import alto.sec.util.DerInputStream;
import alto.sec.util.DerOutputStream;
import alto.sec.util.DerValue;
import alto.sec.x509.AlgorithmId;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;

/**
 * A PKCS12MacData type, as defined in PKCS#12.
 *
 * @author Sharon Liu
 */

class PKCS12MacData {

    private String digestAlgorithmName;
    private AlgorithmParameters digestAlgorithmParams;
    private byte[] digest;
    private byte[] macSalt;
    private int iterations;

    // the ASN.1 encoded contents of this class
    private byte[] encoded = null;

    /**
     * Parses a PKCS#12 MAC data.
     */
    PKCS12MacData(DerInputStream derin)
        throws IOException, ParsingException
    {
        DerValue[] macData = derin.getSequence(2);

        // Parse the digest info
        DerInputStream digestIn = new DerInputStream(macData[0].toByteArray());
        DerValue[] digestInfo = digestIn.getSequence(2);

        // Parse the DigestAlgorithmIdentifier.
        AlgorithmId digestAlgorithmId = AlgorithmId.parse(digestInfo[0]);
        this.digestAlgorithmName = digestAlgorithmId.getName();
        this.digestAlgorithmParams = digestAlgorithmId.getParameters();
        // Get the digest.
        this.digest = digestInfo[1].getOctetString();

        // Get the salt.
        this.macSalt = macData[1].getOctetString();

        // Iterations is optional. The default value is 1.
        if (macData.length > 2) {
            this.iterations = macData[2].getInteger();
        } else {
            this.iterations = 1;
        }
    }

    PKCS12MacData(String algName, byte[] digest, byte[] salt, int iterations)
        throws NoSuchAlgorithmException
    {
        if (algName == null)
           throw new NullPointerException("the algName parameter " +
                                               "must be non-null");

        AlgorithmId algid = AlgorithmId.get(algName);
        this.digestAlgorithmName = algid.getName();
        this.digestAlgorithmParams = algid.getParameters();

        if (digest == null) {
            throw new NullPointerException("the digest " +
                                           "parameter must be non-null");
        } else if (digest.length == 0) {
            throw new IllegalArgumentException("the digest " +
                                                "parameter must not be empty");
        } else {
            this.digest = digest.clone();
        }

        this.macSalt = salt;
        this.iterations = iterations;

        // delay the generation of ASN.1 encoding until
        // getEncoded() is called
        this.encoded = null;

    }

    PKCS12MacData(AlgorithmParameters algParams, byte[] digest, byte[] salt, int iterations)
        throws NoSuchAlgorithmException
    {
        if (algParams == null)
           throw new NullPointerException("the algParams parameter " +
                                               "must be non-null");

        AlgorithmId algid = AlgorithmId.get(algParams);
        this.digestAlgorithmName = algid.getName();
        this.digestAlgorithmParams = algid.getParameters();

        if (digest == null) {
            throw new NullPointerException("the digest " +
                                           "parameter must be non-null");
        } else if (digest.length == 0) {
            throw new IllegalArgumentException("the digest " +
                                                "parameter must not be empty");
        } else {
            this.digest = digest.clone();
        }

        this.macSalt = salt;
        this.iterations = iterations;

        // delay the generation of ASN.1 encoding until
        // getEncoded() is called
        this.encoded = null;

    }

    String getDigestAlgName() {
        return digestAlgorithmName;
    }

    byte[] getSalt() {
        return macSalt;
    }

    int getIterations() {
        return iterations;
    }

    byte[] getDigest() {
        return digest;
    }

    /**
     * Returns the ASN.1 encoding of this object.
     * @return the ASN.1 encoding.
     * @exception IOException if error occurs when constructing its
     * ASN.1 encoding.
     */
    public byte[] getEncoded() throws NoSuchAlgorithmException, IOException
    {
        if (this.encoded != null)
            return this.encoded.clone();

        DerOutputStream out = new DerOutputStream();
        DerOutputStream tmp = new DerOutputStream();

        DerOutputStream tmp2 = new DerOutputStream();
        // encode encryption algorithm
        AlgorithmId algid = AlgorithmId.get(digestAlgorithmName);
        algid.encode(tmp2);

        // encode digest data
        tmp2.putOctetString(digest);

        tmp.write(DerValue.tag_Sequence, tmp2);

        // encode salt
        tmp.putOctetString(macSalt);

        // encode iterations
        tmp.putInteger(iterations);

        // wrap everything into a SEQUENCE
        out.write(DerValue.tag_Sequence, tmp);
        this.encoded = out.toByteArray();

        return this.encoded.clone();
    }

}
