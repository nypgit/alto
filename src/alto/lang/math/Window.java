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
 * <p> A periodic average frequency.  </p>
 * 
 * <h3>Usage</h3>
 * 
 * <p> The window should be used over multiple frames for best effect,
 * for count average greater than one.  The product of window frame
 * size and closed frame count would be the effective frame size.
 * </p>
 * 
 * <p> For an effective window frame size of fifteen minutes, require
 * at least three closed frames over a window size of five minutes.
 * </p>
 * 
 * <h3>Operation</h3>
 * 
 * <p> Each call to "mark" updates the window.  If mark is outside the
 * current frame, that frame is closed and a new windoe frame is
 * opened for the call to "mark".  </p>
 * 
 * <p> When a window frame is closed, the average number of marks per
 * frame is updated.  </p>
 * 
 * @author jdp
 */
public class Window
    extends Statistic
{
    public final static long Seconds = 1000;
    public final static long Minutes = (60*Seconds);
    /**
     * Default window size is fifteen minutes.
     */
    public final static long Default = (5*Minutes);


    /**
     * Size of frame
     */
    public final long frameSize;
    /**
     * Frame coordinate
     */
    private long frameEnd;
    /**
     * Number of marks in frame
     */
    private int frameCount;
    /**
     * Average number of marks over closed frames
     */
    private float average;
    /**
     */
    private double averageNumerator, averageDenominator;


    public Window(long start, long size){
        super();
        this.frameSize = size;
        this.frameEnd = (start+size);
    }
    public Window(long size){
        this(java.lang.System.currentTimeMillis(),size);
    }
    public Window(){
        this(java.lang.System.currentTimeMillis(),Default);
    }


    public final int intValue(){
        return (int)this.average;
    }
    public final long longValue(){
        return (long)this.average;
    }
    public final float floatValue(){
        return this.average;
    }
    public final double doubleValue(){
        return this.average;
    }
    public final boolean isAverage(){
        return true;
    }
    public final boolean isScalar(){
        return false;
    }
    /**
     * Average number of marks per closed window frame
     */
    public final float getAverage(){
        return this.average;
    }
    /**
     * Number of closed frames in average
     */
    public final int countAverage(){
        return (int)this.averageDenominator;
    }
    /**
     * @return Whether the number of closed frames in the average
     * number of marks per window is greater than the argument
     */
    public final boolean isCountAverageGreaterThan(int num){
        return (num < this.countAverage());
    }
    public final boolean isMarkInFrame(){
        return this.isMarkInFrame(java.lang.System.currentTimeMillis());
    }
    public final boolean isMarkInFrame(long time){
        return (time <= this.frameEnd);
    }
    public final void mark(){
        this.mark(java.lang.System.currentTimeMillis());
    }
    /**
     * Synchronized to flush MP data caches on method return.
     */
    public synchronized final void mark(long time){
        if (time > this.frameEnd)
            this.close(time);

        this.frameCount += 1;
    }
    private void close(long time){
        float average = this.average;
        while (time > this.frameEnd){
            this.frameEnd += this.frameSize;
            this.averageNumerator += this.frameCount;
            this.frameCount = 0;
            this.averageDenominator += 1;
            average = (float) (this.averageNumerator / this.averageDenominator);
        }
        this.average = average;
    }
    public int hashCode(){
        return (int)java.lang.Double.doubleToLongBits(this.average);
    }
    public java.lang.String toString(){
        return Format(this.average);
    }
}
