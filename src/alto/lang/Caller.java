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
package alto.lang;

import alto.io.u.Bpo;

/**
 * Functions to work on the current thread's method call strack.
 * 
 * @since 1.2
 */
public final class Caller 
    extends Object
{
    public final static boolean IsIn(java.lang.Object frame){
        return IsIn(frame.getClass());
    }
    public final static boolean IsNotIn(java.lang.Object frame){
        return IsNotIn(frame.getClass());
    }
    public final static boolean IsNotIn(java.lang.Class classframe){
        return (!IsIn(classframe));
    }
    public final static boolean IsIn(java.lang.Class classframe){
        java.lang.String classframename = classframe.getName();
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);
        buf.pop();//pop headline
        buf.pop();//pop this frame

        char[] line;
        while (null != (line = buf.pop())){
            java.lang.String linename = ClassNameFor(line);
            if (classframename.equals(linename))
                return true;
            else {
                try {
                    java.lang.Class lineclass = Class.forName(linename);
                    if (classframe.isAssignableFrom(lineclass))
                        return true;
                }
                catch (ClassNotFoundException exc){
                }
            }
        }
        return false;
    }
    public final static boolean IsIn(java.lang.reflect.Method mframe){
        java.lang.String framename = mframe.getDeclaringClass().getName()+'.'+mframe.getName();
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);
        buf.pop();//pop headline
        buf.pop();//pop this frame

        char[] line;
        while (null != (line = buf.pop())){
            java.lang.String linename = FrameNameFor(line);
            if (framename.equals(linename))
                return true;
        }
        return false;
    }
    public final static boolean IsIn(java.lang.Class mclass, java.lang.String mname){
        return IsIn(mclass.getName()+'.'+mname);
    }
    public final static boolean IsIn(java.lang.String framename){
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);
        buf.pop();//pop headline
        buf.pop();//pop this frame

        char[] line;
        while (null != (line = buf.pop())){
            java.lang.String linename = FrameNameFor(line);
            if (framename.equals(linename))
                return true;
        }
        return false;
    }
    public final static boolean IsIn(java.lang.String[] framenames){
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);
        buf.pop();//pop headline
        buf.pop();//pop this frame

        int count = framenames.length;
        char[] line;
        while (null != (line = buf.pop())){
            java.lang.String linename = FrameNameFor(line);
            for (int cc = 0; cc < count; cc++){
                java.lang.String framename = framenames[cc];
                if (framename.equals(linename))
                    return true;
            }
        }
        return false;
    }
    /**
     * @param skipFrames 
     */
    public static java.lang.String GetCallerClassName(int skipFrames){
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);
        buf.pop();//pop headline
        buf.pop();//pop this frame

        for (int cc = 0; cc < skipFrames; cc++)
            buf.pop();

        java.lang.String line = buf.linebuf().toStringArray()[0], classname;
        int idx = line.indexOf('(');
        if (0 < idx)
            return ClassNameFor(line.substring(0,idx));
        else
            return ClassNameFor(line);
    }
    public static java.lang.Class ClassFor(java.lang.String line){
        java.lang.String classname = ClassNameFor(line);
        if (null != classname){
            try {
                return java.lang.Class.forName(classname);
            }
            catch (java.lang.ClassNotFoundException exc){
            }
        }
        return null;
    }
    public static java.lang.String ClassNameFor(char[] line){
        if (null != line)
            return ClassNameFor(new java.lang.String(line));
        else
            return null;
    }
    public static java.lang.String ClassNameFor(java.lang.String line){
        line = FrameNameFor(line);
        if (null != line){
            int idx = line.lastIndexOf('.');
            if (0 < idx)
                return line.substring(0,idx);
        }
        return null;
    }
    public static java.lang.String FrameNameFor(char[] line){
        if (null != line)
            return FrameNameFor(new java.lang.String(line));
        else
            return null;
    }
    public static java.lang.String FrameNameFor(java.lang.String line){
        int idx = line.lastIndexOf('(');
        if (0 < idx){
            line = line.substring(0,idx);
        }
        idx = line.indexOf("at ");
        if (0 < idx)
            line = line.substring(idx+"at ".length()).trim();
        else
            line = line.trim();
        return line;
    }
    public static Class GetCallerClass(int skipFrames){
        java.lang.String classname = GetCallerClassName(skipFrames);
        if (null != classname){
            try {
                return Class.forName(classname);
            }
            catch (ClassNotFoundException exc){
                return null;
            }
        }
        else
            return null;
    }
    public static ClassLoader GetCallerClassLoader(int skipFrames){
        Class jclass = GetCallerClass(skipFrames);
        if (null != jclass)
            return jclass.getClassLoader();
        else
            return null;
    }
    public static ClassLoader GetCallerContext(){
        return GetCallerContext(1);
    }
    public static ClassLoader GetCallerContext(Object defaultcx){
        if (null == defaultcx)
            return GetCallerContext(1);
        else 
            return defaultcx.getClass().getClassLoader();
    }
    public static ClassLoader GetCallerContext(int skipFrames){
        ClassLoader cl = GetCallerClassLoader(skipFrames);
        if (null != cl)
            return cl;
        else {
            java.lang.Thread T = java.lang.Thread.currentThread();
            cl = T.getContextClassLoader();
            if (null != cl)
                return cl;
            else
                return null;
        }
    }

    public static void main(java.lang.String[] argv){
        
        Bpo buf = new Bpo();
        new Exception().printStackTrace(buf);

        buf.pop();//pop headline

        char[] line;
        while (null != (line = buf.pop())){
            System.out.println("class="+ClassNameFor(line)+" frame="+FrameNameFor(line));
        }

    }
}
