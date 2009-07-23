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
package alto.io.u;

/**
 * <h3> Frame relations</h3>
 * 
 * <p> Values having {@link obfmap} relations need to implement this
 * interface. </p>
 * 
 * <h4>Cloneing</h4>
 * 
 * <p> A frame is not {@link java.lang.Cloneable} because it is not
 * cloned by its users.  Under cloning the frame relation holds, the
 * parent is known by an identical reference.  </p>
 * 
 * @author jdp
 * @since 1.2
 */
public interface Frame {
    /**
     * @return Target of frame relation
     */
    public Frame frameParentGet();

    public boolean frameStale();
}
