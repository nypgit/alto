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
 * Throw this exception from anywhere to cause the server to respond
 * with "Request Entity Too Large" rather than "Error".
 * 
 * @author jdp
 * @since 1.5
 */
public class RequestEntityTooLargeException
    extends Error
{

    public RequestEntityTooLargeException(){
        super();
    }
    public RequestEntityTooLargeException(String msg){
        super(msg);
    }
    public RequestEntityTooLargeException(java.lang.Throwable cause, String msg){
        super(msg,cause);
    }

}