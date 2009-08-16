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
package alto.lang.component;

import alto.lang.Component;
import alto.hash.Function;

/**
 * 
 */
public final class Version
    extends alto.lang.component.Named
    implements Component.Version
{
    private long numeric;


    public Version(byte[] bits){
        super(Function.Xor.Instance,bits);
    }
    public Version(long numeric){
        super(Function.Xor.Instance,String.valueOf(numeric));
        this.numeric = numeric;
    }
    public Version(java.lang.String string){
        super(Function.Xor.Instance,string);
    }
    public Version(Function H, java.lang.String string){
        super(H,string);
    }


    public int getPosition(){
        return Position;
    }
    public long numericValue(){
        long numeric = this.numeric;
        if (0 != numeric)
            return numeric;
        else {
            try {
                numeric = Long.parseLong(this.string);
                this.numeric = numeric;
                return numeric;
            }
            catch (NumberFormatException exc){
                return 0L;
            }
        }
    }
    public Component.Version incrementValue(){
        return new alto.lang.component.Version(this.numericValue()+1);
    }
}
