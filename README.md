Stringz
=====

Stringz makes it easy to access externalized strings which are stored in default
Java Resource Bundles.

```java
public class MSG implements Messages {
    static {
        Stringz.init(MSG.class);
    }
    
    public static String next;
    public static String back;
    public static String cancel;
```

The following is a properties file named 'MSG.properties' contained in the same 
package as the `MSG` class above (as by Stringz default look up procedure, which 
can be customized if needed):

```
next = Next
back = Back
cancel = Cancel
```

This is a properties file named 'MSG_De.properties' within the same package:

```
next = Weiter
back = Zurück
cancel = Abbrechen
```

Calling `Stringz.init(Class)` will initialize all public static String fields 
with values read from a resource bundle using either a globally specified locale
or a locale which can optionally be passed to the init method using a 
`StringsConfiguration` instance.

You can now use the externalized Strings:

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