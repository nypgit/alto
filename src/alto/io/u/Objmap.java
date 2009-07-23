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
public class Objmap 
    extends Hasharray
    implements Frame
{
    protected Objmap frame_parent;


    public Objmap(int initial, float load){
        super(Types.Indeces.Object,initial,load,Values.Types.Object);
    }
    public Objmap(int initial){
        super(Types.Indeces.Object,initial,Values.Types.Object);
    }
    public Objmap(){
        super(Types.Indeces.Object,Values.Types.Object);
    }
    public Objmap(java.lang.Object key, java.lang.Object value){
        this();
        this.put(key,value);
    }
    public Objmap(java.lang.Object key){
        this(key,key);
    }
    public Objmap(Objmap copy){
        this(copy.size()+3);
        this.put(copy);
    }
    public Objmap(Objmap a, Objmap b){
        this(a.size()+b.size()+3);
        this.put(a);
        this.put(b);
    }


    public void destroy(){
        this.frame_parent = null;
        super.destroy();
    }
    public final Objmap frameGet(){
        /*
         * (obfmap frameInit) depends on this method being final: that
         * the value returned here is identical to this.frame_parent
         */
        return this.frame_parent;
    }
    public boolean frameStale(){
        if (null != this.frame_parent)
            throw new java.lang.IllegalStateException("class use bug");
        else
            return false;/*(no frame,- not stale)
                          */
    }
    public final boolean frameExists(){
        return (null != this.frame_parent);
    }
    public final boolean frameExistsNot(){
        return (null == this.frame_parent);
    }
    public Frame frameParentGet(){
        return this.frame_parent;
    }
    public boolean frameParentSet(Objmap pf){
        if (null == pf){
            this.frame_parent = null;
            return true;
        }
        else if (null == this.frame_parent){
            if (this == pf)
                throw new java.lang.IllegalStateException("cyclic frame reference identical");
            else {
                this.frame_parent = pf;
                return true;
            }
        }
        else
            return false;
    }
    public void frameParentReset(Objmap pf){
        if (null == pf)
            this.frame_parent = null;
        else {
            if (this == pf)
                throw new java.lang.IllegalStateException("cyclic frame reference identical");
            else 
                this.frame_parent = pf;
        }
    }

    public java.lang.Object[] keyary(){
        return this.objectKeyary(0);
    }
    public java.lang.Object[] keyary(java.lang.Class comp){
        return this.objectKeyary(0,comp);
    }
    public java.lang.Object[] keyaryFilter(java.lang.Class comp){
        return this.objectKeyaryFilter(0,comp);
    }
    public java.lang.Object[] valary(){
        return this.objectValary(0);
    }
    public java.lang.Object[] valary(java.lang.Class comp){
        return this.objectValary(0,comp);
    }
    public java.lang.Object[] valaryFilter(java.lang.Class comp){
        return this.objectValaryFilter(0,comp);
    }
    public java.lang.Object lastKey(){
        return this.objectGetLastKey(0);
    }
    public java.lang.Object lastValue(){
        return this.objectGetLastValue(0);
    }
    public void lastValue( java.lang.Object val){
        this.objectSetLastValue(0,val);
    }
    public java.util.Enumeration keys(){
        return this.enumerateKeys(0);
    }
    public java.util.Enumeration elements(){
        return this.enumerateValues(0);
    }
    public boolean containsValue(java.lang.Object value){
        return this.objectContainsValue(0,value);
    }
    public boolean contains(java.lang.Object value){
        return this.objectContainsValue(0,value);
    }
    public boolean containsKey(java.lang.Object key){
        return this.objectContainsKey(0,key);
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
    public int indexOfValue( java.lang.Object val){
        return this.objectIndexOfValue(0,val,0);
    }
    public int indexOfValue( java.lang.Object val, int fromIdx){
        return this.objectIndexOfValue(0,val,fromIdx);
    }
    public int lastIndexOfValue( java.lang.Object val){
        return this.objectLastIndexOfValue(0,val,Integer.MAX_VALUE);
    }
    public int lastIndexOfValue( java.lang.Object val, int fromIdx){
        return this.objectLastIndexOfValue(0,val,fromIdx);
    }
    public int indexOfValueClass( Class sup){
        return this.objectIndexOfValueClass(0,sup,-1);
    }
    public int indexOfValueClass( Class sup, int from){
        return this.objectIndexOfValueClass(0,sup,from);
    }
    public int lastIndexOfValueClass( Class sup){
        return this.objectLastIndexOfValueClass(0,sup,Integer.MAX_VALUE);
    }
    public int lastIndexOfValueClass( Class sup, int from){
        return this.objectLastIndexOfValueClass(0,sup,from);
    }
    public java.lang.Object get(java.lang.Object key){
        int idx = this.objectIndexOfKey(0,key);
        return this.objectGetValue(0,idx);
    }
    public java.lang.Object get2(int kindex, java.lang.Object key){
        int idx = this.objectIndexOfKey(kindex,key);
        return this.objectGetValue(0,idx);
    }
    public java.lang.Object get3(int kindex, int vindex, java.lang.Object key){
        int idx = this.objectIndexOfKey(kindex,key);
        return this.objectGetValue(vindex,idx);
    }
    public int count(java.lang.Object key){
        int[] lidx = this.objectIndexListOfKey(0, key);
        if (null != lidx)
            return lidx.length;
        else
            return 0;
    }
    public boolean moreThanOne(java.lang.Object key){
        return (0 < this.count(key));
    }
    public java.lang.Object[] list(java.lang.Object key){
        int[] lidx = this.objectIndexListOfKey(0, key);
        if (null != lidx){
            Values.Object values = this.objectGetValues(0);
            return values.list(lidx);
        }
        else
            return Values.NilArrayObject;
    }
    public java.lang.Object[] list(java.lang.Object key, Class comp){
        int[] lidx = this.objectIndexListOfKey(0, key);
        if (null != lidx){
            Values.Object values = this.objectGetValues(0);
            return (java.lang.Object[])values.list(lidx,comp);
        }
        else
            return Values.NilArrayObject;
    }
    public java.lang.Object[] list2(int index, java.lang.Object key){
        int[] lidx = this.objectIndexListOfKey(index, key);
        if (null != lidx){
            Values.Object values = this.objectGetValues(0);
            return values.list(lidx);
        }
        else
            return Values.NilArrayObject;
    }
    public java.lang.Object[] list2(int index, java.lang.Object key, Class comp){
        int[] lidx = this.objectIndexListOfKey(index, key);
        if (null != lidx){
            Values.Object values = this.objectGetValues(0);
            return (java.lang.Object[])values.list(lidx,comp);
        }
        else
            return Values.NilArrayObject;
    }
    public java.lang.Object key(int idx){
        return this.objectGetKey(0,idx);
    }
    public java.lang.Object value(int idx){
        return this.objectGetValue(0,idx);
    }
    /**
     * @param idx Key- value input- order index
     * @param value Replace existing value with this value
     * @return Replaced argument value, or null for bad index.  (Of
     * course, a null argument produces a null return value).
     */
    public java.lang.Object value(int idx, java.lang.Object value){
        return this.objectSetValue(0,idx,value);
    }
    /**
     * Set "add" performs a put.
     */
    public void add(java.lang.Object object){
        this.put(object,object);
    }
    public void put(Objmap from){
        if (null != from){
            for (int cc = 0, count = from.size(); cc < count; cc++){
                java.lang.Object key = from.key(cc);
                java.lang.Object val = from.value(cc);
                this.put(key,val);
            }
        }
    }
    /**
     * Replace the keyed value
     */
    public java.lang.Object put(java.lang.Object key, java.lang.Object value){
        Entry ent = this.objectPutKey(0,key);
        int aryix = ent.aryix;
        Object old = this.objectGetValue(0,aryix);
        this.objectSetValue(0,aryix,value);
        return old;
    }
    public int put2(int kindex, java.lang.Object key, java.lang.Object val){
        Entry ent = this.objectPutKey(kindex,key);
        int aryix = ent.aryix;
        this.objectSetValue(0,aryix,val);
        return aryix;
    }
    public int put2(int kindex, java.lang.Object key, int idx){
        Entry ent = this.objectPutKey(kindex,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Add a potentially duplicate key.</p>
     */
    public int append( java.lang.Object key, java.lang.Object value){
        Entry ent = this.objectAppendKey(0,key);
        int aryix = ent.aryix;
        this.objectSetValue(0,aryix,value);
        return aryix;
    }
    /**
     * <p> Secondary append.</p>
     * @param kindex Index of indeces
     * @param key Unique key for index 
     * @param idx Index to 'vals' list returned by primary 'append'
     */
    public int append2( int kindex, java.lang.Object key, int idx){
        Entry ent = this.objectAppendKey(kindex,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Insert the argument pair.  If the key has been indexed
     * before, the new key is inserted into the index before it.</p>
     */
    public int insert( int idx, java.lang.Object key, java.lang.Object val){
        Entry ent = this.objectInsertKey(0,idx,key);
        this.objectInsertValue(0,idx,val);
        return ent.aryix;
    }
    /**
     * <p> Secondary "insert" adds secondary key only. </p>
     * @param kindex Secondary index must be greater than zero
     * @param idx Update index pointer to vals list
     * @param key Secondary key external to keys list
     * @return Value of parameter 'idx' 
     */
    public int insert2( int kindex, int idx, java.lang.Object key){
        Entry ent = this.objectInsertKey(kindex,idx,key);
        return ent.aryix;
    }
    /**
     * <p> Replace the key- value pair at key- value index 'idx' with
     * the argument pair.</p>
     */
    public int replace( int idx, java.lang.Object nkey, java.lang.Object nval){
        Entry ent = this.objectReplaceKey(0,idx,nkey);
        this.objectSetValue(0,idx,nval);
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
        Object re = this.objectRemoveValue(0,idx);
        this.objectRemoveKeyByIndex(0,idx);
        return re;
    }
    /**
     * <p> Secondary "remove(int)" drops secondary key only. </p>
     */
    public void remove2( int kindex, int idx){
        this.objectRemoveKeyByIndex(kindex,idx);
    }
    /**
     * Remove key and value, index any latter identical key, truncate
     * data arrays (keys and vals), scan index table and decrement
     * pointer indeces for truncated data arrays.  
     */
    public java.lang.Object remove( java.lang.Object key){
        Entry ent = this.objectRemoveKeyByValue(0,key);
        if (null != ent)
            return this.objectRemoveValue(0,ent.aryix);
        else
            return null;
    }
    /**
     * <p> Secondary "remove(key)" drops secondary key only. </p>
     */
    public void remove2( int kindex, java.lang.Object key){
        this.objectRemoveKeyByValue(kindex,key);
    }
    public final Objmap cloneObjmap(){
        return (Objmap)super.cloneHasharray();
    }


    protected static void usage( java.io.PrintStream out){
        out.println();
        out.println("Usage");
        out.println("    Objmap N");
        out.println();
        out.println("Description");
        out.println("    Test a Objmap for N elements.");
        out.println();
    }
    /**
     * <p> Timed put and get test, with confirmation by enumeration of
     * keys over get.  </p>
     */
    public static void main (java.lang.String[] argv){
        if (null == argv || 1 > argv.length){
            usage(System.err);
            java.lang.System.exit(1);
        }
        else {
            try {
                int N = java.lang.Integer.parseInt(argv[0]);
                if (0 < N){
                    java.util.Dictionary tm = new Objmap();
                    java.lang.Object tt;
                    java.lang.Object[] testvector = new java.lang.Object[N];
                    long start;
                    double duration;
                    java.util.Random prng = new java.util.Random();
                    //
                    start = java.lang.System.currentTimeMillis();
                    for (int cc = 0; cc < N; cc++)
                        testvector[cc] = new Integer(prng.nextInt());
                    duration = (double)(java.lang.System.currentTimeMillis()-start);
                    java.lang.System.out.println("constructed test vector for "+N+" cycles in "+(duration/1000.0)+" seconds.");
                    //
                    int dupe = 0;
                    start = java.lang.System.currentTimeMillis();
                    for (int cc = 0; cc < N; cc++){
                        while (true){
                            tt = testvector[cc];
                            if (null != tm.get(tt)){
                                dupe += 1;
                                java.lang.System.out.println("input-store-test dup "+tt);
                            }
                            //
                            tm.put(tt,tt);
                            //
                            if (tt != tm.get(tt))
                                throw new java.lang.IllegalStateException("input-store-test failed at "+cc);
                            else
                                break;
                        }
                    }
                    duration = (double)(java.lang.System.currentTimeMillis()-start);
                    //
                    if ((N-dupe) != (tm.size()))
                        throw new java.lang.IllegalStateException("input-store-test failed for size "+tm.size()+" != N "+N);
                    else {
                        java.lang.System.out.println("input-store-test completed for "+N+" cycles in "+(duration/1000.0)+" seconds.");
                        java.util.Enumeration keys = tm.keys();
                        int count = 0;
                        java.lang.Object tk, tv;
                        //
                        start = java.lang.System.currentTimeMillis();
                        while (keys.hasMoreElements()){
                            tk = keys.nextElement();
                            tv = tm.get(tk);
                            count += 1;
                            if (tv == tk)
                                continue;
                            else
                                throw new java.lang.IllegalStateException("keys-lookup-test miss "+tv+" != "+tk);
                        }
                        duration = (double)(java.lang.System.currentTimeMillis()-start);
                        //
                        java.lang.System.out.println("keys-lookup-test completed for "+count+" cycles in "+(duration/1000.0)+" seconds.");
                        //
                        start = java.lang.System.currentTimeMillis();
                        for (int cc = 0; cc < N; cc++){
                            tt = testvector[cc];
                            tv = tm.get(tt);
                            if (tt != tv)
                                throw new java.lang.IllegalStateException("vector-lookup-test miss "+tt+" != "+tv);
                        }
                        duration = (double)(java.lang.System.currentTimeMillis()-start);
                        //
                        java.lang.System.out.println("vector-lookup-test completed for "+count+" cycles in "+(duration/1000.0)+" seconds.");
                        return;//(for test2)//System.exit(0);
                    }
                }
                else
                    throw new java.lang.IllegalArgumentException();
            }
            catch (java.lang.NumberFormatException input){
                usage(System.err);
                java.lang.System.exit(1);
            }
            catch (java.lang.Throwable thro){
                thro.printStackTrace();
                java.lang.System.exit(1);
            }
        }
    }

}
