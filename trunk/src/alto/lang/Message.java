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

import alto.sys.Reference;

/**
 * An I/O buffer with a reference.
 * 
 * @author jdp
 * @since 1.6
 */
public interface Message
    extends alto.io.Message,
            alto.sys.IO.Edge, 
            alto.io.Output
{

    public void init();

    public Reference getReference();

    public java.lang.String getReferenceString();

    public void formatMessage()
        throws java.io.IOException;

    /**
     * @return Message reference string
     */
    public java.lang.String toString();
    /**
     * @return Message reference string hashCode
     */
    public int hashCode();
    /**
     * @return Object toString equivalence (on reference). 
     */
    public boolean equals(Object ano);

}
