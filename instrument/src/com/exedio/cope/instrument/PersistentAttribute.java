
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.NotNullViolationException;
import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.UniqueViolationException;
import com.exedio.cope.lib.util.ClassComparator;

public abstract class PersistentAttribute
{
	/**
	 * Defines this attribute as a media attribute.
	 * The dash prevents this name to be used as a java identifier.
	 * @see #getPersistentType()
	 */
	public static final String MEDIA_TYPE = "Media-";

	private final JavaAttribute javaAttribute;
	public final int accessModifier;
	
	final PersistentClass persistentClass;
	private final String persistentType;
	public final boolean readOnly;
	public final boolean notNull;
	public final boolean mapped;
	public final List qualifiers;

	private final String boxedType;
	private final boolean boxed;
	private final String boxingPrefix, boxingPostfix, unboxingPrefix, unboxingPostfix;

	public PersistentAttribute(
			final JavaAttribute javaAttribute,
			final String persistentType,
			final List initializerArguments, final boolean mapped,
			final List qualifiers)
	{
		this.javaAttribute = javaAttribute;
		this.accessModifier = javaAttribute.accessModifier;
		this.persistentClass = PersistentClass.getPersistentClass(javaAttribute.getParent());
		this.persistentType = persistentType;

		final String optionString = (String)initializerArguments.get(0);
		//System.out.println(optionString);
		final Attribute.Option option = getOption(optionString); 

		this.readOnly = option.readOnly;
		this.notNull = option.notNull;

		this.mapped = mapped;
		this.qualifiers = (qualifiers!=null) ? Collections.unmodifiableList(qualifiers) : null;

		persistentClass.addPersistentAttribute(this);
		if(option.unique)
			persistentClass.makeUnique(new PersistentAttribute[]{this});

		{
			final String nativeType = (String)toNativeTypeMapping.get(persistentType);
			if(notNull && nativeType!=null)
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
				boxingPrefix = boxingPostfix = unboxingPrefix = unboxingPostfix = null;
			}
		}
	}
	
	public final String getName()
	{
		return javaAttribute.getName();
	}
	
	public final String getCamelCaseName()
	{
		return javaAttribute.getCamelCaseName();
	}
	
	public final int getMethodModifiers()
	{
		return javaAttribute.getModifiers()
			& (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)
			| Modifier.FINAL;
	}

	public final JavaClass getParent()
	{
		return javaAttribute.getParent();
	}
	
	/**
	 * Returns the persistent type of this attribute.
	 */
	public final String getPersistentType()
	{
		return this.persistentType;
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
		fillNativeTypeMap("Double", "double", "new Double(", ")",                       "(", ").doubleValue()");
	}

	/**
	 * Returns the type of this attribute to be used in accessor (setter/getter) methods.
	 * Differs from {@link #getPersistentType() the persistent type},
	 * if and only if the attribute is {@link #isBoxed() boxed}.
	 */
	public final String getBoxedType()
	{
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
		return boxed;
	}
	
	public final String getBoxingPrefix()
	{
		return boxingPrefix;
	}
	
	public final String getBoxingPostfix()
	{
		return boxingPostfix;
	}
	
	public final String getUnBoxingPrefix()
	{
		return unboxingPrefix;
	}
	
	public final String getUnBoxingPostfix()
	{
		return unboxingPostfix;
	}
	
	public final boolean isPartOfUniqueConstraint()
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

	private final static Attribute.Option getOption(final String optionString)	
	{
		try
		{
			//System.out.println(optionString);
			final Attribute.Option result = 
				(Attribute.Option)Item.class.getDeclaredField(optionString).get(null);
			if(result==null)
				throw new NullPointerException(optionString);
			return result;
		}
		catch(NoSuchFieldException e)
		{
			throw new SystemException(e, optionString);
		}
		catch(IllegalAccessException e)
		{
			throw new SystemException(e, optionString);
		}
	}
	
}
