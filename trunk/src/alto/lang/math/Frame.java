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
 * <p> An average over a fixed number of samples. </p>
 * 
 * @see Window
 * @author jdp
 * @since 1.6
 */
public class Frame
    extends Average
{
    public final static double DefaultSamples = 40.0;
    public final static double DefaultValue   = 0.0;


    private final double frame;
    private final double unit;


    public Frame(){
        this(DefaultValue);
    }
    public Frame(double value){
        this(DefaultSamples,value);
    }
    public Frame(double number, double value){
        super(value);
        double frame = (number - 1.0);
        if (1.0 < frame){
            /*
             * for example, for 'number' is 100, 'frame' is 99, and
             * this.frame = 99/100, and
             * this.unit = 1.0 - 0.99 = 0.01.
             */
            this.frame = (frame / number);
            this.unit = (1.0 - this.frame);
        }
        else
            throw new IllegalArgumentException("Bad number '"+number+"' must be greater than one.");
    }


    public synchronized Average add(double value){
        this.value = ((this.value * this.frame) + (value * this.unit));
        return this;
    }

}
