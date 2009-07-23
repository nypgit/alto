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
 * <p> Number of samples per second. </p>
 */
public class Hertz
    extends Scalar
{

    private long second;
    private int counter;
    private int max;


    public Hertz(){
        super(0L);
    }
    public Hertz(long value){
        super(value);
    }


    public int getMax(){
        return this.max;
    }
    public Scalar add(long value){
        long second = this.second;
        long input = (value/1000);
        if (input == second){
            synchronized(this){
                this.counter += 1;
            }
        }
        else if (input > second){
            int count = this.counter;
            this.max = Math.max(count,this.max);
            synchronized(this){
                this.value = count;
                this.second = input;
                this.counter = 1;
            }
        }
        return this;
    }
}
