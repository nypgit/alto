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
package alto.io;

import alto.io.u.Objmap;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The set of static system roles.  These names are UIDs created
 * during the system initialization process and reserved for the
 * purposes represented in this class.
 * 
 * @see alto.sys.Capabilities
 */
public enum Role {

    SYSTEM("system"),
    NETWORK("network"),
    APPLICATION("application"),

    ROLE("role"),
    ROLE_READ("role-read"),
    ROLE_WRITE("role-write"),

    USER("user"),
    USER_READ("user-read"),
    USER_WRITE("user-write"),

    FILE("file"),
    FILE_READ("file-read"),
    FILE_WRITE("file-write");


    public final static Set<Role> All = Collections.unmodifiableSet(EnumSet.allOf(Role.class));
    /**
     * @see alto.sys.Capabilities$RoleRead
     */
    public final static Set<Role> CanRead = 
        Collections.unmodifiableSet(EnumSet.of(SYSTEM,APPLICATION,ROLE,ROLE_READ));
    /**
     * @see alto.sys.Capabilities$RoleWrite
     */
    public final static Set<Role> CanWrite = 
        Collections.unmodifiableSet(EnumSet.of(SYSTEM,APPLICATION,ROLE,ROLE_WRITE));

    public final static Set<Role> UserCreate = 
        Collections.unmodifiableSet(EnumSet.of(SYSTEM,APPLICATION,USER,USER_WRITE));

    public final static Set<Role> UserKeys = 
        Collections.unmodifiableSet(EnumSet.of(SYSTEM,APPLICATION));

    public final static Set<Role> FileWrite = 
        Collections.unmodifiableSet(EnumSet.of(SYSTEM,APPLICATION,FILE,FILE_WRITE));

    private final static Objmap Index = new Objmap();
    static {
        Iterator<Role> iterator = All.iterator();
        while (iterator.hasNext()){
            Role item = iterator.next();
            Index.put(item.toString(),item);
        }
    }
    /**
     * @see alto.sec.Role
     */
    public final static Role Lookup(java.lang.String name){
        return (Role)Index.get(name);
    }
    public final static boolean IsName(java.lang.String name){
        return (Index.containsKey(name));
    }
    public final static boolean IsNotName(java.lang.String name){
        return (!Index.containsKey(name));
    }


    private Role(java.lang.String name){
    }

    public java.lang.String getUID(){
        return this.toString();
    }
    public Principal.Actual dereference()
        throws java.io.IOException
    {
        return alto.sec.Keys.Tools.Dereference(this.getUID());
    }
}
