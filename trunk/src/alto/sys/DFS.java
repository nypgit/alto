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
import alto.lang.HttpRequest;

/**
 * <p> Asynchronous Distributed File Store (DFS)  </p>
 * 
 * <p> A DFS Network from the perspective of the {@link FileManager}
 * is a class configured by name at a host private store address. </p>
 * 
 * <p> Addresses having host component value zero have no network
 * transit (communication) via DFS.  </p>
 * 
 * <p> Generally DFS is intended to support MHTTP, which is HTTP
 * messages without entity bodies communicated over UDP packet
 * transports including broadcast and multicast. </p>
 * 
 * 
 * @author jdp
 * @since 1.6
 */
public abstract class DFS 
    extends java.lang.ClassLoader
    implements alto.io.DFS
{

    protected final static void DFSRequestGET(File file)
        throws java.io.IOException
    {
        FileManager.Instance().dfsRequestGET(file);
    }
    protected final static void DFSNotifyPUT(File file)
        throws java.io.IOException
    {
        FileManager.Instance().dfsNotifyPUT(file);
    }
    protected final static void DFSNotifyDELETE(File file)
        throws java.io.IOException
    {
        FileManager.Instance().dfsNotifyDELETE(file);
    }


    protected alto.io.DFS dfs;


    protected DFS(){
        super();
    }
    protected DFS(java.lang.ClassLoader parent){
        super(parent);
    }


    public abstract File getStorage(Address address);

    public alto.io.DFS getDfs(){
        return this.dfs;
    }
    public void setDfs(alto.io.DFS dfs){
        if (null == this.dfs)
            this.dfs = dfs;
        else
            throw new Error.State("Previously defined");
    }
    /**
     * May look to location for optional configuration.
     */
    protected void dfsInit()
        throws java.io.IOException
    {}
    public void dfsRequestGET(File file)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsRequestGET(file);
    }
    public void dfsResponseGET(Address local, HttpRequest put)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsResponseGET(local,put);
    }
    public void dfsNotifyPUT(File file)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsNotifyPUT(file);
    }
    public void dfsNotifyDELETE(File file)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsNotifyDELETE(file);
    }
    public void dfsNotifyLOCK(Address reference, long timein, long timeout)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsNotifyLOCK(reference,timein,timeout);
    }
    public void dfsResponseLOCKHolding(Address reference, Reference toUnlock)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsResponseLOCKHolding(reference,toUnlock);
    }
    public void dfsResponseLOCKPending(Address reference, Reference toCancel)
        throws java.io.IOException
    {
        alto.io.DFS dfs = this.dfs;
        if (null != dfs)
            dfs.dfsResponseLOCKPending(reference,toCancel);
    }
}
