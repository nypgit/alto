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
 * A format parser / generator object class with an embedded reference
 * has methods to read and write itself.
 * 
 * @see alto.sys.P
 * @see alto.sys.PHeaders
 * @see alto.sys.PSioFile
 */
public interface Message {

    /**
     * A message with built in list capabilities.  Whether the list is
     * a FIFO (Queue) is implementation specific.  For example, the SX
     * Log is a LIFO for performance reasons.
     */
    public interface Queue 
        extends Message
    {
        /**
         * A queue with extended built in list capabilities
         */
        public interface Head
            extends Message.Queue
        {
            /**
             * 
             */
            public Message.Queue.Head push(Message.Queue.Head m);
            /**
             * 
             */
            public Message.Queue.Head pop();
        }

        /**
         * 
         */
        public Message.Queue pop();
    }

    /**
     * Streams from "open" are closed by the user of this interface.
     * @return If null, then output unavailable.  
     */
    public Input openInput()
        throws java.io.IOException;
    /**
     * Streams from "get" are not closed by the user of this interface.
     * @return If null, then use "open".  
     */
    public Input getInput()
        throws java.io.IOException;
    /**
     * Streams from "open" are closed by the user of this interface.
     * @return If null, then input unavailable.  
     */
    public Output openOutput()
        throws java.io.IOException;
    /**
     * Streams from "get" are not closed by the user of this interface.
     * @return If null, then use "open".  
     */
    public Output getOutput()
        throws java.io.IOException;
    /**
     * Message buffer is filled by {@link #formatMessage()}.  Note
     * that {@link #formatMessage()} is implied by {@link
     * #writeMessage()}.
     * 
     * @return Message buffer.  Null for length zero.
     * @see #formatMessage();
     */
    public byte[] getBuffer();
    /**
     * @return Message buffer size, always greater than negative one.
     * @see #formatMessage();
     */
    public int getBufferLength();
    /**
     * @return Write message into this buffer
     * @see #writeMessage()
     */
    public boolean isTransient();
    /**
     * @return Write message to external destination
     * @see #writeMessage()
     */
    public boolean isPersistent();
    /**
     * Reset, then read from input
     */
    public void readMessage()
        throws java.io.IOException;
    /**
     * Format message, then write to output.
     * 
     * The last line of this method definition should be 
     * <pre>
     * this.reference.setStorageContent(this);
     * </pre>
     */
    public void writeMessage()
        throws java.io.IOException;
}
