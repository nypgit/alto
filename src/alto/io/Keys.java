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
package alto.io;

import alto.hash.SHA1;
import alto.sec.x509.X500Name;
import alto.sec.x509.X509Certificate;

import java.security.PublicKey;
import java.security.KeyPair;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * 
 * @see alto.io.Principal$Authentic
 * @see alto.sec.Keys
 */
public interface Keys {

    public final static java.lang.String SystemPathTo = "/s/system.keys";

    public String getUID();

    public Principal.Authentic getPrincipal();

    public KeyPair getKeyPair(String alg);

    public alto.sec.Auth getAuth(Authentication type);

    public boolean addRole(Keys role);

    public boolean inRole(Keys role);

    public boolean inRole(java.lang.String role)
        throws java.io.IOException;

    public X509Certificate getCertificate(String alg);

    public byte[] signSHA1WithRSAO(byte[] msg);

    public byte[] signWithRSAO(SHA1 sha);

    public byte[] signWithRSA(SHA1 sha);

}

