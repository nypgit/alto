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
import alto.io.u.hasharray.IndexLong;
import alto.io.u.hasharray.Values;


/**
 * <p> Abstract base "hash array" (also known as an "associative
 * array") maintains input order over indexed keys for a map from
 * source (key) to target (value) values (maintained internally).
 * This is the infrastructure for a broad variety of subclasses
 * including not only specialized map key and value types, but also
 * multiple map indeces. </p>
 * 
 * <h3>Usage</h3>
 * 
 * <p> The {@link Hasharray} class permits any combination of map key
 * and value types, and for this purpose presents a tools API for
 * editing the index of keys, and the array of values.  The first best
 * example of its usage would be the implementation of {@link objmap},
 * or other examples include {@link intmap} and {@link intint}.  </p>
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
 * <h3>Multiple key and value types</h3>
 * 
 * <p> This abstract base class supports both object and primitive
 * types for both keys and values.  A primitive integer type key is
 * supported through a 64 bit long integer hash to represent a 31 bit
 * value (32 bit positive integer) plus a special internal value
 * (NIL).  </p>
 *
 * <h3>Multiple indeces</h3>
 * 
 * <p> This abstract base class supports secondary indeces.  A
 * secondary index permits keys external to this keys list to multiply
 * index values in this values list.  Secondary indeces do not permit
 * the retrieval of these secondary keys.  The set of indeces from the
 * primary including any secondary are exposed as a zero- indexed set
 * of indeces.  </p>
 * 
 * <p> The primary and secondary indeces are instances returned by the
 * protected 'new index' method.  Subclasses may employ disparate
 * index classes in any combination for both primary and secondary
 * indeces through the new index and add index methods.  The new index
 * method is called by the constructor for the primary table class,
 * and secondary indeces may be added in the subclass constructor
 * using the add index method. </p>
 * 
 * <h3>Multiple values</h3>
 * 
 * <p> The idea of multiple lists of values is a fairly advanced one.
 * The lists of keys and values are maintained in parallel through
 * this class.  One list of values for one or more indeces is
 * clear. </p>
 * 
 * <p> Multiple lists of values has two distinct areas of application.
 * The first is homogeneous, the second is hetergeneous.  It's
 * possible for a subclass to employ the infrastructure defined here
 * as one map with multiple indeces over one or more classes of
 * values, or like multiple distinct maps. </p>
 * 
 * <p> In order to realize either possibility in subclasses, it is
 * necessary for this class to be aware of multiple lists of values.
 * Otherwise the method code and signatures defined here would be far
 * less reusable -- code would need to be copied and signatures
 * overridden poorly for high bug probability spaces in subclasses.
 * The first idea that prompted this is a routing map. </p>
 * 
 * @see objmap
 * @see intmap
 * @see intint
 * 
 * @author John Pritchard 
 * @since 1.2
 */
public abstract class Hasharray 
    extends java.util.Dictionary 
    implements java.lang.Cloneable,
               alto.io.u.hasharray.IndexConstants
{

    /**
     * Constructor arguments and other well known classes.
     */
    public final static class Types {

        public final static java.lang.Class Index = Index.class;

        /**
         * There are only two builtin classes of indeces, one for
         * 'Object' keys and another for 'int' keys.  This is because
         * the key needs to have a representation of the special out
         * of band value 'null'.  The 'int' keys are represented by
         * 'long' for this purpose.
         */
        public final static class Indeces {


            public final static java.lang.Class Constructor[] = {
                Hasharray.class,
                java.lang.Integer.TYPE,
                java.lang.Float.TYPE
            };


            public final static java.lang.Class Object = IndexObject.class;

            public final static java.lang.Class Long = IndexLong.class;
        }

    }


    /**
     * <p> Precise list (not an optimistic "buffer") of indeces. </p>
     */
    protected Index[] tables;
    /**
     * <p> Precise list of lists of value arrays.
     */
    protected Values[] values;


    public Hasharray(java.lang.Class keys, int initial, float load, java.lang.Class values){
        super();
        this.tables = new Index[]{ this.newIndex(keys,initial,load)};
        if (null != values)
            this.values = new Values[]{ Values.Types.New(values)};
        //else
        // (set)
    }
    public Hasharray(java.lang.Class keys, int initial, java.lang.Class values){
        this (keys, initial, 0.75f, values);
    }
    /**
     * Default initial capacity is 11 elements.  Default load factor
     * is three- quarters (of one).
     */
    public Hasharray(Class keys, Class values){
        this (keys, 11, 0.75f, values);
    }


    /**
     * <p> Called from getIndex with create TRUE (in index of indeces
     * order) to construct secondary indeces. </p>
     * @param jclass Subclass of index, if null this method calls
     * {@link #newIndex(int,float)}.
     */
    protected Index newIndex(java.lang.Class jclass, int initial, float factor){
        if (0 == initial || 0f == factor){
            Index table = this.getIndex(0);
            if (null != table){
                initial = table.grow;
                factor = table.load;
            }
            else if (0f == factor)
                throw new alto.sys.Error.State();
        }
        if (null == jclass)
            throw new IllegalArgumentException("Missing keys (index) class");

        else if (Types.Index.isAssignableFrom(jclass)){
            try {
                java.lang.reflect.Constructor ctor = jclass.getConstructor(Types.Indeces.Constructor);
                java.lang.Object[] argv = 
                    new java.lang.Object[]{
                        this,
                        new java.lang.Integer(initial),
                        new java.lang.Float(factor)
                    };
                return (Index)ctor.newInstance(argv);
            }
            catch (java.lang.NoSuchMethodException exc){
                java.lang.RuntimeException rex = 
                    new alto.sys.Error.State(jclass.getName());
                rex.initCause(exc);
                throw rex;
            }
            catch (java.lang.InstantiationException exc){
                java.lang.RuntimeException rex = 
                    new alto.sys.Error.State(jclass.getName());
                rex.initCause(exc);
                throw rex;
            }
            catch (java.lang.IllegalAccessException exc){
                java.lang.RuntimeException rex = 
                    new alto.sys.Error.State(jclass.getName());
                rex.initCause(exc);
                throw rex;
            }
            catch (java.lang.reflect.InvocationTargetException exc){
                java.lang.RuntimeException rex = 
                    new alto.sys.Error.State(jclass.getName());
                rex.initCause(exc);
                throw rex;
            }
        }
        else if (Values.Types.Object == jclass)
            return new IndexObject(this,initial,factor);

        else if (Values.Types.Long == jclass)
            return new IndexLong(this,initial,factor);

        else
            throw new java.lang.IllegalArgumentException("Class '"+jclass.getName()+"' is not a subclass of 'Index' and not a recognized type of key.");
    }
    /**
     * @return The total number of indeces including the primary and
     * any secondaries.  This number would be the index (int for get
     * index) of the next added index (object for add index), if
     * another were added.
     */
    protected final int countIndex(){
        Index[] tables = this.tables;
        if (null != tables)
            return tables.length;
        else
            return 0;
    }
    /**
     * @param index A subclass may add index classes other than the
     * one used for the primary index
     * @return The index (int) for the added index
     */
    protected final int addIndex(Index index){
        if (null != index){
            int count = this.tables.length;
            if (1 == count){
                this.tables = new Index[]{this.tables[0],index};
                return 1;
            }
            else {
                Index[] copier = new Index[count+1];
                java.lang.System.arraycopy(this.tables,0,copier,0,count);
                copier[count] = index;
                this.tables = copier;
                return count;
            }
        }
        else
            throw new java.lang.IllegalArgumentException("Null 'Index' class argument.");
    }
    /**
     * @param kindex A zero- positive value to get or create an index.
     * Zero for the primary index, one is the first secondary index.
     * @return The indexed index or null if not found
     */
    protected final Index getIndex(int kindex){
        if (0 > kindex)
            throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(kindex));
        else {
            Index[] tables = this.tables;
            if (null != tables){
                int count = tables.length;
                if (kindex < count)
                    return this.tables[kindex];
                else 
                    throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(kindex));
            }
            else
                throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(kindex));
        }
    }
    /**
     * @param kindex Zero for the primary index, one is the first secondary index.
     * @param create If the index is not found, create it with any
     * intermediate missing indeces in the same class as the primary
     * index
     * @param jclass Subclass of Index
     * @return The indexed index
     */
    protected final Index getIndex(int kindex, boolean create, java.lang.Class jclass){
        if (0 > kindex)
            throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(kindex));
        else {
            Index[] tables = this.tables;
            if (null != tables){
                int count = tables.length;
                if (kindex < count)
                    return this.tables[kindex];
                else if (create){
                    if (kindex == count){
                        Index table = this.newIndex(jclass,0,0f);
                        this.addIndex(table);
                        return table;
                    }
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(kindex));
                }
            }
            return null;
        }
    }

    protected final int countValues(){
        Values[] values = this.values;
        if (null != values)
            return values.length;
        else
            return 0;
    }

    /**
     * @return List internal (verbatim)
     */
    protected final Values getValues(int vindex){
        if (0 > vindex)
            throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(vindex));
        else {
            Values[] values = this.values;
            if (null != values){
                int count = values.length;
                if (vindex < count)
                    return this.values[vindex];
                else 
                    throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(vindex));
            }
            else
                throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(vindex));
        }
    }
    protected final Values getValues(int vindex, boolean create, java.lang.Class component){
        Values[] values = this.values;
        if (null == values){
            if (0 == vindex){
                values = new Values[]{Values.Types.New(component)};
                this.values = values;
                return values[vindex];
            }
            else if (create)
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(vindex));
            else
                return null;
        }
        else {
            int count = values.length;
            if (vindex < count)
                return values[vindex];

            else if (create && vindex == count){
                Values re = Values.Types.New(component);
                values = (Values[])Array.grow(values,(vindex+1),Values.class);
                values[vindex] = re;
                this.values = values;
                return re;
            }
            else
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(vindex));
        }
    }
    protected final void setValues(int vindex, Values vary){
        Values[] values = this.values;
        if (null == values)
            throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(vindex));
        else {
            int count = values.length;
            if (vindex < count)
                values[vindex] = vary;
            else
                throw new java.lang.IllegalArgumentException(java.lang.String.valueOf(vindex));
        }
    }
    public void destroy(){
        Index[] tables = this.tables;
        for (int cc = 0, count = tables.length; cc < count; cc++)
            tables[cc].destroy();

        Values[] values = this.values;
        for (int cc = 0, count = values.length; cc < count; cc++)
            values[cc].destroy();
    }
    public void clear(){
        Index[] tables = this.tables;
        for (int cc = 0, count = tables.length; cc < count; cc++)
            tables[cc].clear();

        Values[] values = this.values;
        for (int cc = 0, count = values.length; cc < count; cc++)
            values[cc].clear();
    }
    public int size(){
        return this.sizeHasharray();
    }
    public final int sizeHasharray(){
        return this.getIndex(0).size();
    }
    public boolean isEmpty(){
        return (1 > this.sizeHasharray());
    }
    public boolean isNotEmpty(){
        return (0 < this.sizeHasharray());
    }
    public Hasharray cloneHasharray(){
        try { 
            Hasharray clone = (Hasharray)super.clone();

            Index[] tables = this.tables;
            int count = tables.length;
            clone.tables = new Index[count];
            for (int cc = 0; cc < count; cc++){
                clone.tables[cc] = tables[cc].cloneIndex();
            }
            Values[] values = this.values;
            count = values.length;
            clone.values = new Values[count];
            for (int cc = 0; cc < count; cc++){
                clone.values[cc] = values[cc].cloneValues();
            }
            return clone;
        }
        catch (CloneNotSupportedException e){ 
            throw new InternalError();
        }
    }


    //                    (((gen:begin)))
    /**
     * Object
     */
    protected final IndexObject objectGetCreateIndex(int kindex){
        return (IndexObject)getIndex(kindex,true,Hasharray.Types.Indeces.Object);
    }
    protected final IndexObject objectGetIndex(int kindex){
        return (IndexObject)getIndex(kindex,false,Hasharray.Types.Indeces.Object);
    }
    protected final Object[] objectGetKeys(int kindex){
        return this.objectGetCreateIndex(kindex).keysEx();
    }
    protected final Object objectGetKey(int kindex, int idx){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.keyEx(idx);
        }
        else {
            return null;
        }
    }
    protected final int objectIndexOfKey(int kindex, Object key){
        IndexObject table = this.objectGetCreateIndex(kindex);
        return table.lookup(key);
    }
    protected final int[] objectIndexListOfKey(int kindex, Object key){
        IndexObject table = this.objectGetCreateIndex(kindex);
        return table.lookupList(key);
    }
    protected final int objectIndexOfKey(int kindex, Object key, int from){
        IndexObject table = this.objectGetIndex(kindex);
        return table.lookup(key,from);
    }
    protected final int objectLastIndexOfKey(int kindex, Object key, int from){
        IndexObject table = this.objectGetIndex(kindex);
        int[] list = table.lookupList(key);
        if (null == list)
            return -1;
        else {
            int llen = list.length;
            for (int cc = (llen-1), re; -1 < cc; cc--){
                re = list[cc];
                if (from >= re)
                    return re;
            }
            return -1;
        }
    }
    protected final boolean objectContainsKey(int kindex, Object key){
        return (-1 < this.objectIndexOfKey(kindex,key));
    }
    protected final Entry objectPutKey(int kindex, Object key){
        IndexObject table = this.objectGetCreateIndex(kindex);
        return table.put(key);
    }
    protected final Entry objectPutKey(int kindex, int idx, Object key){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.put(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry objectInsertKey(int kindex, int idx, Object key){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.insert(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry objectReplaceKey(int kindex, int idx, Object key){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.replace(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry objectAppendKey(int kindex, Object key){
        IndexObject table = this.objectGetCreateIndex(kindex);
        return table.append(key);
    }
    protected final Entry objectAppendKey(int kindex, int idx, Object key){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.append(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry objectRemoveKeyByIndex(int kindex, int idx){
        if (-1 < idx){
            IndexObject table = this.objectGetCreateIndex(kindex);
            return table.removeByIndex(idx);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry objectRemoveKeyByValue(int kindex, Object key){
        IndexObject table = this.objectGetCreateIndex(kindex);
        return table.removeByKey(key);
    }
    /**
     * This method always copies a safe and concise export of the internal keys list.
     */
    protected final Object[] objectKeyary(int kindex){
        IndexObject table = this.objectGetCreateIndex(kindex);
        /*
         * not a copy
         */
        Object[] ary = table.keysEx();
        if (null != ary){
            int count = table.size();
            if (0 < count){
                Object[] copy = new Object[count];
                java.lang.System.arraycopy(ary,0,copy,0,count);
                return copy;
            }
        }
        return null;
    }
    protected final Object[] objectKeyary(int kindex, Class component){
        IndexObject table = this.objectGetCreateIndex(kindex);

        Object[] ary = table.keysEx();
        if (null != ary){
            int count = table.size();
            if (0 < count){
                Object[] copy = (Object[])java.lang.reflect.Array.newInstance(component,count);
                java.lang.System.arraycopy(ary,0,copy,0,count);
                return copy;
            }
        }
        return null;
    }
    protected final Object[] objectKeyaryFilter(int kindex, Class component){
        IndexObject table = this.objectGetCreateIndex(kindex);

        Object[] ary = table.keysEx();
        if (null != ary){
            int count = table.size();
            if (0 < count){
                Object[] copy = (Object[])java.lang.reflect.Array.newInstance(component,count);
                int oc = 0; 
                for (int ic = 0; ic < count; ic++){
                    Object item = ary[ic];
                    if (component.isAssignableFrom(item.getClass())){
                        copy[oc++] = item;
                    }
                }
                if (oc < count){
                    if (0 == oc){
                        return null;
                    }
                    else {
                        Object[] copy2 = (Object[])java.lang.reflect.Array.newInstance(component,oc);
                        java.lang.System.arraycopy(copy,0,copy2,0,oc);
                        return copy2;
                    }
                }
                else
                    return copy;
            }
        }
        return null;
    }
    protected final Object objectGetLastKey(int kindex){
        IndexObject table = this.objectGetCreateIndex(kindex);
        int count = table.size();
        if (0 < count){
            Object[] ary = table.keysEx();

            return ary[count-1];
        }
        else {
            return null;
        }
    }
    protected final java.util.Enumeration objectEnumerateKeys(int kindex){
        IndexObject table = this.objectGetCreateIndex(kindex);
        Object[] ary = table.keysEx();
        int count = table.size();
        return new Array.Enumerator.Object(ary, count);
    }
    /**
     * long
     */
    protected final IndexLong longGetCreateIndex(int kindex){
        return (IndexLong)getIndex(kindex,true,Hasharray.Types.Indeces.Long);
    }
    protected final IndexLong longGetIndex(int kindex){
        return (IndexLong)getIndex(kindex,false,Hasharray.Types.Indeces.Long);
    }
    protected final int[] longGetKeys(int kindex){
        return this.longGetCreateIndex(kindex).keysEx();
    }
    protected final int longGetKey(int kindex, int idx){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.keyEx(idx);
        }
        else {
            return 0;
        }
    }
    protected final int longIndexOfKey(int kindex, long key){
        IndexLong table = this.longGetCreateIndex(kindex);
        return table.lookup(key);
    }
    protected final int[] longIndexListOfKey(int kindex, long key){
        IndexLong table = this.longGetCreateIndex(kindex);
        return table.lookupList(key);
    }
    protected final int longIndexOfKey(int kindex, long key, int from){
        IndexLong table = this.longGetIndex(kindex);
        return table.lookup(key,from);
    }
    protected final int longLastIndexOfKey(int kindex, long key, int from){
        IndexLong table = this.longGetIndex(kindex);
        int[] list = table.lookupList(key);
        if (null == list)
            return -1;
        else {
            int llen = list.length;
            for (int cc = (llen-1), re; -1 < cc; cc--){
                re = list[cc];
                if (from >= re)
                    return re;
            }
            return -1;
        }
    }
    protected final boolean longContainsKey(int kindex, long key){
        return (-1 < this.longIndexOfKey(kindex,key));
    }
    protected final Entry longPutKey(int kindex, long key){
        IndexLong table = this.longGetCreateIndex(kindex);
        return table.put(key);
    }
    protected final Entry longPutKey(int kindex, int idx, long key){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.put(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry longInsertKey(int kindex, int idx, long key){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.insert(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry longReplaceKey(int kindex, int idx, long key){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.replace(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry longAppendKey(int kindex, long key){
        IndexLong table = this.longGetCreateIndex(kindex);
        return table.append(key);
    }
    protected final Entry longAppendKey(int kindex, int idx, long key){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.append(idx,key);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry longRemoveKeyByIndex(int kindex, int idx){
        if (-1 < idx){
            IndexLong table = this.longGetCreateIndex(kindex);
            return table.removeByIndex(idx);
        }
        else
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
    }
    protected final Entry longRemoveKeyByValue(int kindex, long key){
        IndexLong table = this.longGetCreateIndex(kindex);
        return table.removeByKey(key);
    }
    /**
     * This method always copies a safe and concise export of the internal keys list.
     */
    protected final int[] longKeyary(int kindex){
        IndexLong table = this.longGetCreateIndex(kindex);
        /*
         * is a copy
         */
        return table.keysEx();
    }
    protected final int longGetLastKey(int kindex){
        IndexLong table = this.longGetCreateIndex(kindex);
        int count = table.size();
        if (0 < count){
            int[] ary = table.keysEx();

            return ary[count-1];
        }
        else {
            return 0;
        }
    }
    protected final java.util.Enumeration longEnumerateKeys(int kindex){
        IndexLong table = this.longGetCreateIndex(kindex);
        int[] ary = table.keysEx();
        int count = table.size();
        return new Array.Enumerator.Int(ary, count);
    }
    protected final java.util.Enumeration enumerateKeys(int kindex){
        Index table = this.getIndex(kindex);
        return table.enumerateKeysEx();
    }
    protected final Object getKeyAsObject(int kindex, int idx){
        if (-1 < idx){
            Index table = this.getIndex(kindex);

            if (table instanceof IndexObject){
                return ((IndexObject)table).key(idx);
            }
            else if (table instanceof IndexLong){
                long keyValue = ((IndexLong)table).key(idx);
                if (KL_NIL == keyValue)
                    return null;
                else {
                    return new Integer( (int)keyValue);
                }
            }
            else {
                java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
                String atypeName;
                if (null != atype){
                    atypeName = atype.getName();
                }
                else {
                    atypeName = "<nil>";
                }
                throw new alto.sys.Error.State("bug "+atypeName);
            }
        }
        else
            return null;
    }
    protected final int indexOfKeyAsObject(int kindex, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).lookup(key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).lookup(keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final int indexOfKeyAsObject(int kindex, Object key, int from){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).lookup(key,from);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).lookup(keyValue,from);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final int[] indexListOfKeyAsObject(int kindex, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).lookupList(key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).lookupList(keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final int[] indexListOfKeyAsObject(int kindex, Object key, int from){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).lookupList(key,from);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).lookupList(keyValue,from);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry removeKeyByIndex(int kindex, int idx){
        if (0 > idx)
            throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(idx));
        else {
            Index table = this.getIndex(kindex);

            return table.removeByIndex(idx);
        }
    }
    protected final Entry removeKeyAsObject(int kindex, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            Object keyValue = key;
            return ((IndexObject)table).removeByKey(keyValue);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).removeByKey(keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry putKeyAsObject(int kindex, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).put(key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).put(keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry putKeyAsObject(int kindex, int idx, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).put(idx,key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).put(idx,keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry insertKeyAsObject(int kindex, int idx, Object key){
        if (0 > idx)
            throw new java.lang.ArrayIndexOutOfBoundsException(java.lang.String.valueOf(idx));
        else {
            Index table = this.getIndex(kindex);

            if (table instanceof IndexObject){

                return ((IndexObject)table).insert(idx,key);
            }
            else if (table instanceof IndexLong){
                long keyValue;
                if (null == key)
                    keyValue = KL_NIL;
                else
                    keyValue = ((Number)key).intValue();

                return ((IndexLong)table).insert(idx,keyValue);
            }
            else {
                java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
                String atypeName;
                if (null != atype){
                    atypeName = atype.getName();
                }
                else {
                    atypeName = "<nil>";
                }
                throw new alto.sys.Error.State("bug "+atypeName);
            }
        }
    }
    protected final Entry replaceKeyAsObject(int kindex, int idx, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){

            return ((IndexObject)table).replace(idx,key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();

            return ((IndexLong)table).replace(idx,keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry appendKeyAsObject(int kindex, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).append(key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).append(keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    protected final Entry appendKeyAsObject(int kindex, int idx, Object key){
        Index table = this.getIndex(kindex);

        if (table instanceof IndexObject){
            return ((IndexObject)table).append(idx,key);
        }
        else if (table instanceof IndexLong){
            long keyValue;
            if (null == key)
                keyValue = KL_NIL;
            else
                keyValue = ((Number)key).intValue();
            return ((IndexLong)table).append(idx,keyValue);
        }
        else {
            java.lang.Class atype = (null != table)?(table.getKeyType()):(null);
            String atypeName;
            if (null != atype){
                atypeName = atype.getName();
            }
            else {
                atypeName = "<nil>";
            }
            throw new alto.sys.Error.State("bug "+atypeName);
        }
    }
    //                    (((gen:end)))

    //                    (((gen:begin)))
    /**
     * List internal (verbatim)
     */
    protected final Values.Object objectGetValues(int vindex){
        return (Values.Object)this.getValues(vindex,true,Values.Types.Object);
    }
    /**
     * Object
     */
    protected final Object objectGetValue(int vindex, int idx){
        Values.Object values = this.objectGetValues(vindex);
        return values.get(idx);
    }
    protected final Object objectSetValue(int vindex, int idx, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.set(idx,value);
    }
    protected final Object objectAppendValue(int vindex, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.append(value);
    }
    protected final Object objectInsertValue(int vindex, int idx, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final Object objectRemoveValue(int vindex, int idx){
        Values.Object values = this.objectGetValues(vindex);
        return values.remove(idx);
    }
    protected final Object[] objectValary(int vindex){
        Values.Object values = this.objectGetValues(vindex);
        return values.copy();
    }
    protected final Object[] objectValary(int vindex, Class component){
        Values.Object values = this.objectGetValues(vindex);
        Object[] ary = values.get();
        if (null != ary){
            int count = ary.length;
            if (0 < count){
                Object[] copy = (Object[])java.lang.reflect.Array.newInstance(component,count);
                java.lang.System.arraycopy(ary,0,copy,0,count);
                return copy;
            }
        }
        return null;
    }
    protected final Object[] objectValaryFilter(int vindex, Class component){
        Values.Object values = this.objectGetValues(vindex);
        Object[] ary = values.get();
        if (null != ary){
            int count = ary.length;
            if (0 < count){
                Object[] copy = (Object[])java.lang.reflect.Array.newInstance(component,count);
                int oc = 0; 
                for (int ic = 0; ic < count; ic++){
                    Object item = ary[ic];
                    if (component.isAssignableFrom(item.getClass())){
                        copy[oc++] = item;
                    }
                }
                if (oc < count){
                    if (0 == oc){
                        return null;
                    }
                    else {
                        Object[] copy2 = (Object[])java.lang.reflect.Array.newInstance(component,oc);
                        java.lang.System.arraycopy(copy,0,copy2,0,oc);
                        return copy2;
                    }
                }
                else
                    return copy;
            }
        }
        return null;
    }
    protected final Object objectGetLastValue(int vindex){
        Values.Object values = this.objectGetValues(vindex);
        return values.last();
    }
    protected final Object objectSetLastValue(int vindex, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration objectEnumerateValues(int vindex){
        Values.Object values = this.objectGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean objectContainsValue(int vindex, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.contains(value);
    }
    protected final int objectIndexOfValue(int vindex, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int objectIndexOfValue(int vindex, Object value, int from){
        Values.Object values = this.objectGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int objectIndexOfValueClass(int vindex, Class jclass, int from){
        Values.Object values = this.objectGetValues(vindex);
        Object[] ary = values.get();
        if (null != ary){
            int count = values.size();
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                Object value = ary[cc];
                if (jclass.isAssignableFrom(value.getClass()))
                    return cc;
            }
        }
        return -1;
    }
    protected final int objectLastIndexOfValue(int vindex, Object value){
        Values.Object values = this.objectGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int objectLastIndexOfValue(int vindex, Object value, int from){
        Values.Object values = this.objectGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    protected final int objectLastIndexOfValueClass(int vindex, Class jclass, int from){
        Values.Object values = this.objectGetValues(vindex);
        Object[] ary = values.get();
        if (null != ary){
            int count = values.size();
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                Object value = ary[cc];
                if (jclass.isAssignableFrom(value.getClass()))
                    return cc;
            }
        }
        return -1;
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Long longGetValues(int vindex){
        return (Values.Long)this.getValues(vindex,true,Values.Types.Long);
    }
    /**
     * long
     */
    protected final long longGetValue(int vindex, int idx){
        Values.Long values = this.longGetValues(vindex);
        return values.get(idx);
    }
    protected final long longSetValue(int vindex, int idx, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.set(idx,value);
    }
    protected final long longAppendValue(int vindex, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.append(value);
    }
    protected final long longInsertValue(int vindex, int idx, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final long longRemoveValue(int vindex, int idx){
        Values.Long values = this.longGetValues(vindex);
        return values.remove(idx);
    }
    protected final long[] longValary(int vindex){
        Values.Long values = this.longGetValues(vindex);
        return values.copy();
    }
    protected final long longGetLastValue(int vindex){
        Values.Long values = this.longGetValues(vindex);
        return values.last();
    }
    protected final long longSetLastValue(int vindex, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration longEnumerateValues(int vindex){
        Values.Long values = this.longGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean longContainsValue(int vindex, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.contains(value);
    }
    protected final int longIndexOfValue(int vindex, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int longIndexOfValue(int vindex, long value, int from){
        Values.Long values = this.longGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int longLastIndexOfValue(int vindex, long value){
        Values.Long values = this.longGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int longLastIndexOfValue(int vindex, long value, int from){
        Values.Long values = this.longGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Int intGetValues(int vindex){
        return (Values.Int)this.getValues(vindex,true,Values.Types.Int);
    }
    /**
     * int
     */
    protected final int intGetValue(int vindex, int idx){
        Values.Int values = this.intGetValues(vindex);
        return values.get(idx);
    }
    protected final int intSetValue(int vindex, int idx, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.set(idx,value);
    }
    protected final int intAppendValue(int vindex, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.append(value);
    }
    protected final int intInsertValue(int vindex, int idx, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final int intRemoveValue(int vindex, int idx){
        Values.Int values = this.intGetValues(vindex);
        return values.remove(idx);
    }
    protected final int[] intValary(int vindex){
        Values.Int values = this.intGetValues(vindex);
        return values.copy();
    }
    protected final int intGetLastValue(int vindex){
        Values.Int values = this.intGetValues(vindex);
        return values.last();
    }
    protected final int intSetLastValue(int vindex, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration intEnumerateValues(int vindex){
        Values.Int values = this.intGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean intContainsValue(int vindex, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.contains(value);
    }
    protected final int intIndexOfValue(int vindex, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int intIndexOfValue(int vindex, int value, int from){
        Values.Int values = this.intGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int intLastIndexOfValue(int vindex, int value){
        Values.Int values = this.intGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int intLastIndexOfValue(int vindex, int value, int from){
        Values.Int values = this.intGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Double doubleGetValues(int vindex){
        return (Values.Double)this.getValues(vindex,true,Values.Types.Double);
    }
    /**
     * double
     */
    protected final double doubleGetValue(int vindex, int idx){
        Values.Double values = this.doubleGetValues(vindex);
        return values.get(idx);
    }
    protected final double doubleSetValue(int vindex, int idx, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.set(idx,value);
    }
    protected final double doubleAppendValue(int vindex, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.append(value);
    }
    protected final double doubleInsertValue(int vindex, int idx, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final double doubleRemoveValue(int vindex, int idx){
        Values.Double values = this.doubleGetValues(vindex);
        return values.remove(idx);
    }
    protected final double[] doubleValary(int vindex){
        Values.Double values = this.doubleGetValues(vindex);
        return values.copy();
    }
    protected final double doubleGetLastValue(int vindex){
        Values.Double values = this.doubleGetValues(vindex);
        return values.last();
    }
    protected final double doubleSetLastValue(int vindex, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration doubleEnumerateValues(int vindex){
        Values.Double values = this.doubleGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean doubleContainsValue(int vindex, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.contains(value);
    }
    protected final int doubleIndexOfValue(int vindex, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int doubleIndexOfValue(int vindex, double value, int from){
        Values.Double values = this.doubleGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int doubleLastIndexOfValue(int vindex, double value){
        Values.Double values = this.doubleGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int doubleLastIndexOfValue(int vindex, double value, int from){
        Values.Double values = this.doubleGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Float floatGetValues(int vindex){
        return (Values.Float)this.getValues(vindex,true,Values.Types.Float);
    }
    /**
     * float
     */
    protected final float floatGetValue(int vindex, int idx){
        Values.Float values = this.floatGetValues(vindex);
        return values.get(idx);
    }
    protected final float floatSetValue(int vindex, int idx, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.set(idx,value);
    }
    protected final float floatAppendValue(int vindex, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.append(value);
    }
    protected final float floatInsertValue(int vindex, int idx, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final float floatRemoveValue(int vindex, int idx){
        Values.Float values = this.floatGetValues(vindex);
        return values.remove(idx);
    }
    protected final float[] floatValary(int vindex){
        Values.Float values = this.floatGetValues(vindex);
        return values.copy();
    }
    protected final float floatGetLastValue(int vindex){
        Values.Float values = this.floatGetValues(vindex);
        return values.last();
    }
    protected final float floatSetLastValue(int vindex, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration floatEnumerateValues(int vindex){
        Values.Float values = this.floatGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean floatContainsValue(int vindex, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.contains(value);
    }
    protected final int floatIndexOfValue(int vindex, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int floatIndexOfValue(int vindex, float value, int from){
        Values.Float values = this.floatGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int floatLastIndexOfValue(int vindex, float value){
        Values.Float values = this.floatGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int floatLastIndexOfValue(int vindex, float value, int from){
        Values.Float values = this.floatGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Byte byteGetValues(int vindex){
        return (Values.Byte)this.getValues(vindex,true,Values.Types.Byte);
    }
    /**
     * byte
     */
    protected final byte byteGetValue(int vindex, int idx){
        Values.Byte values = this.byteGetValues(vindex);
        return values.get(idx);
    }
    protected final byte byteSetValue(int vindex, int idx, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.set(idx,value);
    }
    protected final byte byteAppendValue(int vindex, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.append(value);
    }
    protected final byte byteInsertValue(int vindex, int idx, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final byte byteRemoveValue(int vindex, int idx){
        Values.Byte values = this.byteGetValues(vindex);
        return values.remove(idx);
    }
    protected final byte[] byteValary(int vindex){
        Values.Byte values = this.byteGetValues(vindex);
        return values.copy();
    }
    protected final byte byteGetLastValue(int vindex){
        Values.Byte values = this.byteGetValues(vindex);
        return values.last();
    }
    protected final byte byteSetLastValue(int vindex, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration byteEnumerateValues(int vindex){
        Values.Byte values = this.byteGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean byteContainsValue(int vindex, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.contains(value);
    }
    protected final int byteIndexOfValue(int vindex, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int byteIndexOfValue(int vindex, byte value, int from){
        Values.Byte values = this.byteGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int byteLastIndexOfValue(int vindex, byte value){
        Values.Byte values = this.byteGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int byteLastIndexOfValue(int vindex, byte value, int from){
        Values.Byte values = this.byteGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Short shortGetValues(int vindex){
        return (Values.Short)this.getValues(vindex,true,Values.Types.Short);
    }
    /**
     * short
     */
    protected final short shortGetValue(int vindex, int idx){
        Values.Short values = this.shortGetValues(vindex);
        return values.get(idx);
    }
    protected final short shortSetValue(int vindex, int idx, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.set(idx,value);
    }
    protected final short shortAppendValue(int vindex, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.append(value);
    }
    protected final short shortInsertValue(int vindex, int idx, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final short shortRemoveValue(int vindex, int idx){
        Values.Short values = this.shortGetValues(vindex);
        return values.remove(idx);
    }
    protected final short[] shortValary(int vindex){
        Values.Short values = this.shortGetValues(vindex);
        return values.copy();
    }
    protected final short shortGetLastValue(int vindex){
        Values.Short values = this.shortGetValues(vindex);
        return values.last();
    }
    protected final short shortSetLastValue(int vindex, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration shortEnumerateValues(int vindex){
        Values.Short values = this.shortGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean shortContainsValue(int vindex, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.contains(value);
    }
    protected final int shortIndexOfValue(int vindex, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int shortIndexOfValue(int vindex, short value, int from){
        Values.Short values = this.shortGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int shortLastIndexOfValue(int vindex, short value){
        Values.Short values = this.shortGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int shortLastIndexOfValue(int vindex, short value, int from){
        Values.Short values = this.shortGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Boolean booleanGetValues(int vindex){
        return (Values.Boolean)this.getValues(vindex,true,Values.Types.Boolean);
    }
    /**
     * boolean
     */
    protected final boolean booleanGetValue(int vindex, int idx){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.get(idx);
    }
    protected final boolean booleanSetValue(int vindex, int idx, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.set(idx,value);
    }
    protected final boolean booleanAppendValue(int vindex, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.append(value);
    }
    protected final boolean booleanInsertValue(int vindex, int idx, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final boolean booleanRemoveValue(int vindex, int idx){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.remove(idx);
    }
    protected final boolean[] booleanValary(int vindex){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.copy();
    }
    protected final boolean booleanGetLastValue(int vindex){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.last();
    }
    protected final boolean booleanSetLastValue(int vindex, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration booleanEnumerateValues(int vindex){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean booleanContainsValue(int vindex, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.contains(value);
    }
    protected final int booleanIndexOfValue(int vindex, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int booleanIndexOfValue(int vindex, boolean value, int from){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int booleanLastIndexOfValue(int vindex, boolean value){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int booleanLastIndexOfValue(int vindex, boolean value, int from){
        Values.Boolean values = this.booleanGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    /**
     * List internal (verbatim)
     */
    protected final Values.Char charGetValues(int vindex){
        return (Values.Char)this.getValues(vindex,true,Values.Types.Char);
    }
    /**
     * char
     */
    protected final char charGetValue(int vindex, int idx){
        Values.Char values = this.charGetValues(vindex);
        return values.get(idx);
    }
    protected final char charSetValue(int vindex, int idx, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.set(idx,value);
    }
    protected final char charAppendValue(int vindex, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.append(value);
    }
    protected final char charInsertValue(int vindex, int idx, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.insert(idx,value);
    }
    protected final char charRemoveValue(int vindex, int idx){
        Values.Char values = this.charGetValues(vindex);
        return values.remove(idx);
    }
    protected final char[] charValary(int vindex){
        Values.Char values = this.charGetValues(vindex);
        return values.copy();
    }
    protected final char charGetLastValue(int vindex){
        Values.Char values = this.charGetValues(vindex);
        return values.last();
    }
    protected final char charSetLastValue(int vindex, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.last(value);
    }
    protected final java.util.Enumeration charEnumerateValues(int vindex){
        Values.Char values = this.charGetValues(vindex);
        return values.enumerate();
    }
    protected final boolean charContainsValue(int vindex, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.contains(value);
    }
    protected final int charIndexOfValue(int vindex, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.indexOf(value);
    }
    protected final int charIndexOfValue(int vindex, char value, int from){
        Values.Char values = this.charGetValues(vindex);
        return values.indexOf(value,from);
    }
    protected final int charLastIndexOfValue(int vindex, char value){
        Values.Char values = this.charGetValues(vindex);
        return values.lastIndexOf(value);
    }
    protected final int charLastIndexOfValue(int vindex, char value, int from){
        Values.Char values = this.charGetValues(vindex);
        return values.lastIndexOf(value,from);
    }
    protected final java.util.Enumeration enumerateValues(int vindex){
        Values values = this.getValues(vindex);
        return values.enumerate();
    }
    protected final Object getValueAsObject(int vindex, int idx){
        Values values = this.getValues(vindex);
        return values.getAsObject(idx);
    }
    protected final Object setValueAsObject(int vindex, int idx, Object value){
        Values values = this.getValues(vindex);
        return values.setAsObject(idx,value);
    }
    protected final Object appendValueAsObject(int vindex, Object value){
        Values values = this.getValues(vindex);
        return values.appendAsObject(value);
    }
    protected final Object insertValueAsObject(int vindex, int idx, Object value){
        Values values = this.getValues(vindex);
        return values.insertAsObject(idx,value);
    }
    protected final Object removeValueAsObject(int vindex, int idx){
        Values values = this.getValues(vindex);
        return values.removeAsObject(idx);
    }
    //                    (((gen:end)))



    /**
     * Print table with bracket wrappers, "name equals value, comma"
     * format, as in the following example.
     *
     * <pre>
     * "[" NAME1 "=" VALUE1 "," NAME2 "=" VALUE2 "]"
     * </pre> 
     */
    public String toString(){
        return Chbuf.cat( "[", toString('=',','), "]");
    }
    /**
     * Print elements with characters "subinfix" and "infix" as in the
     * following example for two name, value pairs.
     *
     * <pre>
     *    NAME1 subinfix VALUE1 infix NAME2 subinfix VALUE2
     * </pre> 
     *
     * <p> Uses <tt>"Chbuf.append(Object)"</tt> which defaults to
     * <tt>"Object.toString()".</tt>
     *
     * @param subinfix Character between elements of a name- value
     * pair, eg, <tt>'='.</tt>
     *
     * @param infix Character between name- value pairs, eg,
     * <tt>','</tt> or <tt>'\n'</tt>.
     */
    public String toString( char subinfix, char infix){
        Chbuf sb = new Chbuf();
        java.util.Enumeration keys = this.enumerateKeys(0);
        java.lang.Object k; 
        java.lang.Object v;
        String ks, vs;
        for (int idx = 0; keys.hasMoreElements(); idx++){
            k = keys.nextElement();
            if (null != k){
                ks = k.toString();
                if ( null != ks){
                    if ( 0 < idx)
                        sb.append(infix);
                    sb.append(ks);
                    v = this.getValueAsObject(0,idx);
                    if ( null != v){
                        vs = v.toString();
                        if ( null != vs){
                            sb.append(subinfix);
                            sb.append(vs);
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

}
