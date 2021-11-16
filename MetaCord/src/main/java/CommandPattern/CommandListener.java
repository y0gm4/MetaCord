package CommandPattern;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class CommandListener {

    public abstract void onCommand(MessageCreateEvent event);

    public void help(MessageCreateEvent event){ /* Help method, Override it and annotate with @help */ }

}
