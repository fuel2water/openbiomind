When OpenBioMind is executed with a given task, it often looks for _pipeline.properties_ in its specified classpath to load the user-specified properties about a OpenBioMind project. However, the user should take note that the syntax for setting the classpath for OpenBioMind varies from one operating system to another. If classpath is not set properly, OpenBioMind will not be able to find _pipeline.properties_ and terminate with the fatal error.
```
Exception in thread "main" java.lang.NullPointerException
        at java.util.Properties$LineReader.readLine(Unknown Source)
        at java.util.Properties.load(Unknown Source)
        at task.EnhanceDataset.main(EnhanceDataset.java:98)
```
To troubleshoot this error, attached below is the appropriate syntax to run OpenBioMind jar and include the correct path to _pipeline.properties_.

# Running OpenBioMind on Linux and Mac OS X #

The syntax for executing the OpenBioMind java jar is

```
java -cp .<classpath to pipeline.properties>:task.<taskName> <args>
```

Or if, _pipeline.properties_ is located in the current directory you are in, according to the commandline, then you can alternatively execute the jar by:

```
java -cp .:task.<taskName> <args>
```


# Running OpenBioMind on Windows #

The syntax for executing the OpenBioMind java jar is

```
java -cp .<classpath to pipeline.properties>;task.<taskName> <args>
```

Or if, _pipeline.properties_ is located in the current directory you are in, according to the commandline, then you can alternatively execute the jar by:

```
java -cp .;task.<taskName> <args>
```

Notice the difference of a semicolon (_;_) on Windows to set the classpath versus the period(_._) on Linux to do so.

Also, given the various distributions of Windows and how classpath (considered an environment variable by Windows) is updated either instantaneously or only after a system-restart, you may still get the same fatal error of _pipeline.properties_ not found. In this case, one should go to the Windows Desktop, right-click _My Computer_, select _System Properties_, and then the _Advanced_ tab. From there, one should select _Environment Variables_ and then append to the _CLASSPATH_ string listed under System variables to include either the specific path to your _pipeline.properties_ or append ".;" to instruct Java to always include the current working directory in any jar classpath. You should restart your system for the change in this environment variable to take effect.