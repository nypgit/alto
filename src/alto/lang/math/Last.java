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
package alto.lang.math;

/**
 * <p> A single recorded value, always the most recent. </p>
 * 
 * <p> The last time statistic maintains a most recent value as a long
 * integer date time in milliseconds since 000h, January 1, 1970. </p>
 */
public class Last
    extends Scalar
{

    public Last(long value){
        super(value);
    }

    public Scalar add(long value){
        this.value = value;
        return this;
    }
    public java.lang.String toString(){
        return alto.lang.Date.ToString(this.value);
    }

}
