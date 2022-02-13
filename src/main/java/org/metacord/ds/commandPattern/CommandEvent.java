package org.metacord.ds.commandPattern;

import org.metacord.ds.exceptions.CommandAnnotationNotFoundException;
import lombok.SneakyThrows;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class CommandEvent implements MessageCreateListener {

    private final Map<String, Class<? extends CommandListener>> commands = new HashMap<>();

    private final Map<String, Class<? extends CommandListener>> helpCommands = new HashMap<>();

    protected static final Map<Command, Help> helpMap = new HashMap<>();



    private String packagePath = "";

    public CommandEvent(String packagePath){

        this.packagePath = packagePath;

        for(Class<? extends  CommandListener> c : getListOfSonClass()){
            if(!c.isAnnotationPresent(Command.class)) continue;
            Command a = c.getAnnotation(Command.class);
            String command = a.prefix() + a.name();
            commands.put(command, c);
            for(String alias : a.aliases()) if(!alias.equals("")) commands.put(a.prefix() + alias, c);
            for(Method m : c.getMethods()){
                if(!m.isAnnotationPresent(Help.class)) continue;
                final Help help = m.getAnnotation(Help.class);
                String helpCommand = help.executeOnCall().equals("") ?
                        a.prefix() + "help " + a.name() :
                        help.executeOnCall();
                helpCommands.put(helpCommand, c);
                helpMap.put(a, help);
                CommandListener.helpDescriptions.put(command.replace("" + a.prefix(), ""), help.description());
            }
        }
    }

    @SneakyThrows
    public CommandEvent(CommandListener clazz){
        Class<? extends CommandListener> c = clazz.getClass();
        if(!c.isAnnotationPresent(Command.class)) throw new CommandAnnotationNotFoundException("Annotation not found, annotate the class!");
        Command a = c.getAnnotation(Command.class);
        String command = a.prefix() + a.name();
        commands.put(command, c);
        for(String alias : a.aliases()) if(!alias.equals("")) commands.put(a.prefix() + alias, c);
        for(Method m : c.getMethods()){
            if(!m.isAnnotationPresent(Help.class)) continue;
            final Help help = m.getAnnotation(Help.class);
            String helpCommand = help.executeOnCall().equals("") ?
                    a.prefix() + "help " + a.name() :
                    help.executeOnCall();
            helpCommands.put(helpCommand, c);
            helpMap.put(a, help);
            CommandListener.helpDescriptions.put(command.replace("" + a.prefix(), ""), help.description());
        }
    }

    @SneakyThrows
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String command = event.getMessageContent();

        // if the usage is equal to 0 it means that you have chosen that the usage is not checked
        try {
            if(containsKeyIgnoringCase(helpCommands, command.replace(command.substring(0, 1), "0"))){
                command = command.replace(command.substring(0, 1), "0");
                Class<?> c = helpCommands.get(command);
                Method m = c.getMethod("help", MessageCreateEvent.class);
                m.invoke(c.getDeclaredConstructor().newInstance(), event);
                return;
            }
        }catch(StringIndexOutOfBoundsException ignored){}


        if(containsKeyIgnoringCase(helpCommands, command)){
            Class<?> c = helpCommands.get(command);
            Method m = c.getMethod("help", MessageCreateEvent.class);
            m.invoke(c.getDeclaredConstructor().newInstance(), event);
            return;
        }

        String headCommand =
                command.contains(" ") ? command.substring(0, command.indexOf(" ")).toLowerCase(Locale.ROOT)
                        : command.toLowerCase(Locale.ROOT);

        // if the usage is equal to 0 it means that you have chosen that the usage is not checked
        try {
            if(containsKeyIgnoringCase(commands, headCommand.replace(headCommand.substring(0, 1), "0"))){
                headCommand = headCommand.replace(headCommand.substring(0, 1), "0");
                if(commands.get(headCommand).getAnnotation(Command.class).args() != -1)
                    if(commands.get(headCommand).getAnnotation(Command.class).args() != getCommandArgs(command)) return;
                Class<?> c = commands.get(headCommand);
                Method m = c.getMethod("onCommand", MessageCreateEvent.class);
                m.invoke(c.getDeclaredConstructor().newInstance(), event);
                return;
            }
        }catch(StringIndexOutOfBoundsException ignored){}

        if(!containsKeyIgnoringCase(commands, headCommand)) return;
        if(commands.get(headCommand).getAnnotation(Command.class).args() != -1)
            if(commands.get(headCommand).getAnnotation(Command.class).args() != getCommandArgs(command)) return;

        Class<?> c = commands.get(headCommand);
        Method m = c.getMethod("onCommand", MessageCreateEvent.class);
        m.invoke(c.getDeclaredConstructor().newInstance(), event);
    }

    private Set<Class<? extends CommandListener>> getListOfSonClass(){
        return new Reflections(packagePath).getSubTypesOf(CommandListener.class);
    }

    private boolean containsKeyIgnoringCase(Map<String, Class<? extends CommandListener>> map, String command){
        for(Map.Entry<String, Class<? extends CommandListener>> entry : map.entrySet()){
            if(command.equalsIgnoreCase(entry.getKey())) return true;
        }
        return false;
    }

    private int getCommandArgs(String command){
        return command.length() - command.replace(" ", "").length();
    }
}
