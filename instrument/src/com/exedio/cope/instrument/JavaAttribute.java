
package injection;

import java.lang.reflect.Modifier;

/**
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Injector
 */
public final class JavaAttribute extends JavaFeature
{
	public JavaAttribute(JavaClass parent, int modifiers, String type, String name)
	throws InjectorParseException
	{
		// parent must not be null
		super(parent.getFile(), parent, modifiers, type, name);
		if(type==null)
			throw new RuntimeException();
	}
	
	/**
	 * Constructs a java attribute with the same
	 * {@link #parent}, {@link #modifiers} and {@link #type}
	 * but the given name.
	 * Needed for comma separated attributes.
	 */
	public JavaAttribute(JavaAttribute ja, String name)
	throws InjectorParseException
	{
		this(ja.getParent(), ja.getModifiers(), ja.type, name);
	}
	
	/**
	 * Return a fully qualified name of the attribute,
	 * including class and package path.
	 * Syntax follows the javadoc tags,
	 * with a '#' between class and attribute name.
	 * Is used for type tracing log files.
	 */
	public final String getFullDocName()
	{
		return getFile().getPackageName()+'.'+getParent().getName()+'#'+getName();
	}
	
	public final String getCamelCaseName()
	{
		final String name = getName();
		final char first = name.charAt(0);
		if(Character.isUpperCase(first))
			return name;
		else
			return Character.toUpperCase(first) + name.substring(1);
	}
	
	/**
	 * See Java Specification 8.3.1 &quot;Field Modifiers&quot;
	 */
	public final int getAllowedModifiers()
	{
		return
		Modifier.PUBLIC |
		Modifier.PROTECTED |
		Modifier.PRIVATE |
		Modifier.FINAL |
		Modifier.STATIC |
		Modifier.TRANSIENT |
		Modifier.VOLATILE;
	}
	
	public final int getMethodModifiers()
	{
		return
		getModifiers() &
		(
		Modifier.PUBLIC |
		Modifier.PROTECTED |
		Modifier.PRIVATE
		) |
		Modifier.FINAL;
	}
	
	public final void makePersistent()
	{
		getParent().addPersistentAttribute(this);
	}
	
}
