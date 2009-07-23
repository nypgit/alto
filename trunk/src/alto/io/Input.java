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
 * java.io.InputStream}.
 * 
 * @author jdp
 */
@MemberOf(java.io.InputStream.class)
public interface Input {

    public interface Http 
        extends Input
    {
        public void startMessage();
        public void startHeaders();
        public void endHeaders();
        public void endMessage();
    }

    public int read()
        throws java.io.IOException;

    public int read(byte[] buf, int ofs, int len)
        throws java.io.IOException;

    public byte[] readMany(int many)
        throws java.io.IOException;

    public int copyTo(Output out)
        throws java.io.IOException;

    public java.lang.String readLine()
        throws java.io.IOException;

    public void close()
        throws java.io.IOException;

    public long skip(long n) 
        throws java.io.IOException;

    public int available() 
        throws java.io.IOException;

    public void mark(int readlimit);

    public void reset() 
        throws java.io.IOException;

    public boolean markSupported();

}
