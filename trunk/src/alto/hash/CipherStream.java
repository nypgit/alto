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
package alto.hash;

import java.io.* ;

/**
 * Interface to symmetric stream ciphers.
 * 
 * @author John Pritchard 
 * @since 1.2
 */
public interface CipherStream extends Cipher {

    public void reset( byte[] key);

    /**
     * Encrypt plaintext input.
     * 
     * @return Number of bytes (read in, or written out).
     */
    public int encipher( InputStream in, OutputStream out) throws IOException ;

    /**
     * Decrypt ciphertext input.
     * 
     * @return Number of bytes (read in, or written out).
     */
    public int decipher( InputStream in, OutputStream out) throws IOException ;
}
