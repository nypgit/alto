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

/**
 * This exception indicates an "error" network response.
 * 
 * @see BadRequestException
 * @see BadResponseException
 * @see ConflictException
 * @see NotFoundException
 * @see RequestEntityTooLargeException
 * @see ServiceUnavailableException
 * @see UnauthorizedException
 * @see UnknownProtocolException
 * @author jdp
 * @since 1.5
 */
public class Error
    extends java.lang.IllegalStateException
{
    protected final static String STR = null;
    protected final static org.w3c.dom.Node NOD = null;
    protected final static Throwable THR = null;

    /**
     * <p> Unintended state, incorrect operation. </p>
     */
    public static class Bug 
        extends Error
    {
        public Bug(){
            super();
        }
        public Bug(java.lang.String msg){
            super(msg);
        }
        public Bug(java.lang.Throwable thrown){
            super(thrown);
        }
        public Bug(java.lang.String msg, java.lang.Throwable thrown){
            super(msg,thrown);
        }
        public Bug(org.w3c.dom.Node re){
            super(re);
        }
        public Bug(java.lang.String msg, org.w3c.dom.Node re){
            super(msg,re);
        }
        public Bug(org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(re,thrown);
        }
        public Bug(java.lang.String msg, org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(msg,re,thrown);
        }
    }

    /**
     * <p> Input class file format error. </p>
     */
    public static class Format 
        extends Error
    {
        public Format(){
            super();
        }
        public Format(java.lang.String msg){
            super(msg);
        }
        public Format(java.lang.Throwable thrown){
            super(thrown);
        }
        public Format(java.lang.String msg, java.lang.Throwable thrown){
            super(msg,thrown);
        }
        public Format(org.w3c.dom.Node re){
            super(re);
        }
        public Format(java.lang.String msg, org.w3c.dom.Node re){
            super(msg,re);
        }
        public Format(org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(re,thrown);
        }
        public Format(java.lang.String msg, org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(msg,re,thrown);
        }
    }

    /**
     * <p> Usage error, or other conflict violates operational
     * requirements. </p>
     */
    public static class State
        extends Error
    {
        public State(){
            super();
        }
        public State(java.lang.String msg){
            super(msg);
        }
        public State(java.lang.Throwable thrown){
            super(thrown);
        }
        public State(java.lang.String msg, java.lang.Throwable thrown){
            super(msg,thrown);
        }
        public State(org.w3c.dom.Node re){
            super(re);
        }
        public State(java.lang.String msg, org.w3c.dom.Node re){
            super(msg,re);
        }
        public State(org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(re,thrown);
        }
        public State(java.lang.String msg, org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(msg,re,thrown);
        }
    }

    /**
     * <p> Usage error violates input requirements. </p>
     */
    public static class Argument
        extends java.lang.IllegalArgumentException
    {

        protected java.lang.String re_sid, re_qname, to_string;
        protected int re_lno, re_cno;


        public Argument(){
            this(STR,NOD,THR);
        }
        public Argument(java.lang.String msg){
            this(msg,NOD,THR);
        }
        public Argument(java.lang.Throwable thrown){
            this(STR,NOD,thrown);
        }
        public Argument(java.lang.String msg, java.lang.Throwable thrown){
            this(msg,NOD,thrown);
        }
        public Argument(org.w3c.dom.Node re){
            this(STR,re,THR);
        }
        public Argument(java.lang.String msg, org.w3c.dom.Node re){
            this(msg,re,THR);
        }
        public Argument(org.w3c.dom.Node re, java.lang.Throwable thrown){
            this(STR,re,thrown);
        }
        public Argument(java.lang.String msg, org.w3c.dom.Node re, java.lang.Throwable thrown){
            super(msg);
            if (null == re){
                this.re_sid = null;
                this.re_qname = null;
                this.re_lno = 0;
                this.re_cno = 0;
                this.to_string = super.toString();
            }
            else {
                org.w3c.dom.Element elem;
                if (re instanceof org.w3c.dom.Element)
                    elem = (org.w3c.dom.Element)re;
                else {
                    re = re.getParentNode();
                    if (re instanceof org.w3c.dom.Element)
                        elem = (org.w3c.dom.Element)re;
                    else
                        elem = null;
                }
                if (null != elem){
                    this.re_qname = elem.getNodeName();
                    this.re_sid = null;
                    this.re_lno = 0;
                    this.re_cno = 0;
                    this.to_string = this.ctorString(elem,thrown,msg);
                }
                else {
                    this.re_qname = null;
                    this.re_sid = null;
                    this.re_lno = 0;
                    this.re_cno = 0;
                    this.to_string = super.toString();
                }
            }
            if (null != thrown)
                this.initCause(thrown);
        }

        protected java.lang.String ctorString(org.w3c.dom.Element elem, java.lang.Throwable thrown, java.lang.String msg){
            if (elem instanceof org.w3c.dom.DOMLocator){
                org.w3c.dom.DOMLocator loca = (org.w3c.dom.DOMLocator)elem;
                this.re_sid = loca.getUri();
                this.re_lno = loca.getLineNumber();
                this.re_cno = loca.getColumnNumber();
            }
            java.io.PrintStream ps = new alto.io.u.Bpo();
            ps.println(super.toString());
            ps.print("\t[qname] ");
            ps.println(this.re_qname);
            if (null != this.re_sid){
                ps.print("\t[sysid] ");
                ps.println(this.re_sid);
            }
            if (0 < this.re_lno){
                ps.print("\t[linen] ");
                ps.println(this.re_lno);
            }
            return ps.toString();
        }
        public boolean hasReference(){
            return (null != this.re_sid);
        }
        public java.lang.String getReferenceSystemId(){
            return this.re_sid;
        }
        public int getReferenceLine(){
            return this.re_lno;
        }
        public int getReferenceColumn(){
            return this.re_cno;
        }
        public java.lang.String toString(){
            return this.to_string;
        }
    }


    protected java.lang.String re_sid, re_qname, to_string;
    protected int re_lno, re_cno;

    public Error(){
        this(STR,NOD,THR);
    }
    public Error(java.lang.String msg){
        this(msg,NOD,THR);
    }
    public Error(java.lang.Throwable thrown){
        this(STR,NOD,thrown);
    }
    public Error(java.lang.String msg, java.lang.Throwable thrown){
        this(msg,NOD,thrown);
    }
    public Error(org.w3c.dom.Node re){
        this(STR,re,THR);
    }
    public Error(java.lang.String msg, org.w3c.dom.Node re){
        this(msg,re,THR);
    }
    public Error(org.w3c.dom.Node re, java.lang.Throwable thrown){
        this(STR,re,thrown);
    }
    public Error(java.lang.String msg, org.w3c.dom.Node re, java.lang.Throwable thrown){
        super(msg);
        if (null == re){
            this.re_sid = null;
            this.re_qname = null;
            this.re_lno = 0;
            this.re_cno = 0;
            this.to_string = super.toString();
        }
        else {
            org.w3c.dom.Element elem;
            if (re instanceof org.w3c.dom.Element)
                elem = (org.w3c.dom.Element)re;
            else {
                re = re.getParentNode();
                if (re instanceof org.w3c.dom.Element)
                    elem = (org.w3c.dom.Element)re;
                else
                    elem = null;
            }
            if (null != elem){
                this.re_qname = elem.getNodeName();
                this.re_sid = null;
                this.re_lno = 0;
                this.re_cno = 0;
                this.to_string = this.ctorString(elem,thrown,msg);
            }
            else {
                this.re_sid = null;
                this.re_qname = null;
                this.re_lno = 0;
                this.re_cno = 0;
                this.to_string = super.toString();
            }
        }
        if (null != thrown)
            this.initCause(thrown);
    }


    protected java.lang.String ctorString(org.w3c.dom.Element elem, java.lang.Throwable thrown, java.lang.String msg){
        if (elem instanceof org.w3c.dom.DOMLocator){
            org.w3c.dom.DOMLocator loca = (org.w3c.dom.DOMLocator)elem;
            this.re_sid = loca.getUri();
            this.re_lno = loca.getLineNumber();
            this.re_cno = loca.getColumnNumber();
        }
        java.io.PrintStream ps = new alto.io.u.Bpo();
        ps.println(super.toString());
        ps.print("\t[qname] ");
        ps.println(this.re_qname);
        if (null != this.re_sid){
            ps.print("\t[sysid] ");
            ps.println(this.re_sid);
        }
        if (0 < this.re_lno){
            ps.print("\t[linen] ");
            ps.println(this.re_lno);
        }
        return ps.toString();
    }
    public boolean hasReference(){
        return (null != this.re_sid);
    }
    public java.lang.String getReferenceSystemId(){
        return this.re_sid;
    }
    public int getReferenceLine(){
        return this.re_lno;
    }
    public int getReferenceColumn(){
        return this.re_cno;
    }
    public java.lang.String toString(){
        return this.to_string;
    }
}
