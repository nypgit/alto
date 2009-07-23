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
package alto.hash ;

/**
 * <p> The user of this interface can define any key. </p>
 * 
 * @author jdp
 * @since 1.2
 */
public interface Cipher {
    /**
     * Set the key
     */
    public void reset( byte[] key)
        throws java.security.NoSuchAlgorithmException;

    /**
     * Input block size in bytes.  A cipher with no particular block
     * length (a stream cipher) returns a value less than one
     * indicating that it has no particular block size.
     */
    public int blockLengthPlain();

    /**
     * Output block size in bytes.  A cipher with no particular block
     * length (a stream cipher) returns a value less than one
     * indicating that it has no particular block size.
     */
    public int blockLengthCipher();

    /**
     * If the argument is an acceptable plaintext block length, return
     * the corresponding encrypted block length.  Otherwise throw an
     * illegal argument exception.
     *
     * @param blklen Block length in bytes.
     */
    public int encipherOutputLength( int blklen);

    /**
     * If the argument is an acceptable ciphertext block length,
     * return the corresponding decrypted block length.  Otherwise
     * throw an illegal argument exception.
     *
     * @param blklen Block length in bytes.
     */
    public int decipherOutputLength( int blklen);

    /**
     * Encrypt a plaintext.  A block cipher needs to be called with
     * only only block, a stream cipher can be called with a whole
     * plaintext.
     *
     * @param input Source buffer
     *
     * @param inofs Offset into input buffer
     *
     * @param inlen Number of bytes in the input buffer to work on
     * (must be the plaintext block length).
     *
     * @param output Destination buffer
     *
     * @param outofs Offset into the output buffer for the result.
     * Output will be the ciphertext block length.
     *
     * @returns Number of bytes written to the output buffer.
     */
    public int encipher ( byte[] input, int inofs, int inlen, byte[] output, int outofs);

    /**
     * Decrypt a ciphertext.  A block cipher needs to be called with
     * only only block, a stream cipher can be called with a whole
     * plaintext.
     *
     * @param input Source buffer
     *
     * @param inofs Offset into input buffer
     *
     * @param inlen Number of bytes in the input buffer to work on
     * (must be the ciphertext block length).
     *
     * @param output Destination buffer
     *
     * @param outofs Offset into the output buffer for the result.
     * Output will be the plain text block length.
     *
     * @returns Number of bytes written to the output buffer.
     */
    public int decipher ( byte[] input, int inofs, int inlen, byte[] output, int outofs);

}
