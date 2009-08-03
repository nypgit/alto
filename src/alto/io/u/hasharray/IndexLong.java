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
package alto.io.u.hasharray;

import alto.io.u.Hasharray;
import alto.io.u.Array;


/**
 * <p> Extensible index for object keys. </p>
 *
 * <p> The "keys" array is initialized by this constructor, and is
 * maintained in parallel with "vals" by objmap. </p>
 *
 */
public class IndexLong
    extends Index
    implements Keys.Long
{

    public final static long HASH_MASK = 0x7FFFFFFFL;

    /**
     * <p> Note that the "keys" array is particularly sensitive as
     * index data. </p>
     */
    protected long[] keys ;

    protected int[] keysEx ;


    public IndexLong(Hasharray parent, int initial, float load){
        super(parent,initial,load);
        this.keys = new long[this.grow];
    }


    public final java.lang.Class getKeyType(){
        return Values.Types.Long;
    }
    public Index cloneIndex(){
        IndexLong clone = (IndexLong)super.cloneIndex();
        clone.keys = (long[])this.keys.clone();
        return clone;
    }
    public final long[] keys(){
        return this.keys;
    }
    /**
     * This method always returns a safe and concise copy.  It must
     * copy because it needs to export a foreign external key type,
     * therefore it does a concise (not optimistic) copy.
     * 
     * @see IndexObject#keysEx()
     */
    public final int[] keysEx(){
        int[] ex = this.keysEx;
        if (null == ex){
            int len = this.count;
            if (0 < len){
                long[] keys = this.keys;
                ex = new int[len];
                for (int cc = 0; cc < len; cc++){
                    long k = keys[cc];
                    if (KL_NIL != k)
                        ex[cc] = (int)k;
                    else
                        ex[cc] = 0;
                }
                this.keysEx = ex;
            }
        }
        return ex;
    }
    public final java.util.Enumeration enumerateKeysEx(){
        return new alto.io.u.Array.Enumerator.Int(this.keysEx(),this.count);
    }

    public final long key(int idx){
        return this.keys[idx];
    }
    public final int keyEx(int idx){
        long value = this.keys[idx];
        if (KL_NIL == value)
            return 0;
        else
            return (int)value;
    }
    private final Entry key(Entry ent, long key){
        int aryix = ent.aryix;
        aryix = this.key(aryix,key);
        ent.aryix = aryix;
        return ent;
    }
    private final Entry key(Entry ent, int idx, long key){
        int aryix = this.key(idx,key);
        ent.aryix = aryix;
        return ent;
    }
    private final int key(int idx, long key){

        if (0 > idx)
            idx = this.count;

        long[] keys = this.keys;
        int keys_len = keys.length;
        if (idx < keys_len){
            keys[idx] = key;
            return idx;
        }
        else {
            int n_len = (keys_len+this.grow);
            if (idx < n_len){
                keys = Array.grow(keys,n_len);
                this.keys = keys;
                keys[idx] = key;
                return idx;
            }
            else
                throw new alto.sys.Error.State(java.lang.String.valueOf(idx));
        }
    }
    public final Entry lookupEntry(long key){
        if (KL_NIL == key)
            return null;
        else {
            long hash = this.hash(key);
            Entry table[] = this.table;
            int index = ((int)(hash & HASH_MASK) % table.length);
            for (Entry e = table[index] ; e != null ; e = e.next){
                if (hash == e.hash && 
                    e.kequals(this,key))
                    {
                        return e;
                    }
            }
            return null;
        }
    }
    public final int lookup(long key){
        Entry ent = this.lookupEntry(key);
        if (null == ent)
            return -1;
        else
            return ent.aryix;
    }
    public final int lookup(long key, int from){
        if (KL_NIL == key)
            return -1;
        else {
            long hash = this.hash(key);
            Entry table[] = this.table;
            int index = ((int)(hash & HASH_MASK) % table.length), re;
            for (Entry e = table[index] ; e != null ; e = e.next){
                if (hash == e.hash && 
                    e.kequals(this,key))
                    {
                        re = e.aryix;
                        if (from <= re)
                            return re;
                    }
            }
            return -1;
        }
    }
    public final int[] lookupList(long key){
        if (KL_NIL == key)
            return null;
        else {
            long hash = this.hash(key);
            Entry table[] = this.table;
            int list[] = null;
            int index = ((int)(hash & HASH_MASK) % table.length);
            for (Entry e = table[index] ; e != null ; e = e.next){
                if (hash == e.hash && 
                    e.kequals(this,key))
                    {
                        list = Array.add(list,e.aryix);
                    }
            }
            return list;
        }
    }
    public final int[] lookupList(long key, int from){
        if (KL_NIL == key)
            return null;
        else {
            long hash = this.hash(key);
            Entry table[] = this.table;
            int list[] = null, re;
            int index = ((int)(hash & HASH_MASK) % table.length);
            for (Entry e = table[index] ; e != null ; e = e.next){
                if (hash == e.hash && 
                    e.kequals(this,key))
                    {
                        re = e.aryix;
                        if (from <= re)
                            list = Array.add(list,re);
                    }
            }
            return list;
        }
    }
    private final Entry removeIn(long key){
        if (KL_NIL == key)
            return null;
        else {
            Entry table[] = this.table;
            long hash = this.hash(key);
            int index = (((int)(hash & HASH_MASK)) % table.length);
            for (Entry ie = table[index], le = null ; ie != null ; le = ie, ie = ie.next){
                if ((hash == ie.hash) &&
                    ie.kequals(this,key))
                    {
                        return this.removeIn(le,ie,index);
                    }
            }
            return null;
        }
    }
    public final Entry removeByKey(long key){
        Entry ent = this.removeIn(key);
        if (null != ent){
            this.count -= 1;
            int idx = ent.aryix;
            Array.shift(this.keys,idx);		
        }
        return ent;
    }
    @Override
    public final Entry removeByIndex(int idx){
        Entry ent = super.removeByIndex(idx);
        if (null != ent){
            if (idx != ent.aryix)
                throw new alto.sys.Error.State("bug");
            else {
                this.count -= 1;
                Array.shift(this.keys,idx);
            }
        }
        return ent;
    }
    public final Entry put(long key){
        if (KL_NIL == key)
            return null;
        else {
            long hash = key;
            Entry table[] = this.table;
            int index = ((int)(hash & HASH_MASK) % table.length);
            /*
             * Lookup
             */
            for (Entry ent = table[index] ; ent != null ; ent = ent.next){
                if (ent.hash == hash){
                    if (ent.kequals(this,key))
                        return this.key(ent,key);
                }
            }
            //
            if (this.threshold()){
                this.rehash();
                return this.put(key);
            } 
            else {
                Entry nent = this.newEntry(hash);
                //
                this.key(nent,key);
                table[index] = Entry.List.Append(table[index],nent);
                this.count += 1;
                //
                return nent;
            }
        }
    }
    public final Entry put(int idx, long key){
        if (KL_NIL == key || 0 > idx)
            return null;
        else {
            long hash = key;
            Entry table[] = this.table;
            int index = ((int)(hash & HASH_MASK) % table.length);
            /*
             * Lookup
             */
            for (Entry ent = table[index] ; ent != null ; ent = ent.next){
                if (ent.hash == hash){
                    if (ent.kequals(this,key))
                        return this.key(ent,key);
                }
            }
            //
            if (this.threshold()){
                this.rehash();
                return this.put(idx,key);
            } 
            else {
                Entry nent = this.newEntry(hash);
                //
                this.key(nent,idx,key);
                table[index] = Entry.List.Append(table[index],nent);
                this.count += 1;
                //
                return nent;
            }
        }
    }
    public final Entry insert(int idx, long key){
        if (KL_NIL == key || 0 > idx)
            return null;
        else if (this.threshold()){
            this.rehash();
            return this.append(key);
        } 
        else {
            Entry table[] = this.table, re = null;
            long hash = this.hash(key);
            int index = (((int)(hash & HASH_MASK)) % table.length);
            Entry ne = this.newEntry(hash), ie;
            for (int ii = 0, il = table.length; ii < il; ii++){
                if (ii == index){
                    ie = table[ii];
                    if (null == ie)
                        table[ii] = ne;
                    else {
                        boolean nindexed = true;
                        Entry le = null;
                        for (; null != ie; le = ie, ie = ie.next){
                            if (hash == ie.hash){
                                if (ie.kequals(this,key)){
                                    if (ie.aryix <= idx){
                                        ne.next = ie.next;
                                        ie.next = ne;
                                    }
                                    else if (null == le){
                                        /*
                                         * Insert for (idx >  ie.aryix)
                                         */
                                        table[ii] = ne;
                                        ne.next = ie;
                                    }
                                    else {
                                        le.next = ne;
                                        ne.next = ie;
                                    }
                                    nindexed = false;
                                }
                            }
                            if (ie != ne && idx <= ie.aryix)
                                /*
                                 * Increment pointers for insert
                                 */
                                ie.aryix += 1;
                        }
                        if (nindexed){
                            ne.next = table[ii];
                            table[ii] = ne;
                        }
                    }
                }//(if (ii == index)
                else {
                    for (ie = table[ii]; null != ie; ie = ie.next){
                        if (idx <= ie.aryix)
                            /*
                             * Increment pointers for insert
                             */
                            ie.aryix += 1;
                    }
                }
            }
            //
            this.key(ne,idx,key);
            this.count += 1;
            //
            return ne;
        }
    }
    public final Entry replace(int idx, long nkey){
        Entry table[] = this.table;
        long okey = this.key(idx);
        long nhash = this.hash(nkey);
        long ohash = this.hash(okey);

        int nindex = (((int)(nhash & HASH_MASK)) % table.length);
        int oindex = (((int)(ohash & HASH_MASK)) % table.length);
        Entry nent = this.replaceIn(idx,nhash,nindex,oindex);

        return this.key(nent,idx,nkey);
    }
    public final Entry append( long key){
        if (KL_NIL == key)
            return null;
        else if (this.threshold()){
            this.rehash();
            return this.append(key);
        } 
        else {
            Entry table[] = this.table;
            long hash = this.hash(key);
            int index = ((int)(hash & HASH_MASK) % table.length);
            Entry ne = this.newEntry(hash);
            //
            this.key(ne,key);
            table[index] = Entry.List.Append(table[index],ne);
            this.count += 1;
            //
            return ne;
        }
    }
    public final Entry append( int idx, long key){
        if (KL_NIL == key || 0 > idx)
            return null;
        else if (this.threshold()){
            this.rehash();
            return this.append(key);
        } 
        else {
            Entry table[] = this.table;
            long hash = this.hash(key);
            int index = ((int)(hash & HASH_MASK) % table.length);
            Entry ne = this.newEntry(hash);
            //
            this.key(ne,idx,key);
            table[index] = Entry.List.Append(table[index],ne);
            this.count += 1;
            //
            return ne;
        }
    }
}
