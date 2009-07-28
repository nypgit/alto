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
package alto.tools;

public class Interfaces
    extends java.util.HashSet<Class>
{

    private final Class from;


    public Interfaces(Class jclass){
        super();
        this.from = jclass;
        if (null != jclass){
            Interfaces infs = new Interfaces(jclass.getSuperclass());
            this.addAll(infs);
            for (Class inf : jclass.getInterfaces()){
                infs = new Interfaces(inf);
                this.addAll(infs);
            }
        }
    }


    public Class getFrom(){
        return this.from;
    }


    public static void usage(java.io.PrintStream out){
        out.println("Usage");
        out.println();
        out.println("  alto.tools.Interfaces <class.name>");
        out.println();
        out.println("Description");
        out.println();
        out.println("  List all implemented interfaces by name, one per line.");
        out.println();
    }
    public static void main(String[] argv){
        if (null != argv && 1 == argv.length){
            try {
                alto.io.Tools.SInit();

                Class jclass = Class.forName(argv[0]);
                Interfaces interfaces = new Interfaces(jclass);
                for (Class inf : interfaces){
                    System.out.println(inf.getName());
                }
                System.exit(0);
            }
            catch (ClassNotFoundException exc){
                usage(System.err);
                System.exit(1);
            }
            catch (Throwable exc){
                exc.printStackTrace();
                for (Throwable t = exc.getCause(); null != t; t = t.getCause())
                    t.printStackTrace();
            }
        }
        else {
            usage(System.err);
            System.exit(1);
        }
    }
}