package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Injector
 */
public final class JavaAttribute
	extends JavaFeature
	implements TokenConsumer
{

	public JavaAttribute(
		JavaClass parent,
		int modifiers,
		String type,
		String name)
		throws InjectorParseException
	{
		// parent must not be null
		super(parent.file, parent, modifiers, type, name);
		if (type == null)
			throw new RuntimeException();
	}

	/**
	 * Constructs a java attribute with the same
	 * <code>parent</code>, <code>modifiers</code> and <code>type</code>
	 * but the given name.
	 * Needed for comma separated attributes.
	 */
	public JavaAttribute(JavaAttribute ja, String name)
		throws InjectorParseException
	{
		this(ja.parent, ja.modifier, ja.type, name);
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
		return file.getPackageName()
			+ '.'
			+ parent.name
			+ '#'
			+ name;
	}

	private String camelCaseName = null;

	public final String getCamelCaseName()
	{
		if (camelCaseName != null)
			return camelCaseName;

		final String name = this.name;
		final char first = name.charAt(0);
		if (Character.isUpperCase(first))
			camelCaseName = name;
		else
			camelCaseName = Character.toUpperCase(first) + name.substring(1);

		return camelCaseName;
	}

	/**
	 * See Java Specification 8.3.1 &quot;Field Modifiers&quot;
	 */
	public final int getAllowedModifiers()
	{
		return Modifier.PUBLIC
			| Modifier.PROTECTED
			| Modifier.PRIVATE
			| Modifier.FINAL
			| Modifier.STATIC
			| Modifier.TRANSIENT
			| Modifier.VOLATILE;
	}
	
	private final ArrayList initializerArguments = new ArrayList();
	private final StringBuffer currentArgument = new StringBuffer();
	private int bracketLevel = 0;
	
	public void addToken(final char token)
	{
		switch(token)
		{
			case '(':
			{
				bracketLevel++;
				break;
			}
			case ')':
			{
				if(bracketLevel==1)
				{
					initializerArguments.add(currentArgument.toString());
					currentArgument.setLength(0);
				}
				bracketLevel--;
				break;
			}
			case ' ':
			case '\t':
			case '\n':
			case '\r':
				break;
			case ',':
			{
				if(bracketLevel==1)
				{
					initializerArguments.add(currentArgument.toString());
					currentArgument.setLength(0);
					break;
				}
			}
			default:
			{
				if(bracketLevel==1)
				{
					currentArgument.append(token);
				}
				break;
			}
		}
	}
	
	List getInitializerArguments()
	{
		return initializerArguments;
	}

}
