
package com.exedio.cope.instrument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ClassComparator;

public final class PersistentAttribute
{
	/**
	 * Defines this attribute as a media attribute.
	 * The dash prevents this name to be used as a java identifier.
	 * @see #getPersistentType()
	 */
	public static final String MEDIA_TYPE = "Media-";

	public static final int TYPE_INTEGER = 0;
	public static final int TYPE_BOOLEAN = 1;
	public static final int TYPE_STRING = 2;
	public static final int TYPE_ENUMERATION = 3;
	public static final int TYPE_ITEM = 4;
	public static final int TYPE_MEDIA = 5;

	private final JavaAttribute javaAttribute;
	final int accessModifier;
	
	final PersistentClass persistentClass;
	private final String persistentType;
	private final int persistentTypeType;
	private final boolean readOnly;
	private final boolean notNull;
	private final boolean mapped;
	private final List qualifiers;
	private final List mediaVariants;
	private final String mimeMajor;
	private final String mimeMinor;
	private final List enumerationValues;

	public PersistentAttribute(
			final JavaAttribute javaAttribute,
			final String persistentType, final int persistentTypeType,
			final boolean readOnly, final boolean notNull, final boolean mapped,
			final List qualifiers, final List mediaVariants,
			final String mimeMajor, final String mimeMinor,
			final List enumerationValues)
	{
		this.javaAttribute = javaAttribute;
		this.accessModifier = javaAttribute.accessModifier;
		this.persistentClass = PersistentClass.getPersistentClass(javaAttribute.getParent());
		this.persistentType = persistentType;
		this.persistentTypeType = persistentTypeType;
		this.readOnly = readOnly;
		this.notNull = notNull;
		this.mapped = mapped;
		this.qualifiers = qualifiers;
		this.mediaVariants = mediaVariants;
		this.mimeMajor = mimeMajor;
		this.mimeMinor = mimeMinor;
		this.enumerationValues = enumerationValues;
		persistentClass.addPersistentAttribute(this);
	}
	
	public String getName()
	{
		return javaAttribute.getName();
	}
	
	public String getCamelCaseName()
	{
		return javaAttribute.getCamelCaseName();
	}
	
	public int getMethodModifiers()
	{
		return javaAttribute.getMethodModifiers();
	}
	
	public JavaClass getParent()
	{
		return javaAttribute.getParent();
	}
	
	/**
	 * Returns the persistent type of this attribute.
	 */
	public String getPersistentType()
	{
		return this.persistentType;
	}
	
	public boolean isItemPersistentType()
	{
		return this.persistentTypeType == TYPE_ITEM;
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
		fillNativeTypeMap("Boolean", "boolean", "(",            "?Boolean.TRUE:Boolean.FALSE)","(", ").booleanValue()");
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
		for( final Iterator i = persistentClass.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final PersistentAttribute[] uniqueConstraint = (PersistentAttribute[])i.next();
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
	
	public final boolean isInitial()
	{
		return (readOnly || notNull) && !mapped;
	}
	
	public final boolean hasSetter()
	{
		return !readOnly && !mapped;
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
	 * These are just those thrown by {@link com.exedio.cope.lib.Item#setAttribute(Attribute,Object)}
	 * (or {@link com.exedio.cope.lib.Item#setAttribute(Attribute,Object[],Object)} for qualified attributes)
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
