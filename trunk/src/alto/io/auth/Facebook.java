/*
 * Alto
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
package alto.io.auth;

import alto.hash.MD5;
import alto.io.Authentication;
import alto.io.Code;
import alto.io.Check;
import alto.io.Principal;
import alto.io.u.Url;
import alto.lang.HttpBodyUrlEncoded;
import alto.lang.HttpRequest;
import alto.sec.Auth;

/**
 * Facebook is a request signature scheme that applies exclusively to
 * query parameters.  When the host and path are irrelevant and only
 * the query parameters need security, then this is a very interested
 * authentication scheme.
 */
public final class Facebook
    extends java.lang.Object
    implements Authentication.Method
{

    private Authentication kind;


    public Facebook(){
        super();
    }

    public Authentication getKind(){
        return this.kind;
    }
    public void setKind(Authentication from){
        if (null == this.kind)
            this.kind = from;
        else
            throw new alto.sys.Error.State.Init();
    }
    public boolean isVerifiable(HttpRequest request){
        HttpBodyUrlEncoded params = request.getHttpBodyUrlEncoded();
        if (null != params)
            return (null != params.getParameter("api_key") && null != params.getParameter("sig"));
        else
            return false;
    }
    public String getUID(HttpRequest request){
        HttpBodyUrlEncoded params = request.getHttpBodyUrlEncoded();
        if (null != params)
            return params.getParameter("uid");
        else
            return null;
    }

    public boolean sign(Principal.Authentic keys, HttpRequest request){
        if (null != keys && null != request){
            if (request.isRequestClient()){
                Auth apiKeys = keys.getAuth(Authentication.Facebook);
                if (null != apiKeys){
                    HttpBodyUrlEncoded params = request.getHttpBodyUrlEncoded();
                    if (null != params){
                        params.setParameter("api_key",apiKeys.getAPIKey());
                        java.lang.String[] sortedParams = params.getQueryKeysSorted();
                        if (null != sortedParams){
                            java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
                            for (int cc = 0, count = sortedParams.length; cc < count; cc++){
                                java.lang.String name = sortedParams[cc];
                                if ("sig".equals(name))
                                    continue;
                                else {
                                    java.lang.String value = params.getParameter(name);
                                    /*
                                     * As in 'HttpBodyUrlEncoded', Url
                                     * encode both name and value
                                     */
                                    strbuf.append(Url.encode(name));
                                    strbuf.append('=');
                                    strbuf.append(Url.encode(value));
                                }
                            }
                            strbuf.append(apiKeys.getAPISecret());
                            java.lang.String sig = new MD5(strbuf.toString()).hashHex();
                            params.setParameter("sig",sig);
                            return true;
                        }
                    }
                    return false;
                }
                else
                    throw new alto.sys.Error.Bug("Missing Fb API Keys for principal '"+keys.getUID()+"'.");
            }
            else
                throw new alto.sys.Error.Bug("Not client request");
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public boolean verify(Principal.Authentic keys, HttpRequest request){
        if (null != keys && null != request){
            if (request.isRequestServer()){
                Auth apiKeys = keys.getAuth(Authentication.Facebook);
                if (null != apiKeys){
                    HttpBodyUrlEncoded params = request.getHttpBodyUrlEncoded();
                    if (null != params){
                        if (apiKeys.getAPIKey().equals(params.getParameter("api_key"))){
                            java.lang.String[] sortedParams = params.getQueryKeysSorted();
                            if (null != sortedParams){
                                java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
                                for (int cc = 0, count = sortedParams.length; cc < count; cc++){
                                    java.lang.String name = sortedParams[cc];
                                    if ("sig".equals(name))
                                        continue;
                                    else {
                                        java.lang.String value = params.getParameter(name);
                                        /*
                                         * As in 'HttpBodyUrlEncoded', Url
                                         * encode both name and value
                                         */
                                        strbuf.append(Url.encode(name));
                                        strbuf.append('=');
                                        strbuf.append(Url.encode(value));
                                    }
                                }
                                strbuf.append(apiKeys.getAPISecret());
                                java.math.BigInteger verification = new java.math.BigInteger(new MD5(strbuf.toString()).hash());
                                java.math.BigInteger requested = params.getParameterHex("sig");
                                return verification.equals(requested);
                            }
                        }
                    }
                    return false;
                }
                else
                    throw new alto.sys.Error.Bug("Missing Fb API Keys for principal '"+keys.getUID()+"'.");

            }
            else
                throw new alto.sys.Error.Bug("Not server request");
        }
        else
            throw new alto.sys.Error.Argument();
    }
}
