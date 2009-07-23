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
package alto.sec.x509;

import alto.sec.util.ObjectIdentifier;
import alto.sec.pkcs.PKCS9Attribute;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that allows conversion from String to ObjectIdentifier and
 * vice versa according to RFC1779, RFC2253, and an augmented version of
 * those standards.
 */
public final class AVAKeyword {

    private static final Map<ObjectIdentifier,AVAKeyword> oidMap;
    private static final Map<String,AVAKeyword> keywordMap;

    public static boolean Exists(String name){
        return keywordMap.containsKey(name);
    }
    /**
     * Get an object identifier representing the specified keyword (or
     * string encoded object identifier) in the given standard.
     *
     * @throws IOException If the keyword is not valid in the specified standard
     */
    public static ObjectIdentifier getOID(String keyword, int standard)
            throws IOException {
        return getOID
            (keyword, standard, Collections.<String, String>emptyMap());
    }

    /**
     * Get an object identifier representing the specified keyword (or
     * string encoded object identifier) in the given standard.
     *
     * @param keywordMap a Map where a keyword String maps to a corresponding
     *   OID String. Each AVA keyword will be mapped to the corresponding OID.
     *   If an entry does not exist, it will fallback to the builtin
     *   keyword/OID mapping.
     * @throws IOException If the keyword is not valid in the specified standard
     *   or the OID String to which a keyword maps to is improperly formatted.
     */
    public static ObjectIdentifier getOID
        (String keyword, int standard, Map<String, String> extraKeywordMap)
            throws IOException {

        keyword = keyword.toUpperCase();
        if (standard == AVA.RFC2253) {
            if (keyword.startsWith(" ") || keyword.endsWith(" ")) {
                throw new IOException("Invalid leading or trailing space " +
                        "in keyword \"" + keyword + "\"");
            }
        } else {
            keyword = keyword.trim();
        }

        // check user-specified keyword map first, then fallback to built-in
        // map
        String oidString = extraKeywordMap.get(keyword);
        if (oidString == null) {
            AVAKeyword ak = keywordMap.get(keyword);
            if ((ak != null) && ak.isCompliant(standard)) {
                return ak.oid;
            }
        } else {
            return new ObjectIdentifier(oidString);
        }

        // no keyword found or not standard compliant, check if OID string

        // RFC1779 requires, DEFAULT allows OID. prefix
        if (standard == AVA.RFC1779) {
            if (keyword.startsWith("OID.") == false) {
                throw new IOException("Invalid RFC1779 keyword: " + keyword);
            }
            keyword = keyword.substring(4);
        } else if (standard == AVA.DEFAULT) {
            if (keyword.startsWith("OID.")) {
                keyword = keyword.substring(4);
            }
        }
        boolean number = false;
        if (keyword.length() != 0) {
            char ch = keyword.charAt(0);
            if ((ch >= '0') && (ch <= '9')) {
                number = true;
            }
        }
        if (number == false) {
            throw new IOException("Invalid keyword \"" + keyword + "\"");
        }
        return new ObjectIdentifier(keyword);
    }

    /**
     * Get a keyword for the given ObjectIdentifier according to standard.
     * If no keyword is available, the ObjectIdentifier is encoded as a
     * String.
     */
    public static String getKeyword(ObjectIdentifier oid, int standard) {
        return getKeyword
            (oid, standard, Collections.<String, String>emptyMap());
    }

    /**
     * Get a keyword for the given ObjectIdentifier according to standard.
     * Checks the extraOidMap for a keyword first, then falls back to the
     * builtin/default set. If no keyword is available, the ObjectIdentifier
     * is encoded as a String.
     */
    public static String getKeyword
        (ObjectIdentifier oid, int standard, Map<String, String> extraOidMap) {

        // check extraOidMap first, then fallback to built-in map
        String oidString = oid.toString();
        String keywordString = extraOidMap.get(oidString);
        if (keywordString == null) {
            AVAKeyword ak = oidMap.get(oid);
            if ((ak != null) && ak.isCompliant(standard)) {
                return ak.keyword;
            }
        } else {
            if (keywordString.length() == 0) {
                throw new IllegalArgumentException("keyword cannot be empty");
            }
            keywordString = keywordString.trim();
            char c = keywordString.charAt(0);
            if (c < 65 || c > 122 || (c > 90 && c < 97)) {
                throw new IllegalArgumentException
                    ("keyword does not start with letter");
            }
            for (int i=1; i<keywordString.length(); i++) {
                c = keywordString.charAt(i);
                if ((c < 65 || c > 122 || (c > 90 && c < 97)) &&
                    (c < 48 || c > 57) && c != '_') {
                    throw new IllegalArgumentException
                    ("keyword character is not a letter, digit, or underscore");
                }
            }
            return keywordString;
        }
        // no compliant keyword, use OID
        if (standard == AVA.RFC2253) {
            return oidString;
        } else {
            return "OID." + oidString;
        }
    }

    /**
     * Test if oid has an associated keyword in standard.
     */
    public static boolean hasKeyword(ObjectIdentifier oid, int standard) {
        AVAKeyword ak = oidMap.get(oid);
        if (ak == null) {
            return false;
        }
        return ak.isCompliant(standard);
    }

    static {
        oidMap = new HashMap<ObjectIdentifier,AVAKeyword>();
        keywordMap = new HashMap<String,AVAKeyword>();

        // NOTE if multiple keywords are available for one OID, order
        // is significant!! Preferred *LAST*.
        new AVAKeyword("CN",           X500Name.commonName_oid,   true,  true);
        new AVAKeyword("C",            X500Name.countryName_oid,  true,  true);
        new AVAKeyword("L",            X500Name.localityName_oid, true,  true);
        new AVAKeyword("S",            X500Name.stateName_oid,    false, false);
        new AVAKeyword("ST",           X500Name.stateName_oid,    true,  true);
        new AVAKeyword("O",            X500Name.orgName_oid,      true,  true);
        new AVAKeyword("OU",           X500Name.orgUnitName_oid,  true,  true);
        new AVAKeyword("T",            X500Name.title_oid,        false, false);
        new AVAKeyword("IP",           X500Name.ipAddress_oid,    false, false);
        new AVAKeyword("STREET",       X500Name.streetAddress_oid,true,  true);
        new AVAKeyword("DC",           X500Name.DOMAIN_COMPONENT_OID,
                                                                  false, true);
        new AVAKeyword("DNQUALIFIER",  X500Name.DNQUALIFIER_OID,  false, false);
        new AVAKeyword("DNQ",          X500Name.DNQUALIFIER_OID,  false, false);
        new AVAKeyword("SURNAME",      X500Name.SURNAME_OID,      false, false);
        new AVAKeyword("GIVENNAME",    X500Name.GIVENNAME_OID,    false, false);
        new AVAKeyword("INITIALS",     X500Name.INITIALS_OID,     false, false);
        new AVAKeyword("GENERATION",   X500Name.GENERATIONQUALIFIER_OID,
                                                                  false, false);
        new AVAKeyword("EMAIL", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
        new AVAKeyword("EMAILADDRESS", PKCS9Attribute.EMAIL_ADDRESS_OID,
                                                                  false, false);
        new AVAKeyword("UID",          X500Name.userid_oid,       false, true);
        new AVAKeyword("SERIALNUMBER", X500Name.SERIALNUMBER_OID, false, false);
    }



    private String keyword;
    private ObjectIdentifier oid;
    private boolean rfc1779Compliant, rfc2253Compliant;

    private AVAKeyword(String keyword, ObjectIdentifier oid,
               boolean rfc1779Compliant, boolean rfc2253Compliant) {
        this.keyword = keyword;
        this.oid = oid;
        this.rfc1779Compliant = rfc1779Compliant;
        this.rfc2253Compliant = rfc2253Compliant;

        // register it
        oidMap.put(oid, this);
        keywordMap.put(keyword, this);
    }

    private boolean isCompliant(int standard) {
        switch (standard) {
        case AVA.RFC1779:
            return rfc1779Compliant;
        case AVA.RFC2253:
            return rfc2253Compliant;
        case AVA.DEFAULT:
            return true;
        default:
            // should not occur, internal error
            throw new IllegalArgumentException("Invalid standard " + standard);
        }
    }

}
