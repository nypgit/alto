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
 * @see HttpResponse
 * @author jdp
 */
public interface Status {

    public interface Code {

        public final static int Continue                     = 100;
        public final static int SwitchingProtocols           = 101;
        public final static int Ok                           = 200;
        public final static int Created                      = 201;
        public final static int Accepted                     = 202;
        public final static int NonAuthoritativeInformation  = 203;
        public final static int NoContent                    = 204;
        public final static int ResetContent                 = 205;
        public final static int PartialContent               = 206;
        public final static int Multistatus                  = 207;
        public final static int MultipleChoices              = 300;
        public final static int Redirect10Perm               = 301;
        public final static int Redirect10Temp               = 302;
        public final static int Redirect11Forget             = 303;
        public final static int NotModified                  = 304;
        public final static int UseProxy                     = 305;
        public final static int XRESERVED                    = 306;
        public final static int Redirect11Forany             = 307;
        public final static int BadRequest                   = 400;
        public final static int Unauthorized                 = 401;
        public final static int PaymentRequired              = 402;
        public final static int Forbidden                    = 403;
        public final static int NotFound                     = 404;
        public final static int MethodNotAllowed             = 405;
        public final static int NotAcceptable                = 406;
        public final static int ProxyAuthenticationRequired  = 407;
        public final static int RequestTimeout               = 408;
        public final static int Conflict                     = 409;
        public final static int Gone                         = 410;
        public final static int LengthRequired               = 411;
        public final static int PreconditionFailed           = 412;
        public final static int RequestEntityTooLarge        = 413;
        public final static int RequestURITooLarge           = 414;
        public final static int UnsupportedMediaType         = 415;
        public final static int RequestedRangeNotSatisfiable = 416;
        public final static int ExpectationFailed            = 417;
        public final static int Error                        = 500;
        public final static int NotImplemented               = 501;
        public final static int BadGateway                   = 502;
        public final static int ServiceUnavailable           = 503;
        public final static int GatewayTimeout               = 504;
        public final static int HTTPVersionNotSupported      = 505;
    }


    public boolean isSpecial();

    public int getCode();

    public java.lang.String getCodeString();

    public java.lang.String getMessage();

    public java.lang.String toString();

    public int hashCode();

    public boolean equals(Object ano);

}
