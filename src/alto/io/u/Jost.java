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
 * <p> An indent output stream that abstracts from writing C/Java
 * (syntax) source files.  </p>
 * 
 * @author jdp
 * @since 1.1
 */
public class Jost
    extends Ios
{

    public Jost(java.io.File target)
        throws java.io.IOException
    {
        super(target);
    }
    public Jost(java.lang.String id, java.io.OutputStream target){
        super(id,target);
    }

    /**
     * <p> Called from comment and fcomment to write lines prefixed by
     * star -- without open and close comment character sequences. 
     * </p>
     */
    public final void commentlines(java.lang.String text)
        throws java.io.IOException
    {
        java.util.StringTokenizer strtok = new java.util.StringTokenizer(text,"\r\n",true);
        int nl = 0;
        while (strtok.hasMoreTokens()){
            java.lang.String tok = strtok.nextToken();
            if (1 == tok.length()){
                switch (tok.charAt(0)){
                case '\r':
                case '\n':
                    if (0 < nl)
                        this.indentln(" * ");
                    nl++;
                    break;
                default:
                    nl = 0;
                    this.indentln(" * "+tok);
                    break;
                }
            }
            else {
                nl = 0;
                this.indentln(" * "+tok);
            }
        }
    }
    /**
     * <p> Write plain, one star comment. 
     * </p>
     */
    public final void comment(java.lang.String text)
        throws java.io.IOException
    {
        this.indentln("/*");
        this.commentlines(text);
        this.indentln(" */");
    }
    /**
     * <p> Write function, two star comment. 
     * </p>
     */
    public final void fcomment(java.lang.String text)
        throws java.io.IOException
    {
        this.writeln();
        this.indentln("/**");
        this.commentlines(text);
        this.indentln(" */");
    }
    public final void closeBlock()
        throws java.io.IOException
    {
        this.pop();
        this.indentln("}");
    }
    /**
     * @param last Value from a previous peek, close blocks opened
     * subsequent to that peek.
     */
    public final void closeBlocks(int last)
        throws java.io.IOException
    {
        int open = this.peek()-last;
        for (int cc = 0; cc < open; cc++){
            this.pop();
            this.indentln("}");
        }
    }
    public final void openBlock(java.lang.String line)
        throws java.io.IOException
    {
        this.indentln(line);
        this.push();
    }
    public final void openIf(java.lang.String condition)
        throws java.io.IOException
    {
        this.openBlock("if ("+condition+"){");
    }
    public final void openElseIf(java.lang.String condition)
        throws java.io.IOException
    {
        this.closeBlock();
        this.openBlock("else if ("+condition+"){");
    }
    public final void openElse()
        throws java.io.IOException
    {
        this.closeBlock();
        this.openBlock("else {");
    }
    public final void closeIf()
        throws java.io.IOException
    {
        this.closeBlock();
    }

}
