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
package alto.io.u.hasharray;


/**
 * <p> Index as key container. </p>
 * 
 * <p> Index subclasses implement this interface, a strategy to
 * maximize code reuse.  In the associated (method definition)
 * cases, the same tricky little (code block) patterns get
 * replicated too many times. </p>
 */
public interface Keys {
    /**
     * 
     */
    public interface Object
        extends Keys
    {
        public java.lang.Object key(int idx);
    }
    /**
     * 
     */
    public interface Long
        extends Keys
    {
        public long key(int idx);
    }

    public java.lang.Class getKeyType();
}
