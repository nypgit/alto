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
import alto.io.u.hasharray.IndexObject;
import alto.io.u.hasharray.Values;

/**
 * <p> Hash array structure maintains input order for indeces,
 * enumerations and arrays. </p>
 * 
 * <h3>Not Synchronized</h3>
 *
 * <p> This class is not multi- thread safe, it is intended for use by
 * a single thread- user.  External thread safety must be applied in
 * multi- threaded applications. </p>
 *
 * <h3>Multiple key instances</h3>
 *
 * <p> The key array may contain multiple identical keys.  Dictionary
 * usage implies one key, array usage may imply additional
 * instances. </p>
 * 
 * <h3>Multiple indeces</h3>
 * 
 * <p> Multiple indeces are allocated on demand.  Their correct use
 * and maintenance requires (simply) maintaining the parallel keys for
 * each value.  For example, a call to "put" or "add" for the primary
 * key is matched by a call to "put" or "add" for each additional key,
 * and likewise for "insert" or "remove". </p>
 * 
 *
 * @author John Pritchard 
 * @since 1.2
 */
public class Objset 
    extends Hasharray
{


    public Objset(int initial, float load){
        super(Types.Indeces.Object,initial,load,null);
    }
    public Objset(int initial){
        super(Types.Indeces.Object,initial,null);
    }
    public Objset(){
        super(Types.Indeces.Object,null);
    }
    public Objset(java.lang.Object key){
        this();
        this.add(key);
    }
    public Objset(Objset copy){
        this(copy.size()+3);
        this.add(copy);
    }
    public Objset(Objset a, Objset b){
        this(a.size()+b.size()+3);
        this.add(a);
        this.add(b);
    }



    public java.lang.Object[] list(){
        return this.objectKeyary(0);
    }
    public java.lang.Object[] list(java.lang.Class comp){
        return this.objectKeyary(0,comp);
    }
    public java.lang.Object[] listFilter(java.lang.Class comp){
        return this.objectKeyaryFilter(0,comp);
    }
    public java.lang.Object last(){
        return this.objectGetLastKey(0);
    }
    public java.util.Enumeration keys(){
        return this.enumerateKeys(0);
    }
    public java.util.Enumeration elements(){
        return this.enumerateKeys(0);
    }
    public boolean contains(java.lang.Object value){
        return this.objectContainsKey(0,value);
    }
    public int indexOf(java.lang.Object key){
        return this.objectIndexOfKey(0,key);
    }
    public int[] indexOfList(java.lang.Object key){
        return this.objectIndexListOfKey(0,key);
    }
    public int indexOf( java.lang.Object key, int fromIdx){
        return this.objectIndexOfKey(0,key,fromIdx);
    }
    public int lastIndexOf( java.lang.Object key){
        return this.objectLastIndexOfKey(0,key,Integer.MAX_VALUE);
    }
    public int lastIndexOf( java.lang.Object key, int fromIdx){
        return this.objectLastIndexOfKey(0,key,fromIdx);
    }
    public java.lang.Object get(java.lang.Object key){
        int idx = this.objectIndexOfKey(0,key);
        return this.objectGetKey(0,idx);
    }
    public java.lang.Object get2(int kindex, java.lang.Object key){
        int idx = this.objectIndexOfKey(kindex,key);
        return this.objectGetKey(0,idx);
    }
    public java.lang.Object get(int idx){
        return this.objectGetKey(0,idx);
    }
    public java.lang.Object get(int kindex, int idx){
        return this.objectGetKey(kindex,idx);
    }

    public Object put(Object a, Object b){
        throw new java.lang.UnsupportedOperationException();
    }

    public void add(Objset from){
        if (null != from){
            for (int cc = 0, count = from.size(); cc < count; cc++){
                java.lang.Object key = from.get(cc);
                this.add(key);
            }
        }
    }
    /**
     * Replace the keyed value
     */
    public int add(java.lang.Object key){
        Entry ent = this.objectPutKey(0,key);
        int aryix = ent.aryix;
        return aryix;
    }
    /**
     * <p> Add a potentially duplicate key.</p>
     */
    public int append( java.lang.Object key){
        Entry ent = this.objectAppendKey(0,key);
        int aryix = ent.aryix;
        return aryix;
    }
    /**
     * <p> Secondary append.</p>
     * @param index Index of indeces
     * @param key Unique key for index 
     * @param idx Index returned by primary 'append'
     */
    public int append2( int kindex, java.lang.Object key, int idx){
        Entry ent = this.objectAppendKey(kindex,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Insert the argument pair.  If the key has been indexed
     * before, the new key is inserted into the index before it.</p>
     */
    public int insert( int idx, java.lang.Object key){
        Entry ent = this.objectInsertKey(0,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Secondary. </p>
     * @param kindex Secondary index must be greater than zero
     * @param idx Update index pointer to list
     * @param key Secondary key external to list
     * @return Value of parameter 'idx' 
     */
    public int insert2( int kindex, int idx, java.lang.Object key){
        Entry ent = this.objectInsertKey(kindex,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Replace the key at index 'idx' with the argument.</p>
     */
    public int replace( int idx, java.lang.Object nkey){
        Entry ent = this.objectReplaceKey(0,idx,nkey);
        return ent.aryix;
    }
    /**
     * <p> Secondary "replace" key. 
     * </p>
     * @param kindex Secondary index must be greater than zero
     * @param idx Index into keys and vals for existing key (primary
     * and secondary) and value
     * @param nkey New secondary key replacing old secondary key
     * 
     * @return Index into keys and vals
     */
    public int replace2( int kindex, int idx, java.lang.Object nkey){
        Entry ent = this.objectReplaceKey(kindex,idx,nkey);
        return ent.aryix;
    }
    /**
     * Remove element with known index.
     */
    public java.lang.Object remove( int idx){
        java.lang.Object re = this.get(idx);
        this.objectRemoveKeyByIndex(0,idx);
        return re;
    }
    /**
     * <p> Secondary "remove(int)" drops secondary key. </p>
     */
    public java.lang.Object remove2( int kindex, int idx){
        java.lang.Object re = this.get(kindex,idx);
        this.objectRemoveKeyByIndex(kindex,idx);
        return re;
    }
    /**
     * Remove key and value, index any latter identical key, truncate
     * data arrays (keys and vals), scan index table and decrement
     * pointer indeces for truncated data arrays.  
     */
    public java.lang.Object remove( java.lang.Object key){
        int idx = this.indexOf(key);
        if (-1 < idx)
            return this.remove(idx);
        else
            return null;
    }
    /**
     * <p> Secondary "remove(key)" drops secondary key only. </p>
     */
    public void remove2( int kindex, java.lang.Object key){
        this.objectRemoveKeyByValue(kindex,key);
    }
    public final Objset cloneObjset(){
        return (Objset)super.cloneHasharray();
    }

}
