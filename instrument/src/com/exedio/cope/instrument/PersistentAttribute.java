
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.ComputedFunction;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.LengthViolationException;
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

	public final JavaAttribute javaAttribute;
	public final int accessModifier;
	
	final PersistentClass persistentClass;
	private final String persistentType;
	public final boolean readOnly;
	public final boolean notNull;
	public final boolean lengthConstrained;
	public final boolean computed;
	public final int setterOption;
	public final List qualifiers;

	public PersistentAttribute(
			final JavaAttribute javaAttribute,
			final Class typeClass,
			final String persistentType,
			final List initializerArguments,
			final String setterOptionString,
			final List qualifiers)
		throws InjectorParseException
	{
		this.javaAttribute = javaAttribute;
		this.accessModifier = javaAttribute.accessModifier;
		this.persistentClass = PersistentClass.getPersistentClass(javaAttribute.parent);
		this.persistentType = persistentType;
		this.computed = ComputedFunction.class.isAssignableFrom(typeClass);
		
		if(!computed)
		{
			if(initializerArguments.size()<1)
				throw new InjectorParseException("attribute "+javaAttribute.name+" has no option.");
			final String optionString = (String)initializerArguments.get(0);
			//System.out.println(optionString);
			final Attribute.Option option = getOption(optionString); 
	
			this.readOnly = option.readOnly;
			this.notNull = option.notNull;
			
			if(initializerArguments.size()>1)
			{
				final String secondArgument = (String)initializerArguments.get(1);
				boolean lengthConstrained = true;
				try
				{
					Integer.parseInt(secondArgument);
				}
				catch(NumberFormatException e)
				{
					lengthConstrained = false;
				}
				this.lengthConstrained = lengthConstrained;
			}
			else
				this.lengthConstrained = false;

			if(option.unique)
				persistentClass.makeUnique(new PersistentUniqueConstraint(this));
		}
		else
		{
			this.readOnly = false;
			this.notNull = false;
			this.lengthConstrained = false;
		}
		
		setterOption = Option.getOption(setterOptionString);

		this.qualifiers = (qualifiers!=null) ? Collections.unmodifiableList(qualifiers) : null;

		persistentClass.addPersistentAttribute(this);
	}
	
	public final String getName()
	{
		return javaAttribute.getName();
	}
	
	public final String getCamelCaseName()
	{
		return javaAttribute.getCamelCaseName();
	}
	
	public final int getGeneratedGetterModifier()
	{
		return javaAttribute.modifier
			& (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)
			| Modifier.FINAL;
	}

	public final JavaClass getParent()
	{
		return javaAttribute.parent;
	}
	
	/**
	 * Returns the persistent type of this attribute.
	 */
	public final String getPersistentType()
	{
		return this.persistentType;
	}
	
	/**
	 * Returns the type of this attribute to be used in accessor (setter/getter) methods.
	 * Differs from {@link #getPersistentType() the persistent type},
	 * if and only if the attribute is {@link #isBoxed() boxed}.
	 */
	public String getBoxedType()
	{
		return persistentType;
	}
	
	/**
	 * Returns, whether the persistent type is &quot;boxed&quot; into a native type.
	 * This happens if the attribute has a not-null constraint 
	 * and the persistent type is convertable to a native types (int, double, boolean).
	 * @see #getBoxedType()
	 */
	public boolean isBoxed()
	{
		return false;
	}
	
	public String getBoxingPrefix()
	{
		throw new RuntimeException();
	}
	
	public String getBoxingPostfix()
	{
		throw new RuntimeException();
	}
	
	public String getUnBoxingPrefix()
	{
		throw new RuntimeException();
	}
	
	public String getUnBoxingPostfix()
	{
		throw new RuntimeException();
	}
	
	public final boolean isPartOfUniqueConstraint()
	{
		for( final Iterator i = persistentClass.getUniqueConstraints().iterator(); i.hasNext(); )
		{
			final PersistentAttribute[] uniqueConstraint = ((PersistentUniqueConstraint)i.next()).persistentAttributes;
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
		return (readOnly || notNull) && !computed;
	}
	
	private final boolean isWriteable()
	{
		return !readOnly && !computed;
	}
	
	public final boolean hasGeneratedSetter()
	{
		return isWriteable() && (setterOption!=Option.NONE);
	}
	
	public final int getGeneratedSetterModifier()
	{
		final int result;
		switch(setterOption)
		{
			case Option.NONE:
				throw new RuntimeException();
			case Option.AUTO:
				result = javaAttribute.modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
				break;
			case Option.PRIVATE:
				result = Modifier.PRIVATE;
				break;
			case Option.PROTECTED:
				result = Modifier.PROTECTED;
				break;
			case Option.PACKAGE:
				result = 0;
				break;
			case Option.PUBLIC:
				result = Modifier.PUBLIC;
				break;
			default:
				throw new RuntimeException(String.valueOf(setterOption));
		}
		return result | Modifier.FINAL;
	}
	
	private SortedSet setterExceptions = null;

	public final SortedSet getSetterExceptions()
	{
		if(setterExceptions!=null)
			return setterExceptions;
		
		final TreeSet modifyableSetterExceptions = new TreeSet(ClassComparator.getInstance());
		
		if(isPartOfUniqueConstraint())
			modifyableSetterExceptions.add(UniqueViolationException.class);
		if(readOnly)
			modifyableSetterExceptions.add(ReadOnlyViolationException.class);
		if(notNull && !isBoxed())
			modifyableSetterExceptions.add(NotNullViolationException.class);
		if(lengthConstrained)
			modifyableSetterExceptions.add(LengthViolationException.class);
		

		this.setterExceptions = Collections.unmodifiableSortedSet(modifyableSetterExceptions);
		return this.setterExceptions;
	}


	private SortedSet exceptionsToCatchInSetter = null;

	/**
	 * Compute exceptions to be caught in the setter.
	 * These are just those thrown by {@link com.exedio.cope.lib.Item#setAttribute(ObjectAttribute,Object)}
	 * which are not in the setters throws clause.
	 * (see {@link #getSetterExceptions()})
	 */
	public final SortedSet getExceptionsToCatchInSetter()
	{
		if(exceptionsToCatchInSetter!=null)
			return exceptionsToCatchInSetter;

		final TreeSet result = new TreeSet(ClassComparator.getInstance());
		result.add(UniqueViolationException.class);
		result.add(NotNullViolationException.class);
		result.add(ReadOnlyViolationException.class);
		result.add(LengthViolationException.class);
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
