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
 * <p> A single recorded value. </p>
 * 
 * @author jdp
 */
public class Scalar
    extends Statistic 
{
    protected long value;


    public Scalar(){
        super();
    }
    public Scalar(long value){
        super();
        this.value = value;
    }


    public Scalar add(long value){
        this.value = value;
        return this;
    }
    public final boolean isAverage(){
        return false;
    }
    public final boolean isScalar(){
        return true;
    }
    public final long getValue(){
        return this.value;
    }
    public int intValue(){
        return (int)this.value;
    }
    public long longValue(){
        return this.value;
    }
    public float floatValue(){
        return (float)this.value;
    }
    public double doubleValue(){
        return (double)this.value;
    }
    public int hashCode(){
        return (int)this.value;
    }
    public java.lang.String toString(){
        return java.lang.String.valueOf(this.value);
    }
}
