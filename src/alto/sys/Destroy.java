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
package alto.sys;

/**
 * <p> The {@link #destroy()} method readies an instance object for
 * garbage collection.  
 * </p>
 * 
 * <h3>Responsibility</h3>
 * 
 * <p> After a call to destroy, the instance is ready for garbage
 * collection.  Cyclic references are freed by assigning null to
 * instance fields.  </p>
 * 
 * <h3>Reinitialization</p>
 * 
 * <p> Implementors may support some variation of reinitialization
 * whereby an instance may be reusable after the {@link #destroy()}
 * method has been called.  Individual class documentation will have
 * notes related to {@link #destroy()} and any (or not) initialization
 * or reinitialization that may be supported there.  </p>
 * 
 * @see alive
 * @author jdp
 * @since 1.1
 */
public interface Destroy {

    /**
     * <p> Never throws an exception.  But often doesn't need to catch
     * any exceptions in its definition because other destroy methods
     * are equally reliable. </p>
     */
    public void destroy();
}
