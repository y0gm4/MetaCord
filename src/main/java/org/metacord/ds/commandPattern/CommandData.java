package org.metacord.ds.commandPattern;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.javacord.api.event.message.MessageCreateEvent;
import org.metacord.ds.exceptions.HelpNotFoundException;
import org.metacord.ds.exceptions.NotCommandException;

import java.util.Optional;

@ToString
public class CommandData<C extends CommandListener>{

    private final C c;

    private Help help;

    private Command command;

    @Getter
    private final MessageCreateEvent event;

    @Getter
    private final String name;

    @Getter
    private final char prefix;

    @Getter
    private final Optional<String[]> args;

    @Getter
    private final String[] aliases;


    @SneakyThrows
    CommandData(C c, MessageCreateEvent event) {
        this.c = c;
        command = c.getClass().getAnnotation(Command.class);
        help = CommandEvent.helpMap.get(command);

        if(!c.getClass().isAnnotationPresent(Command.class))
            throw new NotCommandException("Error, the raw type is not a command! try to annotate the class or extend the \"CommandListener\" parent class.");


        this.event = event;
        this.args = event.getMessageContent().split(" ").length == 1 ? Optional.empty() :
                Optional.of(event.getMessageContent().split(" "));
        this.name = command.name();
        this.prefix = command.prefix();
        this.aliases = command.aliases();

    }

    @SneakyThrows
    public String helpDescription(){
        if(help == null) throw new HelpNotFoundException("There is no \"help\" method!");
        return help.description();
    }

    @SneakyThrows
    public String executeOnCall(){
        if(help == null) throw new HelpNotFoundException("There is no \"help\" method!");
        return help.executeOnCall();
    }
}
