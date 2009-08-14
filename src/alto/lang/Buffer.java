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
 * <p> Interface for anything containing a singular byte array.  A
 * buffer returned on this interface is not copied, if at all
 * possible.  This permits may accesses to the {@link #getBuffer()}
 * method without the cost of copying. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public interface Buffer {

    public final static byte[] Empty = new byte[0];

    /**
     * An immutable array.  Only copy as necessary for immutablility.
     * @return Null for length zero.
     */
    public byte[] getBuffer();

    public int getBufferLength();
    /**
     * Interface compatible with javax tools file object.  Convert
     * buffer to string via UTF8.
     */
    public CharSequence getCharContent(boolean igEncErr) throws java.io.IOException;
}
