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


/**
 * <p> Support for cipher text: a payload or content block    </p>
 *
 * <p> A user can attach a transcoder with his key. </p>
 * 
 * @author jdp
 * @since 1.2
 */
public interface Private {

    /**
     * Read plain text with cipher (a null cipher is reading code text. 
     */
    public interface Input {

        /**
         * @return An input stream for reading plain text.
         */
        public java.io.InputStream getPrivateInput();

    }

    /**
     * Write code text with cipher (a null cipher is writing plain text. 
     */
    public interface Output {

        /**
         * @return An output stream for writing plain text.
         */
        public java.io.OutputStream getPrivateOutput();

    }

    /**
     * 
     */
    public interface Cipher {

            /**
             * Install a transcoder into the plain text I/O 
             * chain.
             */
        public Private setPrivateCipher(Cipher cipher);
    }


    /**
     * Cipher text coding.
     */
    public interface Coder {

        /**
         * Base64 coding.
         */
        public interface B64 
            extends Coder
        {

            /**
             * Install a base 64 coder into the plain text I/O chain.
             */
            public Private setPrivateCoderB64();
        }
    }

}
