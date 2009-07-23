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
 * Copyright 1996-2006 Sun Microsystems, Inc.  All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.security.AccessController;
import java.text.Normalizer;
import java.util.*;

import alto.sec.util.*;
import alto.sec.pkcs.PKCS9Attribute;


/**
 * X.500 Attribute-Value-Assertion (AVA):  an attribute, as identified by
 * some attribute ID, has some particular value.  Values are as a rule ASN.1
 * printable strings.  A conventional set of type IDs is recognized when
 * parsing (and generating) RFC 1779 or RFC 2253 syntax strings.
 *
 * <P>AVAs are components of X.500 relative names.  Think of them as being
 * individual fields of a database record.  The attribute ID is how you
 * identify the field, and the value is part of a particular record.
 * <p>
 * Note that instances of this class are immutable.
 *
 * @see X500Name
 * @see RDN
 *
 *
 * @author David Brownell
 * @author Amit Kapoor
 * @author Hemma Prafullchandra
 */
public class AVA implements DerEncoder {

    public final static String escapees = ",=+<>#;\"\\";
    /**
     * Quote special characters for DN string values from user input.
     */
    public final static String Escape(String value){
        int count = value.length();
        StringBuilder sbuffer = new StringBuilder();
        for (int i = 0; i < count; i++) {
            char c = value.charAt(i);
            if (-1 < escapees.indexOf(c)){
                sbuffer.append('\\');
                sbuffer.append(c);
            }
            else {
                sbuffer.append(c);
            }
        }
        if (count == sbuffer.length())
            return value;
        else
            return sbuffer.toString();
    }

    // See CR 6391482: if enabled this flag preserves the old but incorrect
    // PrintableString encoding for DomainComponent. It may need to be set to
    // avoid breaking preexisting certificates generated with alto.sec APIs.
    private static final boolean PRESERVE_OLD_DC_ENCODING = false;


    /**
     * DEFAULT format allows both RFC1779 and RFC2253 syntax and
     * additional keywords.
     */
    final static int DEFAULT = 1;
    /**
     * RFC1779 specifies format according to RFC1779.
     */
    final static int RFC1779 = 2;
    /**
     * RFC2253 specifies format according to RFC2253.
     */
    final static int RFC2253 = 3;

    // currently not private, accessed directly from RDN
    final ObjectIdentifier oid;
    final DerValue value;

    /*
     * If the value has any of these characters in it, it must be quoted.
     * Backslash and quote characters must also be individually escaped.
     * Leading and trailing spaces, also multiple internal spaces, also
     * call for quoting the whole string.
     */
    private static final String specialChars = ",+=\n<>#;";

    /*
     * In RFC2253, if the value has any of these characters in it, it
     * must be quoted by a preceding \.
     */
    private static final String specialChars2253 = ",+\"\\<>;";

    /*
     * includes special chars from RFC1779 and RFC2253, as well as ' '
     */
    private static final String specialCharsAll = ",=\n+<>#;\\\" ";

    /*
     * Values that aren't printable strings are emitted as BER-encoded
     * hex data.
     */
    private static final String hexDigits = "0123456789ABCDEF";

    public AVA(ObjectIdentifier type, DerValue val) {
        if ((type == null) || (val == null)) {
            throw new NullPointerException();
        }
        oid = type;
        value = val;
    }

    /**
     * Parse an RFC 1779 or RFC 2253 style AVA string:  CN=fee fie foe fum
     * or perhaps with quotes.  Not all defined AVA tags are supported;
     * of current note are X.400 related ones (PRMD, ADMD, etc).
     *
     * This terminates at unescaped AVA separators ("+") or RDN
     * separators (",", ";"), or DN terminators (">"), and removes
     * cosmetic whitespace at the end of values.
     */
    AVA(Reader in) throws IOException {
        this(in, DEFAULT);
    }

    /**
     * Parse an RFC 1779 or RFC 2253 style AVA string:  CN=fee fie foe fum
     * or perhaps with quotes. Additional keywords can be specified in the
     * keyword/OID map.
     *
     * This terminates at unescaped AVA separators ("+") or RDN
     * separators (",", ";"), or DN terminators (">"), and removes
     * cosmetic whitespace at the end of values.
     */
    AVA(Reader in, Map<String, String> keywordMap) throws IOException {
        this(in, DEFAULT, keywordMap);
    }

    /**
     * Parse an AVA string formatted according to format.
     *
     * XXX format RFC1779 should only allow RFC1779 syntax but is
     * actually DEFAULT with RFC1779 keywords.
     */
    AVA(Reader in, int format) throws IOException {
        this(in, format, Collections.<String, String>emptyMap());
    }

    /**
     * Parse an AVA string formatted according to format.
     *
     * XXX format RFC1779 should only allow RFC1779 syntax but is
     * actually DEFAULT with RFC1779 keywords.
     *
     * @param in Reader containing AVA String
     * @param format parsing format
     * @param keywordMap a Map where a keyword String maps to a corresponding
     *   OID String. Each AVA keyword will be mapped to the corresponding OID.
     *   If an entry does not exist, it will fallback to the builtin
     *   keyword/OID mapping.
     * @throws IOException if the AVA String is not valid in the specified
     *   standard or an OID String from the keywordMap is improperly formatted
     */
    AVA(Reader in, int format, Map<String, String> keywordMap)
        throws IOException {
        // assume format is one of DEFAULT, RFC1779, RFC2253

        StringBuilder   temp = new StringBuilder();
        int             c;

        /*
         * First get the keyword indicating the attribute's type,
         * and map it to the appropriate OID.
         */
        while (true) {
            c = readChar(in, "Incorrect AVA format");
            if (c == '=') {
                break;
            }
            temp.append((char)c);
        }

        oid = AVAKeyword.getOID(temp.toString(), format, keywordMap);

        /*
         * Now parse the value.  "#hex", a quoted string, or a string
         * terminated by "+", ",", ";", ">".  Whitespace before or after
         * the value is stripped away unless format is RFC2253.
         */
        temp.setLength(0);
        if (format == RFC2253) {
            // read next character
            c = in.read();
            if (c == ' ') {
                throw new IOException("Incorrect AVA RFC2253 format - " +
                                        "leading space must be escaped");
            }
        } else {
            // read next character skipping whitespace
            do {
                c = in.read();
            } while ((c == ' ') || (c == '\n'));
        }
        if (c == -1) {
            // empty value
            value = new DerValue("");
            return;
        }

        if (c == '#') {
            value = parseHexString(in, format);
        } else if ((c == '"') && (format != RFC2253)) {
            value = parseQuotedString(in, temp);
        } else {
            value = parseString(in, c, format, temp);
        }
    }

    /**
     * Get the ObjectIdentifier of this AVA.
     */
    public ObjectIdentifier getObjectIdentifier() {
        return oid;
    }

    /**
     * Get the value of this AVA as a DerValue.
     */
    public DerValue getDerValue() {
        return value;
    }

    /**
     * Get the value of this AVA as a String.
     *
     * @exception RuntimeException if we could not obtain the string form
     *    (should not occur)
     */
    public String getValueString() {
        try {
            String s = value.getAsString();
            if (s == null) {
                throw new RuntimeException("AVA string is null");
            }
            return s;
        } catch (IOException e) {
            // should not occur
            throw new RuntimeException("AVA error: " + e, e);
        }
    }

    private static DerValue parseHexString
        (Reader in, int format) throws IOException {

        int c;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte b = 0;
        int cNdx = 0;
        while (true) {
            c = in.read();

            if (isTerminator(c, format)) {
                break;
            }

            int cVal = hexDigits.indexOf(Character.toUpperCase((char)c));

            if (cVal == -1) {
                throw new IOException("AVA parse, invalid hex " +
                                              "digit: "+ (char)c);
            }

            if ((cNdx % 2) == 1) {
                b = (byte)((b * 16) + (byte)(cVal));
                baos.write(b);
            } else {
                b = (byte)(cVal);
            }
            cNdx++;
        }

        // throw exception if no hex digits
        if (cNdx == 0) {
            throw new IOException("AVA parse, zero hex digits");
        }

        // throw exception if odd number of hex digits
        if (cNdx % 2 == 1) {
            throw new IOException("AVA parse, odd number of hex digits");
        }

        return new DerValue(baos.toByteArray());
    }

    private DerValue parseQuotedString
        (Reader in, StringBuilder temp) throws IOException {

        // RFC1779 specifies that an entire RDN may be enclosed in double
        // quotes. In this case the syntax is any sequence of
        // backslash-specialChar, backslash-backslash,
        // backslash-doublequote, or character other than backslash or
        // doublequote.
        int c = readChar(in, "Quoted string did not end in quote");

        List<Byte> embeddedHex = new ArrayList<Byte>();
        boolean isPrintableString = true;
        while (c != '"') {
            if (c == '\\') {
                c = readChar(in, "Quoted string did not end in quote");

                // check for embedded hex pairs
                Byte hexByte = null;
                if ((hexByte = getEmbeddedHexPair(c, in)) != null) {

                    // always encode AVAs with embedded hex as UTF8
                    isPrintableString = false;

                    // append consecutive embedded hex
                    // as single string later
                    embeddedHex.add(hexByte);
                    c = in.read();
                    continue;
                }

                if (c != '\\' && c != '"' &&
                    specialChars.indexOf((char)c) < 0) {
                    throw new IOException
                        ("Invalid escaped character in AVA: " +
                        (char)c);
                }
            }

            // add embedded hex bytes before next char
            if (embeddedHex.size() > 0) {
                String hexString = getEmbeddedHexString(embeddedHex);
                temp.append(hexString);
                embeddedHex.clear();
            }

            // check for non-PrintableString chars
            isPrintableString &= DerValue.isPrintableStringChar((char)c);
            temp.append((char)c);
            c = readChar(in, "Quoted string did not end in quote");
        }

        // add trailing embedded hex bytes
        if (embeddedHex.size() > 0) {
            String hexString = getEmbeddedHexString(embeddedHex);
            temp.append(hexString);
            embeddedHex.clear();
        }

        do {
            c = in.read();
        } while ((c == '\n') || (c == ' '));
        if (c != -1) {
            throw new IOException("AVA had characters other than "
                    + "whitespace after terminating quote");
        }

        // encode as PrintableString unless value contains
        // non-PrintableString chars
        if (this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID) ||
            (this.oid.equals(X500Name.DOMAIN_COMPONENT_OID) &&
                PRESERVE_OLD_DC_ENCODING == false)) {
            // EmailAddress and DomainComponent must be IA5String
            return new DerValue(DerValue.tag_IA5String,
                                        temp.toString().trim());
        } else if (isPrintableString) {
            return new DerValue(temp.toString().trim());
        } else {
            return new DerValue(DerValue.tag_UTF8String,
                                        temp.toString().trim());
        }
    }

    private DerValue parseString
        (Reader in, int c, int format, StringBuilder temp) throws IOException {

        List<Byte> embeddedHex = new ArrayList<Byte>();
        boolean isPrintableString = true;
        boolean escape = false;
        boolean leadingChar = true;
        int spaceCount = 0;
        do {
            escape = false;
            if (c == '\\') {
                escape = true;
                c = readChar(in, "Invalid trailing backslash");

                // check for embedded hex pairs
                Byte hexByte = null;
                if ((hexByte = getEmbeddedHexPair(c, in)) != null) {

                    // always encode AVAs with embedded hex as UTF8
                    isPrintableString = false;

                    // append consecutive embedded hex
                    // as single string later
                    embeddedHex.add(hexByte);
                    c = in.read();
                    leadingChar = false;
                    continue;
                }

                // check if character was improperly escaped
                if ((format == DEFAULT &&
                        specialCharsAll.indexOf((char)c) == -1) ||
                    (format == RFC1779  &&
                        specialChars.indexOf((char)c) == -1 &&
                        c != '\\' && c != '\"')) {

                    throw new IOException
                        ("Invalid escaped character in AVA: '" +
                        (char)c + "'");

                } else if (format == RFC2253) {
                    if (c == ' ') {
                        // only leading/trailing space can be escaped
                        if (!leadingChar && !trailingSpace(in)) {
                                throw new IOException
                                        ("Invalid escaped space character " +
                                        "in AVA.  Only a leading or trailing " +
                                        "space character can be escaped.");
                        }
                    } else if (c == '#') {
                        // only leading '#' can be escaped
                        if (!leadingChar) {
                            throw new IOException
                                ("Invalid escaped '#' character in AVA.  " +
                                "Only a leading '#' can be escaped.");
                        }
                    } else if (specialChars2253.indexOf((char)c) == -1) {
                        throw new IOException
                                ("Invalid escaped character in AVA: '" +
                                (char)c + "'");

                    }
                }

            } else {
                // check if character should have been escaped
                if (format == RFC2253) {
                    if (specialChars2253.indexOf((char)c) != -1) {
                        throw new IOException
                                ("Character '" + (char)c +
                                "' in AVA appears without escape");
                    }
                }
            }

            // add embedded hex bytes before next char
            if (embeddedHex.size() > 0) {
                // add space(s) before embedded hex bytes
                for (int i = 0; i < spaceCount; i++) {
                    temp.append(" ");
                }
                spaceCount = 0;

                String hexString = getEmbeddedHexString(embeddedHex);
                temp.append(hexString);
                embeddedHex.clear();
            }

            // check for non-PrintableString chars
            isPrintableString &= DerValue.isPrintableStringChar((char)c);
            if (c == ' ' && escape == false) {
                // do not add non-escaped spaces yet
                // (non-escaped trailing spaces are ignored)
                spaceCount++;
            } else {
                // add space(s)
                for (int i = 0; i < spaceCount; i++) {
                    temp.append(" ");
                }
                spaceCount = 0;
                temp.append((char)c);
            }
            c = in.read();
            leadingChar = false;
        } while (isTerminator(c, format) == false);

        if (format == RFC2253 && spaceCount > 0) {
            throw new IOException("Incorrect AVA RFC2253 format - " +
                                        "trailing space must be escaped");
        }

        // add trailing embedded hex bytes
        if (embeddedHex.size() > 0) {
            String hexString = getEmbeddedHexString(embeddedHex);
            temp.append(hexString);
            embeddedHex.clear();
        }

        // encode as PrintableString unless value contains
        // non-PrintableString chars
        if (this.oid.equals(PKCS9Attribute.EMAIL_ADDRESS_OID) ||
            (this.oid.equals(X500Name.DOMAIN_COMPONENT_OID) &&
                PRESERVE_OLD_DC_ENCODING == false)) {
            // EmailAddress and DomainComponent must be IA5String
            return new DerValue(DerValue.tag_IA5String, temp.toString());
        } else if (isPrintableString) {
            return new DerValue(temp.toString());
        } else {
            return new DerValue(DerValue.tag_UTF8String, temp.toString());
        }
    }

    private static Byte getEmbeddedHexPair(int c1, Reader in)
        throws IOException {

        if (hexDigits.indexOf(Character.toUpperCase((char)c1)) >= 0) {
            int c2 = readChar(in, "unexpected EOF - " +
                        "escaped hex value must include two valid digits");

            if (hexDigits.indexOf(Character.toUpperCase((char)c2)) >= 0) {
                int hi = Character.digit((char)c1, 16);
                int lo = Character.digit((char)c2, 16);
                return new Byte((byte)((hi<<4) + lo));
            } else {
                throw new IOException
                        ("escaped hex value must include two valid digits");
            }
        }
        return null;
    }

    private static String getEmbeddedHexString(List<Byte> hexList)
                                                throws IOException {
        int n = hexList.size();
        byte[] hexBytes = new byte[n];
        for (int i = 0; i < n; i++) {
                hexBytes[i] = hexList.get(i).byteValue();
        }
        return new String(hexBytes, "UTF8");
    }

    private static boolean isTerminator(int ch, int format) {
        switch (ch) {
        case -1:
        case '+':
        case ',':
            return true;
        case ';':
        case '>':
            return format != RFC2253;
        default:
            return false;
        }
    }

    private static int readChar(Reader in, String errMsg) throws IOException {
        int c = in.read();
        if (c == -1) {
            throw new IOException(errMsg);
        }
        return c;
    }

    private static boolean trailingSpace(Reader in) throws IOException {

        boolean trailing = false;

        if (!in.markSupported()) {
            // oh well
            return true;
        } else {
            // make readAheadLimit huge -
            // in practice, AVA was passed a StringReader from X500Name,
            // and StringReader ignores readAheadLimit anyways
            in.mark(9999);
            while (true) {
                int nextChar = in.read();
                if (nextChar == -1) {
                    trailing = true;
                    break;
                } else if (nextChar == ' ') {
                    continue;
                } else if (nextChar == '\\') {
                    int followingChar = in.read();
                    if (followingChar != ' ') {
                        trailing = false;
                        break;
                    }
                } else {
                    trailing = false;
                    break;
                }
            }

            in.reset();
            return trailing;
        }
    }

    AVA(DerValue derval) throws IOException {
        // Individual attribute value assertions are SEQUENCE of two values.
        // That'd be a "struct" outside of ASN.1.
        if (derval.tag != DerValue.tag_Sequence) {
            throw new IOException("AVA not a sequence");
        }
        oid = X500Name.intern(derval.data.getOID());
        value = derval.data.getDerValue();

        if (derval.data.available() != 0) {
            throw new IOException("AVA, extra bytes = "
                + derval.data.available());
        }
    }

    AVA(DerInputStream in) throws IOException {
        this(in.getDerValue());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AVA == false) {
            return false;
        }
        AVA other = (AVA)obj;
        return this.toRFC2253CanonicalString().equals
                                (other.toRFC2253CanonicalString());
    }

    /**
     * Returns a hashcode for this AVA.
     *
     * @return a hashcode for this AVA.
     */
    public int hashCode() {
        return toRFC2253CanonicalString().hashCode();
    }

    /*
     * AVAs are encoded as a SEQUENCE of two elements.
     */
    public void encode(DerOutputStream out) throws IOException {
        derEncode(out);
    }

    /**
     * DER encode this object onto an output stream.
     * Implements the <code>DerEncoder</code> interface.
     *
     * @param out
     * the output stream on which to write the DER encoding.
     *
     * @exception IOException on encoding error.
     */
    public void derEncode(OutputStream out) throws IOException {
        DerOutputStream         tmp = new DerOutputStream();
        DerOutputStream         tmp2 = new DerOutputStream();

        tmp.putOID(oid);
        value.encode(tmp);
        tmp2.write(DerValue.tag_Sequence, tmp);
        out.write(tmp2.toByteArray());
    }

    private String toKeyword(int format, Map<String, String> oidMap) {
        return AVAKeyword.getKeyword(oid, format, oidMap);
    }

    /**
     * Returns a printable form of this attribute, using RFC 1779
     * syntax for individual attribute/value assertions.
     */
    public String toString() {
        return toKeywordValueString
            (toKeyword(DEFAULT, Collections.<String, String>emptyMap()));
    }

    /**
     * Returns a printable form of this attribute, using RFC 1779
     * syntax for individual attribute/value assertions. It only
     * emits standardised keywords.
     */
    public String toRFC1779String() {
        return toRFC1779String(Collections.<String, String>emptyMap());
    }

    /**
     * Returns a printable form of this attribute, using RFC 1779
     * syntax for individual attribute/value assertions. It
     * emits standardised keywords, as well as keywords contained in the
     * OID/keyword map.
     */
    public String toRFC1779String(Map<String, String> oidMap) {
        return toKeywordValueString(toKeyword(RFC1779, oidMap));
    }

    /**
     * Returns a printable form of this attribute, using RFC 2253
     * syntax for individual attribute/value assertions. It only
     * emits standardised keywords.
     */
    public String toRFC2253String() {
        return toRFC2253String(Collections.<String, String>emptyMap());
    }

    /**
     * Returns a printable form of this attribute, using RFC 2253
     * syntax for individual attribute/value assertions. It
     * emits standardised keywords, as well as keywords contained in the
     * OID/keyword map.
     */
    public String toRFC2253String(Map<String, String> oidMap) {
        /*
         * Section 2.3: The AttributeTypeAndValue is encoded as the string
         * representation of the AttributeType, followed by an equals character
         * ('=' ASCII 61), followed by the string representation of the
         * AttributeValue. The encoding of the AttributeValue is given in
         * section 2.4.
         */
        StringBuilder typeAndValue = new StringBuilder(100);
        typeAndValue.append(toKeyword(RFC2253, oidMap));
        typeAndValue.append('=');

        /*
         * Section 2.4: Converting an AttributeValue from ASN.1 to a String.
         * If the AttributeValue is of a type which does not have a string
         * representation defined for it, then it is simply encoded as an
         * octothorpe character ('#' ASCII 35) followed by the hexadecimal
         * representation of each of the bytes of the BER encoding of the X.500
         * AttributeValue.  This form SHOULD be used if the AttributeType is of
         * the dotted-decimal form.
         */
        if ((typeAndValue.charAt(0) >= '0' && typeAndValue.charAt(0) <= '9') ||
            !isDerString(value, false))
        {
            byte[] data = null;
            try {
                data = value.toByteArray();
            } catch (IOException ie) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            typeAndValue.append('#');
            for (int j = 0; j < data.length; j++) {
                byte b = data[j];
                typeAndValue.append(Character.forDigit(0xF & (b >>> 4), 16));
                typeAndValue.append(Character.forDigit(0xF & b, 16));
            }
        } else {
            /*
             * 2.4 (cont): Otherwise, if the AttributeValue is of a type which
             * has a string representation, the value is converted first to a
             * UTF-8 string according to its syntax specification.
             *
             * NOTE: this implementation only emits DirectoryStrings of the
             * types returned by isDerString().
             */
            String valStr = null;
            try {
                valStr = new String(value.getDataBytes(), "UTF8");
            } catch (IOException ie) {
                throw new IllegalArgumentException("DER Value conversion");
            }

            /*
             * 2.4 (cont): If the UTF-8 string does not have any of the
             * following characters which need escaping, then that string can be
             * used as the string representation of the value.
             *
             *   o   a space or "#" character occurring at the beginning of the
             *       string
             *   o   a space character occurring at the end of the string
             *   o   one of the characters ",", "+", """, "\", "<", ">" or ";"
             *
             * Implementations MAY escape other characters.
             *
             * NOTE: this implementation also recognizes "=" and "#" as
             * characters which need escaping.
             *
             * If a character to be escaped is one of the list shown above, then
             * it is prefixed by a backslash ('\' ASCII 92).
             *
             * Otherwise the character to be escaped is replaced by a backslash
             * and two hex digits, which form a single byte in the code of the
             * character.
             */
            char[] chars = Escape(valStr).toCharArray();
            StringBuilder sbuffer = new StringBuilder();

            // Find leading and trailing whitespace.
            int lead;   // index of first char that is not leading whitespace
            for (lead = 0; lead < chars.length; lead++) {
                if (chars[lead] != ' ' && chars[lead] != '\r') {
                    break;
                }
            }
            int trail;  // index of last char that is not trailing whitespace
            for (trail = chars.length - 1; trail >= 0; trail--) {
                if (chars[trail] != ' ' && chars[trail] != '\r') {
                    break;
                }
            }

            // escape leading and trailing whitespace
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (i < lead || i > trail) {
                    sbuffer.append('\\');
                }
                sbuffer.append(c);
            }
            typeAndValue.append(sbuffer.toString());
        }
        return typeAndValue.toString();
    }

    public String toRFC2253CanonicalString() {
        /*
         * Section 2.3: The AttributeTypeAndValue is encoded as the string
         * representation of the AttributeType, followed by an equals character
         * ('=' ASCII 61), followed by the string representation of the
         * AttributeValue. The encoding of the AttributeValue is given in
         * section 2.4.
         */
        StringBuilder typeAndValue = new StringBuilder(40);
        typeAndValue.append
            (toKeyword(RFC2253, Collections.<String, String>emptyMap()));
        typeAndValue.append('=');

        /*
         * Section 2.4: Converting an AttributeValue from ASN.1 to a String.
         * If the AttributeValue is of a type which does not have a string
         * representation defined for it, then it is simply encoded as an
         * octothorpe character ('#' ASCII 35) followed by the hexadecimal
         * representation of each of the bytes of the BER encoding of the X.500
         * AttributeValue.  This form SHOULD be used if the AttributeType is of
         * the dotted-decimal form.
         */
        if ((typeAndValue.charAt(0) >= '0' && typeAndValue.charAt(0) <= '9') ||
            !isDerString(value, true))
        {
            byte[] data = null;
            try {
                data = value.toByteArray();
            } catch (IOException ie) {
                throw new IllegalArgumentException("DER Value conversion");
            }
            typeAndValue.append('#');
            for (int j = 0; j < data.length; j++) {
                byte b = data[j];
                typeAndValue.append(Character.forDigit(0xF & (b >>> 4), 16));
                typeAndValue.append(Character.forDigit(0xF & b, 16));
            }
        } else {
            /*
             * 2.4 (cont): Otherwise, if the AttributeValue is of a type which
             * has a string representation, the value is converted first to a
             * UTF-8 string according to its syntax specification.
             *
             * NOTE: this implementation only emits DirectoryStrings of the
             * types returned by isDerString().
             */
            String valStr = null;
            try {
                valStr = new String(value.getDataBytes(), "UTF8");
            } catch (IOException ie) {
                throw new IllegalArgumentException("DER Value conversion");
            }

            /*
             * 2.4 (cont): If the UTF-8 string does not have any of the
             * following characters which need escaping, then that string can be
             * used as the string representation of the value.
             *
             *   o   a space or "#" character occurring at the beginning of the
             *       string
             *   o   a space character occurring at the end of the string
             *
             *   o   one of the characters ",", "+", """, "\", "<", ">" or ";"
             *
             * If a character to be escaped is one of the list shown above, then
             * it is prefixed by a backslash ('\' ASCII 92).
             *
             * Otherwise the character to be escaped is replaced by a backslash
             * and two hex digits, which form a single byte in the code of the
             * character.
             */
            final String escapees = ",+<>;\"\\";
            StringBuilder sbuffer = new StringBuilder();
            boolean previousWhite = false;

            for (int i = 0; i < valStr.length(); i++) {
                char c = valStr.charAt(i);

                if (DerValue.isPrintableStringChar(c) ||
                    escapees.indexOf(c) >= 0 ||
                    (i == 0 && c == '#')) {

                    // escape leading '#' and escapees
                    if ((i == 0 && c == '#') || escapees.indexOf(c) >= 0) {
                        sbuffer.append('\\');
                    }

                    // convert multiple whitespace to single whitespace
                    if (!Character.isWhitespace(c)) {
                        previousWhite = false;
                        sbuffer.append(c);
                    } else {
                        if (previousWhite == false) {
                            // add single whitespace
                            previousWhite = true;
                            sbuffer.append(c);
                        } else {
                            // ignore subsequent consecutive whitespace
                            continue;
                        }
                    }

                } else {

                    // append non-printable/non-escaped char

                    previousWhite = false;
                    sbuffer.append(c);
                }
            }

            // remove leading and trailing whitespace from value
            typeAndValue.append(sbuffer.toString().trim());
        }

        String canon = typeAndValue.toString();
        canon = canon.toUpperCase(Locale.US).toLowerCase(Locale.US);
        return Normalizer.normalize(canon, Normalizer.Form.NFKD);
    }

    /*
     * Return true if DerValue can be represented as a String.
     */
    private static boolean isDerString(DerValue value, boolean canonical) {
        if (canonical) {
            switch (value.tag) {
                case DerValue.tag_PrintableString:
                case DerValue.tag_UTF8String:
                    return true;
                default:
                    return false;
            }
        } else {
            switch (value.tag) {
                case DerValue.tag_PrintableString:
                case DerValue.tag_T61String:
                case DerValue.tag_IA5String:
                case DerValue.tag_GeneralString:
                case DerValue.tag_BMPString:
                case DerValue.tag_UTF8String:
                    return true;
                default:
                    return false;
            }
        }
    }

    boolean hasRFC2253Keyword() {
        return AVAKeyword.hasKeyword(oid, RFC2253);
    }

    private String toKeywordValueString(String keyword) {
        /*
         * Construct the value with as little copying and garbage
         * production as practical.  First the keyword (mandatory),
         * then the equals sign, finally the value.
         */
        StringBuilder   retval = new StringBuilder(40);

        retval.append(keyword);
        retval.append("=");

        try {
            String valStr = value.getAsString();

            if (valStr == null) {

                // rfc1779 specifies that attribute values associated
                // with non-standard keyword attributes may be represented
                // using the hex format below.  This will be used only
                // when the value is not a string type

                byte    data [] = value.toByteArray();

                retval.append('#');
                for (int i = 0; i < data.length; i++) {
                    retval.append(hexDigits.charAt((data [i] >> 4) & 0x0f));
                    retval.append(hexDigits.charAt(data [i] & 0x0f));
                }

            } else {

                boolean quoteNeeded = false;
                StringBuilder sbuffer = new StringBuilder();
                boolean previousWhite = false;
                final String escapees = ",+=\n<>#;\\\"";

                /*
                 * Special characters (e.g. AVA list separators) cause strings
                 * to need quoting, or at least escaping.  So do leading or
                 * trailing spaces, and multiple internal spaces.
                 */
                for (int i = 0; i < valStr.length(); i++) {
                    char c = valStr.charAt(i);
                    if (DerValue.isPrintableStringChar(c) ||
                        escapees.indexOf(c) >= 0) {

                        // quote if leading whitespace or special chars
                        if (!quoteNeeded &&
                            ((i == 0 && (c == ' ' || c == '\n')) ||
                                escapees.indexOf(c) >= 0)) {
                            quoteNeeded = true;
                        }

                        // quote if multiple internal whitespace
                        if (!(c == ' ' || c == '\n')) {
                            // escape '"' and '\'
                            if (c == '"' || c == '\\') {
                                sbuffer.append('\\');
                            }
                            previousWhite = false;
                        } else {
                            if (!quoteNeeded && previousWhite) {
                                quoteNeeded = true;
                            }
                            previousWhite = true;
                        }

                        sbuffer.append(c);

                    } else {

                        // append non-printable/non-escaped char

                        previousWhite = false;
                        sbuffer.append(c);
                    }
                }

                // quote if trailing whitespace
                if (sbuffer.length() > 0) {
                    char trailChar = sbuffer.charAt(sbuffer.length() - 1);
                    if (trailChar == ' ' || trailChar == '\n') {
                        quoteNeeded = true;
                    }
                }

                // Emit the string ... quote it if needed
                if (quoteNeeded) {
                    retval.append("\"" + sbuffer.toString() + "\"");
                } else {
                    retval.append(sbuffer.toString());
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("DER Value conversion");
        }

        return retval.toString();
    }

}
