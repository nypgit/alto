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

/**
 * Produce and consume <code>application/x-www-form-urlencoded</code>
 * content via a query parameters map.
 */
public interface HttpBodyUrlEncoded
    extends alto.io.Uri,
            alto.sys.IO.Edge,
            alto.sys.IO.Source.Parameters.H,
            alto.sys.Destroy
{

    public boolean isRequestClient();

    public boolean isRequestServer();

    public void setParameter(java.lang.String key, java.lang.String value);

    public void setParameterHex(java.lang.String key, java.math.BigInteger value);

    public java.lang.String[] getQueryKeysSorted();

    public java.lang.String toString();

    public java.lang.StringBuilder toStringBuilder();

}
