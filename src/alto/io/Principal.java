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
package alto.io;

import alto.sys.Reference;
import alto.sec.x509.X500Name;
import alto.sec.x509.X509Certificate;

import java.util.Set;

/**
 * Represents the ability to prepare a principal for authentication
 * with or without actually authenticating it.  This permits the
 * server to not perform the authentication process when not required.
 */
public interface Principal
    extends java.security.Principal,
            alto.sys.Destroy,
            java.lang.Cloneable
{
    public abstract static class Tools {

        public final static boolean IsNotName(String string){
            return (!IsName(string));
        }
        /**
         * Rejects bad names as well as reserved names.
         * @see Role#IsNotName
         */
        public final static boolean IsName(String string){
            if (null != string && Role.IsNotName(string)){
                char[] cary = string.toCharArray();
                int count = cary.length;
                if (0 < count){
                    for (int cc = 0; cc < count; cc++){
                        switch (cary[cc]){
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x04:
                        case 0x05:
                        case 0x06:
                        case 0x07:
                        case 0x08:
                        case 0x09:
                        case 0x0a:
                        case 0x0b:
                        case 0x0c:
                        case 0x0d:
                        case 0x0e:
                        case 0x0f:
                        case 0x10:
                        case 0x11:
                        case 0x12:
                        case 0x13:
                        case 0x14:
                        case 0x15:
                        case 0x16:
                        case 0x17:
                        case 0x18:
                        case 0x19:
                        case 0x1a:
                        case 0x1b:
                        case 0x1c:
                        case 0x1d:
                        case 0x1e:
                        case 0x1f:
                        case 0x20:
                        case 0x21:
                        case 0x22:
                        case 0x23:
                        case 0x24:
                        case 0x25:
                        case 0x26:
                        case 0x27:
                        case 0x28:
                        case 0x29:
                        case 0x2a:
                        case 0x2b:
                        case 0x2c:

                        case 0x2f:
                        case 0x3a:
                        case 0x3b:
                        case 0x3c:
                        case 0x3d:
                        case 0x3e:
                        case 0x3f:

                        case 0x5b:
                        case 0x5c:
                        case 0x5d:
                        case 0x5e:

                        case 0x60:

                        case 0x7b:
                        case 0x7c:
                        case 0x7d:
                        case 0x7e:
                        case 0x7f:
                            return false;
                        default:
                            break;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * Common 
     */
    public interface Authentic
        extends Principal,
                alto.io.Keys
    {}
    /**
     * Client side
     */
    public interface Client
        extends Principal,
                Authentic
    {
        public X500Name getX500Name();
    }
    /**
     * Server side
     */
    public interface Server
        extends Principal
    {
    }
    /**
     * Server side authentication placeholder.  This permits an
     * authentication process to be setup, and consumed on demand as
     * required.  Authentication is not performed unless and until
     * required.
     */
    public final static class Conditional 
        extends java.lang.Object
        implements Principal.Server
    {

        private alto.lang.HttpRequest request;

        private Authentication.Method authMethod;

        private String name;

        private boolean once;

        private Principal.Actual actual;


        public Conditional(alto.lang.HttpRequest request){
            super();
            if (null != request){
                this.request = request;
                this.authMethod = Authentication.Tools.For(request);
            }
            else
                throw new alto.sys.Error.Argument();
        }


        public void destroy(){
            this.request = null;
            this.authMethod = null;
            this.actual = null;
        }
        public java.lang.String getName(){
            String name = this.name;
            if (null == name){
                Authentication.Method authMethod = this.authMethod;
                if (null != authMethod){
                    name = authMethod.getUID(this.request);
                    this.name = name;
                }
            }
            return name;
        }
        public boolean hasMethodOfAuthentication(){
            return (null != this.authMethod);
        }
        public Authentication getMethodOfAuthentication(){
            if (null != this.authMethod)
                return this.authMethod.getKind();
            else
                return null;
        }
        public boolean isAuthenticated(){
            return (null != this.actual);
        }
        public Principal.Actual authenticate() 
            throws java.io.IOException 
        {
            if (null != this.actual)
                return this.actual;
            else if (this.once)
                return null;
            else {
                this.once = true;
                alto.lang.HttpRequest request = this.request;
                Authentication.Method authMethod = this.authMethod;
                if (null != authMethod){
                    Authentication auth = authMethod.getKind();
                    String uid = this.getName();
                    if (null != uid){
                        alto.sec.Keys keys = alto.sec.Keys.Tools.Dereference(uid);
                        if (null != keys && authMethod.verify(keys,request)){
                            Principal.Actual principal = keys;
                            this.actual = principal.push(auth);
                            return this.actual;
                        }
                    }
                }
                return null;
            }
        }
    }
    /**
     * Server side
     */
    public interface Actual
        extends Principal.Server,
                Authentic,
                alto.lang.Message
    {

        /**
         * A system- class principal is not contained or protected.
         */
        public boolean isSystem();

        public boolean isNotSystem();
        /**
         * A application- class principal is contained or protected by
         * a system- class principal.
         */
        public boolean isApplication();
        /**
         * A user- class principal is contained or protected by an
         * application- class principal.
         */
        public boolean isUser();

        public boolean addRole(Principal.Authentic role);

        public boolean inRole(Role role);

        public boolean inRole(Set<Role> role);

        public boolean inRole(Principal.Authentic role);

        public boolean inRole(java.lang.String role)
            throws java.io.IOException;

        public Authentication getAuthentication();

        public Principal.Actual getProtector();

        /**
         * Used in the implementation of {@link
         * #push(Principal$Actual)}.
         * 
         * The clone may be destroyed from {@link #pop} without effect
         * on its precursor.  Any other use of {@link #copy} (than for
         * {@link #push}) is a bug, as the class requires destruction,
         * and clone destruction is only defined to operate correctly
         * under {@link #pop}.
         * 
         * @return Shallow clone for {@link #push}
         */
        public Principal.Actual copy(Principal.Actual scope);

        /**
         * Used in the implementation of {@link #push(Authentication)}.
         * 
         * The clone may be destroyed from {@link #pop} without effect
         * on its precursor.  Any other use of {@link #copy} (than for
         * {@link #push}) is a bug, as the class requires destruction,
         * and clone destruction is only defined to operate correctly
         * under {@link #pop}.
         * 
         * @return Shallow clone for {@link #push}
         */
        public Principal.Actual copy(Authentication scope);

        /**
         * Used by the thread context to exit a previously entered
         * scope.
         * 
         * @return Previous scope (or this if none)
         */
        public Principal.Actual pop();

        /**
         * Used by the thread context to enter the scope of the
         * protector.
         * 
         * @return Principal scope of protector, prepared to pop (a
         * {@link copy}).
         */
        public Principal.Actual push();

        /**
         * Used by the thread context to enter an authenticated scope.
         * 
         * @return Principal scope of this, authenticated, prepared to
         * pop (a {@link copy}).
         */
        public Principal.Actual push(Authentication context);

        public X509Certificate getCertificate(java.lang.String alg);

        public X500Name getX500Name() throws java.io.IOException ;
    }


}
