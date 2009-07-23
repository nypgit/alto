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


/**
 * Enumeration of authentication identifiers for quantifying the
 * production and consumption of authentication.
 *  
 * @author jdp
 */
public final class Authentication 
    extends java.lang.Object
{
    public final static class Names {
        public final static String SAuth    = "SAuth";
        public final static String SSL      = "SSL";
        public final static String NotApp   = "NotApp";
        public final static String Facebook = "Facebook";
        public final static String AWS      = "AWS";
        public final static String OpenID   = "OpenID";
        public final static String OAuth    = "OAuth";
        public final static String App      = "App";
        public final static String Any      = "*";
    }
    public final static class Constants {
        public final static int None     = 0x0000;
        public final static int SAuth    = 0x0001;
        public final static int SSL      = 0x0002;
        public final static int NotApp   = 0x00ff;
        public final static int Facebook = 0x1100;
        public final static int AWS      = 0x3100;
        public final static int OpenID   = 0x5100;
        public final static int OAuth    = 0x7100;
        public final static int App      = 0xff00;
        public final static int Any      = 0xffff;
    }
    public final static class Properties {
        public final static int None          = 0x0000;
        public final static int KeyAndSecret  = 0x0001;

        public final static int Facebook      = (KeyAndSecret);
    }
    /**
     * Note that the set of actual (and defined) authentication
     * methods does not include the descriptive (or mask) types
     * "None", "App" or "Any" or "NotApp".
     * 
     * @return One of the actual (and defined) authentication methods,
     * or null for not found.
     */
    public final static Authentication Lookup(int constant){
        switch (constant){
        case Constants.SAuth:
            return Authentication.SAuth;
        case Constants.SSL:
            return Authentication.SSL;
        case Constants.Facebook:
            return Authentication.Facebook;
        case Constants.AWS:
            return Authentication.AWS;
        case Constants.OpenID:
            return Authentication.OpenID;
        case Constants.OAuth:
            return Authentication.OAuth;
        default:
            return null;
        }
    }

    /**
     * Specify "any method, but some method"
     */
    public final static Authentication Any      = new Authentication(Names.Any,     Constants.Any,     Properties.None);
    /**
     * Specify "not any app method"
     */
    public final static Authentication NotApp   = new Authentication(Names.NotApp,  Constants.NotApp,  Properties.None);
    /**
     * "SAuth" is... strong but not private
     */
    public final static Authentication SAuth    = new Authentication(Names.SAuth,   Constants.SAuth,   Properties.None);
    /**
     * "SSL" is... strong and private
     */
    public final static Authentication SSL      = new Authentication(Names.SSL,     Constants.SSL,     Properties.None);
    /**
     * App
     */
    public final static Authentication Facebook = new Authentication(Names.Facebook,Constants.Facebook,Properties.Facebook);
    /**
     * App
     */
    public final static Authentication AWS     = new Authentication(Names.AWS,    Constants.AWS,    Properties.None);
    /**
     * App
     */
    public final static Authentication OpenID   = new Authentication(Names.OpenID,  Constants.OpenID,  Properties.None);
    /**
     * App
     */
    public final static Authentication OAuth    = new Authentication(Names.OAuth,   Constants.OAuth,   Properties.None);



    public final String name;

    public final int constant;

    public final int properties;


    private Authentication(String name, int constant, int properties){
        super();
        this.name = name;
        this.constant = constant;
        this.properties = properties;
    }


    public String getName(){
        return this.name;
    }
    public String getConstant(){
        return this.name;
    }
    /**
     * @return True when the {@link alto.sec.Auth} object uses
     * a Key and Secret layout.
     */
    public boolean hasAuthKeyAndSecret(){
        return (Properties.KeyAndSecret == (Properties.KeyAndSecret & this.properties));
    }
    public String toString(){
        return this.name;
    }
    public int hashCode(){
        return this.constant;
    }
    public boolean equals(Object that){

        if (this == that)
            return true;

        else if (that instanceof Authentication){
            switch (this.constant){
            case Constants.SAuth:
                return (Constants.SAuth    == (((Authentication)that).constant & Constants.SAuth));
            case Constants.SSL:
                return (Constants.SSL      == (((Authentication)that).constant & Constants.SSL));
            case Constants.NotApp:
                return (Constants.None     == (((Authentication)that).constant & Constants.App));
            case Constants.Facebook:
                return (Constants.Facebook == (((Authentication)that).constant & Constants.Facebook));
            case Constants.AWS:
                return (Constants.AWS      == (((Authentication)that).constant & Constants.AWS));
            case Constants.OpenID:
                return (Constants.OpenID   == (((Authentication)that).constant & Constants.OpenID));
            case Constants.OAuth:
                return (Constants.OAuth    == (((Authentication)that).constant & Constants.OAuth));
            case Constants.App:
                return (Constants.None     != (((Authentication)that).constant & Constants.App));
            case Constants.Any:
                return true;
            default:
                throw new alto.sys.Error.Bug();
            }
        }
        else
            return false;
    }
}
