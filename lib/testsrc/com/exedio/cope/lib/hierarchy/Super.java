
package com.exedio.cope.lib.hierarchy;

import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;

/**
 * @persistent
 */
public abstract class Super extends Item
{
	/**
	 * @persistent
	 */
	public static final IntegerAttribute superInt = new IntegerAttribute(NOT_NULL); 
	
/**

	 **
	 * Constructs a new Super with all the attributes initially needed.
	 * @param initialSuperInt the initial value for attribute {@link #superInt}.
	 * @author cope instrumentor
	 *
 */public Super(
				final int initialSuperInt)
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(superInt,new Integer(initialSuperInt)),
		});
	}/**

	 **
	 * Creates an item and sets the given attributes initially.
	 * @author cope instrumentor
	 *
 */protected Super(final com.exedio.cope.lib.AttributeValue[] initialAttributes)
	{
		super(initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */protected Super(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #superInt}.
	 * @author cope instrumentor
	 *
 */public final int getSuperInt()
	{
		return ((Integer)getAttribute(this.superInt)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #superInt}.
	 * @author cope instrumentor
	 *
 */public final void setSuperInt(final int superInt)
	{
		try
		{
			setAttribute(this.superInt,new Integer(superInt));
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.UniqueViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * The persistent type information for super.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			Super.class,
			null
		)
;}
