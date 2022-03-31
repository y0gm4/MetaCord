# MetaCord 
[![](https://jitpack.io/v/CarbonCock/MetaCord.svg)](https://jitpack.io/#CarbonCock/MetaCord)

<img src="https://i.imgur.com/SBZcrMl.png" width="150" align="right">

***

> Command library interfaced with JavaCord!

**MetaCord** (`Annotations` for discord _command_) is a library, working only with javacord, that helps to create _discord_ **commands** in a faster and more precise way!

## Features

   - An [`@Command`](#Command-class) annotation to define the command.
   - An [`@Help`](#Help-annotation) annotation to define the help.
   - An [`CommandData`](#CommandData) class to compact all the info of the command.
   - A **map** that associates _command_ and _description_ (set via @Help annotation).

## MetaCord Dependencies
***
> For now it only works with maven!

### Maven

```xml
        <repositories>
	 	<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
	<dependency>
	    <groupId>com.github.CarbonCock</groupId>
	    <artifactId>MetaCord</artifactId>
	    <version>3.0</version>
	</dependency>
```

# How to use
***
### Command class
```java
@Command(name = "hello", prefix = '$', aliases = {"hi", "hey"}) 
//by default args are 0
public class HelloExample extends CommandListener{
    
    @Override
    public void onCommand(CommandData<? extends CommandListener> command){
        new MessageBuilder().setContent("Hello world").send(command.getEvent().getChannel());
        // When someone writes "$hello", "$hi" or "$hey" will execute this method
    }
    
    @Help
    @Override
    public void help(CommandData<? extends CommandListener> command){
        new MessageBuilder().setContent("This is a help message!").send(command.getEvent().getChannel());
        // When someone writes "$help hello" execute this method
    }
}
```

### Help annotation

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Help {
    //executes the annotated method when a user submits this string
    String executeOnCall() default "";
    
    //it is the description of the command that is saved in a HashMap accessible via this.helpDescriptions
    String description() default ""; 
}
```

### Main class
> The functionality of _MetaCord_ is that you can **register** either a single class at a time or **register all the classes in a certain package**

#### Register commands

```java
public class MyBot{

    public static void main(String...){
        final String token = "your_token";
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        //register only one class
        api.addListener(new CommandEvent(new HelloExample()));
        
        //register all classes of a package
        final String packagePath = "commands"; //package path
        api.addListener(new CommandEvent(packagePath));
    }
}
```

## Unchecked prefix
***

> **We can also avoid prefix control**, suppose we have a database where we save the preferences of each server and among them the `prefix` of commands... we can't change it from the annotation since it is read during **RunTime**.

To do this, simply put a `'0'` (zero) as a character! This will require an extra **check** at the _start_ of the _method_.

```java
@Command(name = "hello", prefix = '0', aliases = {"hi", "hey"}) 
public class HelloExample extends CommandListener{
    
    @Override
    public void onCommand(CommandData<? extends CommandListener> command){
        // We call the prefix "(?)" to mean that it can be replaced by any character during this reading
        final char usage = db.getUsagePreference(command.getEvent().getServer());
        final String content = command.getEvent().getMessageContent();
        
        if(!content.startWith("%shello".formatted(usage))) return; // The famous check
        
        new MessageBuilder().setContent("Hello world").send(command.getEvent().getChannel());
        // When someone writes "(?)hello", "(?)hi" or "(?)hey" will execute this method
    }
    
    @Help
    @Override
    public void help(CommandData<? extends CommandListener> command){
        new MessageBuilder().setContent("This is a help message!").send(command.getEvent().getChannel());
        // When someone writes "(?)help hello" execute this method
    }
}
```

## Unchecked arguments
***
> We can also avoid argument checking, suppose we have a command that takes a variable number of arguments... we can't change the number from the annotation since it is read during RunTime.

To do this, simply put `-1` as the number of _arguments_! 

```java
@Command(name = "ban", prefix = '$', args = -1)
public class BanExample extends CommandListener{
    
    @Override
    public void onCommand(CommandData<? extends CommandListener> command){
        User user = command.getEvent().getMessage().getUserAuthor().get();
        final String[] args = command.getArgs().get();

        if(args.length == 1 || args.length > 3) return; // Error message...
	
	command.getEvent().getServer().ifPresent(server -> server.banUser(command.getEvent().getApi().getUserById(args[1])));
        
        new MessageBuilder().setContent("%s was banned %s"
                .formatted(user.getName(), args.length == 3 ?
                        "for \"" + args[2] + "\"" : "")
        ).send(command.getEvent().getChannel());
    }
}
```

# CommandData
***
> To interface with the command and its information, we can use the CommandData class that is passed directly as a parameter to the `onCommand()` and `onHelp()` methods.

```java
@Command(name = "hello", prefix = '$', aliases = {"hi", "hey"}) 
public class HelloExample extends CommandListener{
    
    @Override
    public void onCommand(CommandData<? extends CommandListener> command){
        new MessageBuilder.setContent("description: %s".formatted(command.helpDescription()))
        // For example we can take the description of the command
    }
    
    @Help(description = "Simple description of what the command does . . .")
    @Override
    public void help(CommandData<? extends CommandListener> command){}
}

```
