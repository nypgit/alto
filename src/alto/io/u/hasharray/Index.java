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

/**
 * <p> Extensible index for the {@link
 * alto.io.u.hasharray}. </p>
 * 
 * <p> Index methods are responsible for maintaining both the collision
 * table and key array.  </p>
 * 
 * @see IndexObject
 * @see IndexLong
 * @see alto.io.u.Hasharray
 */
public abstract class Index
    implements java.lang.Cloneable,
               alto.io.u.hasharray.IndexConstants
{
    public final static int NotFound = -1;


    /**
     * <p> A value between zero and one that defines the threshold
     * for reindexing. </p>
     */
    public final float load;
    /**
     * <p> Internal capacity growth factor. </p> 
     */
    public final int grow;
    /**
     * <p> Collision map. </p> 
     */
    protected Entry table[];
    /**
     * <p> Current boundary for reindexing is table length times
     * load. </p>
     */
    protected int threshold;

    protected int count;


    protected Index(Hasharray parent, int initial, float load){
        super();
        if ( null == parent || (initial <= 0) || (load <= 0f) || (load >= 1f))
            throw new IllegalArgumentException();
        else {
            if (0 == (initial & 1))
                initial |= 1;
		
            this.load = load;
            this.table = new Entry[initial];
            this.threshold = (int)((float)initial * load);
            this.grow = initial;
        }
    }


    public final int size(){
        return this.count;
    }
    public abstract java.lang.Class getKeyType();

    /**
     * Enumerate keys in export type.
     * @see IndexLong#enumerateKeysEx()
     * @see IndexObject#enumerateKeysEx()
     */
    public abstract java.util.Enumeration enumerateKeysEx();

    protected Entry newEntry(long hash){
        return new Entry(hash);
    }
    /**
     * @return Non negative 32 bit (31 bits) value for key hash code
     */
    protected long hash(java.lang.Object key){
        if (null == key)
            return 0;
        else if (key instanceof java.lang.String){
            java.lang.String string = (java.lang.String)key;
            /*
             * known - portably - consistent hash
             */
            byte[] raw = alto.io.u.Utf8.encode(string);
            return alto.hash.Function.Xor.Hash32(raw);
        }
        else
            return (key.hashCode());
    }
    /**
     * @return Non negative 32 bit (31 bits) value for key hash code
     */
    protected long hash(long key){
        return (key & HASH_MASK);
    }
    public void destroy(){
        this.count = 0;
        this.table = null;
    }
    public void clear(){
        this.count = 0;
        Entry table[] = this.table;
        for (int index = 0, tlen = table.length; index < tlen; index++){
            table[index] = null;
        }
    }
    protected java.lang.Object clone(){
        return this.cloneIndex();
    }
    public Index cloneIndex(){
        try {
            Index index = (Index)super.clone();
            index.table = (Entry[])this.table.clone();
            Entry ent;
            for (int ac = 0, an = this.table.length, bc, bn; ac < an; ac++){
                ent = index.table[ac];
                if (null != ent)
                    index.table[ac] = ent.cloneEntry();
            }
            return index;
        }
        catch (CloneNotSupportedException cns){
            throw new alto.sys.Error.State();
        }
    }
    /** <p> Grow the table. </p>
     */
    protected final void rehash(){
        Entry ot[] = this.table, pp;
        int olen = ot.length, px, pn;
        int nlen = (olen + this.grow), cc, index;
        if (0 == (nlen & 1))
            nlen |= 1;
        Entry nt[] = new Entry[nlen],  pl[];
        this.threshold = (int)(nlen * this.load);
        this.table = nt;
        for (cc = (olen-1); -1 < cc; cc--){
            pl = Entry.List.Copy(ot[cc]);
            if (null != pl)
                for (px = 0, pn = pl.length; px < pn; px++){
                    pp = pl[px];
                    index = (((int)this.hash(pp.hash)) % nlen);
                    nt[index] = Entry.List.Append(nt[index],pp);
                }
        }
    }
    protected final boolean threshold(){
        return (this.count >= this.threshold);
    }
    protected final Entry replaceIn(int idx, long nhash, int nindex, int oindex){
        Entry[] table = this.table;
        Entry ne = this.newEntry(nhash), ie, le;
        /*
         * Drop OE
         */
        for (le = null, ie = table[oindex]; null != ie; le = ie, ie = ie.next){
            if (idx == ie.aryix){
                if (null == le)
                    table[oindex] = ie.next;
                else 
                    le.next = ie.next;

                break;
            }
        }
        /*
         * Append NE
         */
        table[nindex] = Entry.List.Append(table[nindex],ne);
        //
        return ne;
    }
    protected final Entry removeIn(Entry le, Entry ie, int index){
        Entry[] table = this.table;
        Entry re = ie;
        int aryix = ie.aryix;
        /*
         * Drop IE
         */
        if (null != le) 
            le.next = ie.next;
        else 
            table[index] = ie.next;
        /*
         * Sanitize RE=IE
         */
        re.next = null;
        /*
         * Decrement pointers for remove
         */
        for (int tc = 0, tlen = table.length; tc < tlen; tc++){
            ie = table[tc];
            while ( null != ie){
                if (aryix < ie.aryix)
                    ie.aryix -= 1;
                ie = ie.next;
            }
        }
        return re;
    }
    /**
     * This is overridden in subclasses.
     * 
     * @return Dropped duplicate index entry 
     */
    public Entry removeByIndex( int idx){
        Entry table[] = this.table, ie, le, re = null;
        int iearyix;
        scanindex:
        for (int tx = 0, tl = table.length; tx < tl; tx++){
            for (le = null, ie = table[tx]; null != ie; le = ie, ie = ie.next){
                iearyix = ie.aryix;
                if (idx < iearyix)
                    /*
                     * Decrement pointers for remove
                     */
                    ie.aryix -= 1;

                else if (idx == iearyix){
                    /*
                     * Drop IE
                     */
                    re = ie;
                    if (null == le)
                        table[tx] = ie.next;
                    else
                        le.next = ie.next;
                    re.next = null;
                }
            }
        }
        return re;
    }
}
