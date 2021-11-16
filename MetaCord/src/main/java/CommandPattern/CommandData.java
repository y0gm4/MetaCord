package CommandPattern;

import Exceptions.HelpNotFoundException;
import Exceptions.NotACommandException;
import lombok.SneakyThrows;

public class CommandData<C extends CommandListener> {

    private C c;

    private Command command;

    private Help help;

    public CommandData(C c){
        this.c = c;
        command = c.getClass().getAnnotation(Command.class);
        help = CommandEvent.helpMap.get(command);

        if(!c.getClass().isAnnotationPresent(Command.class)){
            try {
                throw new NotACommandException("Error, the generic parameter is not a command! try to annotate the class.");
            } catch (NotACommandException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCommand(CommandType type){
        switch(type){
            case COMMAND:
                return command.prefix() + command.name();

            case COMMAND_NAME:
                return command.name();

            default:
                return null;
        }
    }

    public int getArguments(){
        return command.args();
    }

    public String[] getAliases(){
        return command.aliases();
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
