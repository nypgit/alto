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

import alto.io.u.hasharray.Entry;
import alto.io.u.hasharray.Index;
import alto.io.u.hasharray.IndexObject;
import alto.io.u.hasharray.Values;

/**
 * <p> {@link frame} map may be the child frame of a linked relation
 * to an {@link Objmap} (or {@link Obfmap}) parent frame.  Mapped
 * values in the child and parent frames implement the {@link frame}
 * interface, which identifies directional, one to one links between
 * the mapped values from the child frame to the parent frame.  </p>
 * 
 * <p> {@link Obfmap} is a subclass of {@link Objmap} for frame
 * relations.  A {@link frame} relation links a child hash array to a
 * parent hash array for collisions and distinctions (sorted by the
 * frame list for the frame map).  The code here depends on {@link
 * frame} class values that link themselves independently, and that
 * these links are in the subject maps.  The {@link Obfmap} is highly
 * dependent on the {@link hasharray}, while highly independent of the
 * linking that its applications may implement.  </p>
 * 
 * <p> The basic hash array is the class {@link Objmap} implementing
 * {@link frame}.  The {@link Objmap} can be the parent (target) of a
 * frame relation, but not the child.  Frame relations are linear, and
 * can be chained to any number (subject only to practical or systemic
 * limits). </p>
 * 
 * <p> This frame map maintains a {@link Obfmap$framelist} mapping the
 * frame relation sources with targets.  The frame list is updated
 * when the relation becomes stale, when either of the parent or child
 * of the relation become stale.  </p>
 * 
 * <h3>Application notes</h3>
 * 
 * <p> The conventional use of frame relations is to implement some
 * kind of inheritance.  The inheritance algorithm defines the linking
 * between {@link frame} values.  The {@link Obfmap#frameInit} method
 * is then called to define the {@link Obfmap$framelist framelist}.
 * </p>
 * 
 *
 * 
 * @author jdp
 * @since 1.2
 */
public class Obfmap 
    extends Objmap
{
    public final static java.lang.Class CLA_NIL = null;

    /**
     * <p> Index set describes the client user exposed nodeset of a
     * frame map in a frame relation.  </p>
     * 
     * <h3>Structure</h3>
     * 
     * <p> The integer value lists <i>xfc</i> and <i>xfp</i> are
     * maintained in parallel.  Only the keys of these
     * {@link alto.io.u.intint} maps are used.  Their values 
     * are ignored.  </p>
     * 
     * <p> The <b>xfc</b> array is constructed in node array order by
     * {@link #frameInit}.  This is a list of negative one for no client
     * frame member in this slot, or an index value greater than
     * negative one for an index into the client frame.  </p>
     * 
     * <p> The <b>xfp</b> array is parallel, indexing members of the
     * parent frame.  </p>
     * 
     * @author jdp
     */
    public final static class framelist
    /*
     * [TODO] ?(rewrite for "extends intint" with multiple indeces)?
     */
        extends java.lang.Object
        implements java.lang.Cloneable
    {
        private boolean stale =  true;
        /**
         * List parallel with xfp in global list order.  If greater
         * than negative one, the parent frame index value.
         */
        private Intint xfp = new Intint();
        /**
         * List parallel with xfc in global list order.  If greater
         * than negative one, the child frame index value.
         */
        private Intint xfc = new Intint();

        framelist(){
            super();
        }
        protected java.lang.Object clone(){
            return this.cloneFramelist();
        }
        public framelist cloneFramelist(){
            try {
                framelist clone = (framelist)super.clone();
                clone.xfp = this.xfp.cloneIntint();
                clone.xfc = this.xfc.cloneIntint();
                return clone;
            }
            catch (java.lang.CloneNotSupportedException cns){
                throw new java.lang.IllegalStateException();
            }
        }
        int size(){
            return this.xfc.size();
        }
        void stale(){
            this.stale = true;
        }
        boolean ok(){
            return (!this.stale);
        }
        boolean nok(){
            return (this.stale);
        }
        /** <p> As <code>reset(false)</code>. </p> 
         */
        synchronized void reset(){
            this.stale = true;
            this.xfc.clear();
            this.xfp.clear();
            this.stale = false;
        }
        synchronized void reset(boolean stale){
            this.stale = true;
            this.xfc.clear();
            this.xfp.clear();
            this.stale = stale;
        }
        /**
         * @param gidx Called in global index sequence from zero to
         * global (linked + exposed) set length
         * @param fp_idx Greater than negative one parent frame index for gidx
         * @param fc_idx Greater than negative one client frame index for gidx
         */
        void update( int gidx, int fp_idx, int fc_idx){
            int sz = this.size();
            if (gidx != sz){
                if (-1 < gidx && gidx < sz){
                    this.xfc.replace(gidx,fc_idx,fc_idx);
                    this.xfp.replace(gidx,fp_idx,fp_idx);
                    return;
                }
                else
                    throw new java.lang.IllegalStateException("bug:gidx "+gidx+" != "+sz);
            }
            else {
                if ((-1 < fp_idx)&&(this.hasPxNot(fp_idx))){
                    this.xfc.append(fc_idx,fc_idx);
                    this.xfp.append(fp_idx,fp_idx);
                }
                else {
                    if (0 > fc_idx)
                        return;
                    else {
                        this.xfc.append(fc_idx,fc_idx);
                        this.xfp.append(-1,-1);
                    }
                }
            }
        }
        /**
         */
        void update_fin(){
            this.stale = false;
        }
        /** @return Whether this parent frame slot index is exposed or
         * overridden by a client frame slot.
         */
        boolean hasPx( int px){
            return (-1 < this.xfp.indexOf(px));
        }
        boolean hasPxNot( int px){
            return (0 > this.xfp.indexOf(px));
        }
        /** @return Whether this child frame index overrides a slot in
         * the parent frame
         */
        boolean hasCx( int cx){
            return (-1 < this.xfc.indexOf(cx));
        }
        boolean hasCxNot( int cx){
            return (0 > this.xfc.indexOf(cx));
        }
        /** @return Given a parent frame index, return the overriding
         * child frame slot index or negative one
         */
        int cx4px( int px){
            int gidx = this.xfp.indexOf(px);
            if (-1 < gidx)
                return this.xfc.key(gidx);
            else
                return -1;
        }
        /** @return Given a child frame index, return the linked
         * parent frame slot index or negative one
         */
        int px4cx( int cx){
            int gidx = this.xfc.indexOf(cx);
            if (-1 < gidx)
                return this.xfp.key(gidx);
            else
                return -1;
        }
        /**
         * @return Global index has parent member not overridden
         */
        boolean infp(int gidx){
            /*(not over- ridden)
             */
            return (-1 < this.px4gx(gidx))&&(0 > this.cx4gx(gidx));
        }
        /**
         * @return Global index has client member
         */
        boolean infc(int gidx){
            /*(present)
             */
            return (-1 < this.cx4gx(gidx));
        }
        /** @return Parent frame index for global index
         */
        int px4gx(int gidx){
            if (-1 < gidx && gidx < this.size())
                return this.xfp.key(gidx);
            else
                return -1;
        }
        /** @return Child frame index for global index
         */
        int cx4gx(int gidx){
            if (-1 < gidx && gidx < this.size())
                return this.xfc.key(gidx);
            else
                return -1;
        }

        private int x4gxf(boolean fc, int gidx, boolean asc){
            if (-1 < gidx && gidx < this.size()){
                int tmpi;
                if (fc)
                    tmpi = this.xfc.key(gidx);
                else
                    tmpi = this.xfp.key(gidx);
                //
                if (-1 < tmpi)
                    return tmpi;
                else {
                    /*
                     * This can be non-linear and so can cause
                     * infinite loops -- a monotonic algo will take
                     * more time than available today -- don't think
                     * this code is actually used in practice.
                     */
                    int sz = this.size();
                    if (asc){
                        if (0 == gidx)
                            return 0;
                        else {
                            for (int test = gidx; test < sz; test++){
                                if (fc)
                                    tmpi = this.xfc.key(test);
                                else
                                    tmpi = this.xfp.key(test);
                                //
                                if (-1 < tmpi)
                                    return tmpi;
                            }
                            return sz;
                        }
                    }
                    else {
                        if (gidx >= (sz-1))
                            return gidx;
                        else {
                            for (int test = gidx; -1 < test; test--){
                                if (fc)
                                    tmpi = this.xfc.key(test);
                                else
                                    tmpi = this.xfp.key(test);
                                //
                                if (-1 < tmpi)
                                    return tmpi;
                            }
                            return 0;
                        }
                    }
                }
            }
            else
                return -1;
        }
        /** @return Parent frame region index for global index
         */
        int px4gxf(int gidx, boolean asc){
            return this.x4gxf(false,gidx,asc);
        }
        /** @return Child frame region index for global index
         */
        int cx4gxf(int gidx, boolean asc){
            return this.x4gxf(true,gidx,asc);
        }
        /** @return Global index for child frame index
         */
        int gx4cx(int fcx){
            if (0 > fcx)
                return -1;
            else 
                return this.xfc.indexOf(fcx);
        }
        /** @return Global index for parent frame index
         */
        int gx4px(int fpx){
            if (0 > fpx)
                return -1;
            else 
                return this.xfp.indexOf(fpx);
        }
        void drop(int gidx){
            this.xfc.drop(gidx);
            this.xfp.drop(gidx);
        }
    }



    private framelist frame_distinct;

    public Obfmap(int init, float load){
        super(init,load);
    }
    public Obfmap(int init){
        super(init);
    }
    public Obfmap(){
        super();
    }

    public void clear(){
        if (null != this.frame_distinct)
            this.frame_distinct.reset();
        super.clear();
    }
    private void stale(){
        if (null != this.frame_parent){
            if (null == this.frame_distinct)
                this.frame_distinct = new framelist();
            else
                this.frame_distinct.stale = true;
        }
    }
    public final boolean frameStale(){
        if (null != this.frame_parent){
            if ((null == this.frame_distinct)||(this.frame_distinct.nok()))
                return true;//(known stale)
            else
                return this.frame_parent.frameStale();//(may be stale)
        }
        else
            return false;//(cant be stale, with distinct corner case ok for init)
    }
    public final boolean frameStaleNot(){
        return (!this.frameStale());
    }
    public final boolean frameParentSet(Objmap pf){
        if (null == pf){
            this.frame_parent = null;
            if (null != this.frame_distinct)
                this.frame_distinct.reset();
            return true;
        }
        else if (null == this.frame_parent){
            if (this == pf)
                throw new java.lang.IllegalStateException("cyclic frame reference identical");
            else {
                this.frame_parent = pf;
                if (null != this.frame_distinct)
                    this.frame_distinct.reset(true);
                return true;
            }
        }
        else
            return false;
    }
    public final void frameParentReset(Objmap pf){
        if (null == pf){
            this.frame_parent = null;
            if (null != this.frame_distinct)
                this.frame_distinct.reset();
        }
        else {
            if (this == pf)
                throw new java.lang.IllegalStateException("cyclic frame reference identical");
            else {
                if (null != this.frame_distinct)
                    this.frame_distinct.reset(true);
                this.frame_parent = pf;
            }
        }
    }
    public final void frameInit(){
        Objmap fp = this.frameGet();
        if (null == fp)
            return;
        else if (null == this.frame_distinct)
            this.frame_distinct = new framelist();
        else
            this.frame_distinct.reset(true);//(true) := see (this.frame_distinct.update_fin())
        //
        try {
            this.frame_parent = null;//(obscure [this.frame_distinct] during update)
            //
            int fp_len = fp.size();
            int fc_len = this.sizeHasharray();
            //
            java.lang.Object fcv, fck;
            int fp_idx = 0, fc_idx = 0, fp_test_idx, g_test_idx, fc_test_idx;
            Frame child, parent, fp_test;
            int gidx = 0;
            /*
             * Index Frame (parent) Map (children)
             * directly.
             */
            for (; ; gidx++){
                if (fp_idx < fp_len)
                    this.frame_distinct.update(gidx,fp_idx++,-1);//(no fc, unique fp)
                else
                    break;
            }
            /*
             * Index Frame (child) Map (children)
             * by editing Frame (parent) slots.
             */
            for (; ; gidx++){
                if (fc_idx < fc_len){
                    fcv = this.value(fc_idx);
                    fck = this.key(fc_idx);
                    if (fcv instanceof Frame){
                        child = (Frame)fcv;
                        fp_test = child.frameParentGet();
                        if (null != fp_test){/*(collision [child frame overriding parent frame])
                                              */
                            fp_test_idx = fp.indexOfValue(fp_test);/*(test for collision @ [fp_test_idx,fc_idx])
                                                                    */
                            if (0 > fp_test_idx)
                                this.frame_distinct.update(gidx,-1,fc_idx++);//(collision off frame)
                            else {
                                g_test_idx = this.frame_distinct.gx4px(fp_test_idx);
                                if (-1 < g_test_idx){
                                    fc_test_idx = this.frame_distinct.cx4gx(g_test_idx);

                                    if (0 > fc_test_idx){/*(update previous for found collision: 
                                                          * pull fc into fp override position)
                                                          */
                                        this.frame_distinct.update(g_test_idx,fp_test_idx,fc_idx++);
                                        gidx -= 1;
                                    }
                                    else {
                                        /*(duplicate collision)
                                         */
                                        this.frame_distinct.update(gidx,fp_test_idx,fc_idx++);
                                    }
                                }
                                else
                                    this.frame_distinct.update(gidx,fp_test_idx,fc_idx++);//(simple collision)
                            }
                        }
                        else 
                            this.frame_distinct.update(gidx,-1,fc_idx++);//(no fp, unique fc)
                    }
                    else
                        this.frame_distinct.update(gidx,-1,fc_idx++);//(no fp, unique fc)
                }//(fc_idx < fc_len)
                else
                    break;
            }//(for gidx)
            this.frame_distinct.update_fin();
        }
        finally {
            this.frame_parent = fp;
        }
    }
    protected final java.lang.Object[] frary(Class comp, boolean keys, boolean filter){
        if (null == this.frameGet())
            throw new java.lang.IllegalStateException("bug:missing-frame");
        else if (this.frameStale())
            throw new java.lang.IllegalStateException("bug:frame-stale");
        else {
            int glen = this.frame_distinct.size();
            java.lang.Object test, gary[] = Array.grow(null,glen,comp);
            for (int gidx = 0; gidx < glen; gidx++){
                if (keys)
                    test = this.key(gidx);
                else
                    test = this.value(gidx);
                //
                if (null == test)
                    throw new java.lang.IllegalStateException("bug:null-pointer @ "+gidx+" in "+glen);
                else if (filter){
                    if (comp.isAssignableFrom(test.getClass()))
                        gary[gidx] = test;
                    else
                        /////////////////////////////////////////////////////////////////////////////////////////////////////
                        throw new java.lang.IllegalStateException("bug:unimplemented-filter-failure @ "+gidx+" in "+glen); //
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                }
                else 
                    gary[gidx] = test;
            }
            return gary;
        }
    }

    public int size(){
        if (null == this.frame_parent)
            return super.size();
        else if (null != this.frame_distinct)
            return (this.frame_distinct.size());
        else
            return super.size();/*(special case for 'size' needed by frame-init)
                                 */
    }
    public int sizeNof(){
        return super.sizeHasharray();
    }
    public boolean isEmpty(){
        return (1 > this.size());
    }
    public boolean isEmptyNof(){
        return (1 > this.sizeNof());
    }
    public boolean isNotEmpty(){
        return (0 < this.size());
    }
    public boolean isNotEmptyNof(){
        return (0 < this.sizeNof());
    }

    public int indexOf(java.lang.Object key) {
        if (null == this.frame_parent)
            return super.indexOf(key);
        else if (null != this.frame_distinct){
            int xfc_idx = super.indexOf(key);
            int xfp_idx = this.frame_parent.indexOf(key);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return -1;
                else 
                    return this.frame_distinct.gx4px(xfp_idx);
            }
            else if (0 > xfp_idx)
                return this.frame_distinct.gx4cx(xfc_idx);
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx)
                    return xfc_gidx;
                else
                    return xfp_gidx;
            }
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int re = this.frame_parent.indexOf(key);
                if (-1 < re)
                    return re;
                else {
                    re = super.indexOf(key);
                    if (-1 < re)
                        return (this.frame_parent.size()+re);
                    else 
                        return -1;
                }
            }
    }
    public int indexOfNof(java.lang.Object key) {
        return super.indexOf(key);
    }
    public int indexOf(java.lang.Object key, int from) {
        if (null == this.frame_parent)
            return super.indexOf(key,from);
        else if (null != this.frame_distinct){
            if (0 > from)
                from = 0;
            else if (from >= this.frame_distinct.size())
                return -1;
            //
            int xfc_from = this.frame_distinct.cx4gxf(from,true);
            int xfp_from = this.frame_distinct.px4gxf(from,true);
            int xfc_idx = super.indexOf(key,xfc_from);
            int xfp_idx = this.frame_parent.indexOf(key,xfp_from);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return -1;
                else 
                    return this.frame_distinct.gx4px(xfp_idx);
            }
            else if (0 > xfp_idx)
                return this.frame_distinct.gx4cx(xfc_idx);
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx)
                    return xfc_gidx;
                else
                    return xfp_gidx;
            }
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int re = this.frame_parent.indexOf(key,from);
                if (-1 < re)
                    return re;
                else {
                    re = super.indexOf(key,from);
                    if (-1 < re)
                        return re+this.frame_parent.size();
                    else 
                        return -1;
                }
            }
    }
    public int indexOfNof(java.lang.Object key, int from) {
        return super.indexOf(key,from);
    }    
    public int lastIndexOf(java.lang.Object key) {
        if (null == this.frame_parent)
            return super.lastIndexOf(key);
        else if (null != this.frame_distinct){
            int xfp_idx = this.frame_parent.lastIndexOf(key);
            int xfc_idx = super.lastIndexOf(key);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            return Math.max(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int lastIndexOf(java.lang.Object key, int from) {
        if (null == this.frame_parent)
            return super.lastIndexOf(key,from);
        else if (null != this.frame_distinct){
            int xfp_from = this.frame_distinct.px4gxf(from,false);
            int xfc_from = this.frame_distinct.cx4gxf(from,false);
            int xfp_idx = this.frame_parent.lastIndexOf(key,xfp_from);
            int xfc_idx = super.lastIndexOf(key,xfc_from);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            return Math.max(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int lastIndexOfNof(java.lang.Object key, int from) {
        return super.lastIndexOf(key,from);
    }
    public int indexOfValue(java.lang.Object val) {
        if (null == this.frame_parent)
            return super.indexOfValue(val);
        else if (null != this.frame_distinct){
            int xfp_idx = this.frame_parent.indexOfValue(val);
            int xfc_idx = super.indexOfValue(val);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            /*(-1 problem)
             */
            if (0 > xgc)
                return xgp;
            else if (0 > xgp)
                return xgc;
            else
                return Math.min(xgc,xgp);
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int re = this.frame_parent.indexOfValue(val);
                if (-1 < re)
                    return re;
                else {
                    re = super.indexOfValue(val);
                    if (-1 < re)
                        return re+this.frame_parent.size();
                    else 
                        return -1;
                }
            }
    }
    public int indexOfValueClass(Class vc) {
        if (null == this.frame_parent)
            return super.indexOfValueClass(vc);
        else if (null != this.frame_distinct){
            int xfp_idx = this.frame_parent.indexOfValueClass(vc);
            int xfc_idx = super.indexOfValueClass(vc);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            /*(-1 problem)
             */
            if (0 > xgc)
                return xgp;
            else if (0 > xgp)
                return xgc;
            else
                return Math.min(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int indexOfValueClassNof(Class vc) {
        return super.indexOfValueClass(vc,0);
    }
    public int indexOfValue(java.lang.Object val, int from) {
        if (null == this.frame_parent)
            return super.indexOfValue(val,from);
        else if (null != this.frame_distinct){
            int xfp_from = this.frame_distinct.px4gxf(from,true);
            int xfc_from = this.frame_distinct.cx4gxf(from,true);
            int xfp_idx = this.frame_parent.indexOfValue(val,xfp_from);
            int xfc_idx = super.indexOfValue(val,xfc_from);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            /*(-1 problem)
             */
            if (0 > xgc)
                return xgp;
            else if (0 > xgp)
                return xgc;
            else
                return Math.min(xgc,xgp);
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int re = this.frame_parent.indexOfValue(val,from);
                if (-1 < re)
                    return re;
                else {
                    int sz = this.frame_parent.size();
                    int test = (from-sz);
                    if (-1 < test)
                        re = super.indexOfValue(val,test);
                    else
                        re = super.indexOfValue(val,from);
                    if (-1 < re)
                        return re+this.frame_parent.size();
                    else
                        return -1;
                }
            }
    }
    public int indexOfValueNof(java.lang.Object key, int from) {
        return super.indexOfValue(key,from);
    }
    public int lastIndexOfValue(java.lang.Object val) {
        if (null == this.frame_parent)
            return super.lastIndexOfValue(val);
        else if (null != this.frame_distinct){
            int xfp_idx = this.frame_parent.indexOfValue(val);
            int xfc_idx = super.indexOfValue(val);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            return Math.max(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int lastIndexOfValue(java.lang.Object val, int from) {
        if (null == this.frame_parent)
            return super.lastIndexOfValue(val,from);
        else if (null != this.frame_distinct){
            int xfp_from = this.frame_distinct.px4gx(from);
            int xfc_from = this.frame_distinct.cx4gx(from);
            int xfp_idx = this.frame_parent.lastIndexOfValue(val,xfp_from);
            int xfc_idx = super.lastIndexOfValue(val,xfc_from);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            return Math.max(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int lastIndexOfValueClass(Class clas) {
        if (null == this.frame_parent)
            return super.lastIndexOfValueClass(clas);
        else if (null != this.frame_distinct){
            int xfp_idx = this.frame_parent.indexOfValue(clas);
            int xfc_idx = super.indexOfValue(clas);
            int xgp = this.frame_distinct.gx4px(xfp_idx);
            int xgc = this.frame_distinct.gx4cx(xfc_idx);
            return Math.max(xgc,xgp);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale");
    }
    public int lastIndexOfValueClassNof(Class key) {
        return super.lastIndexOfValueClass(key,(this.sizeNof()-1));
    }
    public java.lang.Object value(int idx){
        if (null == this.frame_parent)
            return super.value(idx);
        else if (null != this.frame_distinct){
            int xfc_idx = this.frame_distinct.cx4gx(idx);
            int xfp_idx = this.frame_distinct.px4gx(idx);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return null;
                else 
                    return this.frame_parent.value(xfp_idx);
            }
            else if (0 > xfp_idx)
                return super.value(xfc_idx);
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx)
                    return super.value(xfc_idx);
                else
                    return this.frame_parent.value(xfp_idx);
            }
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int psz = this.frame_parent.size();
                int lx = (idx-psz);
                if (-1 < lx)
                    return super.value(lx);
                else 
                    return this.frame_parent.value(idx);
            }
    }
    public java.lang.Object valueNof(int idx){
        return super.value(idx);
    }
    public java.lang.Object get(java.lang.Object key){
        if (null == this.frame_parent)
            return super.get(key);
        else if (null != this.frame_distinct){
            int gidx = this.indexOf(key);
            if (0 > gidx)
                return null;
            else
                return this.value(gidx);
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                java.lang.Object re = this.frame_parent.get(key);
                if (null != re)
                    return re;
                else 
                    return super.get(key);
            }
    }
    public java.lang.Object getNof(java.lang.Object key){
        return super.get(key);
    }
    public java.lang.Object key(int idx){
        if (null == this.frame_parent)
            return super.key(idx);
        else if (null != this.frame_distinct){
            int xfc_idx = this.frame_distinct.cx4gx(idx);
            int xfp_idx = this.frame_distinct.px4gx(idx);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return null;
                else 
                    return this.frame_parent.key(xfp_idx);
            }
            else if (0 > xfp_idx)
                return super.key(xfc_idx);
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx)
                    return super.key(xfc_idx);
                else
                    return this.frame_parent.key(xfp_idx);
            }
        }
        else
            {/*
              * (bootstrap for link target discovery)
              */
                int psz = this.frame_parent.size();
                int lx = (idx-psz);
                if (-1 < lx)
                    return super.key(lx);
                else 
                    return this.frame_parent.key(idx);
            }
    }
    public java.lang.Object keyNof(int idx){
        return super.key(idx);
    }
    public java.lang.Object put( java.lang.Object key, java.lang.Object value){
        this.stale();
        //
        return super.put(key,value);
    }
    public java.lang.Object value(int idx, java.lang.Object value){
        if (null == this.frame_parent)
            return super.value(idx,value);
        else if (null != this.frame_distinct){
            this.stale();
            int xfc_idx = this.frame_distinct.cx4gx(idx);
            int xfp_idx = this.frame_distinct.px4gx(idx);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return null;
                else 
                    return this.frame_parent.value(xfp_idx,value);
            }
            else if (0 > xfp_idx)
                return super.value(xfc_idx);
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx)
                    return super.value(xfc_idx,value);
                else
                    return this.frame_parent.value(xfp_idx,value);
            }
        }
        else
            throw new java.lang.IllegalStateException("frame-stale @ "+this.toString()+" @ "+idx);
    }
    public java.lang.Object valueNof(int idx, java.lang.Object value){
        this.stale();
        return super.value(idx,value);
    }
    public java.lang.Object remove(int idx){
        if (null == this.frame_parent)
            return super.remove(idx);
        else if (null != this.frame_distinct){
            int xfc_idx = this.frame_distinct.cx4gx(idx);
            int xfp_idx = this.frame_distinct.px4gx(idx);
            if (0 > xfc_idx){
                if (0 > xfp_idx)
                    return null;
                else {
                    this.stale();
                    return this.frame_parent.remove(xfp_idx);
                }
            }
            else if (0 > xfp_idx){
                this.stale();
                return super.remove(xfc_idx);
            }
            else {
                int xfc_gidx = this.frame_distinct.gx4cx(xfc_idx);
                int xfp_gidx = this.frame_distinct.gx4px(xfp_idx);
                if (0 > xfc_gidx || 0 > xfp_gidx){
                    if (0 > xfc_gidx && 0 > xfp_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc-and-xfp");
                    else if (0 > xfc_gidx)
                        throw new java.lang.IllegalStateException("bug:xfc");
                    else 
                        throw new java.lang.IllegalStateException("bug:xfp");
                }
                else if (xfc_gidx <= xfp_gidx){
                    this.stale();
                    return super.remove(xfc_idx);
                }
                else {
                    this.stale();
                    return this.frame_parent.remove(xfp_idx);
                }
            }
        }
        else
            throw new java.lang.IllegalStateException("frame-stale @ "+this.toString()+" @ "+idx);
    }
    public java.util.Enumeration keys(){
        if (null == this.frame_parent)
            return super.keys();
        else
            return new Array.Enumerator.Object(this.keyary());
    }
    public java.util.Enumeration keysNof(){
        return super.keys();
    }
    public java.util.Enumeration elements(){
        if (null == this.frame_parent)
            return super.elements();
        else
            return new Array.Enumerator.Object(this.valary());
    }
    public java.util.Enumeration elementsNof(){
        return super.elements();
    }
    public java.lang.Object[] keyary(){
        if (null != this.frame_parent)
            return this.frary(CLA_NIL,true,false);
        else
            return super.keyary();
    }
    public java.lang.Object[] keyaryNof(){
        return super.keyary();
    }
    public java.lang.Object[] keyary(Class comptype){
        if (null != this.frame_parent)
            return this.frary(comptype,true,false);
        else
            return super.keyary(comptype);
    }
    public java.lang.Object[] keyaryNof(Class comptype){
        return super.keyary(comptype);
    }
    public java.lang.Object[] valary(){
        if (null != this.frame_parent)
            return this.frary(CLA_NIL,false,false);
        else
            return super.valary();
    }
    public java.lang.Object[] valaryNof(){
        return super.valary();
    }
    public java.lang.Object[] valary(Class comptype){
        if (null != this.frame_parent)
            return this.frary(comptype,false,false);
        else
            return super.valary(comptype);
    }
    public java.lang.Object[] valaryNof(Class comptype){
        return super.valary(comptype);
    }
    public java.lang.Object[] valaryFilter(Class comptype){
        if (null != this.frame_parent)
            return this.frary(comptype,false,true);
        else
            return super.valaryFilter(comptype);
    }
    public java.lang.Object[] valaryFilterNof(Class comptype){
        return super.valaryFilter(comptype);
    }
    public boolean containsKey (java.lang.Object value){
        if (null != this.frame_parent && this.frame_parent.containsKey(value))
            return true;
        else
            return super.containsKey(value);
    }
    public boolean containsKeyNof (java.lang.Object value){
        return super.containsKey(value);
    }
    public boolean containsValue (java.lang.Object value){
        if (null != this.frame_parent && this.frame_parent.containsValue(value))
            return true;
        else
            return super.containsValue(value);
    }
    public boolean containsValueNof (java.lang.Object value){
        return super.containsValue(value);
    }
    public boolean contains (java.lang.Object value){
        if (null != this.frame_parent && this.frame_parent.contains(value))
            return true;
        else
            return super.contains(value);
    }
    public boolean containsNof (java.lang.Object value){
        return super.contains(value);
    }
    public int insert( int idx, java.lang.Object key, java.lang.Object val){
        if (null == this.frame_parent)
            return super.insert(idx,key,val);
        else if (null != this.frame_distinct){
            this.stale();
            int xfc_idx = this.frame_distinct.cx4gx(idx);
            if (0 > xfc_idx)
                return super.insert(0,key,val);
            else
                return super.insert(xfc_idx,key,val);
        }
        //
        throw new java.lang.IllegalStateException("frame-stale @ "+this.toString()+" @ "+idx);
    }
    public int insertNof( int idx, java.lang.Object key, java.lang.Object val){
        return super.insert(idx,key,val);
    }
    public Obfmap cloneObfmap(boolean inherit){
        Obfmap clone = (Obfmap)super.cloneObjmap();
        if (null != clone.frame_distinct)
            clone.frame_distinct = this.frame_distinct.cloneFramelist();
        if (inherit)
            clone.frameParentReset(this);
        return clone;
    }

}
