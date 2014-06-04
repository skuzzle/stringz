[![Build Status](https://travis-ci.org/skuzzle/stringz.svg?branch=master)](https://travis-ci.org/skuzzle/stringz)

stringz
=====
Make externalized Strings as easy accessible as inline Strings!

stringz makes it easy to access externalized strings which are stored in default
Java Resource Bundles. It provides some additional features like properties file
inheritance to include translation keys from other resource bundles and 
references to other keys within the same file.



## License
stringz is distributed under the MIT License. See `LICENSE.md` in this directory
for detailed information.

## Documentation
JavaDoc is available at www.skuzzle.de/stringz/1.0.0/doc

Scroll down in this readme for a quick start guide and some advanced topics.

Further support can be found in IRC (irc.euirc.net, #pollyisawesome) or via
Twitter (@ProjectPolly).



## Building
Building stringz requires Apache Maven to be installed. You may then run 
`mvn install` to build stringz and install it into your local repository. If you 
want to add stringz to your existing projects, declare the following dependency 
within your `pom.xml`:

```xml
<dependency>
    <groupId>de.skuzzle</groupId>
    <artifactId>stringz</artifactId>
    <version>0.1.0-rc1</version>
    <scope>build</scope>
</dependency>
```



## Basic Usage
Declare a class marked with `@ResourceMapping` with public static String fields 
whose names conform to entries of the resource bundle which contains the 
translation.

```java
@ResourceMapping
public class MSG {
    static {
        Stringz.init(MSG.class);
    }
    
    public static String next;
    public static String back;
    public static String cancel;
```

The following is a properties file named `MSG.properties` contained in the same 
package as the `MSG` class above (as by Stringz default look up procedure, which 
can be customized if needed):

```
next = Next
back = Back
cancel = Cancel
```

This is a properties file named `MSG_De.properties` within the same package:

```
next = Weiter
back = Zurück
cancel = Abbrechen
```

Calling `Stringz.init(Class)` will initialize all public static String fields 
with values read from a resource bundle using either a globally specified locale
or a locale which can optionally be passed to the init method.

You can now easily access the externalized Strings:

```java
public class MyWizard {

    public MyWizard() {
        createNextButton(MSG.next);
        createBackButton(MSG.back);
        createCancelButton(MSG.cancel);
    }
    
    // ...
}
```