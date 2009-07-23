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
package alto.io.u ;

import alto.io.u.hasharray.Entry;
import alto.io.u.hasharray.Index;
import alto.io.u.hasharray.IndexLong;
import alto.io.u.hasharray.Values;

/**
 * <p> Hasharray mapping from int primitive to int primitive. </p>
 *
 * @see intmap
 * 
 * @author jdp
 * @since 1.2
 */
public class Intint 
    extends Hasharray
{

    public Intint(int initial, float load){
        super(Types.Indeces.Long,initial,load,Values.Types.Int);
    }
    public Intint(int initial){
        super(Types.Indeces.Long,initial,Values.Types.Int);
    }
    public Intint(){
        super(Types.Indeces.Long,Values.Types.Int);
    }


    public int[] keyary(){
        return this.longGetKeys(0);
    }
    public int[] valary(){
        return this.intGetValues(0).get();
    }
    public long lastKey(){
        return this.longGetLastKey(0);
    }
    public int lastValue(){
        return this.intGetLastValue(0);
    }
    public void lastValue( int val){
        this.intSetLastValue(0,val);
    }
    public java.util.Enumeration keys(){

        return this.enumerateKeys(0);
    }
    public java.util.Enumeration elements(){

        return this.enumerateValues(0);
    }
    public boolean containsValue(int value){
        return this.intContainsValue(0,value);
    }
    public boolean contains(int value){
        return this.intContainsValue(0,value);
    }
    public boolean containsKey(int key){
        return this.longContainsKey(0,key);
    }
    public int indexOf(int key){
        return this.longIndexOfKey(0,key);
    }
    public int indexOf( int key, int fromIdx){
        return this.longIndexOfKey(0,key,fromIdx);
    }
    public int[] indexOfList(int key){
        return this.longIndexListOfKey(0,key);
    }
    public int lastIndexOf( int key){
        return this.longLastIndexOfKey(0,key,Integer.MAX_VALUE);
    }
    public int lastIndexOf( int key, int fromIdx){
        return this.longLastIndexOfKey(0,key,fromIdx);
    }
    public int indexOfValue( int val){
        return this.intIndexOfValue(0,val,Integer.MAX_VALUE);
    }
    public int indexOfValue( int val, int from){
        return this.intIndexOfValue(0,val,from);
    }
    public int lastIndexOfValue( int val){
        return this.intLastIndexOfValue(0,val,Integer.MAX_VALUE);
    }
    public int lastIndexOfValue( int val, int fromIdx){
        return this.intLastIndexOfValue(0,val,fromIdx);
    }
    public java.lang.Object get(java.lang.Object key){
        int idx = this.indexOfKeyAsObject(0,key);
        if (-1 < idx){
            int value = this.intGetValue(0,idx);
            return new java.lang.Integer(value);
        }
        else
            return Values.NilObject;
    }
    public int get(int key){
        int idx = this.longIndexOfKey(0,key);
        if (-1 < idx)
            return this.intGetValue(0,idx);
        else
            return Values.NilInt;
    }
    public int[] list(int key){
        int[] lidx = this.longIndexListOfKey(0, key);
        if (null != lidx){
            Values.Int values = this.intGetValues(0);
            return values.list(lidx);
        }
        else
            return Values.NilArrayInt;
    }
    public java.lang.Object list(int key, java.lang.Class comp){
        int[] lidx = this.longIndexListOfKey(0, key);
        if (null != lidx){
            Values.Int values = this.intGetValues(0);
            return values.list(lidx,comp);
        }
        else
            return Values.NilObject;
    }
    public int key(int idx){
        return this.longGetKey(0,idx);
    }
    public int value(int idx){
        return this.intGetValue(0,idx);
    }
    public int value(int idx, int value){
        return this.intSetValue(0,idx,value);
    }
    public java.lang.Object put(java.lang.Object key, java.lang.Object value){
        Entry ent = this.putKeyAsObject(0,key);
        return this.setValueAsObject(0,ent.aryix,value);
    }
    public int put(int key, int value){
        Entry ent = this.longPutKey(0,key);
        return this.intSetValue(0,ent.aryix,value);
    }
    public int append( int key, int val){
        Entry ent = this.longAppendKey(0,key);
        if (null != ent){
            int idx = ent.aryix;
            this.intSetValue(0,idx,val);
            return idx;
        }
        else
            return Index.NotFound;
    }
    public int insert( int idx, int key, int val){
        Entry ent = this.longInsertKey(0,idx,key);
        if (null != ent){
            idx = ent.aryix;
            this.intInsertValue(0,idx,val);
            return idx;
        }
        else
            return Index.NotFound;
    }
    public int replace( int idx, int nkey, int nval){
        Entry ent = this.longReplaceKey(0,idx,nkey);
        if (null != ent){
            idx = ent.aryix;
            this.intSetValue(0,idx,nval);
            return idx;
        }
        else
            return Index.NotFound;
    }
    /**
     * Drop slot by index
     */
    public int drop( int idx){
        Entry ent = this.longRemoveKeyByIndex(0,idx);
        if (null != ent)
            return this.intRemoveValue(0,ent.aryix);
        else
            return Values.NilInt;
    }
    public java.lang.Object remove( java.lang.Object key){
        Entry ent = this.removeKeyAsObject(0,key);
        if (null != ent)
            return this.removeValueAsObject(0,ent.aryix);
        else
            return Values.NilObject;
    }
    public int remove( int key){
        Entry ent = this.longRemoveKeyByValue(0,key);
        if (null != ent)
            return this.intRemoveValue(0,ent.aryix);
        else
            return Values.NilInt;
    }
    public final Intint cloneIntint(){
        return (Intint)super.cloneHasharray();
    }
}
