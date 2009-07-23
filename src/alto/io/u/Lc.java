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

/**
 * <p> A strictly minimized line oriented character buffer for
 * subclasses.  The buffer methods are not exposed to the users of the
 * subclass.  Any newline characters are appended by the user. </p>
 * 
 * <p> Strictly single threaded.  It's assumed that all of the users
 * are in one thread, and there's no need for cpu cache flushing. </p>
 * 
 * @author jdp
 * @since 1.1
 */
public abstract class Lc {

    protected final static char CH_NIL = (char)0;


    private char[][] buf ;

    private int p = 0;

    private int f = 10;


    protected Lc(){
        super();
        this.buf = new char[this.f][];
    }


    protected final int lcCount(){
        return this.p;
    }
    protected final char[] lcPeek(){
        int p = this.p;
        if ( 0 < p){
            int idx = (p - 1);
            return this.buf[idx];
        }
        else
            return null;
    }
    protected final char[] lcPeek(int ridx){
        int p = this.p;
        if (0 < p){
            int idx = (p - 1) + ridx;
            if (-1 < idx && idx < p)
                return this.buf[idx];
        }
        return null;
    }
    protected final boolean lcIsInLine(){
        char[] current = this.lcPeek();
        if (null == current)
            return false;
        else 
            return HasOpenGroup(current);
    }
    protected final boolean lcIsInOpenGroup(){
        char[] current = this.lcPeek();
        if (null == current)
            return false;
        else 
            return HasOpenGroup(current);
    }
    /**
     * Classic 'C' multiline comments. 
     * 
     * @return Less than zero for close comment, zero for no comment
     * open or close, or greater than zero for an open comment. 
     */
    protected final int lcScanComment(){
        char[] current = this.lcPeek();
        if (null == current)
            return 0;
        else 
            return ScanComment(current);
    }
    /**
     * @return True for current (last) line ends with newline (LF) or
     * this buffer empty.
     */
    protected final boolean lcEndsWithLF(){
        char[] current = this.lcPeek();
        if (null == current)
            return (0 == this.p);
        else {
            int len = current.length;
            return ('\n' == current[len-1]);
        }
    }
    protected final boolean lcEndsWith(char ch){
        if ('\n' == ch)
            return this.lcEndsWithLF();
        else {
            char[] current = this.lcPeek();
            if (null == current)
                return false;
            else {
                int idx = (current.length-1);
                if ('\n' == current[idx]){
                    idx -= 1;
                    if ('\r' == current[idx]){
                        idx -= 1;
                    }
                }
                return (ch == current[idx]);
            }
        }
    }
    protected final char lcEndsWith(char[] choice){
        char[] current = this.lcPeek();
        if (null == current)
            return CH_NIL;
        else {
            int idx = (current.length-1);
            if ('\n' == current[idx]){
                idx -= 1;
                if ('\r' == current[idx]){
                    idx -= 1;
                }
            }
            for (int cc = 0, ccc = choice.length; cc < ccc; cc++){
                char ch = choice[cc];
                if (ch == current[idx])
                    return ch;
            }
            return CH_NIL;
        }
    }
    protected final boolean lcStartsWith(char ch){
        char[] current = this.lcPeek();
        if (null == current)
            return false;
        else if (ch == current[0])
            return true;
        else if (' ' == current[0]){
            int len = current.length;
            for (int cc = 0; cc < len; cc++){
                char subject = current[cc];
                if (ch == subject)
                    return true;
                else if (' ' != subject)
                    return false;
            }
        }
        return false;
    }
    protected final int lcLastIndexOf( char ch){
        return this.lcLastIndexOf(ch,-1);
    }
    protected final int lcLastIndexOf( char ch, int from){
        char[] current = this.lcPeek();
        if (null == current)
            return -1;
        else {
            int len = current.length;
            int start;
            if (-1 < from){
                if (from < len)
                    start = from;
                else
                    throw new alto.sys.Error.Argument(java.lang.String.valueOf(from));
            }
            else
                start = (len-1);
            //
            for (int cc = start; cc > -1; cc--){
                if (ch == current[cc])
                    return cc;
            }
            return -1;
        }
    }
    protected final int lcLastIndexOf( char[] search){
        return this.lcLastIndexOf(search,-1);
    }
    protected final int lcLastIndexOf( char[] search, int from){
        char[] current = this.lcPeek();
        if (null == current || null == search)
            return -1;
        else {
            int current_len = current.length;
            int start;
            if (-1 < from){
                if (from < current_len)
                    start = from;
                else
                    throw new alto.sys.Error.Argument(java.lang.String.valueOf(from));
            }
            else
                start = (current_len-1);
            //
            int search_len = search.length;
            char search_first = search[0];
            for (int cc = start; cc > -1; cc--){
                if (search_first == current[cc]){
                    if (Match(search,1,search_len,current,(cc+1),current_len)){
                        return cc;
                    }
                }
            }
            return -1;
        }
    }
    protected final int lcIndexOf( char ch){
        return this.lcIndexOf(ch,-1);
    }
    protected final int lcIndexOf( char ch, int from){
        char[] current = this.lcPeek();
        if (null == current)
            return -1;
        else {
            int len = current.length;
            int start;
            if (-1 < from){
                if (from < len)
                    start = from;
                else
                    throw new alto.sys.Error.Argument(java.lang.String.valueOf(from));
            }
            else
                start = 0;
            //
            for (int cc = start; cc < len; cc--){
                if (ch == current[cc])
                    return cc;
            }
            return -1;
        }
    }
    protected final int lcIndexOf( char[] search){
        return this.lcIndexOf(search,-1);
    }
    protected final int lcIndexOf( char[] search, int from){
        char[] current = this.lcPeek();
        if (null == current || null == search)
            return -1;
        else {
            int current_len = current.length;
            int start;
            if (-1 < from){
                if (from < current_len)
                    start = from;
                else
                    throw new alto.sys.Error.Argument(java.lang.String.valueOf(from));
            }
            else
                start = 0;
            //
            int search_len = search.length;
            char search_first = search[0];
            for (int cc = start; cc < current_len; cc++){
                if (search_first == current[cc]){
                    if (Match(search,1,search_len,current,(cc+1),current_len)){
                        return cc;
                    }
                }
            }
            return -1;
        }
    }
    protected final Lc lcInline ( char[] line, boolean newline){
        char[][] buf = this.buf;
        int p = this.p;
        if (0 == p){
            if (newline)
                return this.lcNewline(line);
            else {
                p = 1;
                this.p = p;
            }
        }
        int idx = (p - 1);
        char[] current = buf[idx];
        if (null == current)
            buf[idx] = line;
        else {
            int current_len = current.length;
            int line_len = line.length;
            int new_len = (current_len + line_len);
            char[] copier = new char[new_len];
            java.lang.System.arraycopy(current,0,copier,0,current_len);
            java.lang.System.arraycopy(line,0,copier,current_len,line_len);
        }
        //
        if (newline){
            this.p += 1;
        }
        return this;
    }
    protected final Lc lcNewline ( char[] line){
        char[][] buf = this.buf;
        int p = this.p;
        int len = buf.length;
        if ( p >= len){
            char[][] copier = new char[len+this.f][];
            java.lang.System.arraycopy(buf,0,copier,0,len);
            buf = copier;
            this.buf = buf;
        }
        buf[p] = line;
        this.p += 1;

        return this;
    }
    protected final Lc lcReset(){
        int p = this.p;
        char[][] buf = this.buf;
        for ( int cc = 0; cc < p; cc++){
            buf[cc] = null;
        }
        this.p = 0;
        return this;
    }
    /**
     * @return UTF8 encoded content 
     */
    public final byte[] toByteArray()
        throws java.io.IOException
    {
        char[][] buf = this.buf;
        Bbuf bytes = new Bbuf();
        for (int cc = 0,  count = this.p; cc < count; cc++){
            Utf8.encode(buf[cc],bytes);
        }
        return bytes.toByteArray();
    }
    public final int writeTo(java.io.OutputStream out)
        throws java.io.IOException
    {
        char[][] buf = this.buf;
        Bbuf bytes = new Bbuf();
        for (int cc = 0,  count = this.p; cc < count; cc++){
            Utf8.encode(buf[cc],bytes);
        }
        return bytes.writeTo(out);
    }
    public final static boolean Match(char[] a, int a_ofs, int a_len, char[] b, int b_ofs, int b_len){
        for (int ac = a_ofs, bc = b_ofs; ac <= a_len && bc <= b_len; ac++, bc++){
            if (ac == a_len)
                return true;
            else if (bc == b_len)
                return false;
            else if (a[ac] == b[bc])
                continue;
            else
                return false;
        }
        return false;
    }
    /**
     * @return A group open '(' not followed by a corresponding group
     * close ')'.
     */
    public final static boolean HasOpenGroup(char[] c){
        int open = 0;
        for (int cc = 0, ccc = c.length; cc < ccc; cc++){
            switch(c[cc]){
            case '(':
                open += 1;
                break;
            case ')':
                if (0 < open)
                    open -= 1;
                break;
            default:
                break;
            }
        }
        return (0 < open);
    }
    /**
     * Classic 'C' multiline comments. 
     * 
     * @return Less than zero for close comment, zero for no comment
     * open or close, or greater than zero for an open comment. 
     */
    public final static int ScanComment(char[] c){
        int open = 0;
        int last_open = 0, last_close = 0;
        for (int cc = 0, ccc = c.length; cc < ccc; cc++){
            switch(c[cc]){
            case '/':
                if (last_close == (cc-1)){
                    last_open = 0; last_close = 0;
                    open -= 1;
                }
                else {
                    last_close = 0;
                    last_open = cc;
                }
                break;
            case '*':
                if (last_open == (cc-1)){
                    last_open = 0; last_close = 0;
                    open += 1;
                }
                else {
                    last_open = 0;
                    last_close = cc;
                }
                break;
            default:
                break;
            }
        }
        return open;
    }
}
