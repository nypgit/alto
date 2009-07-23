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

import alto.io.Code;
import alto.io.Check;
import alto.io.Principal;

/**
 * All threads extend from this one, for a default group.
 * 
 * @author jdp
 * @since 1.1
 */
public class Thread
    extends java.lang.Thread
    implements Millis
{
    public final static boolean In(){
        return (java.lang.Thread.currentThread() instanceof alto.sys.Thread);
    }
    public final static Thread Get(){
        return (Thread)currentThread();
    }
    public final static boolean IsMain(){
        return ((Thread)currentThread()).isMain();
    }
    /**
     * @return True when in an *HTTP network request context.
     */
    public final static boolean IsInContext(){
        return ((Thread)currentThread()).hasContext();
    }
    public final static boolean IsNotInContext(){
        return ((Thread)currentThread()).hasNotContext();
    }
    public final static IO.Context Context(){
        return ((Thread)currentThread()).getContext();
    }
    public final static boolean MaySetContext(IO.Context cx){
        if (null != cx){
            Thread T = (Thread)currentThread();
            if (T.hasNotContext()){
                return (cx == T.pushContext(cx));
            }
        }
        return false;
    }
    public final static void ResetContext(IO.Context cx){
        ((Thread)currentThread()).resetContext(cx);
    }
    public final static Principal GetPrincipal(){
        return ((Thread)currentThread()).getPrincipal();
    }
    @Code(Check.Locking)
    public final static Principal GetPrincipal(boolean authenticate)
        throws java.io.IOException
    {
        return ((Thread)currentThread()).getPrincipal(authenticate);
    }
    @Code(Check.SecTrusted)
    public final static Principal PushPrincipal(){
        return ((Thread)currentThread()).pushPrincipal();
    }
    @Code(Check.SecSafe)
    public final static Principal PopPrincipal(){
        return ((Thread)currentThread()).popPrincipal();
    }



    private volatile IO.Context context;


    protected Thread(java.lang.ThreadGroup group, java.lang.String name){
        super(group,name);
        this.inherit();
    }


    /**
     * This is needed by threads like FileTransaction to inherit their
     * correct security context.
     */
    private void inherit(){
        java.lang.Thread T = currentThread();
        if (T instanceof Thread){
            this.context = ((Thread)T).getContext();
        }
    }
    /**
     * Only {@link Init$Main} returns true from here.
     */
    public boolean isMain(){
        return false;
    }
    public final boolean hasContext(){
        return (null != this.context);
    }
    public final boolean hasNotContext(){
        return (null == this.context);
    }
    public final IO.Context getContext(){
        return this.context;
    }
    @Code(Check.OnlyServer)
    public final IO.Context pushContext(IO.Context context){
        if (null != this.context){
            return (this.context = this.context.pushContext(context));
        }
        else
            return (this.context = context);
    }
    @Code(Check.OnlyServer)
    public final IO.Context popContext(){
        if (null != this.context){
            return (this.context = this.context.popContext());
        }
        else
            throw new Error.Bug();
    }
    @Code(Check.OnlyClient)
    public final void resetContext(IO.Context context){
        this.context = context;
    }
    public final Principal getPrincipal(){
        IO.Context context = this.context;
        if (null != context){
            if (context.isAuthenticated())
                return context.getPrincipal();
            else if (context.isAuthenticating()){
                /*
                 * Authenticate this path from a place known external
                 * to all locks.
                 */
                throw new Error.Bug("Should have been authenticated");
            }
        }
        return null;
    }
    /**
     * (Care with locks)
     */
    @Code(Check.Locking)
    public final Principal getPrincipal(boolean authenticate)
        throws java.io.IOException
    {
        IO.Context context = this.context;
        if (null != context){
            if (context.isAuthenticated())
                return context.getPrincipal();
            else if (context.isAuthenticating()){
                context.authenticate();
                return context.getPrincipal();
            }
        }
        return null;
    }
    @Code(Check.SecTrusted)
    public final Principal pushPrincipal(){
        IO.Context context = this.context;
        if (null != context)
            return context.pushPrincipal();
        else
            return null;
    }
    @Code(Check.SecSafe)
    public final Principal popPrincipal(){
        IO.Context context = this.context;
        if (null != context)
            return context.popPrincipal();
        else
            return null;
    }

    public final java.lang.String toString(){
        return this.getThreadGroup().getName()+'/'+this.getName();
    }
}
