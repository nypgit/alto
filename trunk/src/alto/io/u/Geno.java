/* 
 *  Syntelos iou
 *  Copyright (C) 2006 John Pritchard and the Alto Project Group.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as
 *  published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307 USA
 */
package alto.io.u;

/**
 * <p> This print stream is an abstraction for writing JavaScript and
 * Java source files.  It writes to a buffer internally, which must be
 * either written to a file or copied to an output stream using the
 * methods provided. </p>
 * 
 * @author jdp
 * @since 1.1
 */
public class Geno
    extends java.io.PrintStream
{

    private int indent = 0;

    private final java.lang.String id;

    private final java.io.File target;


    public Geno(java.lang.String id, java.io.File target)
        throws java.io.IOException
    {
        super(new java.io.ByteArrayOutputStream());
        this.target = target;
        if (null == id)
            throw new alto.sys.Error.Format("Missing 'id'");
        else {
            this.id = id.trim();
            if (1 > this.id.length())
                throw new alto.sys.Error.Format("Empty 'id'");
            else
                return;
        }
    }


    public final void writeToFile()
        throws java.io.IOException
    {
        java.io.ByteArrayOutputStream buf = (java.io.ByteArrayOutputStream)this.out;
        java.io.FileOutputStream out = new java.io.FileOutputStream(this.target);
        try {
            out.write(buf.toByteArray());
            out.flush();
        }
        finally {
            out.close();
        }
    }
    public final void copyToOutput(java.io.OutputStream out)
        throws java.io.IOException
    {
        java.io.ByteArrayOutputStream buf = (java.io.ByteArrayOutputStream)this.out;
        out.write(buf.toByteArray());
        out.flush();
    }
    public final java.lang.String getId(){
        return this.id;
    }
    public final java.io.File getTarget(){
        return this.target;
    }
    public final int peek(){
        return this.indent;
    }
    public final int push(){
        int indent = this.indent;
        indent += 1;
        return (this.indent = indent);
    }
    public final int push(int p){
        if (p >= this.indent)
            return (this.indent = p);
        else
            throw new alto.sys.Error.Argument(String.valueOf(p)+" < "+this.indent);
    }
    public final int pop(){
        int indent = this.indent;
        if (0 < indent){
            indent -= 1;
            return (this.indent = indent);
        }
        else
            return indent;
    }
    public final int currentIndent(){
        return this.indent;
    }
    /**
     * @param level 
     * 
     * @return Current indent is greater than argument 'level' (some
     * known indent level).
     */
    public final boolean isIndentedFrom(int level){
        if (-1 < level)
            return (this.indent > level);
        else
            throw new alto.sys.Error.Argument(String.valueOf(level));
    }
    public final boolean isNotIndentedFrom(int level){
        return (!this.isIndentedFrom(level));
    }
    /**
     * Print indent- many "tabs" (as spaces).
     */
    public final void indent()
        throws java.io.IOException
    {
        this.indent( this.indent); 
    }
    public final void indent(int indent)
        throws java.io.IOException
    {
        for (int cc = 0; cc < indent; cc++){
            super.print("    ");
        }
    }
    public final void indent(java.lang.String text)
        throws java.io.IOException
    {
        this.indent();
        this.print(text);
    }
    public final void indentln(java.lang.String text)
        throws java.io.IOException
    {
        this.indent();
        this.println(text);
    }
    public final void indent(int indent, java.lang.String text)
        throws java.io.IOException
    {
        this.indent(indent);
        this.print(text);
    }
    public final void indentln(int indent, java.lang.String text)
        throws java.io.IOException
    {
        this.indent(indent);
        this.println(text);
    }
    /**
     * <p> Called from comment and fcomment to write lines prefixed by
     * star -- without open and close comment character sequences. 
     * </p>
     */
    public final void commentlines(java.lang.String text)
        throws java.io.IOException
    {
        if (null != text){
            java.util.StringTokenizer strtok = new java.util.StringTokenizer(text.replace('\r','\n'),"\n",true);
            int nl = 0;
            while (strtok.hasMoreTokens()){
                java.lang.String tok = strtok.nextToken();
                if ("\n".equals(tok)){
                    if (0 < nl)
                        this.indentln(" * ");
                    nl++;
                }
                else {
                    nl = 0;
                    this.indent(" * ");
                    this.println(tok);
                }
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
        if (null != text)
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
        this.indentln("/**");
        if (null != text)
            this.commentlines(text);
        else
            this.indentln(" * ");
        this.indentln(" */");
    }
    public final void fcomment()
        throws java.io.IOException
    {
        this.fcomment(null);
    }
    /**
     * Print close block '}', throw exception on failure.
     */
    public final void popBlock()
        throws java.io.IOException
    {
        if (1 < this.peek()){
            this.pop();
            this.indentln("}");
        }
        else
            throw new java.lang.IllegalStateException("no open block");
    }
    /**
     * Print close block '}' if possible.
     */
    public final void closeBlock()
        throws java.io.IOException
    {
        if (0 < this.indent){
            this.pop();
            this.indentln("}");
        }
    }
    /**
     * Return to level
     * 
     * @param level Close blocks within but not including 'level' to
     * return to 'level'.
     */
    public final void popBlockTo(int level)
        throws java.io.IOException
    {
        while (this.isIndentedFrom(level))
            this.closeBlock();
    }
    /**
     * Pop to parent of level.
     * 
     * @param level Close blocks including 'level' to return to "level - 1".
     */
    public final void closeBlocks(int level)
        throws java.io.IOException
    {
        while (level <= this.indent){
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
    public final void popClose()
        throws java.io.IOException
    {
        this.closeBlock();
    }

}
