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
 * Used with {@link Code}.  The values under the {@link Check} enum
 * are programming issues.  Some concern problems in execution time or
 * space, others make notes or issue warnings, and others mark system
 * state changes.
 * 
 * The "Sec*" names concern issues in system security (code trust).
 * 
 * The "Sys*" names mark system state changes for entering the
 * annotated method.
 * 
 * @author jdp
 * @see Sec
 * @see Code
 */
public enum Check {
    /**
     * Makes a copy which may not be necessary.
     */
    Copies,
    /**
     * Builds a structure which may not be necessary (a form of
     * {@link Copy}), internally or in its return type.
     */
    Builds,
    /**
     * Code multiplies input / state in time or space.
     */
    Multiplier,
    /**
     * Code performs notable side effects as a network client.
     */
    Client,
    /**
     * Code only employed by network client under thread sys init
     * main.
     */
    OnlyClient,
    /**
     * Code only employed by network server (would be under thread SX
     * Service).
     */
    OnlyServer,
    /**
     * Code implements disparate use cases that may be tricky to
     * use or understand.
     */
    Multiuse,
    /**
     * Known bugs
     */
    Bad,
    /**
     * Known bugs
     */
    Bug,
    /**
     * Something obvious to be done
     */
    TODO,
    /**
     * Choices ready to be reviewed
     */
    Review,
    /**
     * The execution path into this method has (probably) entered a
     * lock.  Do not enter any locks under this path.
     */
    Locked,
    /**
     * The execution path under this method may enter locks.  Do not
     * call into this path from within a lock.
     */
    Locking,
    /**
     * This API is abnormal, for initialization purposes only.
     */
    Bootstrap,
    /**
     * Code copies when it may not need to
     */
    SecCopies,
    /**
     * Code does not copy when it really should
     */
    SecNoCopy,
    /**
     * Caller is particularly trusted.
     */
    SecTrusted,
    /**
     * Caller is not "trusted" (no dependency, this is safe).
     */
    SecSafe,
    /**
     * This proceedure will throw an Unauthorized exception when not
     * capable of fulfilling the request.
     */
    SecThrows,
    /**
     * This proceedure will return when not capable of fulfilling the
     * request.
     */
    SecReturns,
    /**
     * This proceedure is only implemented for Authentication type
     * SAuth.  This case may be reviewed if another suitable
     * authentication type is implemented.
     */
    SecImplSAuth,
    /**
     * On entering the annotated method, the thread context principal
     * should change to "system".
     */
    SysPrincipalSystem,
    /**
     * On entering the annotated method, the thread context principal
     * should change to the application.
     */
    SysPrincipalApplication,
    /**
     * On entering the annotated method, the thread context principal
     * should change to the authenticated user.
     */
    SysPrincipalUser,
    /**
     * On entering the annotated method, the thread context principal
     * should change to "none".
     */
    SysPrincipalNone
}
