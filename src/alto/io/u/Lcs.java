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

/**
 * <p> A C / Java <code>"{"</code> block <code>"}"</code> style
 * programming language (source code) output buffer and formatter.
 * </p>
 * 
 * <p> Compatibility requires each block indent level in four space
 * characters. </p>
 * 
 * @author jdp
 * @since 1.1
 */
public class Lcs 
    extends Lc
{
    protected final static char BLOCK_OPEN = '{';
    protected final static char BLOCK_CLOSE = '}';
    protected final static char[] BLOCK  = { BLOCK_OPEN, BLOCK_CLOSE};

    protected final static char[] NIL = null;

    protected final static char[] LF  = { '\n'};

    protected final static char[] TAB = { ' ', ' ', ' ', ' '};

    private static char[][] INDENT = { 
        NIL,
        { ' ', ' ', ' ', ' '},
        { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
        { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
    };
    protected final static char[] INDENT(int lvl){
        if (0 > lvl)
            throw new alto.sys.Error.Bug(java.lang.String.valueOf(lvl));
        else {
            int len = INDENT.length;
            if (lvl >= len){
                int need = (lvl-len)+1;
                int nlen = (len+need);
                char[][] NINDENT = new char[nlen][];
                java.lang.System.arraycopy(INDENT,0,NINDENT,0,len);

                for (int nidx = (nlen - need); nidx < nlen; nidx++){

                    NINDENT[nidx] = Chbuf.cat(NINDENT[nidx-1],TAB);
                }
                INDENT = NINDENT;
            }
            return INDENT[lvl];
        }
    }


    private int indent = 0;


    public Lcs(){
        super();
    }


    public Lcs append(java.lang.String text){
        if (null == text)
            return this;
        else {
            char[][] lines = LinesPp(text);
            if (null == lines)
                return this;
            else {
                int count = lines.length;
                int term = (count-1);
                char[] line;
                boolean newline;
                for (int cc = 0; cc < count; cc++){
                    line = lines[cc];
                    newline = ('\n' == line[line.length-1]);
                    this.appendLine(line,newline);
                }
                return this;
            }
        }
    }

    protected final void appendLine(char[] line, boolean newline){
        if (this.lcIsInLine()){
            this.lcInline(line,newline);
        }
        else {
            switch(this.lcEndsWith(BLOCK)){
            case BLOCK_OPEN:
                this.pushIndent();
                break;
            case BLOCK_CLOSE:
                this.popIndent();
                break;
            default:
                break;
            }
            char[] indent = this.getIndent();
            if (null != indent)
                line = Chbuf.cat(indent,line);
            this.lcNewline(line);
        }
    }

    protected final int peekIndent(){
        return this.indent;
    }
    protected final int pushIndent(){
        return (++this.indent);
    }
    protected final int popIndent(){
        return (--this.indent);
    }
    protected final char[] getIndent(){
        return INDENT(this.indent);
    }

    /**
     * @return A list of trimmed lines, with newlines (LF).
     */
    protected final static char[][] LinesPp(java.lang.String text){
        char[][] re = null;
        java.util.StringTokenizer tok = new java.util.StringTokenizer(text,"\r\n",true);
        while (tok.hasMoreTokens()){
            String token = tok.nextToken();
            String trimmed = token.trim();
            if (0 == trimmed.length()){
                if (-1 < token.lastIndexOf('\n')){
                    if (null != re){
                        int idx = (re.length-1);
                        char[] test = re[idx];
                        if ('\n' != test[test.length-1])
                            re[idx] = Chbuf.cat(re[idx],LF);
                    }
                    else
                        re = Chbuf.cat(re,LF);
                }
            }
            else {
                char[] lc = trimmed.toCharArray();
                re = Chbuf.cat(re,lc);
            }
        }
        return re;
    }
}
