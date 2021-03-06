/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import org.xmpp.extensions.jingle.ContentExtensionElement;
import org.xmpp.extensions.jingle.JingleIQ;

/**
 * Represents functionality which allows a <tt>TransportManagerJabberImpl</tt> implementation to
 * send <tt>transport-info</tt> {@link JingleIQ}s for the purposes of expediting candidate negotiation.
 *
 * @author Lyubomir Marinov
 */
public interface TransportInfoSender
{
    /**
     * Sends specific {@link ContentExtensionElement}s in a <tt>transport-info</tt> {@link JingleIQ}
     * from the local peer to the remote peer.
     *
     * @param contents the <tt>ContentExtensionElement</tt>s to be sent in a <tt>transport-info</tt>
     * <tt>JingleIQ</tt> from the local peer to the remote peer
     */
    void sendTransportInfo(Iterable<ContentExtensionElement> contents);
}
