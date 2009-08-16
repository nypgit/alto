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

import alto.hash.Function;

/**
 * 
 */
public final class Relation
    extends alto.lang.component.Named
    implements alto.lang.Component.Relation
{

    public Relation(byte[] bits){
        super(Function.Xor.Instance,bits);
    }
    public Relation(java.lang.String string){
        super(Function.Xor.Instance,string);
    }
    public Relation(Function H, java.lang.String string){
        super(H,string);
    }


    public int getPosition(){
        return Position;
    }
}
