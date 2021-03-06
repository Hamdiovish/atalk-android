package org.xmpp.jnodes.smack;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.provider.IQProvider;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class JingleTrackerProvider extends IQProvider
{
    @Override
    public Element parse(XmlPullParser parser, int depth, XmlEnvironment xmlEnvironment)
            throws XmlPullParserException, IOException
    {
        JingleTrackerIQ iq = new JingleTrackerIQ();

        boolean done = false;
        int eventType;
        String elementName;

        while (!done) {
            eventType = parser.getEventType();
            elementName = parser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                final TrackerEntry.Type type;
                if (elementName.equals(TrackerEntry.Type.relay.toString())) {
                    type = TrackerEntry.Type.relay;
                }
                else if (elementName.equals(TrackerEntry.Type.tracker.toString())) {
                    type = TrackerEntry.Type.tracker;
                }
                else {
                    parser.next();
                    continue;
                }

                final String protocol = parser.getAttributeValue(null, "protocol");
                final TrackerEntry.Policy policy = TrackerEntry.Policy.valueOf("_"
                        + parser.getAttributeValue(null, "policy"));
                final String address = parser.getAttributeValue(null, "address");
                final String verified = parser.getAttributeValue(null, "verified");

                if (address != null && address.length() > 0) {
                    final TrackerEntry entry = new TrackerEntry(type, policy,
                            JidCreate.from(address), protocol);
                    if (verified != null && verified.equals("true")) {
                        entry.setVerified(true);
                    }
                    iq.addEntry(entry);
                }
            }
            else if (eventType == XmlPullParser.END_TAG) {
                if (elementName.equals(JingleTrackerIQ.NAME)) {
                    done = true;
                }
            }
            if (!done) {
                parser.next();
            }
        }
        return iq;
    }
}
