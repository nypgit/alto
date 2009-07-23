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

/**
 * Optional extension of the X509KeyManager for server side handshake
 * extensions.
 *
 * @since 1.6
 * @author John D Pritchard
 */
public interface X509ServerKeyManager
    extends X509KeyManager 
{

    public String chooseServerAlias(String keyType, SSLSocket socket, HandshakeClientHello hello);

    public String chooseServerAlias(String keyType, SSLEngine engine, HandshakeClientHello hello);

}
