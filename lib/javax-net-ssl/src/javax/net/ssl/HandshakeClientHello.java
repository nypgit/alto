/*
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
 */

package javax.net.ssl;

import javax.net.ssl.SSLSocket;

import java.security.Principal;

/**
 * Server side handshake extensions.  See RFC 3546.
 *
 * @see X509ServerKeyManager
 * @since 1.6
 * @author John D Pritchard
 */
public interface HandshakeClientHello {

    /**
     * Support for 'server_name'.
     */
    public interface ServerName 
        extends HandshakeClientHello
    {

        public String getHelloServerName();
    }

}
