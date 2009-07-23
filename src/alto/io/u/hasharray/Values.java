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
package alto.io.u.hasharray;

import alto.io.u.Array;

/**
 * 
 */
public abstract class Values 
    extends java.lang.Object
    implements java.lang.Cloneable
{

    public final static java.lang.Object NilObject = null;
    public final static long NilLong = 0L;
    public final static int NilInt = 0;
    public final static double NilDouble = 0d;
    public final static float NilFloat = 0f;
    public final static byte NilByte = (byte)0;
    public final static short NilShort = (short)0;
    public final static boolean NilBoolean = java.lang.Boolean.FALSE;
    public final static char NilChar = (char)0;

    public final static java.lang.Object[] NilArrayObject = null;
    public final static long[] NilArrayLong = null;
    public final static int[] NilArrayInt = null;
    public final static double[] NilArrayDouble = null;
    public final static float[] NilArrayFloat = null;
    public final static byte[] NilArrayByte = null;
    public final static short[] NilArrayShort = null;
    public final static boolean[] NilArrayBoolean = null;
    public final static char[] NilArrayChar = null;

    public final static class Types {

        public final static class Abstracts {

            public final static java.lang.Class Object = java.lang.Object.class;

            public final static java.lang.Class Byte = java.lang.Number.class;

            public final static java.lang.Class Short = java.lang.Number.class;

            public final static java.lang.Class Int = java.lang.Number.class;

            public final static java.lang.Class Long = java.lang.Number.class;

            public final static java.lang.Class Float = java.lang.Number.class;

            public final static java.lang.Class Double = java.lang.Number.class;

            public final static java.lang.Class Char = java.lang.Character.TYPE;

            public final static java.lang.Class Boolean = java.lang.Boolean.TYPE;
        }

        public final static class Objects {

            public final static java.lang.Class Object = java.lang.Object.class;

            public final static java.lang.Class Byte = java.lang.Byte.class;

            public final static java.lang.Class Short = java.lang.Short.class;

            public final static java.lang.Class Int = java.lang.Integer.class;

            public final static java.lang.Class Long = java.lang.Long.class;

            public final static java.lang.Class Float = java.lang.Float.class;

            public final static java.lang.Class Double = java.lang.Double.class;

            public final static java.lang.Class Char = java.lang.Character.class;

            public final static java.lang.Class Boolean = java.lang.Boolean.class;
        }

        public final static java.lang.Class Object = java.lang.Object.class;

        public final static java.lang.Class Byte = java.lang.Byte.TYPE;

        public final static java.lang.Class Short = java.lang.Short.TYPE;

        public final static java.lang.Class Int = java.lang.Integer.TYPE;

        public final static java.lang.Class Long = java.lang.Long.TYPE;

        public final static java.lang.Class Float = java.lang.Float.TYPE;

        public final static java.lang.Class Double = java.lang.Double.TYPE;

        public final static java.lang.Class Char = java.lang.Character.TYPE;

        public final static java.lang.Class Boolean = java.lang.Boolean.TYPE;


        public final static Values New(java.lang.Class clas){

            if (clas.isPrimitive()){
                if (Byte == clas)
                    return new Values.Byte();
                else if (Short == clas)
                    return new Values.Short();
                else if (Int == clas)
                    return new Values.Int();
                else if (Long == clas)
                    return new Values.Long();
                else if (Float == clas)
                    return new Values.Float();
                else if (Double == clas)
                    return new Values.Double();
                else if (Char == clas)
                    return new Values.Char();
                else if (Boolean == clas)
                    return new Values.Boolean();
                else
                    throw new java.lang.IllegalStateException(clas.getName());
            }
            else
                return new Values.Object();
        }
    }

    //(begin gen)

    /**
     * 
     */
    public final static class Object
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Object;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Object;

        public final static java.lang.Object ToObject( java.lang.Object instance){
            return instance;
        }


        private final int grow;

        private java.lang.Object[] list;

        private int count;


        public Object(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new java.lang.Object[this.grow];
        }
        public Object(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new java.lang.Object[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Object cloneValues(){
            try {
                Object clone = (Object)super.clone();
                java.lang.Object[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public java.lang.Object[] get(){
            return this.list;
        }
        public java.lang.Object[] copy(){
            java.lang.Object[] list = this.list;
            if (null != list){
                int count = this.count;
                java.lang.Object[] copy = new java.lang.Object[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(java.lang.Object[] list){
            this.list = list;
        }
        public java.lang.Object get(int idx){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilObject;
        }
        public java.lang.Object set(int idx, java.lang.Object value){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        java.lang.Object old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilObject;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilObject;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public java.lang.Object[] list(int[] lidx){
            java.lang.Object[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                java.lang.Object[] copy = new java.lang.Object[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayObject;
        }
        public java.lang.Object list(int[] lidx, Class component){
            java.lang.Object[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    java.lang.Object[] copy = (java.lang.Object[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Object[] copy = (java.lang.Object[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayObject;
        }
        public java.lang.Object last(){
            java.lang.Object[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilObject;
        }
        public java.lang.Object last(java.lang.Object value){
            java.lang.Object[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilObject;
        }
        public java.lang.Object append( java.lang.Object value){
            java.lang.Object[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public java.lang.Object insert(int idx, java.lang.Object value){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public java.lang.Object remove(int idx){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        java.lang.Object old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(java.lang.Object value){
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(java.lang.Object value){
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(java.lang.Object value, int from){
            java.lang.Object[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(java.lang.Object value){
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(java.lang.Object value, int from){
            java.lang.Object[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            java.lang.Object[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Object(list, count);
        }
        @Override
        public java.lang.Object getAsObject(int idx){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return null;
        }
        @Override
        public java.lang.Object setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        java.lang.Object old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = value;
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Object lastAsObject(){
            java.lang.Object[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Object lastAsObject(java.lang.Object value){
            java.lang.Object[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Object appendAsObject(java.lang.Object objectValue){
            java.lang.Object value = (java.lang.Object)objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Object insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Object value = (java.lang.Object)objectValue;
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value;
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Object removeAsObject(int idx){
            if (-1 < idx){
                java.lang.Object[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        java.lang.Object old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            java.lang.Object value = objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            java.lang.Object value = objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            java.lang.Object value = objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            java.lang.Object value = objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            java.lang.Object value = objectValue;
            java.lang.Object[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                java.lang.Object test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Long
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Long;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Long;

        public final static java.lang.Number ToObject( long instance){
            return new java.lang.Long(instance);
        }


        private final int grow;

        private long[] list;

        private int count;


        public Long(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new long[this.grow];
        }
        public Long(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new long[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Long cloneValues(){
            try {
                Long clone = (Long)super.clone();
                long[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public long[] get(){
            return this.list;
        }
        public long[] copy(){
            long[] list = this.list;
            if (null != list){
                int count = this.count;
                long[] copy = new long[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(long[] list){
            this.list = list;
        }
        public long get(int idx){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilLong;
        }
        public long set(int idx, long value){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        long old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilLong;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilLong;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public long[] list(int[] lidx){
            long[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                long[] copy = new long[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayLong;
        }
        public java.lang.Object list(int[] lidx, Class component){
            long[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    long[] copy = (long[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayLong;
        }
        public long last(){
            long[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilLong;
        }
        public long last(long value){
            long[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilLong;
        }
        public long append( long value){
            long[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public long insert(int idx, long value){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public long remove(int idx){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        long old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(long value){
            long[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(long value){
            long[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(long value, int from){
            long[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(long value){
            long[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(long value, int from){
            long[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            long[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Long(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Long(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        long old = list[idx];
                        list[idx] = ((java.lang.Number)value).longValue();
                        return new java.lang.Long(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).longValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            long[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            long[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            long[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.longValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.longValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.longValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                long[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        long old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Long(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            long value = ((java.lang.Number)objectValue).longValue();
            long[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            long value = ((java.lang.Number)objectValue).longValue();
            long[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            long value = ((java.lang.Number)objectValue).longValue();
            long[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            long value = ((java.lang.Number)objectValue).longValue();
            long[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            long value = ((java.lang.Number)objectValue).longValue();
            long[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                long test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Int
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Int;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Int;

        public final static java.lang.Number ToObject( int instance){
            return new java.lang.Integer(instance);
        }


        private final int grow;

        private int[] list;

        private int count;


        public Int(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new int[this.grow];
        }
        public Int(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new int[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Int cloneValues(){
            try {
                Int clone = (Int)super.clone();
                int[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public int[] get(){
            return this.list;
        }
        public int[] copy(){
            int[] list = this.list;
            if (null != list){
                int count = this.count;
                int[] copy = new int[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(int[] list){
            this.list = list;
        }
        public int get(int idx){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilInt;
        }
        public int set(int idx, int value){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        int old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilInt;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilInt;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public int[] list(int[] lidx){
            int[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                int[] copy = new int[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayInt;
        }
        public java.lang.Object list(int[] lidx, Class component){
            int[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    int[] copy = (int[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayInt;
        }
        public int last(){
            int[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilInt;
        }
        public int last(int value){
            int[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilInt;
        }
        public int append( int value){
            int[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public int insert(int idx, int value){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public int remove(int idx){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        int old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(int value){
            int[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(int value){
            int[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(int value, int from){
            int[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(int value){
            int[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(int value, int from){
            int[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            int[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Int(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Integer(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        int old = list[idx];
                        list[idx] = ((java.lang.Number)value).intValue();
                        return new java.lang.Integer(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).intValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            int[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            int[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            int[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.intValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.intValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.intValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                int[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        int old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Integer(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            int value = ((java.lang.Number)objectValue).intValue();
            int[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            int value = ((java.lang.Number)objectValue).intValue();
            int[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            int value = ((java.lang.Number)objectValue).intValue();
            int[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            int value = ((java.lang.Number)objectValue).intValue();
            int[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            int value = ((java.lang.Number)objectValue).intValue();
            int[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                int test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Double
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Double;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Double;

        public final static java.lang.Number ToObject( double instance){
            return new java.lang.Double(instance);
        }


        private final int grow;

        private double[] list;

        private int count;


        public Double(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new double[this.grow];
        }
        public Double(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new double[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Double cloneValues(){
            try {
                Double clone = (Double)super.clone();
                double[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public double[] get(){
            return this.list;
        }
        public double[] copy(){
            double[] list = this.list;
            if (null != list){
                int count = this.count;
                double[] copy = new double[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(double[] list){
            this.list = list;
        }
        public double get(int idx){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilDouble;
        }
        public double set(int idx, double value){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        double old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilDouble;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilDouble;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public double[] list(int[] lidx){
            double[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                double[] copy = new double[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayDouble;
        }
        public java.lang.Object list(int[] lidx, Class component){
            double[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    double[] copy = (double[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayDouble;
        }
        public double last(){
            double[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilDouble;
        }
        public double last(double value){
            double[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilDouble;
        }
        public double append( double value){
            double[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public double insert(int idx, double value){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public double remove(int idx){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        double old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(double value){
            double[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(double value){
            double[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(double value, int from){
            double[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(double value){
            double[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(double value, int from){
            double[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            double[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Double(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Double(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        double old = list[idx];
                        list[idx] = ((java.lang.Number)value).doubleValue();
                        return new java.lang.Double(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).doubleValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            double[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            double[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            double[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.doubleValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.doubleValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.doubleValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                double[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        double old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Double(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            double value = ((java.lang.Number)objectValue).doubleValue();
            double[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            double value = ((java.lang.Number)objectValue).doubleValue();
            double[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            double value = ((java.lang.Number)objectValue).doubleValue();
            double[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            double value = ((java.lang.Number)objectValue).doubleValue();
            double[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            double value = ((java.lang.Number)objectValue).doubleValue();
            double[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                double test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Float
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Float;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Float;

        public final static java.lang.Number ToObject( float instance){
            return new java.lang.Float(instance);
        }


        private final int grow;

        private float[] list;

        private int count;


        public Float(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new float[this.grow];
        }
        public Float(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new float[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Float cloneValues(){
            try {
                Float clone = (Float)super.clone();
                float[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public float[] get(){
            return this.list;
        }
        public float[] copy(){
            float[] list = this.list;
            if (null != list){
                int count = this.count;
                float[] copy = new float[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(float[] list){
            this.list = list;
        }
        public float get(int idx){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilFloat;
        }
        public float set(int idx, float value){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        float old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilFloat;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilFloat;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public float[] list(int[] lidx){
            float[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                float[] copy = new float[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayFloat;
        }
        public java.lang.Object list(int[] lidx, Class component){
            float[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    float[] copy = (float[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayFloat;
        }
        public float last(){
            float[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilFloat;
        }
        public float last(float value){
            float[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilFloat;
        }
        public float append( float value){
            float[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public float insert(int idx, float value){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public float remove(int idx){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        float old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(float value){
            float[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(float value){
            float[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(float value, int from){
            float[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(float value){
            float[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(float value, int from){
            float[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            float[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Float(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Float(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        float old = list[idx];
                        list[idx] = ((java.lang.Number)value).floatValue();
                        return new java.lang.Float(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).floatValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            float[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            float[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            float[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.floatValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.floatValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.floatValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                float[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        float old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Float(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            float value = ((java.lang.Number)objectValue).floatValue();
            float[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            float value = ((java.lang.Number)objectValue).floatValue();
            float[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            float value = ((java.lang.Number)objectValue).floatValue();
            float[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            float value = ((java.lang.Number)objectValue).floatValue();
            float[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            float value = ((java.lang.Number)objectValue).floatValue();
            float[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                float test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Byte
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Byte;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Byte;

        public final static java.lang.Number ToObject( byte instance){
            return new java.lang.Byte(instance);
        }


        private final int grow;

        private byte[] list;

        private int count;


        public Byte(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new byte[this.grow];
        }
        public Byte(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new byte[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Byte cloneValues(){
            try {
                Byte clone = (Byte)super.clone();
                byte[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public byte[] get(){
            return this.list;
        }
        public byte[] copy(){
            byte[] list = this.list;
            if (null != list){
                int count = this.count;
                byte[] copy = new byte[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(byte[] list){
            this.list = list;
        }
        public byte get(int idx){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilByte;
        }
        public byte set(int idx, byte value){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        byte old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilByte;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilByte;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public byte[] list(int[] lidx){
            byte[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                byte[] copy = new byte[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayByte;
        }
        public java.lang.Object list(int[] lidx, Class component){
            byte[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    byte[] copy = (byte[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayByte;
        }
        public byte last(){
            byte[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilByte;
        }
        public byte last(byte value){
            byte[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilByte;
        }
        public byte append( byte value){
            byte[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public byte insert(int idx, byte value){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public byte remove(int idx){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        byte old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(byte value){
            byte[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(byte value){
            byte[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(byte value, int from){
            byte[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(byte value){
            byte[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(byte value, int from){
            byte[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            byte[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Byte(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Byte(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        byte old = list[idx];
                        list[idx] = ((java.lang.Number)value).byteValue();
                        return new java.lang.Byte(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).byteValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            byte[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            byte[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            byte[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.byteValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.byteValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.byteValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                byte[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        byte old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Byte(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            byte value = ((java.lang.Number)objectValue).byteValue();
            byte[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            byte value = ((java.lang.Number)objectValue).byteValue();
            byte[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            byte value = ((java.lang.Number)objectValue).byteValue();
            byte[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            byte value = ((java.lang.Number)objectValue).byteValue();
            byte[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            byte value = ((java.lang.Number)objectValue).byteValue();
            byte[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                byte test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Short
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Short;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Short;

        public final static java.lang.Number ToObject( short instance){
            return new java.lang.Short(instance);
        }


        private final int grow;

        private short[] list;

        private int count;


        public Short(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new short[this.grow];
        }
        public Short(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new short[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Short cloneValues(){
            try {
                Short clone = (Short)super.clone();
                short[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public short[] get(){
            return this.list;
        }
        public short[] copy(){
            short[] list = this.list;
            if (null != list){
                int count = this.count;
                short[] copy = new short[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(short[] list){
            this.list = list;
        }
        public short get(int idx){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilShort;
        }
        public short set(int idx, short value){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        short old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilShort;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilShort;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public short[] list(int[] lidx){
            short[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                short[] copy = new short[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayShort;
        }
        public java.lang.Object list(int[] lidx, Class component){
            short[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    short[] copy = (short[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Number[] copy = (java.lang.Number[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayShort;
        }
        public short last(){
            short[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilShort;
        }
        public short last(short value){
            short[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilShort;
        }
        public short append( short value){
            short[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public short insert(int idx, short value){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public short remove(int idx){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        short old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(short value){
            short[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(short value){
            short[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(short value, int from){
            short[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(short value){
            short[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(short value, int from){
            short[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            short[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Short(list, count);
        }
        @Override
        public java.lang.Number getAsObject(int idx){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Short(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Number setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        short old = list[idx];
                        list[idx] = ((java.lang.Number)value).shortValue();
                        return new java.lang.Short(old);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Number)value).shortValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number lastAsObject(){
            short[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number lastAsObject(java.lang.Object value){
            short[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Number appendAsObject(java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            short[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.shortValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Number insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Number value = (java.lang.Number)objectValue;
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.shortValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.shortValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Number removeAsObject(int idx){
            if (-1 < idx){
                short[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        short old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Short(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            short value = ((java.lang.Number)objectValue).shortValue();
            short[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            short value = ((java.lang.Number)objectValue).shortValue();
            short[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            short value = ((java.lang.Number)objectValue).shortValue();
            short[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            short value = ((java.lang.Number)objectValue).shortValue();
            short[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            short value = ((java.lang.Number)objectValue).shortValue();
            short[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                short test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Boolean
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Boolean;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Boolean;

        public final static java.lang.Boolean ToObject( boolean instance){
            return (instance)?(java.lang.Boolean.TRUE):(java.lang.Boolean.FALSE);
        }


        private final int grow;

        private boolean[] list;

        private int count;


        public Boolean(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new boolean[this.grow];
        }
        public Boolean(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new boolean[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Boolean cloneValues(){
            try {
                Boolean clone = (Boolean)super.clone();
                boolean[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public boolean[] get(){
            return this.list;
        }
        public boolean[] copy(){
            boolean[] list = this.list;
            if (null != list){
                int count = this.count;
                boolean[] copy = new boolean[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(boolean[] list){
            this.list = list;
        }
        public boolean get(int idx){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilBoolean;
        }
        public boolean set(int idx, boolean value){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        boolean old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilBoolean;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilBoolean;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean[] list(int[] lidx){
            boolean[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                boolean[] copy = new boolean[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayBoolean;
        }
        public java.lang.Object list(int[] lidx, Class component){
            boolean[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    boolean[] copy = (boolean[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Boolean[] copy = (java.lang.Boolean[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayBoolean;
        }
        public boolean last(){
            boolean[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilBoolean;
        }
        public boolean last(boolean value){
            boolean[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilBoolean;
        }
        public boolean append( boolean value){
            boolean[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public boolean insert(int idx, boolean value){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean remove(int idx){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        boolean old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(boolean value){
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(boolean value){
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(boolean value, int from){
            boolean[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(boolean value){
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(boolean value, int from){
            boolean[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            boolean[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Boolean(list, count);
        }
        @Override
        public java.lang.Boolean getAsObject(int idx){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return (list[idx])?(java.lang.Boolean.TRUE):(java.lang.Boolean.FALSE);
                }
            }
            return null;
        }
        @Override
        public java.lang.Boolean setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        boolean old = list[idx];
                        list[idx] = ((java.lang.Boolean)value).booleanValue();
                        return (old)?(java.lang.Boolean.TRUE):(java.lang.Boolean.FALSE);
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Boolean)value).booleanValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Boolean lastAsObject(){
            boolean[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Boolean lastAsObject(java.lang.Object value){
            boolean[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Boolean appendAsObject(java.lang.Object objectValue){
            java.lang.Boolean value = (java.lang.Boolean)objectValue;
            boolean[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value.booleanValue();
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Boolean insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Boolean value = (java.lang.Boolean)objectValue;
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value.booleanValue();
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        list[idx] = value.booleanValue();
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Boolean removeAsObject(int idx){
            if (-1 < idx){
                boolean[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        boolean old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return (old)?(java.lang.Boolean.TRUE):(java.lang.Boolean.FALSE);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            boolean value = ((java.lang.Boolean)objectValue).booleanValue();
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            boolean value = ((java.lang.Boolean)objectValue).booleanValue();
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            boolean value = ((java.lang.Boolean)objectValue).booleanValue();
            boolean[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            boolean value = ((java.lang.Boolean)objectValue).booleanValue();
            boolean[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            boolean value = ((java.lang.Boolean)objectValue).booleanValue();
            boolean[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                boolean test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }
    /**
     * 
     */
    public final static class Char
        extends Values
    {
        public final static java.lang.Class Type = Values.Types.Char;

        public final static java.lang.Class TypeObject = Values.Types.Objects.Char;

        public final static java.lang.Character ToObject( char instance){
            return new java.lang.Character(instance);
        }


        private final int grow;

        private char[] list;

        private int count;


        public Char(int initial){
            super();
            if (0 < initial)
                this.grow = initial;
            else
                this.grow = 11;
            this.list = new char[this.grow];
        }
        public Char(){
            this(0);
        }

        public int size(){
            return this.count;
        }
        public void clear(){
            this.count = 0;
            this.list = new char[this.grow];
        }
        public void destroy(){
            this.count = 0;
            this.list = null;
        }
        @Override
        public Char cloneValues(){
            try {
                Char clone = (Char)super.clone();
                char[] list = this.list;
                if (null != list)
                    clone.list = list.clone();
                return clone;
            }
            catch (CloneNotSupportedException exc){
                throw new IllegalStateException(exc);
            }
        }
        public char[] get(){
            return this.list;
        }
        public char[] copy(){
            char[] list = this.list;
            if (null != list){
                int count = this.count;
                char[] copy = new char[count];
                java.lang.System.arraycopy(list,0,copy,0,count);
                return copy;
            }
            else
                return null;
        }
        public void set(char[] list){
            this.list = list;
        }
        public char get(int idx){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return list[idx];
                }
            }
            return NilChar;
        }
        public char set(int idx, char value){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        char old = list[idx];
                        list[idx] = value;
                        return old;
                    }
                    else if (idx == count){
                        if (count >= list.length){
                            list = Array.grow(list,(count+this.grow));
                            list[idx] = value;
                            this.list = list;
                        }
                        else
                            list[idx] = value;

                        this.count += 1;
                        return NilChar;
                    }
                }
                else if (0 == idx){
                    this.count = 1;
                    list = Array.grow(list,(idx+this.grow));
                    list[idx] = value;
                    this.list = list;
                    return NilChar;
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public char[] list(int[] lidx){
            char[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                char[] copy = new char[licount];
                for (int cc = 0; cc < licount; cc++){
                    int idx = lidx[cc];
                    if (idx < count)
                        copy[cc] = list[idx];
                    else
                        throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                }
                return copy;
            }
            else
                return Values.NilArrayChar;
        }
        public java.lang.Object list(int[] lidx, Class component){
            char[] list = this.list;
            if (null != list && null != lidx){
                int count = this.count;
                int licount = lidx.length;
                if (Type.isAssignableFrom(component)){
                    /* 
                     * (identity relation for primitives)
                     */
                    char[] copy = (char[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = list[idx];
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else if (TypeObject.isAssignableFrom(component)){
                    java.lang.Character[] copy = (java.lang.Character[])java.lang.reflect.Array.newInstance(component,licount);
                    for (int cc = 0; cc < licount; cc++){
                        int idx = lidx[cc];
                        if (idx < count)
                            copy[cc] = ToObject(list[idx]);
                        else
                            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx)+" in "+count);
                    }
                    return copy;
                }
                else
                    throw new java.lang.IllegalArgumentException(component.getClass().getName());
            }
            else
                return Values.NilArrayChar;
        }
        public char last(){
            char[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return list[idx];
            }
            return NilChar;
        }
        public char last(char value){
            char[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                if (-1 < idx)
                    return (list[idx] = value);
            }
            return NilChar;
        }
        public char append( char value){
            char[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            list[count] = value;
            this.count += 1;
            return value;
        }
        public char insert(int idx, char value){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        list[idx] = value;
                        this.count += 1;
                        this.list = list;
                        return value;
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            list[idx] = value;
                            this.count += 1;
                            this.list = list;
                        }
                        else {
                            this.count += 1;
                            list[idx] = value;
                        }
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public char remove(int idx){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        char old = list[idx];
                        Array.shift(list,idx);
                        return old;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        public boolean contains(char value){
            char[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        public int indexOf(char value){
            char[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int indexOf(char value, int from){
            char[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(char value){
            char[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public int lastIndexOf(char value, int from){
            char[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        public java.util.Enumeration enumerate(){
            char[] list = this.list;
            int count = this.count;
            return new Array.Enumerator.Char(list, count);
        }
        @Override
        public java.lang.Character getAsObject(int idx){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    if (idx < this.count)
                        return new java.lang.Character(list[idx]);
                }
            }
            return null;
        }
        @Override
        public java.lang.Character setAsObject(int idx, java.lang.Object value){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        char old = list[idx];
                    }
                    else if (idx == count){
                        int length = list.length;
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        list[idx] = ((java.lang.Character)value).charValue();
                        return null;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Character lastAsObject(){
            char[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.getAsObject(idx);
            }
            else
                return null;
        }
        @Override
        public java.lang.Character lastAsObject(java.lang.Object value){
            char[] list = this.list;
            if (null != list){
                int idx = (this.count-1);
                return this.setAsObject(idx,value);
            }
            else
                return null;
        }
        @Override
        public java.lang.Character appendAsObject(java.lang.Object objectValue){
            java.lang.Character value = (java.lang.Character)objectValue;
            char[] list = this.list;
            int count = this.count;
            int length = (null != list)?(list.length):(0);
            if (count >= length){
                list = Array.grow(list,(length+this.grow));
                this.list = list;
            }
            this.count += 1;
            return value;
        }
        @Override
        public java.lang.Character insertAsObject(int idx, java.lang.Object objectValue){
            java.lang.Character value = (java.lang.Character)objectValue;
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    int length = list.length;
                    if (idx < count){
                        list = Array.insert(list,idx);//(copy)
                        this.list = list;
                        this.count += 1;
                        return value;
                    }
                    else if (idx == count){
                        if (count >= length){
                            list = Array.grow(list,(length+this.grow));
                            this.list = list;
                        }
                        this.count += 1;
                        return value;
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public java.lang.Character removeAsObject(int idx){
            if (-1 < idx){
                char[] list = this.list;
                if (null != list){
                    int count = this.count;
                    if (idx < count){
                        char old = list[idx];
                        Array.shift(list,idx);
                        this.count -= 1;
                        return new java.lang.Character(old);
                    }
                }
            }
            throw new java.lang.ArrayIndexOutOfBoundsException(String.valueOf(idx));
        }
        @Override
        public boolean containsAsObject(java.lang.Object objectValue){
            char value = ((java.lang.Character)objectValue).charValue();
            char[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return true;
            }
            return false;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue){
            char value = ((java.lang.Character)objectValue).charValue();
            char[] list = this.list;
            int count = this.count;
            for (int cc = 0; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int indexOfAsObject(java.lang.Object objectValue, int from){
            char value = ((java.lang.Character)objectValue).charValue();
            char[] list = this.list;
            int count = this.count;
            if (0 > from) from = 0;
            for (int cc = from; cc < count; cc++){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue){
            char value = ((java.lang.Character)objectValue).charValue();
            char[] list = this.list;
            int count = this.count;
            for (int cc = (count-1); -1 < cc; cc--){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
        @Override
        public int lastIndexOfAsObject(java.lang.Object objectValue, int from){
            char value = ((java.lang.Character)objectValue).charValue();
            char[] list = this.list;
            int count = this.count;
            if (from >= count) from = (count-1);
            for (int cc = from; -1 < cc; cc--){
                char test = list[cc];
                if (value == test)
                    return cc;
            }
            return -1;
        }
    }


    //(end gen)


    protected Values(){
        super();
    }


    public abstract int size();

    public abstract void destroy();

    public abstract void clear();

    public abstract java.util.Enumeration enumerate();

    public abstract Values cloneValues();

    public abstract java.lang.Object getAsObject(int idx);

    public abstract java.lang.Object setAsObject(int idx, java.lang.Object value);

    public abstract java.lang.Object lastAsObject();

    public abstract java.lang.Object lastAsObject(java.lang.Object value);

    public abstract java.lang.Object appendAsObject(java.lang.Object value);

    public abstract java.lang.Object insertAsObject(int idx, java.lang.Object value);

    public abstract java.lang.Object removeAsObject(int idx);

    public abstract boolean containsAsObject(java.lang.Object objectValue);

    public abstract int indexOfAsObject(java.lang.Object objectValue);

    public abstract int indexOfAsObject(java.lang.Object objectValue, int from);

    public abstract int lastIndexOfAsObject(java.lang.Object objectValue);

    public abstract int lastIndexOfAsObject(java.lang.Object objectValue, int from);

}
