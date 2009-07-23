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
package alto.io.u ;

/**
 * Wrapper for {@link Bbuf} exporting standard output and data APIs,
 * plus others from {@link Bbo}.
 * 
 * @author John Pritchard (john@syntelos.org)
 * 
 * @see Bbuf
 * @see Bbid
 * @since 1.1
 */
public class Bbod 
    extends Bbo
    implements java.io.DataOutput
{

    public Bbod( int capacity){
        super(capacity);
    }
    public Bbod(){
        super();
    }
    public Bbod( Bbuf buf){
        super(buf);
    }


    public final void writeBoolean(boolean value) throws java.io.IOException {
        int ch = (value)?(1):(0);
        this.write(ch);
    }

    public final void writeByte(int value) throws java.io.IOException {
        this.write(value);
    }

    public final void writeShort(int value) throws java.io.IOException {
        Bbuf store = this.getByteBuffer();
        store.write2(value);
    }

    public final void writeChar(int value) throws java.io.IOException {
        Bbuf store = this.getByteBuffer();
        store.write2(value);
    }

    public final void writeInt(int value) throws java.io.IOException {
        Bbuf store = this.getByteBuffer();
        store.write4(value);
    }

    public final void writeLong(long value) throws java.io.IOException {
        Bbuf store = this.getByteBuffer();
        store.write8(value);
    }

    public final void writeFloat(float value) throws java.io.IOException {
        this.writeInt(java.lang.Float.floatToIntBits(value));
    }

    public final void writeDouble(double value) throws java.io.IOException {
        this.writeLong(java.lang.Double.doubleToLongBits(value));
    }

    public final void writeBytes(String str) throws java.io.IOException {
        char[] cary = str.toCharArray();
        int cary_len = (null != cary)?(cary.length):(0);
        byte ch;
        for (int cc = 0 ; cc < cary_len ; cc++) {
            ch = (byte)cary[cc];
            this.write(ch);
        }
    }

    public final void writeChars(String str) throws java.io.IOException {
        char[] cary = str.toCharArray();
        int cary_len = (null != cary)?(cary.length):(0), ch;
        for (int cc = 0 ; cc < cary_len ; cc++) {
            ch = cary[cc];
            this.write( (ch >>> 8) & 0xff);
            this.write( ch & 0xff);
        }
    }

    public final void writeUTF(String str) throws java.io.IOException {
        byte[] utf = Utf8.encode(str);
        int utf_len = (null != utf)?(utf.length):(0);
        this.writeShort(utf_len);
        this.write(utf);
    }

}
