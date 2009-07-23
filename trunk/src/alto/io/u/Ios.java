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
 * <p> An indent output stream.  </p>
 * 
 * @author jdp
 * @since 1.1
 */
public class Ios
    extends java.io.FilterOutputStream
{
    private final static byte[] EMPTY = new byte[0];

    private volatile static byte[][] INDENT = {
        {} //(0...+)
    };

    private int indent = 0;

    private byte[] linesep = Utf8.encode("\r\n");

    private final java.lang.String indentString = " ";

    private final java.lang.String id;


    public Ios(java.io.File target)
        throws java.io.IOException
    {
        super(new java.io.FileOutputStream(target));
        this.id = target.getPath();
    }
    public Ios(java.lang.String id, java.io.OutputStream target){
        super(target);
        this.id = id;
    }
    public Ios(java.io.OutputStream target){
        this("<undefined>",target);
    }

    public final java.lang.String getId(){
        return this.id;
    }
    public final java.lang.String getLineSeparator(){
        return new java.lang.String(Utf8.decode(this.linesep));
    }
    public final synchronized void setLineSeparator(java.lang.String sep){
        this.linesep = Utf8.encode(sep);
    }
    public final int peek(){
        return this.indent;
    }
    public final synchronized int push(){
        return (++this.indent);
    }
    public final synchronized int pop(){
        return (--this.indent);
    }
    public final byte[] getIndent()
        throws java.io.IOException
    {
        int indent = this.indent;
        if (0 >= indent)
            return EMPTY;
        else {
            byte[] bits;
            byte[][] indentary = INDENT;
            int indentary_len = indentary.length;
            if (indent >= indentary_len){
                bits = Bbuf.ngen("    ",this.indent);
                byte[][] copier = new byte[indent+1][];
                java.lang.System.arraycopy(indentary,0,copier,0,indentary_len);
                indentary = copier;
                indentary[indent] = bits;
                /*
                 * fill any intermediates
                 */
                java.lang.String indentString = this.indentString;
                for (int cc = indentary_len; cc < indent; cc++){
                    byte[] gen = Bbuf.ngen(indentString,cc);
                    indentary[cc] = gen;
                }
                INDENT = indentary;
            }
            else
                bits = indentary[indent];
            //
            return bits;
        }
    }
    public final void indent()
        throws java.io.IOException
    {
        byte[] indent = this.getIndent();
        if (EMPTY == indent)
            return;
        else {
            this.write(indent,0,indent.length);
        }
    }
    public final void indent(java.lang.String text)
        throws java.io.IOException
    {
        this.indent();
        byte[] out = Utf8.encode(text);
        if (null != out)
            this.write(out,0,out.length);
    }
    public final void indentln(java.lang.String text)
        throws java.io.IOException
    {
        this.indent(text);
        this.writeln();
    }
    public void write(String string)
        throws java.io.IOException
    {
        this.write(Utf8.encode(string));
    }
    public void writeln(String string)
        throws java.io.IOException
    {
        this.write(string);
        this.writeln();
    }
    public final void writeln()
        throws java.io.IOException
    {
        byte[] linesep = this.linesep;
        if (null != linesep)
            this.write(linesep,0,linesep.length);
        this.flush();
    }
    public void print(String string)
        throws java.io.IOException
    {
        this.write(Utf8.encode(string));
    }
    public void println(String string)
        throws java.io.IOException
    {
        this.write(Utf8.encode(string));
        this.writeln();
    }
    public final void println()
        throws java.io.IOException
    {
        byte[] linesep = this.linesep;
        if (null != linesep)
            this.write(linesep,0,linesep.length);
        this.flush();
    }
    public final void dataln(byte[] data)
        throws java.io.IOException
    {
        if (null == data)
            this.println();
        else {
            int count = data.length;
            if (1 > count)
                this.println();
            else {
                for (int dc = 0; dc < count; dc++){
                    this.indent();
                    for (int cc = 0; cc < 4; cc++){
                        if (dc < count){
                            this.print(Hex.encode(data[dc++])+' ');
                        }
                    }
                    if (dc < count){
                        this.print(" ");
                        for (int cc = 0; cc < 4; cc++){
                            if (dc < count){
                                this.print(Hex.encode(data[dc++])+' ');
                            }
                        }
                    }
                    this.println();
                }
            }
        }
    }

}
