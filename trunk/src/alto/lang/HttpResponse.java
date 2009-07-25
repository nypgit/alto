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

import alto.io.Code;
import alto.io.Check;

/**
 * <p> Client and server. </p>
 * 
 * @author jdp
 * @since 1.5
 */
public interface HttpResponse
    extends HttpMessage,
            alto.sys.IO.H.Target
{
    public static java.lang.String STDOUT_STR = "response:out";

    public static java.lang.String STDIN_STR = "response:in";


    public void redirectToUrl(java.lang.String url);

    public boolean isPersistentResponse();

    public boolean isNotPersistentResponse();

    public boolean hasRequest();

    public HttpRequest getRequest();

    public Status getStatus();

    public int getStatusCode();

    public java.lang.String getStatusMessage();

    public boolean isStatusContinue();

    public boolean isStatusSwitchingProtocols();

    public boolean isStatusOk();

    public boolean isStatusCreated();

    public boolean isStatusNoContent();

    public boolean isStatusMultistatus();

    public boolean isStatusNotModified();

    public boolean isStatusBadRequest();

    public boolean isStatusUnauthorized();

    public boolean isStatusNotFound();

    public boolean isStatusConflict();

    public boolean isStatusError();

    public boolean isStatusNotImplemented();

    public void setStatus(Status status);

    public void setStatusContinue();

    public void setStatusContinue(java.lang.String msg);

    public void setStatusSwitchingProtocols();

    public void setStatusSwitchingProtocols(java.lang.String msg);

    public void setStatusOk();

    public void setStatusOk(java.lang.String msg);

    public void setStatusCreated();

    public void setStatusCreated(java.lang.String msg);

    public void setStatusAccepted();

    public void setStatusAccepted(java.lang.String msg);

    public void setStatusNonAuthoritativeInformation();

    public void setStatusNonAuthoritativeInformation(java.lang.String msg);

    public void setStatusNoContent();

    public void setStatusNoContent(java.lang.String msg);

    public void setStatusResetContent();

    public void setStatusResetContent(java.lang.String msg);

    public void setStatusPartialContent();

    public void setStatusPartialContent(java.lang.String msg);

    public void setStatusMultistatus();

    public void setStatusMultistatus(java.lang.String msg);

    public void setStatusMultipleChoices();

    public void setStatusMultipleChoices(java.lang.String msg);

    public void setStatusNotModified();

    public void setStatusNotModified(java.lang.String msg);

    public void setStatusNotModified(alto.sys.Reference reference)
        throws java.io.IOException;

    public void setStatusBadRequest();

    public void setStatusBadRequest(java.lang.String msg);

    /**
     * This server response (not {@link #setStatus(Status)})
     * programming method calls {@link
     * alto.lang.HttpMessage#setWWWAuthenticate()} to define
     * an appropriate response according to the authentication method.
     */
    public void setStatusUnauthorized();

    public void setStatusUnauthorized(java.lang.String msg);

    public void setStatusForbidden();

    public void setStatusForbidden(java.lang.String msg);

    public void setStatusNotFound();

    public void setStatusNotFound(java.lang.String msg);

    @Code(Check.Review)
    public void setStatusUnknownProtocol();

    public void setStatusUnknownProtocol(java.lang.String msg);

    public void setStatusConflict();

    public void setStatusConflict(java.lang.String msg);

    public void setStatusRequestEntityTooLarge();

    public void setStatusError();

    public void setStatusError(java.lang.String msg);

    public void setStatusNotImplemented();

    public void setStatusNotImplemented(java.lang.String msg);

    public void setStatusServiceUnavailable();

    public void setStatusServiceUnavailable(java.lang.String msg);

}
