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
JavaDoc is available at www.stringz.skuzzle.de/0.2.0/doc/

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
    <version>0.2.0</version>
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
}
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

# Advanced Usage

## ResourceBundles
This section describes how Java `ResourceBundles` are referenced from within
Stringz and which additional features it offers.

### Look Up
ResourceBundles in Java should follow a specific naming scheme. This scheme
consists of a so called _base name_ or _family name_ and an optional locale
specific postfix. For details about ResourceBundles in Java please refer to the
[Sun Online Trail](http://docs.oracle.com/javase/tutorial/i18n/resbundle/index.html).

So ResourceBundle look up in Stringz is all about specifying the base name of
the resource. All other magic is handled by the
`ResourceBundle.getResource(...)` method.

By default, Stringz uses the full qualified name of the class annotated with
`@ResourceMapping` as base name for resource look up. If your class's name is
`com.your.domain.MSG`, then Stringz will use that String as base name to find
resources named `MSG` within the same java package. So you would place a
`MSG.properties` into that package and additionally further files like
`MSG_de_De.properties` etc.

You can also explicitly specify a base name for a message class. The preferred
way is to put it into the `@ResourceMapping` annotation:

```java
@ResourceMapping("com.your.domain.SomeResourceName")
public class MSG {
    // ...
}
```

If you need some weird dynamic logic for specifying a bundle's base name, you
can use a `BundleFamilyLocator`. Please refer to the
[documentation](http://www.skuzzle.de/stringz/1.0.0/doc/de/skuzzle/stringz/Stringz.html "JavaDoc for the Stringz class")
of the Stringz class.

### Control Customization
`Control` instances are responsible for materializing a `ResourceBundle`
instance from a base name and locale. Thus they implement the actual logic of
how a resource is resolved. Stringz uses a modification of Java's default
Control for loading property resources. The modification allows the user to
specify an optional charset which should be used when reading the property file.

If for any reason this does not suit your needs, you can specify a custom
Control to be used for retrieving the ResourceBundle of a certain message class.
The first step to do so is, to implement a `ControlFactory` which is responsible
for creating an instance of your Control implementation:

```java
public class MyControlFactory implements ControlFactory {
    @Override
    public Control create(ResourceMapping mapping, String[] args) {
        // process optional arguments and create a Control instance
        if (args.length != 2) {
            throw new ControlFactoryException("Wrong argument count");
        }
        return new MyControl(args[0], args[1]);
    }
}
```

The second step is to advise Stringz to use that factory for a certain message
class by using the `@ResourceControl` annotation:

```java
@ResourceMapping
@ResourceControl(value = MyControlFactory.class, args = {"string", "arguments"})
public class MSG {
    // ...
}
```

### Extended ResourceBundle Features
While Stringz uses normal Java ResourceBundles it brings some extra features
for dealing with entries of a properties file. That is, _inclusion_ and
_key references_.

#### Property File Inclusion
Your property file which stores the resource strings, can specify a property with
the special key `@include`. The value is interpreted as a semicolon separated
list of resource bundle base names which should be included into the current
property file. All included values are then available for being mapped to a
field or to be referenced by other properties (see _key references_ below). If
an included resource bundle contains a key which is also present in the current
bundle, the current bundle takes precedence. Beyond this, the included bundles
are searched in the order in which they were included.

#### Key References
From a resource string you can reference another resource string to be inserted
at a certain point. This allows you to specify often used string parts
atomically and then build the full string by referencing those parts.

```
userName = username
password = password
promptLogin = Please specify your ${userName} and ${password}
forgotPassword = Forgot your ${password}?
```
You can use references to any key from the same file and to keys from included
files (see _Property File Inclusion_ above). Key references are also resolved
recursively. That is, if you reference a key which value references another key,
that nested reference is resolved first.

# Fieldmapping
This section describes the behavior applied to assign resource values to fields
of a class.


## Customize Resource Keys
By default, Stringz uses the name of the variable as key to find its resource.
You can also specify an explicit key for a variable using `@ResourceKey`:

```java
    @ResourceKey("someOtherKey")
    public static String myResource;
```

The variable `myResource` will get the value stored at the key `someOtherKey`
assigned.

### Ignore Fields
If for any resason your message class contains a variable that should not be
mapped to a resource value, you can mark it with `@NoResource`:

```java
    @NoResource
    public static String myResource;
```

Otherwise if Stringz tries to map the field and there exists no value for its
key, it will throw a `MissingResourceException` upon initialization.

### Mapping Array Fields
Stringz will also attempt to automatically map values to String[] variables
within your message class. Hence, it uses the semicolon as default delimiter to
split the referenced resource. For example, your resource could look like this:

```
myResource = First column;Second column;Third column
```

and within your message class you would simply declare a String array with the
resource's name:

```java
public static String[] myResource;
```

The `@ResourceKey` and `@NoResource` annotations are used on String arrays in
the same way they are used for normal Strings.

You can also define a custom delimiter pattern if the semicolon delimiter does
not suit your needs using the `@Delimiter` annotation:
```java
@Delimiter("\\s+")
public static String[] myResource;
```

This will split the resource value at any sequence of whitespaces.

You may also instruct Stringz to create an array of arbitrary other resources
referenced by their keys using `@ResourceCollection`. Consider the following
resource file:

```
first = First column
second = Second column
third = Third column
```

You can let Stringz create an array out of theses resources:

```java
@ResourceCollection({"first", "second", "third"})
public static String[] myResource;
```
