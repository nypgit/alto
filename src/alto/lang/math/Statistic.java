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
 * <p> The value of a statistic, as for monitoring live quality of
 * service.  </p>
 * 
 * <h3>Hash Code</h3>
 * 
 * <p> Subclasses are mutable objects with dynamic hash codes.  Their
 * subclasses should override the hash code and equals methods if they
 * may be used as hash table keys. </p>
 * 
 * @author jdp
 * @since 1.6
 */
public abstract class Statistic
    extends java.lang.Number
    implements java.lang.Comparable<java.lang.Number>
{

    public final java.lang.String Format(double value){
        java.lang.String re = java.lang.String.valueOf(value);
        int idx = re.indexOf('.');
        if (1 > idx)
            return re;
        else {
            int trunc = (idx + 4);
            if (trunc < re.length())
                return re.substring(0,trunc);
            else
                return re;
        }
    }



    /**
     */
    protected Statistic(){
        super();
    }


    public abstract boolean isAverage();

    public abstract boolean isScalar();

    public abstract java.lang.String toString();

    public final int compareTo(java.lang.Number that){
        if (null == that)
            return 1;
        else {
            double thisValue = this.doubleValue();
            double thatValue = that.doubleValue();
            if (thisValue < thatValue)
                return -1;
            else if (thisValue == thatValue)
                return 0;
            else
                return 1;
        }
    }
    public int hashCode(){
        return this.intValue();
    }
    public final boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano instanceof java.lang.Number){
            java.lang.Number that = (java.lang.Number)ano;
            return (this.doubleValue() == that.doubleValue());
        }
        else
            return this.toString().equals(ano.toString());
    }
}
