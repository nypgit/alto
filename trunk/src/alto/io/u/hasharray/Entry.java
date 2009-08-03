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

/**
 * <p> Extensible {@link Index} table record</p>
 * 
 */
public class Entry 
    extends Object
    implements Cloneable,
               IndexConstants
{
    /**
     * Tools for working on the linked and array lists of entries.
     */
    public final static class List {

        /**
         * <p> Copy the linked list to an array for the consistent
         * iteration over this list of table elements during changes
         * to the list. </p>
         */
        public final static Entry[] Copy( Entry li){
            if (null == li)
                return null;
            else if (null == li.next)
                return new Entry[]{li};
            else {
                int bl = 10;/*(init optimistic output buffer)
                             */
                Entry[] re = new Entry[bl], copier;
                int rx = 0, rl = re.length;
                for (Entry pp = li; null != pp; rx += 1, pp = pp.next){
                    if (rx >= rl){
                        copier = new Entry[rl+bl];/*(grow output buffer optimistically)
                                                   */
                        System.arraycopy(re,0,copier,0,rl);
                        re = copier;
                        rl += bl;
                    }
                    re[rx] = pp;
                }
                if (rx < rl){
                    /*(truncate optimistic buffer)
                     */
                    copier = new Entry[rx];
                    System.arraycopy(re,0,copier,0,rx);
                    return copier;
                }
                else
                    return re;
            }
        }
        /**
         * <p> Append the element 'in' to the list 'li', maintaining
         * both the uniqueness of the element 'in' within the set 'li'
         * and the input order represented by each elements 'aryix'
         * value. </p>
         * 
         * <p> The basic action here is to append 'in' to 'li', while
         * the 'aryix' value of 'li' can require an insertion for
         * maintaining the index table in the input order of the keys
         * and values arrays of the hasharray subclass. </p>
         * 
         * @param li Table list
         * @param in Table element 
         */
        public final static Entry Append( Entry li, Entry in){
            if (null == li){
                in.next = null;
                return in;
            }
            else if (null == in)
                throw new IllegalArgumentException();
            else if (li == in)
                return li;
            else {
                int ix = in.aryix;
                if (Entry.XINIT < ix){
                    /*
                     * Insert (in) into list (li) where (ix=in.aryix) < (lx=lp.aryix)
                     */
                    int lx;
                    Entry ll = null, lp = li;
                    for (; null != lp; ll = lp, lp = lp.next){
                        if (in == lp)
                            return li;
                        else {
                            lx = lp.aryix;
                            if (ix < lx){
                                if (null == ll){
                                    in.next = lp;
                                    return in;
                                }
                                else {
                                    in.next = lp;
                                    ll.next = in;
                                    return li;
                                }
                            }
                            else if (ix == lx){
                                /*
                                 * Replace entry
                                 */
                                if (null == ll){
                                    in.next = lp.next;
                                    lp.next = null;//delete(lp)
                                    return in;
                                }
                                else {
                                    ll.next = in;
                                    in.next = lp.next;
                                    return li;
                                }
                            }
                        }
                    }
                    //
                    ll.next = in;
                    in.next = null;
                    return li;
                }
                else
                    throw new alto.sys.Error.State();
            }
        }
        public Entry[] Add(Entry[] list, Entry item){
            if (null == list)
                return new Entry[]{item};
            else {
                int list_len = list.length;
                switch(list_len){
                case 1:
                    return new Entry[]{list[0],item};
                case 2:
                    return new Entry[]{list[0],list[1],item};
                case 3:
                    return new Entry[]{list[0],list[1],list[2],item};
                default:
                    Entry[] nlist = new Entry[list_len+1];
                    java.lang.System.arraycopy(list,0,nlist,0,list_len);
                    nlist[list_len] = item;
                    return nlist;
                }
            }
        }


    }

    public final static int XINIT = -1;

    /**
     * Thirty two bit value, or high bit on for nil.
     * @see hasharray.KL_NIL
     */
    public final long hash;

    /** <p> Initialized to {@link #XINIT}. </p> 
     */
    public int aryix = XINIT;

    protected Entry next;


    public Entry(long hash){
        super();
        this.hash = hash;
    }


    /**
     * @return Clean 31 bit value (positive integer) for table
     * arithmetic (modulo table length).
     */
    public final long hash(){
        return (this.hash & HASH_MASK);
    }
    /**
     * <p> This method is called from the index.
     * <pre>
     *	  return (keys.key(this.aryix).equals(query));
     * </pre>
     * </p> 
     * @param keys Key container
     * @param index Container
     * @param query Test key identity with internal value
     * @return Key identity
     */
    public final boolean kequals( Keys.Object keys, java.lang.Object query){
        int aryix = this.aryix;
        if (XINIT < aryix){
            java.lang.Object key = keys.key(aryix);
            if (KO_NIL != key)
                return key.equals(query);
        }
        return false;
    }
    /**
     * <p> This method is called from the index.
     * <pre>
     *	  return (keys.key(this.aryix) == (query));
     * </pre>
     * </p> 
     * @param keys Key container
     * @param index Container
     * @param query Test key identity with internal value
     * @return Key identity
     */
    public final boolean kequals( Keys.Long keys, long query){
        int aryix = this.aryix;
        if (XINIT < aryix){
            long key = keys.key(aryix);
            if (KL_NIL != key)
                return (key == query);
        }
        return false;
    }

    protected java.lang.Object clone(){
        return this.cloneEntry();
    }
    protected Entry cloneEntry(){
        try {
            Entry entry = (Entry)super.clone();
            entry.next = (null != this.next)?(this.next.cloneEntry()):(null);
            return entry;
        }
        catch (CloneNotSupportedException cns){
            throw new alto.sys.Error.State();
        }
    }
}
