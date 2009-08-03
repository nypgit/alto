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

import alto.lang.Address;

/**
 * A storage cache fs root directory.  Used by the {@link File}
 * constructor to resolve an {@link Address} to a cache fs location.
 * 
 * @author jdp
 * @since 1.6
 */
public final class Partition
    extends java.lang.Object
{

    private final java.io.File dir;

    private final String path, uri;

    private long last, total, free;


    public Partition(java.io.File dir){
        super();
        long test = dir.getTotalSpace();
        while (1L > test){
            dir = dir.getParentFile();
            if (null == dir)
                throw new alto.sys.Error.State("Unable to find partition for directory.");
            else
                test = dir.getTotalSpace();
        }
        this.dir = dir;
        this.path = dir.getPath();
        this.uri = "partition:"+this.path;
        this.total = test;
        this.free = dir.getUsableSpace();
        this.last = System.currentTimeMillis();
    }

    public synchronized void stat(){
        this.total = dir.getTotalSpace();
        this.free = dir.getUsableSpace();
        this.last = System.currentTimeMillis();
    }
    public String pathTo(Address address){

        return alto.io.u.Chbuf.fcat(this.path,address.getPathStorage());
    }
    public long getTotalSpace(){
        return this.total;
    }
    public long getFreeSpace(){
        return this.free;
    }
    public long getStatLast(){
        return this.last;
    }
    public String getPath(){
        return this.path;
    }
    public java.io.File getFile(){
        return this.dir;
    }
    public String toString(){
        return this.uri;
    }
    public int hashCode(){
        return this.uri.hashCode();
    }
    public boolean equals(Object ano){
        if (this == ano)
            return true;
        else if (null == ano)
            return false;
        else if (ano instanceof String)
            return this.toString().equals(ano);
        else
            return this.toString().equals(ano.toString());
    }
}
