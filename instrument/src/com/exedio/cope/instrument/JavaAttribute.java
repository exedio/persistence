
package injection;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import persistence.UniqueViolationException;

/**
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Injector
 */
public final class JavaAttribute extends JavaFeature
{
	private String persistentType = null;
	private boolean unique = false;
	private boolean readOnly = false;

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
	 * Returns the persistent type of this attribute.
	 */
	public String getPersistentType()
	{
		return this.persistentType;
	}
	
	public boolean isUnique()
	{
		return unique;
	}
	
	public boolean isReadOnly()
	{
		return readOnly;
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
	
	private String camelCaseName = null;
	
	public final String getCamelCaseName()
	{
		if(camelCaseName!=null)
			return camelCaseName;

		final String name = getName();
		final char first = name.charAt(0);
		if(Character.isUpperCase(first))
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
	
	public final void makePersistent(final String persistentType)
	{
		if(persistentType==null)
			throw new RuntimeException("Du Sau!");
		if(this.persistentType!=null)
			throw new RuntimeException("Du Schwein!");
		getParent().addPersistentAttribute(this);
		this.persistentType = persistentType;
	}

	public final void makeUnique()
	{
		unique = true;
	}

	public final void makeReadOnly()
	{
		readOnly = true;
	}

	private SortedSet setterExceptions = null;

	public final SortedSet getSetterExceptions()
	{
		if(setterExceptions!=null)
			return setterExceptions;
		
		final TreeSet modifyableSetterExceptions = new TreeSet();
		
		if(unique)
			modifyableSetterExceptions.add(UniqueViolationException.class);

		this.setterExceptions = Collections.unmodifiableSortedSet(modifyableSetterExceptions);
		return this.setterExceptions;
	}

}
