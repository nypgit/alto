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
 * <p> An average of samples. </p>
 * 
 * <p> A continuous average of double long floating point values adds
 * the existing average to the most recent value and divides by two.
 * The first value becomes the current average in constructing the
 * statistic. </p>
 * 
 * @see Statistic$Number
 * @see Statistic$Period
 * @author jdp
 */
public class Average 
    extends Statistic 
{

    protected double value;

    public Average(double value){
        super();
        this.value = value;
    }


    public Average add(double value){
        double sum = (this.value + value);
        this.value = (sum / 2.0);
        return this;
    }
    public final boolean isAverage(){
        return true;
    }
    public final boolean isScalar(){
        return false;
    }
    public final double getValue(){
        return this.value;
    }
    public int intValue(){
        return (int)this.value;
    }
    public long longValue(){
        return (long)this.value;
    }
    public float floatValue(){
        return (float)this.value;
    }
    public double doubleValue(){
        return this.value;
    }
    public int hashCode(){
        return (int)java.lang.Double.doubleToLongBits(this.value);
    }
    public java.lang.String toString(){
        return Format(this.value);
    }
}
