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
package alto.sys;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * <p> The {@link #init()} method is called after instance
 * construction and before first use.  </p>
 * 
 * @see Alive
 * @author jdp
 * @since 1.1
 */
public interface Init {

    public static class Tools {

        protected final static Class[] InitParameters = new Class[0];
        protected final static Object[] InitArguments = new Object[0];

        public final static Object Init(Object instance){
            if (instance instanceof Init){
                return ((Init)instance).init();
            }
            else {
                Class instanceClass = instance.getClass();
                try {
                    Method init = instanceClass.getMethod("init",InitParameters);

                    return init.invoke(instance,InitArguments);
                }
                catch (NoSuchMethodException exc){
                    return instance;
                }
                catch (IllegalAccessException exc){
                    throw new alto.sys.Error.State(instanceClass.getName(),exc);
                }
                catch (InvocationTargetException exc){
                    throw new alto.sys.Error.State(instanceClass.getName(),exc);
                }
            }
        }
    }

    /**
     * If a runtime exception is thrown, the instance is discarded and
     * related initialization processing fails.  
     * @return This, or a substitute
     */
    public Init init();
}
