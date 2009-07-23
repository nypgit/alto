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
package alto.lang;

import alto.io.Uri;
import alto.sys.FileManager;
import alto.sys.IO;
import alto.sys.Reference;

/**
 * An instance defines GET, PUT and DELETE operators to work on
 * fragments of a class of content objects defined by mimetype.  
 * 
 * <h3>Constructor</h3>
 * 
 * Each subclass has a public constructor taking one parameter, {@link
 * Type}.
 * 
 * <h3>MT Safe</h3>
 * 
 * Each subclass is stateless, an instance is shared by all threads.
 * 
 * <h3>Semantics</h3>
 * 
 * For each operator, the implementor will list a {@link
 * alto.io.u.uri} or fetch a {@link alto.sys.Reference}
 * to work on referenced fragments (as defined per content type).
 * 
 * 
 * @author jdp
 */
public interface Fio {

    public Type getFioType();

    /**
     * Write the first identified fragment to the response
     */
    public boolean fioGet(FileManager fm, IO.Source.Fio request, IO.Target response)
        throws java.io.IOException;
    /**
     * Replace all identified fragments with the request content
     */
    public boolean fioPut(FileManager fm, IO.Source.Fio request, IO.Target response)
        throws java.io.IOException;
    /**
     * Delete all identified fragments 
     */
    public boolean fioDelete(FileManager fm, IO.Source.Fio request, IO.Target response)
        throws java.io.IOException;

}
