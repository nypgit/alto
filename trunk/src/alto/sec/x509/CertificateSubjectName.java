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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import alto.sec.util.DerInputStream;
import alto.sec.util.DerOutputStream;
import alto.sec.util.DerValue;

/**
 * This class defines the X500Name attribute for the Certificate.
 *
 * @author Amit Kapoor
 * @author Hemma Prafullchandra
 * @see CertAttrSet
 */
public class CertificateSubjectName implements CertAttrSet<String> {
    /**
     * Identifier for this attribute, to be used with the
     * get, set, delete methods of Certificate, x509 type.
     */
    public static final String IDENT = "x509.info.subject";
    /**
     * Sub attributes name for this CertAttrSet.
     */
    public static final String NAME = "subject";
    public static final String DN_NAME = "dname";

    // accessor name for cached X500Name only
    // do not allow a set() of this value, do not advertise with getElements()
    public static final String DN_PRINCIPAL = "x500principal";


    private X500Name dnName;

    /**
     * Default constructor for the certificate attribute.
     *
     * @param name the X500Name
     */
    public CertificateSubjectName(X500Name name) {
        super();
        this.dnName = name;
    }

    /**
     * Create the object, decoding the values from the passed DER stream.
     *
     * @param in the DerInputStream to read the X500Name from.
     * @exception IOException on decoding errors.
     */
    public CertificateSubjectName(DerInputStream in) throws IOException {
        super();
        dnName = new X500Name(in);
    }

    /**
     * Create the object, decoding the values from the passed stream.
     *
     * @param in the InputStream to read the X500Name from.
     * @exception IOException on decoding errors.
     */
    public CertificateSubjectName(InputStream in) throws IOException {
        super();
        DerValue derVal = new DerValue(in);
        dnName = new X500Name(derVal);
    }

    /**
     * Return the name as user readable string.
     */
    public String toString() {
        if (dnName == null) 
            return "";
        else
            return dnName.toString();
    }

    /**
     * Encode the name in DER form to the stream.
     *
     * @param out the DerOutputStream to marshal the contents to.
     * @exception IOException on errors.
     */
    public void encode(OutputStream out) throws IOException {
        DerOutputStream tmp = new DerOutputStream();
        dnName.encode(tmp);

        out.write(tmp.toByteArray());
    }

    /**
     * Set the attribute value.
     */
    public void set(String name, Object obj) throws IOException {
        if (!(obj instanceof X500Name)) {
            throw new IOException("Attribute must be of type X500Name.");
        }
        else if (name.equalsIgnoreCase(DN_NAME)) {
            this.dnName = (X500Name)obj;
        }
        else {
            throw new IOException("Attribute name not recognized by " +
                                  "CertAttrSet:CertificateSubjectName.");
        }
    }

    /**
     * Get the attribute value.
     */
    public Object get(String name) throws IOException {
        if (name.equalsIgnoreCase(DN_NAME)||name.equalsIgnoreCase(DN_PRINCIPAL)) {
            return dnName;
        }
        else {
            throw new IOException("Attribute name not recognized by " +
                                  "CertAttrSet:CertificateSubjectName.");
        }
    }

    /**
     * Delete the attribute value.
     */
    public void delete(String name) throws IOException {
        if (name.equalsIgnoreCase(DN_NAME)) {
            dnName = null;
        } 
        else {
            throw new IOException("Attribute name not recognized by " +
                                  "CertAttrSet:CertificateSubjectName.");
        }
    }

    /**
     * Return an enumeration of names of attributes existing within this
     * attribute.
     */
    public Enumeration<String> getElements() {
        AttributeNameEnumeration elements = new AttributeNameEnumeration();
        elements.addElement(DN_NAME);

        return(elements.elements());
    }

    /**
     * Return the name of this attribute.
     */
    public String getName() {
        return(NAME);
    }
}
