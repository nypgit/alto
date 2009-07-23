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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * <p> An instance of {@link lobjmap} using soft references' wrapping
 * and unwrapping on get, put and value methods. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public class Slobjmap
    extends Lobjmap
{

    private final ReferenceQueue refq;


    public Slobjmap(){
        super();
        this.refq = this.ctorRQ();
    }


    /**
     * @return The private final reference queue defined by this
     * constructor.
     */
    public final ReferenceQueue getReferenceQueue(){
        return this.refq;
    }
    /**
     * A subclass may override this method to return its own reference
     * queue class.  It is called once from the constructor to define
     * a private final field.
     * @return New reference queue
     */
    protected ReferenceQueue ctorRQ(){
        return new ReferenceQueue();
    }
    /**
     * A subclass may override this method to return its own soft reference
     * class.
     * @return New soft reference 
     */
    protected SoftReference newSR(Object value, ReferenceQueue rq){
        return new SoftReference(value,rq);
    }
    public Object get(Object key){
        SoftReference ref = (SoftReference)super.get(key);
        if (null != ref)
            return ref.get();
        else
            return null;
    }
    public Object put(Object key, Object value){
        SoftReference ref = this.newSR(value,this.refq);
        ref = (SoftReference)super.put(key,ref);
        if (null != ref)
            return ref.get();
        else
            return null;
    }
    public Object remove(Object key){
        SoftReference ref = (SoftReference)super.remove(key);
        if (null != ref)
            return ref.get();
        else
            return null;
    }
    public Object value(int idx){
        SoftReference ref = (SoftReference)super.value(idx);
        if (null != ref)
            return ref.get();
        else
            return null;
    }
    public SoftReference getReference(Object key){
        return (SoftReference)super.get(key);
    }
    public SoftReference getReference(int idx){
        return (SoftReference)super.value(idx);
    }
    public Object value(int idx, Object value){
        SoftReference ref = this.newSR(value,this.refq);
        super.value(idx,ref);
        return value;
    }
    public int append( java.lang.Object key, java.lang.Object value){
        SoftReference ref = this.newSR(value,this.refq);
        return super.append(key,ref);
    }
    public int insert( int idx, java.lang.Object key, java.lang.Object value){
        SoftReference ref = this.newSR(value,this.refq);
        return super.insert(idx,key,ref);
    }
    public int replace( int idx, java.lang.Object key, java.lang.Object value){
        SoftReference ref = this.newSR(value,this.refq);
        return super.replace(idx,key,ref);
    }
    /*
     * TODO additional methods...
     */
}
