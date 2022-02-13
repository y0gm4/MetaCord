package org.metacord.ds.commandPattern;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Map;
import java.util.TreeMap;

public abstract class CommandListener {

    protected static Map<String, String> helpDescriptions = new TreeMap<>();

    public abstract void onCommand(MessageCreateEvent event);

    public void help(MessageCreateEvent event){ /* Help method, Override it and annotate with @help */ }

}
