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
 * Mark and continue tracing buffer.  Mark is initialized to the first
 * byte written.  Call mark to retrieve bytes written and reset for
 * next segment.  Call reset to discard buffer contents.
 * 
 * @author jdp
 * @since 1.5
 */
public final class Trace 
    extends java.io.ByteArrayOutputStream
    implements Buffer
{

    protected int mark = 0;


    public Trace(){
        super();
    }


    public synchronized byte[] mark(){
        int start = this.mark;
        int end = this.count;
        this.mark = end;
        int count = (end-start);
        if (0 < count){
            byte[] re = new byte[count];
            System.arraycopy(this.buf,start,re,0,count);
            return re;
        }
        else
            return null;
    }
    public synchronized void reset(){
        this.mark = 0;
        this.count = 0;
    }
    public int length(){
        return this.count;
    }
    public byte[] getBuffer(){
        if (0 < this.count)
            return super.toByteArray();
        else
            return null;
    }
    public int getBufferLength(){
        return this.count;
    }
}
