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


/**
 * 
 * 
 * @since 1.5
 */
public final class Constructor 
    extends java.lang.Object
{
    protected final static java.lang.Class ObjectClass = java.lang.Object.class;

    protected final static java.lang.Class ClassFor(java.lang.String pkg, java.lang.String name){
        java.lang.String classname = pkg+'.'+name;
        try {
            return java.lang.Class.forName(classname);
        }
        catch (java.lang.ClassNotFoundException exc){
            throw new alto.sys.Error.Argument(classname,exc);
        }
    }
    protected final static java.lang.Class ClassFor(java.lang.String classname){
        try {
            return java.lang.Class.forName(classname);
        }
        catch (java.lang.ClassNotFoundException exc){
            throw new alto.sys.Error.Argument(classname,exc);
        }
    }


    private final java.lang.Class declaring;
    private final java.lang.reflect.Constructor ctor;
    private final java.lang.Class[] ctorParameters;

    /**
     * No args
     */
    public Constructor(java.lang.String pkg, java.lang.String name){
        this( ClassFor(pkg,name));
    }
    public Constructor(java.lang.String classname){
        this( ClassFor(classname));
    }
    public Constructor(java.lang.Class declaring){
        super();
        this.declaring = declaring;
        this.ctor = null;
        this.ctorParameters = null;
    }
    /**
     * One arg
     */
    public Constructor(java.lang.String pkg, java.lang.String name, Class parameter){
        this( ClassFor(pkg,name), parameter);
    }
    public Constructor(java.lang.String classname, Class parameter){
        this( ClassFor(classname), parameter);
    }
    public Constructor(java.lang.Class declaring, java.lang.Class parameter){
        super();
        this.declaring = declaring;

        java.lang.reflect.Constructor ctor = null;
        java.lang.Class[] ctorParameters = null;
        if (null != parameter){
            java.lang.reflect.Constructor[] ctors = declaring.getConstructors();
            for (int cc = 0, count = ctors.length; cc < count; cc++){
                java.lang.reflect.Constructor ctorTest = ctors[cc];
                ctorParameters = ctorTest.getParameterTypes();
                if (null != ctorParameters && 1 == ctorParameters.length){
                    java.lang.Class ctorParam = ctorParameters[0];
                    /*
                     * Permit a null argument to be represented by object class
                     */
                    if (ctorParam.isAssignableFrom(parameter)){
                        ctor = ctorTest;
                        break;
                    }
                    else if (parameter == ObjectClass && ObjectClass.isAssignableFrom(ctorParam)){
                        ctor = ctorTest;
                        break;
                    }
                }
            }
            if (null == ctor)
                throw new alto.sys.Error.Argument("Class '"+declaring.getName()+"' missing constructor ("+parameter.getName()+").");
        }
        this.ctor = ctor;
        this.ctorParameters = ctorParameters;
    }
    /**
     * Many args
     */
    public Constructor(java.lang.String pkg, java.lang.String name, Class[] parameters){
        this( ClassFor(pkg,name), parameters);
    }
    public Constructor(java.lang.String classname, Class[] parameters){
        this( ClassFor(classname), parameters);
    }
    public Constructor(java.lang.Class declaring, java.lang.Class[] parameters){
        super();
        this.declaring = declaring;

        java.lang.reflect.Constructor ctor = null;
        java.lang.Class[] ctorParameters = null;
        if (null != parameters){
            int parametersCount = parameters.length;
            java.lang.reflect.Constructor[] ctors = declaring.getConstructors();
            scanconstructors:
            for (int cc = 0, count = ctors.length; cc < count; cc++){
                java.lang.reflect.Constructor ctorTest = ctors[cc];
                ctorParameters = ctorTest.getParameterTypes();
                if (null != ctorParameters && parametersCount == ctorParameters.length){
                    scanparameters:
                    for (int xc = 0; xc < parametersCount; xc++){
                        java.lang.Class ctorParam = ctorParameters[xc];
                        java.lang.Class paramClass = parameters[xc];
                        /*
                         * Permit a null argument to be represented by object class
                         */
                        if (null == paramClass){
                            if (ObjectClass.isAssignableFrom(ctorParam))
                                ctor = ctorTest;
                            else {
                                ctor = null;
                                break scanparameters;
                            }
                        }
                        else if (ctorParam.isAssignableFrom(paramClass)){
                            ctor = ctorTest;
                        }
                        else {
                            ctor = null;
                            break scanparameters;
                        }
                    }
                    if (null != ctor){
                        break scanconstructors;
                    }
                }
            }
            if (null == ctor)
                throw new alto.sys.Error.Argument("Class '"+declaring.getName()+"' missing intended constructor for ("+parametersCount+") parameters.");
        }
        this.ctor = ctor;
        this.ctorParameters = ctorParameters;
    }


    public final java.lang.Class getDeclaring(){
        return this.declaring;
    }
    public final java.lang.reflect.Constructor getCtor(){
        return this.ctor;
    }
    public final java.lang.Class[] getParameters(){
        return this.ctorParameters;
    }
    public final boolean hasParameters(){
        java.lang.Class[] ctorParameters = this.ctorParameters;
        return (null != ctorParameters);
    }
    public final int countParameters(){
        java.lang.Class[] ctorParameters = this.ctorParameters;
        if (null == ctorParameters)
            return 0;
        else
            return ctorParameters.length;
    }
    public final int checkParameters(java.lang.Object[] argv){
        if (null == argv){
            java.lang.Class[] ctorParameters = this.ctorParameters;
            if (null == ctorParameters || 0 == ctorParameters.length)
                return 0;
            else 
                return 1;
        }
        else {
            int count = argv.length;
            java.lang.Class[] argvTypes = new java.lang.Class[count];
            for (int cc = 0; cc < count; cc++){
                /*
                 * represent null by object class
                 */
                Object arg = argv[cc];
                if (null == arg)
                    argvTypes[cc] = ObjectClass;
                else
                    argvTypes[cc] = arg.getClass();
            }
            return this.checkParameters(argvTypes);
        }
    }
    /**
     * @return Zero (0) for Ok, One (1) for wrong number, Two (2) for
     * wrong types.
     */
    public final int checkParameters(java.lang.Class[] argvTypes){
        java.lang.Class[] ctorParameters = this.ctorParameters;
        if (null == ctorParameters){
            if (null == argvTypes || 0 == argvTypes.length)
                return 0;
            else 
                return 1;
        }
        else if (null == argvTypes || 0 == argvTypes.length){
            if (0 == ctorParameters.length)
                return 0;
            else
                return 1;
        }
        else {
            int ctorParametersLength = ctorParameters.length;
            int argvLength = argvTypes.length;
            if (ctorParametersLength == argvTypes.length){
                for (int cc = 0; cc < ctorParametersLength; cc++){
                    java.lang.Class ctorParameter = ctorParameters[cc];
                    java.lang.Class argvParameter = argvTypes[cc];
                    /*
                     * Permit a null argument to be represented by object class
                     */
                    if (ctorParameter.isAssignableFrom(argvParameter))
                        continue;
                    else if (argvParameter == ObjectClass && ctorParameter.isAssignableFrom(ObjectClass))
                        continue;
                    else
                        return 2;
                }
                return 0;
            }
            else
                return 1;
        }
    }
    /**
     * No args
     */
    public java.lang.Object newInstance(){
        if (null == this.ctor){
            try {
                return this.declaring.newInstance();
            }
            catch (java.lang.InstantiationException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.lang.IllegalAccessException exc){
                throw new alto.sys.Error.State(exc);
            }
        }
        else
            throw new alto.sys.Error.Bug("Constructor misused.");
    }
    /**
     * One arg
     */
    public java.lang.Object newInstance(java.lang.Object parameter){
        java.lang.reflect.Constructor ctor = this.ctor;
        if (null != ctor){
            try {
                java.lang.Object[] argv = new java.lang.Object[]{parameter};

                int err;
                switch (err = this.checkParameters(argv)){
                case 0:
                    return ctor.newInstance(argv);
                case 1:
                    throw new alto.sys.Error.Argument("Wrong number of parameters, input '"+argv.length+"' require '"+this.countParameters()+"'.");
                case 2:
                    throw new alto.sys.Error.Bug("Wrong types in parameters ("+this.countParameters()+").");
                default:
                    throw new alto.sys.Error.Bug(java.lang.String.valueOf(err));
                }
            }
            catch (java.lang.InstantiationException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.lang.IllegalAccessException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.lang.reflect.InvocationTargetException exc){
                throw new alto.sys.Error.State(exc);
            }
        }
        else
            throw new alto.sys.Error.Bug("Constructor misused.");
    }
    /**
     * Many args
     */
    public java.lang.Object newInstance(java.lang.Object[] argv){
        java.lang.reflect.Constructor ctor = this.ctor;
        if (null != ctor){
            try {
                int err;
                switch (err = this.checkParameters(argv)){
                case 0:
                    return ctor.newInstance(argv);
                case 1:
                    throw new alto.sys.Error.Argument("Wrong number of parameters, input '"+argv.length+"' require '"+this.countParameters()+"'.");
                case 2:
                    throw new alto.sys.Error.Bug("Wrong types in parameters ("+this.countParameters()+").");
                default:
                    throw new alto.sys.Error.Bug(java.lang.String.valueOf(err));
                }
            }
            catch (java.lang.InstantiationException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.lang.IllegalAccessException exc){
                throw new alto.sys.Error.State(exc);
            }
            catch (java.lang.reflect.InvocationTargetException exc){
                throw new alto.sys.Error.State(exc);
            }
        }
        else
            throw new alto.sys.Error.Bug("Constructor misused.");
    }
}
