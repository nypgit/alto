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
 * <p> Character buffer. </p>
 * 
 * @author John Pritchard (john@syntelos.org)
 * @since 1.1
 */
public class Chbuf
    extends Object
{

    public static String line_separator = "\r\n";


    private char[] buf = null;

    private int p = 0;

    private int f = 10;

    private char token_infix = (char)0;


    public Chbuf(){this(-1);}

    public Chbuf( int initsz){
        super();
        if ( 0 < initsz)
            f = initsz;
        buf = new char[f];
    }

    public Chbuf ( Object obj){
        this();
        append(obj);
    }

    /**
     * Append a SPACE character after each array element, but not the
     * last. */
    public Chbuf ( String[] text){
        this(text,' ');
    }

    /**
     * Append the argument character after each array element, but not
     * the last. 
     * 
     * @param text Source
     * 
     * @param ch Source string delimeter
     */
    public Chbuf ( String[] text, char ch){
        this();

        if ( 0 < ch)
            token_infix = ch;

        append(text);
    }

    /**
     * Append the argument character after each array element, but not
     * the last. 
     * 
     * @param text Source
     * 
     * @param ch_head Source string head
     * 
     * @param ch Source string delimeter
     */
    public Chbuf ( char ch_head, String[] text, char ch){
        this(text,ch);

        prepend(ch_head);
    }

    public boolean startsWith(String s){
        if (null ==  s)
            return (0 == this.p);
        else {
            char[] sc = s.toCharArray();
            int slen = sc.length;
            if (0 >= slen)
                return (0 == this.p);
            else if (slen <= this.p){
                char[] buf = this.buf;
                for (int cc = 0; cc < slen; cc++){
                    if (buf[cc] != sc[cc])
                        return false;
                }
                return true;
            }
            else
                return false;
        }
    }

    public boolean endsWith(String s){
        if (null == s)
            return (0 == this.p);
        else {
            char[] sc = s.toCharArray();
            int slen = sc.length;
            if (0 >= slen)
                return (0 == this.p);
            else if (slen <= this.p){
                char[] buf = this.buf;
                for (int cc = (slen-1), pc = (this.p-1); cc >= 0; ){
                    if (buf[pc--] != sc[cc--])
                        return false;
                }
                return true;
            }
            else
                return false; 
        }
    }

    public void replace(char from, char to){
        char[] buf = this.buf;
        int buflen = this.p;
        char ch;
        for (int cc = 0; cc < buflen; cc++){
            ch = buf[cc];
            if (from == ch)
                buf[cc] = to;
        }
    }

    /**
     * Set a character to separate strings appended or printed into
     * this buffer.  Characters appended or prepended to this buffer
     * will not be prefixed with the token infix character.  
     *
     * <p> Set this to zero, or a negative value, to disable the token
     * infix process.  
     *
     * <p> Disabled by default.
     */
    public void token_infix ( char ch){
        if ( 0 < ch)
            token_infix = ch;
        else
            token_infix = (char)0;
    }

    /**
     * Inserts character at head of buffer, maintains content. */
    public Chbuf prepend(char ch){

        if ( p >= buf.length){

            char[] copier = new char[buf.length+f];

            System.arraycopy(buf,0,copier,1,buf.length); // shift up

            buf = copier;
        }
        else {

            char[] copier = new char[buf.length];

            System.arraycopy(buf,0,copier,1,p); // shift up

            buf = copier;
        }
        p += 1;

        buf[0] = ch;

        return this;
    }

    public Chbuf append(char ch){

        int count = p;

        if ( count >= buf.length){

            char[] copier = new char[buf.length+f];

            System.arraycopy(buf,0,copier,0,buf.length);

            buf = copier;
        }

        buf[count] = ch;

        p = count+1;

        return this;
    }

    public Chbuf hex32 ( int value){
        this.hex8((value >>> 24) & 0xff);
        this.hex8((value >>> 16) & 0xff);
        this.hex8((value >>> 8) & 0xff);
        this.hex8(value & 0xff);
        return this;
    }
    public Chbuf hex24 ( int value){
        this.hex8((value >>> 16) & 0xff);
        this.hex8((value >>> 8) & 0xff);
        this.hex8(value & 0xff);
        return this;
    }
    public Chbuf hex16 ( int value){
        this.hex8((value >>> 8) & 0xff);
        this.hex8(value & 0xff);
        return this;
    }
    public Chbuf hex8 ( int value){
        this.append(Hex.encode8High(value));
        this.append(Hex.encode8Low(value));
        return this;
    }

    public Chbuf bin32 ( int value){
        this.bin8((value >>> 24) & 0xff);
        this.append(' ');
        this.bin8((value >>> 16) & 0xff);
        this.append(' ');
        this.bin8((value >>> 8) & 0xff);
        this.append(' ');
        this.bin8(value & 0xff);
        return this;
    }
    public Chbuf bin24 ( int value){
        this.bin8((value >>> 16) & 0xff);
        this.append(' ');
        this.bin8((value >>> 8) & 0xff);
        this.append(' ');
        this.bin8(value & 0xff);
        return this;
    }
    public Chbuf bin16 ( int value){
        this.bin8((value >>> 8) & 0xff);
        this.append(' ');
        this.bin8(value & 0xff);
        return this;
    }
    public Chbuf bin8 ( int value){
        this.bin4((value >>> 4) & 0xf);
        this.append(' ');
        this.bin4(value & 0xf);
        return this;
    }
    public Chbuf bin4 ( int value){
        char ret[] = new char[4], one = '1', zero = '0';
        for ( int lc = 0; lc < 4; lc++){
            if ( 1 == (value & 1))
                ret[3-lc] = one;
            else
                ret[3-lc] = zero;

            value >>>= 1;
        }
        this.append(ret,0,4);
        return this;
    }

    public char charAt ( int idx){

        return buf[idx];
    }

    public void setCharAt( int idx, char ch) {

        buf[idx] = ch;
    }

    public Chbuf delete(int start, int end) {

        int count = p;

        if (end > count)
            end = count;

        int len = end - start;

        if (len > 0) {

            System.arraycopy(buf, start+len, buf, start, count-end);

            p = count-len;
        }
        return this;
    }

    public Chbuf deleteCharAt( int idx) {

        int count = p;

        System.arraycopy( buf, idx+1, buf, idx, count-idx-1);

        p = count-1;

        return this;
    }

    public Chbuf insert ( int idx, char ch){

        int count = p;

        if ( count >= buf.length){

            char[] copier = new char[buf.length+f];

            System.arraycopy(buf,0,copier,0,buf.length);

            buf = copier;
        }

        System.arraycopy( buf, idx, buf, idx+1, count-idx);

        buf[idx] = ch;

        p = count+1;

        return this;
    }

    public Chbuf insert ( int idx, String s){

        if ( null == s || 0 > idx || idx > p)
            return this;
        else {
            char[] cary = s.toCharArray();

            int slen = cary.length;

            if ( 1 > slen)
                return this;
            else {

                int bound = idx+slen, 
                    count = p+slen;

                if ( count >= buf.length){

                    int grow = (f>bound)?(f):(bound);

                    char[] copier = new char[buf.length+grow];

                    System.arraycopy(buf,0,copier,0,buf.length);

                    buf = copier;
                }

                System.arraycopy( buf, idx, buf, idx+slen, count-idx);

                System.arraycopy( cary, 0, buf, idx, slen);

                p = count+1;
		
                return this;
            }
        }
    }

    public void ensureCapacity(int min) {

        if (min > buf.length) {

            int delt = min-buf.length;

            int nlen = buf.length+f;

            char[] copier;

            if ( nlen < min)
                copier = new char[nlen+delt];
            else
                copier = new char[nlen];

            System.arraycopy(buf,0,copier,0,p);

            this.buf = copier;
        }
    }

    public int capacity() {
        return buf.length;
    }

    /**
     * Typically used to truncate the string.  Can be used to expand
     * the string with null characters.
     */
    public void setLength ( int len){

        if (len < 0) 
            throw new StringIndexOutOfBoundsException(len);
	
        else if ( len < p){

            // truncate

            p = len;

            return;
        }
        else {
            // grow

            int many = len- p;

            char ZERO = (char)0;

            appendMany( ZERO, many);
        }
    }
    /**
     * Calls append many
     */
    public final void printMany( char ch, int many){
        this.appendMany(ch,many);
    }
    /**
     * Calls append many
     */
    public final void printMany( String str, int many){
        this.appendMany(str,many);
    }
    /**
     * Buffer `ch' by `many' times for one append.
     */
    public void appendMany( char ch, int many){
        char[] buf = new char[many];
        for ( int cc = 0; cc < many; cc++)
            buf[cc] = ch;
        this.append(buf);
    }
    /**
     * Buffer `str' by `many' times for one append.
     */
    public final void appendMany( String str, int many){
        if (null != str){
            char[] str_ary = str.toCharArray();
            int str_len = str_ary.length;
            if (0 < str_len){
                int nlen = (str_len*many);
                char[] buf = new char[nlen];
                for (int idx = 0; idx < nlen; idx += str_len){
                    java.lang.System.arraycopy(str_ary,0,buf,idx,str_len);
                }
                this.append(buf);
            }
        }
    }
    /**
     * Call 'print' many times
     */
    public void nprint(int many, String str){
        if (null != str && 0 < str.length())
            for (int idx = 0; idx < many; idx++)
                this.print(str);
    }
    /**
     * Call 'append' many times
     */
    public void nappend(int many, String str){
        if (null != str && 0 < str.length())
            for (int idx = 0; idx < many; idx++)
                this.append(str);
    }
    public void print( String s){
        append(s);
    }
    public void print( char ch){
        append(ch);
    }
    public void print( boolean bool){
        append(java.lang.String.valueOf(bool));
    }
    public void print( int dval){
        append(java.lang.String.valueOf(dval));
    }
    public void print( long dval){
        append(java.lang.String.valueOf(dval));
    }
    public void print( float rval){
        append(java.lang.String.valueOf(rval));
    }
    public void print( double rval){
        append(java.lang.String.valueOf(rval));
    }
    public void print( Object ob){
        append(ob);
    }
    public void println( String s){
        append(s);
        append(line_separator);
    }
    public void println( char ch){
        append(ch);
        append(line_separator);
    }
    public void println( boolean bool){
        append(java.lang.String.valueOf(bool));
        append(line_separator);
    }
    public void println( int dval){
        append(java.lang.String.valueOf(dval));
        append(line_separator);
    }
    public void println( long dval){
        append(java.lang.String.valueOf(dval));
        append(line_separator);
    }
    public void println( float rval){
        append(java.lang.String.valueOf(rval));
        append(line_separator);
    }
    public void println( double rval){
        append(java.lang.String.valueOf(rval));
        append(line_separator);
    }
    public void println( Object ob){
        append(ob);
        append(line_separator);
    }
    public void println(){
        append(line_separator);
    }

    public Chbuf append ( String s){
        if ( null == s) 
            return this;
        else {
            char[] cary = s.toCharArray();

            int sl = cary.length;

            return append( cary, 0, sl);
        }
    }

    public Chbuf append ( char[] cary, int ofs, int len){

        if ( 0 < p && 0 < token_infix && 0 < len){

            char[] copier = new char[len+1];

            copier[0] = token_infix;

            System.arraycopy(cary,ofs,copier,1,len);

            cary = copier;

            len = cary.length;
        }
        else if ( 0 >= len)

            return this;

        if ( p+len >= buf.length){

            if ( len > f) f = len;

            char[] copier = new char[buf.length+f];

            System.arraycopy(buf,0,copier,0,buf.length);

            buf = copier;
        }

        System.arraycopy(cary,ofs,buf,p,len);

        p += len;

        return this;
    }

    /**
     * Calls on <code>Object.toString</code>. */
    public Chbuf append ( Object o){

        if ( null == o){

            return this;
        }
        else if ( o instanceof String){

            append( (String)o);

            return this;
        }
        else {
            Class cla = o.getClass();

            if ( cla.isArray()){

                Class ccla = cla.getComponentType();

                if ( ccla.isPrimitive()){

                    switch(cla.getName().charAt(1)){
                    case 'I':
                        append( (int[])o);
                        return this;
                    case 'B':
                        append( (byte[])o);
                        return this;
                    case 'C':
                        append( (char[])o);
                        return this;
                    case 'J':
                        append( (long[])o);
                        return this;
                    case 'F':
                        append( (float[])o);
                        return this;
                    case 'S':
                        append( (short[])o);
                        return this;
                    case 'D':
                        append( (double[])o);
                        return this;
                    case 'Z':
                        append( (boolean[])o);
                        return this;
                    case '[':
                        append( (Object[])o);
                        return this;
                    default:
                        throw new IllegalArgumentException("Unrecognized primitive array component type `"+ccla.getName()+"'.");
                    }
                }
                else {
                    append( (Object[])o);

                    return this;
                }
            }
            else {
                append(o.toString());

                return this;
            }
        }
    }

    /**
     * In support of autogenerated source use of Chbuf in 'toString'.
     */
    public Chbuf append ( Object[] oary){

        if ( null == oary || 0 >= oary.length)

            return this;

        else {
            append('[');

            int len = oary.length;

            for ( int cc = 0; cc < len; cc++){

                if ( 0 < cc)
                    append(',');

                append( oary[cc]);
            }

            append(']');

            return this;
        }
    }

    /**
     * Appends each element, followed by the`token_infix'
     * character when defined. */
    public Chbuf append ( String[] ary){
        if ( null == ary) 
            return this;
        else {
            char infix = token_infix ;
            String arg;

            for ( int c = 0; c < ary.length; c++){

                arg = ary[c];

                if ( null != arg){

                    if ( 0 < infix && 0 < c)
                        append(infix);

                    append(arg);
                }
            }
            return this;
        }
    }

    /**
     * Appends the characters, followed by the`token_infix'
     * character when defined. */
    public Chbuf append ( char[] ary){

        if ( null == ary) 
            return this;
        else {
            char infix = token_infix ;

            for ( int c = 0; c < ary.length; c++){

                append(ary[c]);
            }

            if ( 0 < infix)
                append(infix);

            return this;
        }
    }

    /**
     * Uses "true" and "false". */
    public Chbuf append ( boolean truf){
        if ( truf)
            return append("true");
        else
            return append("false");
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( byte b){
        return append(String.valueOf(b));
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( short s){
        return append(String.valueOf(s));
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( int i){
        return append(String.valueOf(i));
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( long l){
        return append(String.valueOf(l));
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( float f){
        return append(String.valueOf(f));
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( double d){
        return append(String.valueOf(d));
    }

    private final static char setopen = '[';
    private final static char setclose = ']';
    private final static char comma = ',';

    /**
     * Uses "true" and "false". */
    public Chbuf append ( boolean[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( short[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( int[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( long[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( float[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Calls on <code>String.valueOf</code>. */
    public Chbuf append ( double[] ary){
        if ( null == ary)
            return this;
        else {
            int alen = ary.length;

            append(setopen);

            for ( int cc = 0; cc < alen; cc++){

                if ( 0 < cc)
                    append(comma);

                append(String.valueOf(ary[cc]));
            }
            return append(setclose);
        }
    }

    /**
     * Returns null for an empty buffer.  Doesn't reset the buffer,
     * but allows continuation.  
     *
     * @see #reset */
    public char[] toCary(){

        int pp = p;

        if ( 0 == pp)
            return null;

        else {
            char[] copier = new char[pp];

            System.arraycopy(buf,0,copier,0,pp);

            return copier;
        }
    }

    /**
     * Returns null for an empty buffer.  Doesn't reset the buffer,
     * but allows continuation.  
     *
     * @see #reset */
    public char[] toCharArray(){
        return toCary();
    }

    /**
     * Returns null for an empty buffer. */
    public String toString(){
        char[] cary = toCary();
        if ( null != cary)
            return new String(cary,0,cary.length);
        else
            return null;
    }

    /**
     * Content length. */
    public int length(){
        return p;
    }

    /**
     * Discard content, reuse buffer. */
    public Chbuf reset(){
        p = 0;
        return this;
    }

    /**
     * Read one character from the buffer and reset to previous
     * character for overwriting the popped character. */
    public char popChar(){

        if ( 0 < p)
            return buf[p--];
        else
            return (char)0;
    }

    /**
     * Similar to popChar, reset back "many" characters for using
     * `Chbuf' like a stream buffer. 
     *
     * @param many Return "many", or fewer characters.
     */
    public char[] popBuf( int many){

        if ( 0 == p)

            return null;

        else if ( 0 < many && many <= p){

            char[] copier = new char[many];

            p -= many;

            System.arraycopy(buf,p,copier,0,many);

            return copier;
        }
        else {
            char[] ret = toCary();

            p = 0;

            return ret;
        }
    }
    /**
     * Return buffer and reset. */
    public char[] popBuf(){

        char[] ret = toCary();

        p = 0;

        return ret;
    }

    public final static char[] toCharArray ( Object[] oary){
        if ( null == oary)
            return null;
        else {
            Chbuf cb = new Chbuf();
            int olen = oary.length;
            Object o;

            for ( int cc = 0; cc < olen; cc++){

                o = oary[cc];

                if ( null != o){

                    if ( o instanceof boolean[])
                        cb.append((boolean[])o);
                    else if ( o instanceof byte[])
                        cb.append((byte[])o);
                    else if ( o instanceof short[])
                        cb.append((short[])o);
                    else if ( o instanceof int[])
                        cb.append((int[])o);
                    else if ( o instanceof long[])
                        cb.append((long[])o);
                    else if ( o instanceof float[])
                        cb.append((float[])o);
                    else if ( o instanceof double[])
                        cb.append((double[])o);
                    else if ( o instanceof Object[])
                        cb.append((Object[])o);
                    else
                        cb.append(o);
                }
            }
            return cb.toCary();
        }
    }

    public final static String cat ( Object[] oary){

        return toString(oary);
    }
    public final static String toString ( Object[] oary){
        char[] cary = toCharArray(oary);
        if ( null == cary)
            return null;
        else
            return new String(cary);
    }

    public final static String toString ( Object obj){

        return new Chbuf(obj).toString();
    }

    public final static char[] ngen(char a, int count){
        char[] re = new char[count];
        for (int cc = 0; cc < count; cc++)
            re[cc] = a;
        return re;
    }
    public final static boolean equals(char[] a, char[] b){
        if (null == a){
            if (null == b)
                return true;
            else
                return false;
        }
        else if (null == b)
            return false;
        else {
            if (a.length == b.length){
                for (int cc = 0, count = a.length; cc < count; cc++){
                    if (a[cc] != b[cc])
                        return false;   
                }
                return true;
            }
            else
                return false;
        }
    }
    public final static char[] cat (char a, char[] b){
        if (null == b)
            return new char[]{a};
        else {
            int b_len = b.length;
            if (1 > b_len)
                return new char[]{a};
            else {
                char[] r = new char[b_len+1];
                System.arraycopy(b,0,r,1,b_len);
                r[0] = a;
                return r;
            }
        }
    }
    public final static char[] cat (char[] a, char b){
        if (null == a)
            return new char[]{b};
        else {
            int a_len = a.length;
            if (1 > a_len)
                return new char[]{b};
            else {
                char[] r = new char[a_len+1];
                System.arraycopy(a,0,r,0,a_len);
                r[a_len] = b;
                return r;
            }
        }
    }
    public final static char[] cat ( char[] a, char[] b){

        if ( null == a)

            return b;

        else if ( null == b)

            return a;

        else {
            char[] rcary;

            int aclen = a.length, bclen = b.length;

            rcary = new char[aclen+bclen];

            System.arraycopy( a, 0, rcary, 0, aclen);

            System.arraycopy( b, 0, rcary, aclen, bclen);

            return rcary;
        }
    }
    public final static char[][] cat ( char[][] a, char[][] b){

        if ( null == a)

            return b;

        else if ( null == b)

            return a;

        else {
            char[][] rcary;

            int aclen = a.length, bclen = b.length;

            rcary = new char[aclen+bclen][];

            System.arraycopy( a, 0, rcary, 0, aclen);

            System.arraycopy( b, 0, rcary, aclen, bclen);

            return rcary;
        }
    }
    public final static char[][] cat ( char[][] a, char[] b){

        if ( null == a){
            return new char[][]{b};
        }
        else if ( null == b)

            return a;

        else {
            char[][] rcary;

            int aclen = a.length;

            rcary = new char[aclen+1][];

            System.arraycopy( a, 0, rcary, 0, aclen);

            rcary[aclen] = b;

            return rcary;
        }
    }
    public final static char[] cat ( char[] a, char[] b, int bofs, int blen){

        if ( null == a)

            return b;

        else if ( null == b || 1 > blen)

            return a;

        else {
            char[] rcary;

            int aclen = a.length;

            rcary = new char[aclen+blen];

            System.arraycopy( a, 0, rcary, 0, aclen);

            System.arraycopy( b, bofs, rcary, aclen, blen);

            return rcary;
        }
    }

    /**
     * Concatenate three character arrays */
    public final static char[] cat ( char[] a, char[] b, char[] c){

        if ( null == a)

            return cat(b,c);

        else if ( null == b)

            return cat(a,c);

        else if ( null == c)

            return cat(a,b);

        else {
            char[] rcary;

            int a_clen = a.length, 
                b_clen = b.length,
                c_clen = c.length;

            rcary = new char[a_clen+b_clen+c_clen];

            System.arraycopy( a, 0, rcary, 0, a_clen);

            System.arraycopy( b, 0, rcary, a_clen, b_clen);

            System.arraycopy( c, 0, rcary, a_clen+b_clen, c_clen);

            return rcary;
        }
    }



    /**
     * Concatenate two strings.  (This is faster than using the string
     * concatenation operator in Java programming which results in a
     * StringBuffer object allocation and its synchronized use at
     * runtime.)  */
    public final static String cat ( String a, String b){

        if ( null == a)

            return b;

        else if ( null == b)

            return a;

        else {
            char[] acary = a.toCharArray(), bcary = b.toCharArray(), rcary;

            int aclen = acary.length, bclen = bcary.length;

            rcary = new char[aclen+bclen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, 0, rcary, aclen, bclen);

            return new String(rcary);
        }
    }
    public final static String cat ( String a, char b, String c){

        if ( null == a)

            return cat(b,c);

        else if ( null == c)

            return cat(a,b);

        else {
            char[] acary = a.toCharArray(), ccary = c.toCharArray(), rcary;

            int aclen = acary.length, cclen = ccary.length;

            rcary = new char[aclen+1+cclen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            rcary[aclen] = b;

            System.arraycopy( ccary, 0, rcary, (aclen+1), cclen);

            return new String(rcary);
        }
    }
    public final static String cat ( char a, String b){
        if (null == b)
            return new String(new char[]{a});
        else {
            int len = b.length();
            if (1 > len)
                return new String(new char[]{a});
            else {
                char[] copier = new char[len+1];
                copier[0] = a;
                System.arraycopy(b.toCharArray(),0,copier,1,len);
                return new String(copier);
            }
        }
    }
    public final static String cat ( String a, char b){
        if (null == a)
            return new String(new char[]{b});
        else {
            int len = a.length();
            if (1 > len)
                return new String(new char[]{b});
            else {
                char[] copier = new char[len+1];
                System.arraycopy(a.toCharArray(),0,copier,0,len);
                copier[len] = b;
                return new String(copier);
            }
        }
    }
    /**
     * Concatenate two character arrays */
    public final static String cat ( String a, char[] bcary){

        if ( null == a){

            if ( null == bcary)

                return null;
            else
                return new String(bcary);
        }
        else if ( null == bcary)

            return a;

        else {
            char[] acary = a.toCharArray(), rcary;

            int aclen = acary.length, bclen = bcary.length;

            rcary = new char[aclen+bclen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, 0, rcary, aclen, bclen);

            return new String(rcary);
        }
    }
    /**
     * Concatenate two character arrays */
    public final static String cat ( String a, char[] bcary, int bofs, int blen){

        if ( null == a){

            if ( null == bcary || 1 > blen)

                return null;
            else
                return new String(bcary,bofs,blen);
        }
        else if ( null == bcary)

            return a;

        else {
            char[] acary = a.toCharArray(), rcary;

            int aclen = acary.length;

            rcary = new char[aclen+blen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, bofs, rcary, aclen, blen);

            return new String(rcary);
        }
    }
    /**
     * Concatenate three strings
     */
    public final static String cat ( String a, String b, String c){

        if ( null == a)

            return cat(b,c);

        else if ( null == b)

            return cat(a,c);

        else if ( null == c)

            return cat(a,b);

        else {
            char[] acary = a.toCharArray(), 
                bcary = b.toCharArray(), 
                ccary = c.toCharArray(), 
                rcary;

            int aclen = acary.length, bclen = bcary.length, cclen = ccary.length;

            rcary = new char[aclen+bclen+cclen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, 0, rcary, aclen, bclen);

            System.arraycopy( ccary, 0, rcary, aclen+bclen, cclen);

            return new String(rcary);
        }
    }

    /**
     * Concatenate three strings
     */
    public final static String cat ( String a, String b, char[] ccary){

        if ( null == a)

            return cat(b,ccary);

        else if ( null == b)

            return cat(a,ccary);

        else if ( null == ccary)

            return cat(a,b);

        else {
            char[] acary = a.toCharArray(), 
                bcary = b.toCharArray(), 
                rcary;

            int aclen = acary.length, bclen = bcary.length, cclen = ccary.length;

            rcary = new char[aclen+bclen+cclen];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, 0, rcary, aclen, bclen);

            System.arraycopy( ccary, 0, rcary, aclen+bclen, cclen);

            return new String(rcary);
        }
    }
    /**
     * Concatenate three strings
     */
    public final static String cat ( String a, String b, char[] ccary, int ccary_ofs, int ccary_len){

        if ( null == a)

            return cat(b,ccary);

        else if ( null == b)

            return cat(a,ccary);

        else if ( null == ccary)

            return cat(a,b);

        else {
            char[] acary = a.toCharArray(), 
                bcary = b.toCharArray(), 
                rcary;

            int aclen = acary.length, bclen = bcary.length;

            rcary = new char[aclen+bclen+ccary_len];

            System.arraycopy( acary, 0, rcary, 0, aclen);

            System.arraycopy( bcary, 0, rcary, aclen, bclen);

            System.arraycopy( ccary, ccary_ofs, rcary, aclen+bclen, ccary_len);

            return new String(rcary);
        }
    }

    /**
     * Concatenate four strings
     */
    public final static String cat ( String a, String b, String c, String d){

        if ( null == a)

            return cat(b,c,d);

        else if ( null == b)

            return cat(a,c,d);

        else if ( null == c)

            return cat(a,b,d);

        else if ( null == d)

            return cat(a,b,c);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            return new String(r_cary);
        }
    }

    /**
     * Concatenate five strings
     */
    public final static String cat ( String a, String b, String c, String d, String e){

        if ( null == a)

            return cat(b,c,d,e);

        else if ( null == b)

            return cat(a,c,d,e);

        else if ( null == c)

            return cat(a,b,d,e);

        else if ( null == d)

            return cat(a,b,c,e);

        else if ( null == e)

            return cat(a,b,c,d);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                e_cary = e.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                e_clen = e_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen+e_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            clen += d_clen;

            System.arraycopy( e_cary, 0, r_cary, clen, e_clen);

            return new String(r_cary);
        }
    }

    /**
     * Concatenate six strings
     */
    public final static String cat ( String a, String b, String c, String d, String e, String f){

        if ( null == a)

            return cat(b,c,d,e,f);

        else if ( null == b)

            return cat(a,c,d,e,f);

        else if ( null == c)

            return cat(a,b,d,e,f);

        else if ( null == d)

            return cat(a,b,c,e,f);

        else if ( null == e)

            return cat(a,b,c,d,f);

        else if ( null == f)

            return cat(a,b,c,d,e);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                e_cary = e.toCharArray(), 
                f_cary = f.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                e_clen = e_cary.length,
                f_clen = f_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen+e_clen+f_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            clen += d_clen;

            System.arraycopy( e_cary, 0, r_cary, clen, e_clen);

            clen += e_clen;

            System.arraycopy( f_cary, 0, r_cary, clen, f_clen);

            return new String(r_cary);
        }
    }

    /**
     * Concatenate seven strings
     */
    public final static String cat ( String a, String b, String c, String d, String e, String f, String g){

        if ( null == a)

            return cat(b,c,d,e,f,g);

        else if ( null == b)

            return cat(a,c,d,e,f,g);

        else if ( null == c)

            return cat(a,b,d,e,f,g);

        else if ( null == d)

            return cat(a,b,c,e,f,g);

        else if ( null == e)

            return cat(a,b,c,d,f,g);

        else if ( null == f)

            return cat(a,b,c,d,e,g);

        else if ( null == g)

            return cat(a,b,c,d,e,f);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                e_cary = e.toCharArray(), 
                f_cary = f.toCharArray(), 
                g_cary = g.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                e_clen = e_cary.length,
                f_clen = f_cary.length,
                g_clen = g_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen+e_clen+f_clen+g_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            clen += d_clen;

            System.arraycopy( e_cary, 0, r_cary, clen, e_clen);

            clen += e_clen;

            System.arraycopy( f_cary, 0, r_cary, clen, f_clen);

            clen += f_clen;

            System.arraycopy( g_cary, 0, r_cary, clen, g_clen);

            return new String(r_cary);
        }
    }

    /**
     * Concatenate eight strings
     */
    public final static String cat ( String a, String b, String c, String d, String e, String f, String g, String h){

        if ( null == a)

            return cat(b,c,d,e,f,g,h);

        else if ( null == b)

            return cat(a,c,d,e,f,g,h);

        else if ( null == c)

            return cat(a,b,d,e,f,g,h);

        else if ( null == d)

            return cat(a,b,c,e,f,g,h);

        else if ( null == e)

            return cat(a,b,c,d,f,g,h);

        else if ( null == f)

            return cat(a,b,c,d,e,g,h);

        else if ( null == g)

            return cat(a,b,c,d,e,f,h);

        else if ( null == h)

            return cat(a,b,c,d,e,f,g);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                e_cary = e.toCharArray(), 
                f_cary = f.toCharArray(), 
                g_cary = g.toCharArray(), 
                h_cary = h.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                e_clen = e_cary.length,
                f_clen = f_cary.length,
                g_clen = g_cary.length,
                h_clen = h_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen+e_clen+f_clen+g_clen+h_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            clen += d_clen;

            System.arraycopy( e_cary, 0, r_cary, clen, e_clen);

            clen += e_clen;

            System.arraycopy( f_cary, 0, r_cary, clen, f_clen);

            clen += f_clen;

            System.arraycopy( g_cary, 0, r_cary, clen, g_clen);

            clen += g_clen;

            System.arraycopy( h_cary, 0, r_cary, clen, h_clen);

            return new String(r_cary);
        }
    }

    /**
     * Concatenate nine strings
     */
    public final static String cat ( String a, String b, String c, String d, String e, String f, String g, String h, String i){

        if ( null == a)

            return cat(b,c,d,e,f,g,h,i);

        else if ( null == b)

            return cat(a,c,d,e,f,g,h,i);

        else if ( null == c)

            return cat(a,b,d,e,f,g,h,i);

        else if ( null == d)

            return cat(a,b,c,e,f,g,h,i);

        else if ( null == e)

            return cat(a,b,c,d,f,g,h,i);

        else if ( null == f)

            return cat(a,b,c,d,e,g,h,i);

        else if ( null == g)

            return cat(a,b,c,d,e,f,h,i);

        else if ( null == h)

            return cat(a,b,c,d,e,f,g,i);

        else if ( null == i)

            return cat(a,b,c,d,e,f,g,h);

        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray(), 
                c_cary = c.toCharArray(), 
                d_cary = d.toCharArray(), 
                e_cary = e.toCharArray(), 
                f_cary = f.toCharArray(), 
                g_cary = g.toCharArray(), 
                h_cary = h.toCharArray(), 
                i_cary = i.toCharArray(), 
                r_cary;

            int a_clen = a_cary.length, 
                b_clen = b_cary.length, 
                c_clen = c_cary.length,
                d_clen = d_cary.length,
                e_clen = e_cary.length,
                f_clen = f_cary.length,
                g_clen = g_cary.length,
                h_clen = h_cary.length,
                i_clen = i_cary.length,
                clen = a_clen;

            r_cary = new char[a_clen+b_clen+c_clen+d_clen+e_clen+f_clen+g_clen+h_clen+i_clen];

            System.arraycopy( a_cary, 0, r_cary, 0, a_clen);

            System.arraycopy( b_cary, 0, r_cary, clen, b_clen);

            clen += b_clen;

            System.arraycopy( c_cary, 0, r_cary, clen, c_clen);

            clen += c_clen;

            System.arraycopy( d_cary, 0, r_cary, clen, d_clen);

            clen += d_clen;

            System.arraycopy( e_cary, 0, r_cary, clen, e_clen);

            clen += e_clen;

            System.arraycopy( f_cary, 0, r_cary, clen, f_clen);

            clen += f_clen;

            System.arraycopy( g_cary, 0, r_cary, clen, g_clen);

            clen += g_clen;

            System.arraycopy( h_cary, 0, r_cary, clen, h_clen);

            clen += h_clen;

            System.arraycopy( i_cary, 0, r_cary, clen, i_clen);

            return new String(r_cary);
        }
    }
    /**
     * Rejoin from split with one char 'sep'.
     */
    public final static String cat(String[] source, int idx, int length, char sep){
        if (null != source && -1 < idx && 0 < length){
            int count = (length-idx);
            switch (count){
            case 0:
                return null;
            case 1:
                return source[idx];
            default:
                Chbuf buf = new Chbuf();
                for (int cc = idx; cc < length; cc++){
                    if (cc > idx)
                        buf.append(sep);
                    buf.append(source[cc]);
                }
                return buf.toString();
            }

        }
        else
            return null;
    }

    private final static char   fcat_ch = '/';
    private final static char[] fcat_ca = {fcat_ch};

    /**
     * Concatenate two file or url path strings, guaranteeing exactly one url
     * path separator <tt>("/")</tt> between each string.  
     */
    public final static String fcat ( String a, String b){

        if ( null == a){
            /*
             * Prepend
             */
            if (null == b)
                return null;
            else {
                char[] b_cary = b.toCharArray();
                int b_len = b_cary.length;
                if ( 0 >= b_len)
                    return null;
                else if (fcat_ch == b_cary[0])
                    return b;
                else {
                    char[] r_cary = new char[b_len+1];
                    r_cary[0] = fcat_ch;
                    System.arraycopy(b_cary,0,r_cary,1,b_len);
                    return new String(r_cary);
                }
            }
        }
        else if ( null == b){
            /*
             * Append
             */
            if (null == a)
                return null;
            else {
                char[] a_cary = a.toCharArray();
                int a_len = a_cary.length;
                if ( 0 >= a_len)
                    return null;
                else if (fcat_ch == a_cary[ (a_len - 1) ])
                    return a;
                else {
                    char[] r_cary = new char[a_len+1];
                    r_cary[a_len] = fcat_ch;
                    System.arraycopy(a_cary,0,r_cary,0,a_len);
                    return new String(r_cary);
                }
            }
        }
        else {
            char[] a_cary = a.toCharArray(), 
                b_cary = b.toCharArray();

            if ( 1 > a_cary.length) 

                return b;

            else if ( 1 > b_cary.length)

                return a;

            else {
                char aN = a_cary[a_cary.length-1];
                char b0 = b_cary[0];

                if ( fcat_ch != aN && fcat_ch != b0)

                    return new String( cat( a_cary, fcat_ca, b_cary));

                else if ( fcat_ch == aN && fcat_ch == b0)

                    return new String( cat( a_cary, b_cary,1,b_cary.length-1));

                else 
                    return new String( cat( a_cary, b_cary));
            }
        }
    }



    public final static char[] substring ( char[] cary, int idx){
        if ( null == cary)
            return null;
        else 
            return substring(cary,idx,cary.length);
    }
    public final static char[] substring ( char[] cary, int from_idx, int to_idx){

        if ( null == cary || 0 >= to_idx || 0 > from_idx)

            return null;

        else {
            char[] copier = new char[to_idx-from_idx];

            System.arraycopy(cary,from_idx,copier,0,copier.length);

            return copier;
        }
    }
    public final static int indexOf ( char[] str, char ch){
        if ( null == str)
            return -1;
        else {
            int strlen = str.length;

            for ( int cc = 0; cc < strlen; cc++){
                if ( ch == str[cc])
                    return cc;
            }
            return -1;
        }
    }
    public final static int indexOf ( char[] str, char[] term){
        return indexOf(str,term,0);
    }
    public final static int indexOf ( char[] str, char[] term, int fromidx){
        if ( null == str || null == term)
            return -1;
        else {
            int strlen = str.length;

            int termlen = term.length;

            boolean found = true;

            for ( int cc = fromidx, tc, tcc; cc < strlen; cc++){

                if ( term[0] == str[cc]){

                    found = true;

                    for ( tc = 1, tcc = (cc+1); tc < termlen && tcc < strlen; tc++, tcc++){
                        if ( term[tc] != str[tcc]){

                            found = false;

                            break;
                        }
                    }
                    if (found)
                        return cc;
                }
            }
            return -1;
        }
    }
    public final static int lastIndexOf ( char[] str, char ch){
        if ( null == str)
            return -1;
        else {

            for ( int cc = str.length-1; cc >= 0; cc--){
                if ( ch == str[cc])
                    return cc;
            }
            return -1;
        }
    }
    public final static int lastIndexOf ( char[] str, char ch, int from){
        if ( null == str || from >= str.length)
            return -1;
        else {
            for ( int cc = from; cc >= 0; cc--){
                if ( ch == str[cc])
                    return cc;
            }
            return -1;
        }
    }

    public final static char[] copy ( char[] valc){
        if ( null == valc)
            return null;
        else {
            int valclen = valc.length;

            char[] copier = new char[valclen];

            System.arraycopy(valc,0,copier,0,valclen);

            return copier;
        }
    }

    /**
     * Accept hex, octal and decimal number strings using traditional
     * string formatting: "0x", "0X" and "#" prefixes for hexadecimal,
     * "0" for octal and otherwise presume decimal. 
     */
    public final static long decode_long ( String s) throws NumberFormatException {

        char ch = s.charAt(0);

        if ( '0' == ch){

            ch = s.charAt(1);

            if ( 'x' == ch || 'X' == ch)
                return Long.parseLong(s.substring(2), 16);
            else
                return Long.parseLong(s.substring(1), 8);

        }
        else if ( '#' == ch)

            return Long.parseLong(s.substring(1), 16);

        else 
            return Long.parseLong(s,10);
    }

    /**
     * Accept hex, octal and decimal number strings using traditional
     * string formatting: "0x", "0X" and "#" prefixes for hexadecimal,
     * "0" for octal and otherwise presume decimal. 
     */
    public final static int decode_int ( String s) throws NumberFormatException {

        char ch = s.charAt(0);

        if ( '0' == ch){

            ch = s.charAt(1);

            if ( 'x' == ch || 'X' == ch)
                return Integer.parseInt(s.substring(2), 16);
            else
                return Integer.parseInt(s.substring(1), 8);
        }
        else if ( '#' == ch) 

            return Integer.parseInt(s.substring(1), 16);

        else 
            return Integer.parseInt(s,10);
    }

    /**
     * Return 64 characters in ASCII `1' or `0' representing input number.
     */
    public final static String toBinaryString( long ln){

        char ret[] = new char[64], one = '1', zero = '0';

        for ( int lc = 0; lc < 64; lc++){

            if ( 1 == (ln & 1))

                ret[63-lc] = one;
            else
                ret[63-lc] = zero;

            ln >>>= 1;
        }
        return new String(ret);
    }

    /**
     * Return 32 characters in ASCII `1' or `0' representing input number.
     */
    public final static String toBinaryString( int ln){

        char ret[] = new char[32], one = '1', zero = '0';

        for ( int lc = 0; lc < 32; lc++){

            if ( 1 == (ln & 1))

                ret[31-lc] = one;
            else
                ret[31-lc] = zero;

            ln >>>= 1;
        }
        return new String(ret);
    }
    public final static String toBinaryString( byte ln){

        char ret[] = new char[8], one = '1', zero = '0';

        for ( int lc = 0; lc < 8; lc++){

            if ( 1 == (ln & 1))

                ret[7-lc] = one;
            else
                ret[7-lc] = zero;

            ln >>>= 1;
        }
        return new String(ret);
    }
    public final static String toBinaryString( byte[] bits){
        if (null == bits)
            return null;
        else {
            int bits_len = bits.length;
            if (1 > bits_len)
                return null;
            else {
                Chbuf strbuf = new Chbuf();
                for (int cc = 0; cc < bits_len; cc++){
                    if (0 < cc)
                        strbuf.append(' ');
                    strbuf.append(toBinaryString(bits[cc]));
                }
                return strbuf.toString();
            }
        }
    }
    public final static String toBinaryString( int[] bits){
        if (null == bits)
            return null;
        else {
            int bits_len = bits.length;
            if (1 > bits_len)
                return null;
            else {
                Chbuf strbuf = new Chbuf();
                for (int cc = 0; cc < bits_len; cc++){
                    if (0 < cc)
                        strbuf.append(' ');
                    strbuf.append(toBinaryString(bits[cc]));
                }
                return strbuf.toString();
            }
        }
    }
    /**
     * Convert a string to title case, where an initial alpha
     * character, and any alpha character following whitespace, is
     * upper case.  As the title of a book.
     */
    public final static String toTitleCase( String s){
        if ( null == s)
            return null;
        else {
            char[] cary = s.toCharArray();

            int len = cary.length; 

            if ( 0 < len){

                cary[0] = Character.toUpperCase(cary[0]);

                boolean next = false;

                for ( int cc = 1; cc < len; cc++){

                    if (next){
                        if (!Character.isWhitespace(cary[cc])){

                            cary[cc] = Character.toUpperCase(cary[cc]);

                            next = false;
                        }
                    }
                    else if (Character.isWhitespace(cary[cc]))

                        next = true;
                    else
                        continue;
                }
                return new String(cary);
            }
            else
                return s;
        }
    }
    /**
     * Truncate substring from source
     * @param src Source string buffer
     * @param src_ofs Source string offset within source string buffer
     * @param src_len Source string length from offset within buffer
     * @param trunc_ofs Truncate substring offset within source string
     * in absolute buffer index coordinates --- same as 'src_ofs', not
     * relative to 'src_ofs', so that src_ofs value zero points to the
     * identical string character as 'trunc_ofs' value zero, etc.
     * @param trunc_len Truncate substring length from substring offset
     */
    public final static char[] truncate(char[] src, int src_ofs, int src_len, 
                                        int trunc_ofs, int trunc_len)
    {
        if (null == src || 0 > src_ofs || 0 > src_len)
            throw new java.lang.IllegalArgumentException("src");
        else if (0 > trunc_ofs || 1 > trunc_len || trunc_ofs < src_ofs || trunc_len > src_len)
            throw new java.lang.IllegalArgumentException("trunc");
        else if (trunc_ofs == src_ofs){
            if (trunc_len == src_len){
                if (src.length == src_len)
                    return src;
                else if (0 == src_len)
                    return null;
                else {
                    char[] re = new char[src_len];
                    System.arraycopy(src,src_ofs,re,0,src_len);
                    return re;
                }
            }
            else /*(trunc_len < src_len)*/{
                int re_len = (src_len - trunc_len);
                if (0 == re_len)
                    return null;
                else {
                    char[] re = new char[src_len];
                    System.arraycopy(src,src_ofs,re,0,re_len);
                    return re;
                }
            }
        }
        else if (trunc_len == src_len)
            throw new java.lang.IllegalArgumentException("trunc");

        else /*(trunc_ofs > src_ofs)&&
              *(trunc_len < src_len)
              */
            {
                int a_ofs  = src_ofs;
                int a_len  = (trunc_ofs-src_ofs);
                char[] a = substring(src,a_ofs,a_len);

                int b_ofs  = trunc_ofs+trunc_len;
                int b_len  = (src_len - (b_ofs + 1));
                char[] b = substring(src,b_ofs,b_len);

                return cat(a,b);
            }
    }
    /**
     * Truncate substring from source
     * @param src Source string
     * @param trunc_ofs Truncate substring offset 
     * @param trunc_len Truncate substring length
     */
    public final static char[] truncate(char[] src, int trunc_ofs, int trunc_len){
        if (null == src)
            throw new java.lang.IllegalArgumentException("Null src");
        else
            return truncate(src,0,src.length,trunc_ofs,trunc_len);
    }
    /**
     * Truncate substring from source
     * @param src Source string
     * @param trunc_ofs Truncate substring offset 
     * @param trunc_len Truncate substring length
     */
    public final static java.lang.String truncate(java.lang.String src, int trunc_ofs, int trunc_len){
        if (null == src)
            throw new java.lang.IllegalArgumentException("Null src");
        else {
            char[] cary = src.toCharArray();
            char[] re = truncate(cary,0,cary.length,trunc_ofs,trunc_len);
            if (null == re)
                return null;
            else
                return new java.lang.String(re);
        }
    }

    public final static java.lang.String[] add(java.lang.String[] list, java.lang.String string){
        if (null == string)
            return list;
        else if (null == list)
            return new java.lang.String[]{string};
        else {
            int len = list.length;
            java.lang.String[] copier = new java.lang.String[len+1];
            System.arraycopy(list,0,copier,0,len);
            copier[len] = string;
            return copier;
        }
    }

    public final static java.lang.String[] split(java.lang.String string, char sep){
        java.lang.String[] re = null;
        char[] cary = string.toCharArray();
        int length = cary.length, ofs = 0, len = 0;
        for (int cc = 0; cc < length; cc++){
            if (sep == cary[cc]){
                len = (cc-ofs);
                if (0 < len){
                    re = add(re,new String(cary,ofs,len));
                }
                ofs = (cc + 1);
            }
        }
        if (0 == ofs)
            return new String[]{string};
        else if (ofs < length){
            len = (length-ofs);
            return add(re,new String(cary,ofs,len));
        }
        else
            return re;
    }
    public final static java.lang.String[] split(java.lang.String string, char sep, char excl){
        java.lang.String[] re = null;
        char ch, cary[] = string.toCharArray();
        int length = cary.length, ofs = 0, len = 0;
        for (int cc = 0; cc < length; cc++){
            ch = cary[cc];
            if (sep == ch){
                len = (cc-ofs);
                if (0 < len){
                    re = add(re,new String(cary,ofs,len));
                }
                ofs = (cc + 1);
            }
            else if (excl == ch)
                return null;
        }
        if (0 == ofs)
            return new String[]{string};
        else if (ofs < length){
            len = (length-ofs);
            return add(re,new String(cary,ofs,len));
        }
        else
            return re;
    }
}
