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

import alto.io.Code;
import alto.io.Check;
import alto.io.Output;
import alto.io.u.Objmap;
import alto.sys.IO;
import alto.sys.Reference;

import java.net.URL;

/**
 * <p> Collection of name, value pairs used in meta data files and
 * messages, and HTTP headers.  </p>
 * 
 * <p> A special implementation of the XML I/O context intended for
 * reuse in other contexts or document I/O under HTTP.  </p>
 * 
 * 
 * @author jdp
 * @since 1.5
 */
public interface Headers
    extends alto.sys.Xml,
            alto.sys.IO.Headers
{

    public void writeHeaders(alto.io.Output out)
        throws java.io.IOException;

    public void readHeaders(java.io.File file)
        throws java.io.IOException;

    public void readHeaders(alto.io.Input in)
        throws java.io.IOException;

    public void clearHeaders();

    public int countHeaders();

    public Header getHeader(int idx);

    public java.lang.String getHeaderName(int idx);

    public java.lang.String getHeaderValue(int idx);

    public Header[] listHeaders(java.lang.String name);

    public boolean isList(java.lang.String name);

    public Header getHeader(java.lang.String name);

    public java.lang.String validateHeaderString(java.lang.String name);

    public java.lang.String getHeaderAsString(java.lang.String name);


    public Type getHeaderType(java.lang.String name);


    public boolean hasHeader(Header header);

    public boolean hasNotHeader(java.lang.String name);

    public boolean hasNotHeader(Header header);

    public boolean hasHeader(java.lang.String name, java.lang.String value);


    public void setHeader(Header header);

    public void addHeader(java.lang.String name, java.lang.String value);

    public void addHeader(Header header);

    public void removeHeader(java.lang.String name);

    public void removeHeader(Header header);

}
