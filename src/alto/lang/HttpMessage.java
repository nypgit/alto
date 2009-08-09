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

import alto.hash.MD5;
import alto.hash.RSA;
import alto.hash.SHA1;
import alto.io.Authentication;
import alto.io.Code;
import alto.io.Check;
import alto.io.Keys;
import alto.io.Principal;
import alto.io.u.Chbuf;


/**
 * An instance of {@link HttpMessage} represents a network message as
 * parsed and recognized.
 * 
 * <h3>Storage</h3>
 * 
 * A storage object is typically an instance of {@link HttpRequest}.
 * See {@link alto.sys.File} and {@link alto.sys.Reference} for more
 * information.  
 * 
 * <h3>Authentication</h3>
 * 
 * A message is referenced from the store on "read", and cloned from
 * the store on "write", therefore the sourcing of a principal from a
 * message is incorrect in every case excepting that of a network
 * request message to a server.
 * 
 * @author jdp
 */
public interface HttpMessage
    extends alto.lang.Headers,
            alto.io.Message,
            alto.sys.IO.Context,
            alto.sys.IO.Edge,
            alto.io.Authentication.Container
{
    /**
     * Body buffer returned by get create buffer.
     */
    public static class Body
        extends Buffer.IOB
    {
        private final HttpMessage m;


        public Body(HttpMessage m){
            super(m.getBody());
            this.m = m;
        }


        public java.io.OutputStream openOutputStream()
            throws java.io.IOException
        {
            this.m.clearBody();
            return super.openOutputStream();
        }
        public alto.io.Output openOutput()
            throws java.io.IOException
        {
            this.m.clearBody();
            return super.openOutput();
        }
        public java.io.OutputStream getOutputStream()
            throws java.io.IOException
        {
            if (null == this.out)
                this.m.clearBody();
            return super.getOutputStream();
        }
        public alto.io.Output getOutput()
            throws java.io.IOException
        {
            if (null == this.out)
                this.m.clearBody();
            return super.getOutput();
        }
    }


    public HttpMessage cloneHttpMessage();

    public void disconnect()
        throws java.io.IOException;


    public boolean headTo(HttpMessage dst)
        throws java.io.IOException;

    public boolean copyTo(HttpMessage dst)
        throws java.io.IOException;

    public boolean hasSocket();

    public Socket getSocket();

    public boolean hasProtocol();

    public boolean hasNotProtocol();

    public Protocol getProtocol();

    public void setProtocol(Protocol protocol);

    public void setProtocol(java.lang.String value);

    public void setProtocolDefaultHTTP10();

    public boolean hasHeadline();

    public boolean hasNotHeadline();

    public java.lang.String getHeadline();

    public boolean hasMethod();

    public boolean hasNotMethod();

    public java.lang.String getMethod();

    public java.lang.String validateMethod();

    public void setMethod(java.lang.String method);

    public void setMethodDefaultPUT();

    public boolean hasPathComplete();

    public boolean hasNotPathComplete();

    public boolean isContextSystem();

    public java.lang.String getPathComplete();

    public java.lang.String validatePathComplete();

    public void setPathComplete(java.lang.String pathComplete);

    public void setPathCompleteWithDefaults(java.lang.String pathComplete);

    public void setPathCompleteWithDefaults(alto.sys.Reference reference)
        throws java.io.IOException;

    public boolean hasBody();

    public byte[] getBody();

    public void setBody(byte[] body);

    public void clearBody();

    public Buffer.IOB getCreateBuffer();

    public boolean isTransient();

    public void readBody(alto.io.Input in, int length)
        throws java.io.IOException;

    public void readMessage()
        throws java.io.IOException;

    public void readMessage(byte[] buf)
        throws java.io.IOException;

    public void readMessage(alto.io.Input in)
        throws java.io.IOException;

    public void readMessageHead(alto.io.Input in)
        throws java.io.IOException;

    public void writeMessage()
        throws java.io.IOException;

    /**
     * This method must not enter any locks.
     */
    @Code(Check.Locked)
    public void writeMessage(alto.io.Output out)
        throws java.io.IOException;

    public void writeMessageHead(alto.io.Output out)
        throws java.io.IOException;

    public boolean hasContentLength();

    public boolean isContentLengthPositive();

    public Header getContentLengthHeader();

    public java.lang.String validateContentLengthHeaderString();

    public int getContentLength();

    public void setContentLength(int length);

    public boolean hasContentType();

    public boolean hasNotContentType();

    public Header getContentTypeHeader();

    public java.lang.String getContentTypeHeaderString();

    public Type getContentType();

    public void setContentType(Type type);

    public void setContentType(alto.sys.Reference reference)
        throws java.io.IOException;

    public void setContentTypeText();

    public void setContentTypeHtml();

    public void setContentTypeXml();

    public void setContentTypeApplicationXml();

    public void setContentTypeJavaSource();

    public void setContentTypeJavaClass();

    public void setContentTypeMultipart();

    public void setContentTypeMessageHttp();

    public void setContentTypeUrlEncoded();

    public void setContentTypeJar();

    public void setContentTypeZip();

    public boolean hasContentLocation();

    public boolean hasNotContentLocation();

    public Header getContentLocationHeader();

    public java.lang.String getContentLocation();

    public java.lang.String getContentLocationHeaderString();

    public void setContentLocation(java.lang.String encoding);

    public boolean hasContentEncoding();

    public boolean hasNotContentEncoding();

    public Header getContentEncodingHeader();

    public java.lang.String getContentEncodingHeaderString();

    public java.lang.String getContentEncoding();

    public void setContentEncoding(java.lang.String encoding);

    public boolean hasContentRange();

    public boolean hasNotContentRange();

    public Header getContentRangeHeader();

    public java.lang.String getContentRangeHeaderString();

    public java.lang.String getContentRange();

    public void setContentRange(java.lang.String range);

    public boolean hasDate();

    public boolean hasNotDate();

    public Header getDateHeader();

    public long getDate();

    public void setDate();

    public void maySetDate();

    public void setDate(long date);

    public java.lang.String getDateHeaderString();

    @Code(Check.TODO)
    public java.lang.String validateDateHeaderString();

    public boolean hasExpires();

    public boolean hasNotExpires();

    public Header getExpiresHeader();

    public java.lang.String getExpiresHeaderString();

    public long getExpires();

    public void setExpires(long date);

    public boolean hasHost();

    public boolean hasNotHost();

    public java.lang.String getHostHeaderString();

    public Header getHostHeader();

    public java.lang.String validateHostHeaderString();

    public java.lang.String getHost();

    public void setHost(java.lang.String value);

    public void setHost(alto.sys.Reference reference);

    public void setHost();

    public void maySetHost();

    public void setHost(alto.lang.Headers headers);

    public boolean hasConnection();

    public boolean hasNotConnection();

    public Header getConnectionHeader();

    public java.lang.String getConnection();

    public void setConnection(java.lang.String value);

    public boolean hasServer();

    public boolean hasNotServer();

    public Header getServerHeader();

    public java.lang.String getServer();

    public void setServer(java.lang.String value);

    public boolean hasETag();

    public boolean hasNotETag();

    public Header getETagHeader();

    public java.lang.String getETagHeaderString();

    public java.lang.String getETag();

    public void unsetETag();

    public void setETag(java.lang.String value);

    public java.lang.String setETag();

    public boolean hasAuthorization();

    public boolean hasNotAuthorization();

    public Header getAuthorizationHeader();

    public java.lang.String getAuthorization();

    public void setAuthorization(java.lang.String value);
    /**
     * Has authentication method.
     * @return Authentication method
     */
    public Authentication setAuthorization();

    public boolean hasIfNoneMatch();

    public boolean hasNotIfNoneMatch();

    public Header getIfNoneMatchHeader();

    public java.lang.String getIfNoneMatch();

    public void setIfNoneMatch(java.lang.String value);

    public java.lang.String setIfNoneMatch();

    public boolean hasLastModified();

    public boolean hasNotLastModified();

    public Header getLastModifiedHeader();

    public java.lang.String getLastModifiedHeaderString();

    public long getLastModified();

    public java.lang.String getLastModifiedString();

    public void setLastModified(java.lang.String date);

    public void setLastModifiedString(java.lang.String date);

    public boolean setLastModified(long date);

    public boolean setLastModified();

    public void maySetLastModified();

    public boolean hasIfModifiedSince();

    public boolean hasNotIfModifiedSince();

    public Header getIfModifiedSinceHeader();

    public long getIfModifiedSince();

    public boolean setIfModifiedSince(long date);

    public boolean hasIfUnmodifiedSince();

    public boolean hasNotIfUnmodifiedSince();

    public Header getIfUnmodifiedSinceHeader();

    public long getIfUnmodifiedSince();

    public boolean setIfUnmodifiedSince(long date);

    public boolean hasXPrincipal();

    public boolean hasNotXPrincipal();

    public Header getXPrincipalHeader();

    public java.lang.String getXPrincipal();

    public void setXPrincipal(java.lang.String value);

    public void setXPrincipal();

    public boolean hasContentMD5();

    public boolean hasNotContentMD5();

    public Header getContentMD5Header();

    /**
     * If the <code>"Content-MD5"</code> header exists, then this
     * method also verifies the message digest of the entity body.
     * @see #verifyContentMD5()
     */
    public java.lang.String validateContentMD5HeaderString();

    public java.lang.String getContentMD5();

    public java.lang.String getContentMD5(boolean set);

    public void setContentMD5(java.lang.String value);

    public java.lang.String hashContentMD5();

    public java.lang.String setContentMD5();

    /**
     * @return False for missing header, no body, or failed verification.
     */
    public boolean verifyContentMD5();

    public boolean hasUserAgent();

    public boolean hasNotUserAgent();

    public Header getUserAgentHeader();

    public java.lang.String getUserAgent();

    public void setUserAgent(java.lang.String value);

    public boolean hasHeaderMethod();

    public boolean hasNotHeaderMethod();

    public Header getHeaderMethod();

    public void setHeaderMethod(Header header);

    public boolean hasLocation();

    public boolean hasNotLocation();

    public Header getLocationHeader();

    public java.lang.String getLocation();

    public java.net.URL getLocationURL();

    public alto.sys.Reference getLocationReference();

    public void setLocation(java.lang.String url);

    public void setLocation(alto.lang.Headers headers);

    public void setLocation(alto.sys.Reference reference);

    public boolean hasDestination();

    public boolean hasNotDestination();

    public Header getDestinationHeader();

    public java.lang.String getDestination();

    public java.net.URL getDestinationURL();

    public alto.sys.Reference getDestinationReference();

    public void setDestination(java.lang.String url);

    public void setDestination(alto.lang.Headers headers);

    public boolean hasVersionTag();

    public boolean hasNotVersionTag();

    public Header getVersionTagHeader();

    public java.lang.String getVersionTag();

    public void setVersionTag(java.lang.String url);

    public void setVersionTag(alto.lang.Headers headers);

    public void addVersionTag(java.lang.String url);

    public void addVersionTag(alto.lang.Headers headers);

    public void setPersistent(boolean keepalive);

    public void setPersistent(HttpMessage headers);

    public void setPersistent(HttpRequest headers);

    public boolean hasPersistent();

    public boolean hasNotPersistent();

    public boolean isPersistent();

    public boolean isNotPersistent();

    /*
     * SDFS
     */
    public boolean hasSDFSVersion();

    public boolean hasNotSDFSVersion();

    public Header getSDFSVersionHeader();

    public java.lang.String getSDFSVersion();

    public java.lang.String getSDFSVersionHeaderString();

    public void setSDFSVersion(java.lang.String value);

    public void maySetSDFSVersion(java.lang.String uid);

    public boolean hasSDFSMID();

    public boolean hasNotSDFSMID();

    public Header getSDFSMIDHeader();

    public java.lang.String getSDFSMID();

    public java.lang.String getSDFSMIDHeaderString();

    public void setSDFSMID(java.lang.String value);

    public void maySetSDFSMID(java.lang.String uid);

    public boolean hasSDFSTTL();

    public boolean hasNotSDFSTTL();

    public Header getSDFSTTLHeader();

    public long getSDFSTTL();

    public java.lang.String getSDFSTTLHeaderString();

    public void setSDFSTTL(long value);

    public void maySetSDFSTTL(long value);

    public boolean hasSDFSAffinity();

    public boolean hasNotSDFSAffinity();

    public Header getSDFSAffinityHeader();

    public long getSDFSAffinity();

    public java.lang.String getSDFSAffinityHeaderString();

    public void setSDFSAffinity(long value);

    public void maySetSDFSAffinity(long value);

    public boolean hasSDFSMethod();

    public boolean hasNotSDFSMethod();

    public Header getSDFSMethodHeader();

    public java.lang.String getSDFSMethod();

    public java.lang.String getSDFSMethodHeaderString();

    public void setSDFSMethod(java.lang.String value);

    public void maySetSDFSMethod(java.lang.String uid);

    public boolean hasSDFSMSig();

    public boolean hasNotSDFSMSig();

    public Header getSDFSMSigHeader();

    public java.lang.String getSDFSMSig();

    public java.lang.String getSDFSMSigHeaderString();

    public void setSDFSMSig(java.lang.String value);

    public void maySetSDFSMSig(java.lang.String uid);

    public boolean hasSDFSNotify();

    public boolean hasNotSDFSNotify();

    public Header getSDFSNotifyHeader();

    public java.lang.String getSDFSNotify();

    public java.lang.String getSDFSNotifyHeaderString();

    public void setSDFSNotify(java.lang.String value);

    public void maySetSDFSNotify(java.lang.String uid);

    /**
     * Server side response unauthorized called from {@link
     * HttpResponse#setStatusUnauthorized()}.
     */
    public void setWWWAuthenticate();

    /**
     * Server side response Unauthorized
     */
    public void setWWWAuthenticate(HttpRequest request);

    /**
     * Set content length as the size of an existing buffer.  No
     * buffer will have no affect on the content length header
     * existance or value.
     */
    public int updateContentLength();

    public void writeToBuffer(alto.sys.Reference reference)
        throws java.io.IOException;
}
