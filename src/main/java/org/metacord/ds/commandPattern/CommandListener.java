package org.metacord.ds.commandPattern;

import java.util.Map;
import java.util.TreeMap;

public abstract class CommandListener {

    protected static Map<String, String> helpDescriptions = new TreeMap<>();

    public abstract void onCommand(CommandData<? extends CommandListener> command);

    public void help(CommandData<? extends CommandListener> command){ /* Help method, Override it and annotate with @help */ }

}
