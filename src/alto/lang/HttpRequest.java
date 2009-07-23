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
package alto.lang;

import alto.io.Uri;

/**
 * <p> Subclassed for client and server. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public interface HttpRequest
    extends HttpMessage,
            alto.sys.IO.Source.Parameters.Headers
{
    public final static java.lang.String STDIN_STR = "request:in";
    public final static java.lang.String STDOUT_STR = "request:out";


    public java.lang.String getHostName();

    public boolean isRequestClient();

    public boolean isRequestServer();

    public java.lang.String getPath();

    public boolean hasReference();

    public boolean hasNotReference();

    public alto.sys.Reference getReference();

    /**
     * @return Public network URL, e.g., "http".
     */
    public java.lang.String getRequestLocation();

    public void setLocation();

    public HttpBodyUrlEncoded getCreateHttpBodyUrlEncoded();

    public HttpBodyUrlEncoded getHttpBodyUrlEncoded();

    public java.lang.String[] getParameterList(java.lang.String key, java.lang.String seps);

    public alto.io.u.Objset getParameterSet(java.lang.String key, java.lang.String seps);

    public alto.io.u.Objmap getParameterMap(java.lang.String key, java.lang.String seps, char infix);

    public boolean isPersistentRequest();

    /**
     * @return May be null for SSL Peer Unverified 
     */
    public java.security.Principal getSecureRemote();

}
