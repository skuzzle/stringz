/**
 * Contains interfaces to customize the way in which 
 * {@link de.skuzzle.stringz.Stringz Stringz} looks up 
 * {@link java.util.ResourceBundle ResourceBundles} and how it maps fields to resources.
 * Except for the {@link de.skuzzle.stringz.strategy.BundleFamilyLocator BundleFamilyLocator},
 * the general pattern of customization is:
 * <ul>
 *  <li>Create an implementation of the desired strategy, e.g. implement 
 *      {@link de.skuzzle.stringz.strategy.FieldMapper FieldMapper} to specify a custom
 *      field mapping behavior. 
 *  <li>Create a <em>YourStrategy</em>Configurator implementation with a no arguments 
 *      public constructor. Make its <tt>configureXX</tt> method return an instance of 
 *      your strategy implementation.</li>
 *  <li>Mark your message class with the respective annotation to specify the 
 *      configurator. In case of the 
 *      {@link de.skuzzle.stringz.strategy.FieldMapperConfigurator FieldMapperConfigurator},
 *      use the {@link de.skuzzle.stringz.annotation.FieldMapping FieldMapping} annotation
 *      to specify the Class of your configurator.
 *      </li>
 * </ul>
 * 
 * <p>This shows the available strategies and their corresponding configurators and 
 * annotations:</p>
 * <table summary="Available strategies">
 * <tr>
 *     <th>Strategy</th><th>Configurator</th><th>Annotation</th>
 * </tr>
 * <tr>
 *     <td>{@link de.skuzzle.stringz.strategy.FieldMapper FieldMapper}</td>
 *     <td>{@link de.skuzzle.stringz.strategy.FieldMapperConfigurator FieldMapperConfigurator}</td>
 *     <td>{@link de.skuzzle.stringz.annotation.FieldMapping &#64;FieldMapping}</td>
 * </tr>
 * <tr>
 *     <td>{@link java.util.ResourceBundle.Control Control}</td>
 *     <td>{@link de.skuzzle.stringz.strategy.ControlConfigurator ControlConfigurator}</td>
 *     <td>{@link de.skuzzle.stringz.annotation.ResourceControl &#64;ResourceControl}</td>
 * </tr>
 * <tr>
 *     <td>{@link de.skuzzle.stringz.strategy.BundleFamilyLocator BundleFamilyLocator}</td>
 *     <td style="text-align:center">-</td>
 *     <td>{@link de.skuzzle.stringz.annotation.FamilyLocator &#64;FamilyLocator}</td>
 * </tr>
 * </table>
 * 
 * <h2>Strategy Usage of Stringz</h2>
 * <p>The {@link de.skuzzle.stringz.Stringz Stringz} class makes use of all these 
 * strategies when initializing a message class. The exact way in which the strategies
 * are used is defined using a {@link de.skuzzle.stringz.strategy.Strategies Strategies}
 * instance. E.g. the default behavior is to cache all created configurator classes. If
 * you do not like the caching behavior, you could 
 * {@link de.skuzzle.stringz.Stringz#setStrategies(Strategies) switch} the strategy
 * to e.g. {@link de.skuzzle.stringz.SimpleStrategies SimpleStrategies} or an own 
 * implementation.
 * 
 * <h2>Sample Strategy implementation</h2>
 * <p>Below follows a typical example strategy definition for a field mapper. Start with 
 * the <tt>FieldMapper</tt> implementation:</p>
 * <pre>
 * public class PatternFieldMapper extends DefaultFieldMapper {
 * 
 *     private final Pattern pattern;
 *     
 *     public PatternFieldMapper(Pattern pattern) {
 *         this.pattern = pattern;
 *     }
 * 
 *     &#64;Override
 *     public boolean accept(Field field) {
 *         return super.accept(field) &amp;&amp; this.pattern.matcher(field.getName()).matches();
 *     }
 * }
 * </pre>
 * 
 * Create the configurator:
 * 
 * <pre>
 * public class PatternFieldMapperConfigurator implements FieldMapperConfigurator {
 * 
 *     &#64;Override
 *     public FieldMapper configure(ResourceMapping mapping, String[] args) {
 *         if (args.length != 1) {
 *             throw new FieldMappingException("Missing pattern argument");
 *         }
 *         try {
 *             final Pattern pattern = Pattern.compile(args[0]);
 *             return new PatternFieldMapper(pattern);
 *         } catch (PatternSyntaxException e) {
 *             throw new FieldMappingException(String.format(
 *                 "Illegal pattern: %s", args[0]), e);
 *         }
 *     }
 * }
 * </pre>
 * 
 * Now mark your message class to use the new configurator:
 * 
 * <pre>
 * &#64;ResourceMapping
 * &#64;FieldMapping(value = PatternFieldMapperConfigurator.class, args = { "prefix.*" })
 * public class MSG {
 *     static {
 *         Stringz.init(MSG.class);
 *     }
 *     
 *     // ...
 * }
 * </pre>
 */
package de.skuzzle.stringz.strategy;