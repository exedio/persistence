
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ClassComparator;

/**
 * Represents an attribute of a class parsed by the
 * java parser.
 * Contains additional information about this attribute
 * described in the doccomment of this attribute.
 * @see Injector
 */
public final class JavaAttribute extends JavaFeature
{
	/**
	 * Defines this attribute as a media attribute.
	 * The dash prevents this name to be used as a java identifier.
	 * @see #getPersistentType()
	 */
	public static final String MEDIA_TYPE = "Media-";


	private String persistentType = null;
	private boolean readOnly = false;
	private boolean notNull = false;
	private boolean mapped = false;
	private List qualifiers = null;
	private List mediaVariants = null;
	private String mimeMajor = null;
	private String mimeMinor = null;
	private List enumerationValues = null;

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
	
	public boolean isMediaPersistentType()
	{
		return this.persistentType.equals(MEDIA_TYPE);
	}

	public boolean isEnumerationAttribute()
	{
		return this.enumerationValues != null;
	}

	private static final HashMap toNativeTypeMapping = new HashMap(3);
	private static final HashMap toBoxingPrefixMapping = new HashMap(3);
	private static final HashMap toBoxingPostfixMapping = new HashMap(3);
	private static final HashMap toUnboxingPrefixMapping = new HashMap(3);
	private static final HashMap toUnboxingPostfixMapping = new HashMap(3);
	
	private static final void fillNativeTypeMap(final String persistentType, final String nativeType,
															  final String boxingPrefix, final String boxingPostfix,
															  final String unboxingPrefix, final String unboxingPostfix)
	{
		toNativeTypeMapping.put(persistentType, nativeType);
		toBoxingPrefixMapping.put(persistentType, boxingPrefix);
		toBoxingPostfixMapping.put(persistentType, boxingPostfix);
		toUnboxingPrefixMapping.put(persistentType, unboxingPrefix);
		toUnboxingPostfixMapping.put(persistentType, unboxingPostfix);
	}
	
	static
	{
		fillNativeTypeMap("Boolean", "boolean", "(",            "?Boolean.TRUE:Boolean.TRUE)", "(", ").booleanValue()");
		fillNativeTypeMap("Integer", "int",     "new Integer(", ")",                           "(", ").intValue()");
	}


	private String boxedType = null;
	private boolean boxed;
	private String boxingPrefix;
	private String boxingPostfix;
	private String unboxingPrefix;
	private String unboxingPostfix;

	private final String makeBoxedTypeAndFlag()
	{
		if(boxedType!=null)
			return boxedType;

		final String accessorType;
		if(notNull)
		{
			final String nativeType = (String)toNativeTypeMapping.get(persistentType);
			if(nativeType!=null)
			{
				boxedType = nativeType;
				boxed = true;
				boxingPrefix = (String)toBoxingPrefixMapping.get(persistentType);
				boxingPostfix = (String)toBoxingPostfixMapping.get(persistentType);
				unboxingPrefix = (String)toUnboxingPrefixMapping.get(persistentType);
				unboxingPostfix = (String)toUnboxingPostfixMapping.get(persistentType);
			}
			else
			{
				boxedType = persistentType;
				boxed = false;
			}
		}
		else
			boxedType = persistentType;
		
		this.boxedType = boxedType;
		return boxedType;
	}

	/**
	 * Returns the type of this attribute to be used in accessor (setter/getter) methods.
	 * Differs from {@link #getPersistentType() the persistent type},
	 * if and only if the attribute is {@link #isBoxed() boxed}.
	 */
	public final String getBoxedType()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return boxedType;
	}
	
	/**
	 * Returns, whether the persistent type is &quot;boxed&quot; into a native type.
	 * This happens if the attribute has a not-null constraint 
	 * and the persistent type is convertable to a native types (int, double, boolean).
	 * @see #getBoxedType()
	 */
	public final boolean isBoxed()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return boxed;
	}
	
	public final String getBoxingPrefix()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return boxingPrefix;
	}
	
	public final String getBoxingPostfix()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return boxingPostfix;
	}
	
	public final String getUnBoxingPrefix()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return unboxingPrefix;
	}
	
	public final String getUnBoxingPostfix()
	{
		if(boxedType==null)
			makeBoxedTypeAndFlag();
		
		return unboxingPostfix;
	}
	
	public boolean isPartOfUniqueConstraint()
	{
		for( final Iterator i=getParent().getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final JavaAttribute[] uniqueConstraint = (JavaAttribute[])i.next();
			for(int j=0; j<uniqueConstraint.length; j++)
			{
				if(this == uniqueConstraint[j])
					return true;
			}
		}
		return false;
	}
	
	public boolean isReadOnly()
	{
		return readOnly;
	}
	
	public boolean isNotNull()
	{
		return notNull;
	}
	
	public boolean isMapped()
	{
		return mapped;
	}
	
	public List getQualifiers()
	{
		return qualifiers;
	}
	
	public List getMediaVariants()
	{
		return mediaVariants;
	}
	
	public String getMimeMajor()
	{
		return mimeMajor;
	}
	
	public String getMimeMinor()
	{
		return mimeMinor;
	}
	
	public List getEnumerationValues()
	{
		return enumerationValues;
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
			throw new NullPointerException();
		if(this.persistentType!=null)
			throw new RuntimeException("Du Schwein!");
		getParent().addPersistentAttribute(this);
		this.persistentType = persistentType;
	}

	public final void makeReadOnly()
	{
		if(this.qualifiers!=null)
			throw new RuntimeException();
		readOnly = true;
	}
	
	public final void makeNotNull()
	{
		if(this.qualifiers!=null)
			throw new RuntimeException();
		notNull = true;
	}
	
	public final void makeMapped()
	{
		mapped = true;
	}
	
	public final boolean isInitial()
	{
		return (readOnly || notNull) && !mapped;
	}
	
	public final boolean hasSetter()
	{
		return !readOnly && !mapped;
	}
	
	public final void makeQualified(final List qualifiers)
	{
		if(qualifiers==null)
			throw new NullPointerException();
		if(qualifiers.isEmpty())
			throw new RuntimeException();
		if(this.qualifiers!=null)
			throw new RuntimeException();
		if(this.readOnly)
			throw new RuntimeException();
		if(this.notNull)
			throw new RuntimeException();
		this.qualifiers = Collections.unmodifiableList(qualifiers);
	}

	public final void makeMediaVarianted(final List mediaVariants)
	{
		if(mediaVariants==null)
			throw new NullPointerException();
		if(mediaVariants.isEmpty())
			throw new RuntimeException();
		if(this.mediaVariants!=null)
			throw new RuntimeException();
		if(!isMediaPersistentType())
			throw new RuntimeException();
		this.mediaVariants = Collections.unmodifiableList(mediaVariants);
	}
	
	public final void contrainMediaMime(final String mimeMajor, final String mimeMinor)
	{
		if(!isMediaPersistentType())
			throw new RuntimeException();
		if(mimeMajor==null && mimeMinor!=null)
			throw new RuntimeException();
		this.mimeMajor = mimeMajor;
		this.mimeMinor = mimeMinor;
	}

	public final void makeEnumerationAttribute(final List enumerationValues)
	{
		if(enumerationValues==null)
			throw new NullPointerException();
		if(enumerationValues.isEmpty())
			throw new RuntimeException();
		if(this.enumerationValues!=null)
			throw new RuntimeException();
		this.enumerationValues = Collections.unmodifiableList(enumerationValues);
	}
	

	private SortedSet setterExceptions = null;

	public final SortedSet getSetterExceptions()
	{
		if(setterExceptions!=null)
			return setterExceptions;
		
		final TreeSet modifyableSetterExceptions = new TreeSet(ClassComparator.newInstance());
		
		if(isPartOfUniqueConstraint())
			modifyableSetterExceptions.add(UniqueViolationException.class);
		if(readOnly)
			modifyableSetterExceptions.add(ReadOnlyViolationException.class);
		if(notNull && !toNativeTypeMapping.containsKey(persistentType))
			modifyableSetterExceptions.add(NotNullViolationException.class);

		this.setterExceptions = Collections.unmodifiableSortedSet(modifyableSetterExceptions);
		return this.setterExceptions;
	}


	private SortedSet exceptionsToCatchInSetter = null;

	/**
	 * Compute exceptions to be caught in the setter.
	 * These are just those thrown by {@link persistence.Item#setAttribute(Attribute,Object)}
	 * (or {@link persistence.Item#setAttribute(Attribute,Object[],Object)} for qualified attributes)
	 * which are not in the setters throws clause.
	 * (see {@link #getSetterExceptions()})
	 */
	public final SortedSet getExceptionsToCatchInSetter()
	{
		if(exceptionsToCatchInSetter!=null)
			return exceptionsToCatchInSetter;

		final TreeSet result = new TreeSet(ClassComparator.newInstance());
		result.add(UniqueViolationException.class);
		if(qualifiers==null)
		{
			// qualified setAttribute does not throw not-null/read-only
			result.add(NotNullViolationException.class);
			result.add(ReadOnlyViolationException.class);
		}
		result.removeAll(getSetterExceptions());
		
		this.exceptionsToCatchInSetter = Collections.unmodifiableSortedSet(result);
		return this.exceptionsToCatchInSetter;
	}

}
