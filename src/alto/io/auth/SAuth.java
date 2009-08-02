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

import alto.hash.Function;
import alto.hash.RSA;
import alto.hash.SHA1;
import alto.io.Authentication;
import alto.io.Code;
import alto.io.Check;
import alto.io.Principal;
import alto.lang.Header;
import alto.lang.HttpRequest;

/**
 * Implements SAuth version one.
 * 
 * @author jdp
 */
public final class SAuth
    extends java.lang.Object
    implements Authentication.Method
{

    private Authentication kind;


    public SAuth(){
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
        return this.hasCredentialsSAuth(request);
    }
    public String getUID(HttpRequest request){
        return this.getSAuthUID(request);
    }

    public boolean sign(Principal.Authentic keys, HttpRequest request){
        if (null != keys){
            RSA rsa = new RSA(keys);
            request.maySetDate();
            request.maySetHost();
            request.setContentMD5();
            this.setSAuthVersion(request);
            this.setSAuthUID(request,keys.getUID());
            this.setSAuthNonce(request);

            java.lang.String requestline = request.validateMethod()+' '+request.validatePathComplete();
            java.lang.String headerHost = request.validateHostHeaderString();
            java.lang.String headerDate = request.validateDateHeaderString();
            java.lang.String headerSAuthVersion = this.validateSAuthVersionHeaderString(request);
            java.lang.String headerSAuthUID = this.validateSAuthUIDHeaderString(request);
            java.lang.String headerSAuthNonce = this.validateSAuthNonceHeaderString(request);

            java.lang.String headerContentType = null;
            java.lang.String headerContentLength = null;
            java.lang.String headerContentEncoding = null;
            java.lang.String headerContentRange = null;
            java.lang.String headerContentLocation = null;
            java.lang.String headerETag = null;
            java.lang.String headerLastModified = null;
            java.lang.String headerExpires = null;
            java.lang.String headerContentMD5 = null;

            SHA1 sha = new SHA1();
            sha.update(requestline);
            sha.update(headerHost);
            sha.update(headerDate);
            if (request.isContentLengthPositive()){

                headerContentType = request.getContentTypeHeaderString();
                sha.update(headerContentType);

                headerContentLength = request.validateContentLengthHeaderString();
                sha.update(headerContentLength);

                headerContentEncoding = request.getContentEncodingHeaderString();
                sha.update(headerContentEncoding);

                headerContentRange = request.getContentRangeHeaderString();
                sha.update(headerContentRange);

                headerContentLocation = request.getContentLocationHeaderString();
                sha.update(headerContentLocation);

                headerETag = request.getETagHeaderString();
                sha.update(headerETag);

                headerLastModified = request.getLastModifiedHeaderString();
                sha.update(headerLastModified);

                headerExpires = request.getExpiresHeaderString();
                sha.update(headerExpires);

                headerContentMD5 = request.validateContentMD5HeaderString();//(verifyContentMD5)
                sha.update(headerContentMD5);
            }
            sha.update(headerSAuthVersion);
            sha.update(headerSAuthUID);
            sha.update(headerSAuthNonce);

            long signature = Function.Xor.Hash64(rsa.sign(sha));

            this.setSAuthSignature(request,signature);
            return true;
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public boolean verify(Principal.Authentic keys, HttpRequest request){
        if (null != keys){
            if (this.hasSAuthVersion(request)){
                long requested = this.getSAuthSignatureLong(request);

                RSA rsa = new RSA(keys);

                java.lang.String requestline = request.validateMethod()+' '+request.validatePathComplete();
                java.lang.String headerHost = request.validateHostHeaderString();
                java.lang.String headerDate = request.validateDateHeaderString();
                java.lang.String headerSAuthVersion = this.validateSAuthVersionHeaderString(request);
                java.lang.String headerSAuthUID = this.validateSAuthUIDHeaderString(request);
                java.lang.String headerSAuthNonce = this.validateSAuthNonceHeaderString(request);

                java.lang.String headerContentType = null;
                java.lang.String headerContentLength = null;
                java.lang.String headerContentEncoding = null;
                java.lang.String headerContentRange = null;
                java.lang.String headerContentLocation = null;
                java.lang.String headerETag = null;
                java.lang.String headerLastModified = null;
                java.lang.String headerExpires = null;
                java.lang.String headerContentMD5 = null;

                SHA1 sha = new SHA1();
                sha.update(requestline);
                sha.update(headerHost);
                sha.update(headerDate);
                if (request.isContentLengthPositive()){

                    headerContentType = request.getContentTypeHeaderString();
                    sha.update(headerContentType);

                    headerContentLength = request.validateContentLengthHeaderString();
                    sha.update(headerContentLength);

                    headerContentEncoding = request.getContentEncodingHeaderString();
                    sha.update(headerContentEncoding);

                    headerContentRange = request.getContentRangeHeaderString();
                    sha.update(headerContentRange);

                    headerContentLocation = request.getContentLocationHeaderString();
                    sha.update(headerContentLocation);

                    headerETag = request.getETagHeaderString();
                    sha.update(headerETag);

                    headerLastModified = request.getLastModifiedHeaderString();
                    sha.update(headerLastModified);

                    headerExpires = request.getExpiresHeaderString();
                    sha.update(headerExpires);

                    headerContentMD5 = request.validateContentMD5HeaderString();//(verifyContentMD5)
                    sha.update(headerContentMD5);
                }
                sha.update(headerSAuthVersion);
                sha.update(headerSAuthUID);
                sha.update(headerSAuthNonce);

                long signature = Function.Xor.Hash64(rsa.sign(sha));

                return (requested == signature);
            }
            else
                return false;
        }
        else
            throw new alto.sys.Error.Argument();
    }

    /*
     * TODO: (all of these should be static)
     */
    public boolean hasCredentialsSAuth(HttpRequest request){
        return (this.hasSAuthVersion(request) && 
                this.hasSAuthUID(request) &&
                this.hasSAuthNonce(request) &&
                this.hasSAuthSignature(request));
    }
    /**
     * Authentication moved into lang/Headers so that any Headers
     * object could be employed as a client authentication context.
     */
    public boolean hasSAuthVersion(HttpRequest request){
        return (request.hasHeader(Header.SAuth.Version));
    }
    public boolean hasNotSAuthVersion(HttpRequest request){
        return (request.hasNotHeader(Header.SAuth.Version));
    }
    public alto.lang.Header getSAuthVersionHeader(HttpRequest request){
        return request.getHeader(Header.SAuth.Version);
    }
    public java.lang.String getSAuthVersion(HttpRequest request){
        return request.getHeaderString(Header.SAuth.Version);
    }
    public java.lang.String getSAuthVersionHeaderString(HttpRequest request){
        alto.lang.Header header = request.getHeader(Header.SAuth.Version);
        if (null != header)
            return header.toString();
        else
            return null;
    }
    public java.lang.String validateSAuthVersionHeaderString(HttpRequest request){
        java.lang.String re = request.validateHeaderString(Header.SAuth.Version);
        if (null != re && re.equals(Header.SAuth.Value.Version.toString())){
            return re;
        }
        else
            throw new alto.sys.BadRequestException(re);
    }
    /**
     * Define client request for authentication.
     */
    public void setSAuthVersion(HttpRequest request){
        if (request.hasNotHeader(Header.SAuth.Value.Authorization))
            request.setHeader(Header.SAuth.Value.Authorization);

        if (request.hasNotHeader(Header.SAuth.Value.Version))
            request.setHeader(Header.SAuth.Value.Version);
    }
    public void maySetSAuthVersion(HttpRequest request){
        if (request.hasNotHeader(Header.SAuth.Value.Authorization))
            request.setHeader(Header.SAuth.Value.Authorization);

        if (request.hasNotHeader(Header.SAuth.Value.Version))
            request.setHeader(Header.SAuth.Value.Version);
    }
    public boolean hasSAuthUID(HttpRequest request){
        return (request.hasHeader(Header.SAuth.UID));
    }
    public boolean hasNotSAuthUID(HttpRequest request){
        return (request.hasNotHeader(Header.SAuth.UID));
    }
    public alto.lang.Header getSAuthUIDHeader(HttpRequest request){
        return request.getHeader(Header.SAuth.UID);
    }
    public java.lang.String validateSAuthUIDHeaderString(HttpRequest request){
        return request.validateHeaderString(Header.SAuth.UID);
    }
    public java.lang.String getSAuthUID(HttpRequest request){
        return request.getHeaderString(Header.SAuth.UID);
    }
    public java.lang.String getSAuthUIDHeaderString(HttpRequest request){
        alto.lang.Header header = request.getHeader(Header.SAuth.UID);
        if (null != header)
            return header.toString();
        else
            return null;
    }
    public void setSAuthUID(HttpRequest request, java.lang.String value){
        request.setHeader(Header.SAuth.UID,value);
    }
    public void maySetSAuthUID(HttpRequest request, java.lang.String uid){
        java.lang.String value = this.getSAuthUID(request);
        if (null == value || (!value.equals(uid)))
            request.setHeader(Header.SAuth.UID,uid);
    }
    public void setSAuthUID(HttpRequest request, Principal principal){
        request.setHeader(Header.SAuth.UID,principal.getName());
    }
    public boolean hasSAuthNonce(HttpRequest request){
        return (request.hasHeader(Header.SAuth.Nonce));
    }
    public boolean hasNotSAuthNonce(HttpRequest request){
        return (request.hasNotHeader(Header.SAuth.Nonce));
    }
    public alto.lang.Header getSAuthNonceHeader(HttpRequest request){
        return request.getHeader(Header.SAuth.Nonce);
    }
    public java.lang.String getSAuthNonce(HttpRequest request){
        return request.getHeaderString(Header.SAuth.Nonce);
    }
    public java.lang.String getSAuthNonceHeaderString(HttpRequest request){
        alto.lang.Header header = request.getHeader(Header.SAuth.Nonce);
        if (null != header)
            return header.toString();
        else
            return null;
    }
    public java.lang.String validateSAuthNonceHeaderString(HttpRequest request){
        java.lang.String re = request.validateHeaderString(Header.SAuth.Nonce);
        java.math.BigInteger value = request.getHeaderHex(Header.SAuth.Nonce);
        if (null != value && 14 < value.bitLength())
            return re;
        else
            throw new alto.sys.BadRequestException(re);
    }
    public void setSAuthNonce(HttpRequest request){
        request.setHeader(Header.SAuth.Nonce,alto.io.u.Prng.RandLongStringHex());
    }
    public void maySetSAuthNonce(HttpRequest request){
        if (request.hasNotHeader(Header.SAuth.Nonce))
            request.setHeader(Header.SAuth.Nonce,alto.io.u.Prng.RandLongStringHex());
    }
    public void setSAuthNonce(HttpRequest request, java.lang.String value){
        request.setHeader(Header.SAuth.Nonce,value);
    }
    public boolean hasSAuthSignature(HttpRequest request){
        return (request.hasHeader(Header.SAuth.Signature));
    }
    public boolean hasNotSAuthSignature(HttpRequest request){
        return (request.hasNotHeader(Header.SAuth.Signature));
    }
    public alto.lang.Header getSAuthSignatureHeader(HttpRequest request){
        return request.getHeader(Header.SAuth.Signature);
    }
    public java.lang.String getSAuthSignature(HttpRequest request){
        return request.getHeaderString(Header.SAuth.Signature);
    }
    public long getSAuthSignatureLong(HttpRequest request){
        java.math.BigInteger bint = request.getHeaderHex(Header.SAuth.Signature);
        if (null != bint)
            return bint.longValue();
        else
            return 0L;
    }
    public void setSAuthSignature(HttpRequest request, java.lang.String value){
        request.setHeader(Header.SAuth.Signature,value);
    }
    public void setSAuthSignature(HttpRequest request, long folded){
        java.lang.String value = alto.io.u.Hex.encode(folded);
        request.setHeader(Header.SAuth.Signature,value);
    }
}
