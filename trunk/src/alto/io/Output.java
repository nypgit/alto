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
package alto.io;

/**
 * Implementors must be members of the class {@link
 * java.io.OutputStream}.
 * 
 * @author jdp
 */
@MemberOf(java.io.OutputStream.class)
public interface Output {

    public interface Http 
        extends Output
    {
        public void startMessage();
        public void startHeaders();
        public void endHeaders();
        public void endMessage();
    }

    public void write(int uint8)
        throws java.io.IOException;

    public void write(byte[] buf, int ofs, int len)
        throws java.io.IOException;

    public void print(char ch)
        throws java.io.IOException;
    
    public void print(java.lang.String string)
        throws java.io.IOException;

    public void println()
        throws java.io.IOException;

    public void println(char ch)
        throws java.io.IOException;

    public void println(java.lang.String string)
        throws java.io.IOException;

    public void flush()
        throws java.io.IOException;

    public void close()
        throws java.io.IOException;

}
