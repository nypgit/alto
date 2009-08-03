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
 * List all files, excluding those under a specific directory.  For
 * example, <code>"CVS"</code> or <code>".svn"</code>
 * 
 * @author jdp
 * @since 1.5
 */
public final class Find 
    extends java.lang.Object
    implements java.util.Iterator<java.io.File>
{
    public final static java.io.FileFilter NulFilter = null;

    /**
     * Exclude files under a specific directory.  For example,
     * <code>"/CVS/"</code> or <code>"/.svn/"</code>
     */
    public final static class ExpFilter
        extends java.lang.Object
        implements java.io.FileFilter
    {
        private final boolean exclude;
        private final String excludeP;
        private final String excludeN;

        public ExpFilter(String exclude){
            super();
            if (null != exclude){
                this.exclude = true;
                this.excludeP = '/'+exclude+'/';
                this.excludeN = exclude;
            }
            else {
                this.exclude = false;
                this.excludeP = null;
                this.excludeN = null;
            }
        }

        public boolean accept(java.io.File file){
            if (this.exclude){
                if (file.isFile()){
                    String path = file.getPath();
                    if (path.contains(this.excludeP))
                        return false;
                    else
                        return true;
                }
                else {
                    String name = file.getName();
                    if (name.equals(this.excludeN))
                        return false;
                    else
                        return true;
                }
            }
            else
                return true;
        }
    }

    /**
     * 
     */
    public final static class File 
        extends java.lang.Object
    {
        public final static File[] List(Find parent, java.io.File[] list){
            if (null != list){
                int count = list.length;
                if (0 < count){
                    File[] re = new File[count];
                    for (int cc = 0; cc < count; cc++){
                        re[cc] = new File(parent,list[cc]);
                    }
                    return re;
                }
            }
            return null;
        }


        private final java.io.File file;
        private final Find directory;

        public File(Find parent, java.io.File fd){
            super();
            if (fd.isFile()){
                this.file = fd;
                this.directory = null;
            }
            else if (fd.isDirectory()){
                this.file = null;
                this.directory = new Find(parent,fd);
            }
            else
                throw new java.lang.IllegalArgumentException(fd.getPath());
        }

        public boolean isFile(){
            return (null != this.file);
        }
        public java.io.File getFile(){
            java.io.File file = this.file;
            if (null != file)
                return file;
            else
                throw new alto.sys.Error.State();
        }
        public boolean isDirectory(){
            return (null != this.directory);
        }
        public boolean hasNext(){
            Find directory = this.directory;
            if (null != directory)
                return directory.hasNext();
            else
                throw new alto.sys.Error.State();
        }
        public java.io.File next(){
            Find directory = this.directory;
            if (null != directory)
                return directory.next();
            else
                throw new alto.sys.Error.State();
        }
    }


    private final java.io.File dir;
    private final java.io.FileFilter filter;
    private final File[] list;
    private final int length;
    private int index = 0;


    public Find(java.io.File dir){
        this(NulFilter,dir);
    }
    public Find(java.io.FileFilter filter, java.io.File dir){
        super();
        if (null != dir){
            this.filter = filter;

            //
            if (dir.isDirectory()){
                this.dir = dir;
                if (null != filter)
                    this.list = File.List(this,dir.listFiles(filter));
                else
                    this.list = File.List(this,dir.listFiles());

                if (null == list)
                    this.length = 0;
                else
                    this.length = this.list.length;
            }
            else 
                throw new java.lang.IllegalArgumentException("Directory not found '"+dir+"'.");
        }
        else
            throw new java.lang.IllegalArgumentException();
    }
    private Find(Find parent, java.io.File dir){
        super();
        if (null == parent)
            throw new IllegalArgumentException();
        else
            this.filter = parent.filter;

        if (null != dir){
            if (dir.isDirectory()){
                this.dir = dir;
                if (null != this.filter)
                    this.list = File.List(this,dir.listFiles(this.filter));
                else
                    this.list = File.List(this,dir.listFiles());

                if (null == list)
                    this.length = 0;
                else
                    this.length = this.list.length;
            }
            else
                throw new java.lang.IllegalArgumentException("Directory not found '"+dir+"'.");
        }
        else
            throw new java.lang.IllegalArgumentException();
    }


    public final File peek(int ahead){
        int index = (this.index + ahead);
        if (-1 < index && index < this.length)
            return this.list[index];
        else
            return null;
    }
    public final boolean hasNext(){
        if (this.index < this.length){
            for (int cc = 0; ; cc++){
                File peek = this.peek(cc);
                if (null == peek)
                    return false;
                else if (peek.isFile())
                    return true;
                else if (peek.hasNext())
                    return true;
            }
        }
        return false;
    }
    public final java.io.File getRoot(){
        return this.dir;
    }
    public final java.io.File next(){
        for (int cc = 0; ; cc++){
            File peek = this.peek(cc);
            if (null == peek)
                throw new java.util.NoSuchElementException();
            else if (peek.isFile()){
                this.index += (cc+1);
                return peek.getFile();
            }
            else if (peek.hasNext()){
                this.index += (cc);
                return peek.next();
            }
        }
    }
    /**
     * @exception java.lang.UnsupportedOperationException Always,
     * this method is not supported here.
     */
    public final void remove()
        throws java.lang.UnsupportedOperationException
    {
        throw new java.lang.UnsupportedOperationException();
    }

    public final static void usage(java.io.PrintStream out){
        out.println("Usage");
        out.println();
        out.println("  alto.io.u.Find [-exclude dirn] -root dir");
        out.println();
        out.println("Description");
        out.println();
        out.println("  List files under 'dir'.  Optionally exclude branches contained by");
        out.println("  directories named 'dirn', for example '.svn' or 'CVS'.");
        out.println();
    }
    public static void main(String[] argv){
        Options opts = new Options(argv);
        if (opts.hasOption("-root")){
            String exclude = opts.getOption("-exclude");
            java.io.File root = opts.getOptionFile("-root");
            if (root.isDirectory()){
                java.io.FileFilter filter = new ExpFilter(exclude);
                Find list = new Find(filter,root);
                while (list.hasNext()){
                    java.io.File next = list.next();
                    System.out.println(next.getPath());
                }
                System.exit(0);
            }
            else if (!"true".equals(root.getPath())){
                System.err.println("Error, directory not found '"+root+"'.");
                System.exit(1);
            }
        }
        usage(System.err);
        System.exit(1);
    }

}
