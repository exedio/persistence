
package com.exedio.cope.lib.hierarchy;

import com.exedio.cope.lib.StringAttribute;

/**
 * @persistent
 */
public class FirstSub extends Super
{
	/**
	 * @persistent
	 */
	public static final StringAttribute firstSubString = new StringAttribute();
	
	public FirstSub(final int initialSuperInt)
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(superInt,new Integer(initialSuperInt)),
		});
	} 

/**

	 **
	 * Constructs a new FirstSub with all the attributes initially needed.
	 * @author cope instrumentor
	 *
 */public FirstSub()
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private FirstSub(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #firstSubString}.
	 * @author cope instrumentor
	 *
 */public final String getFirstSubString()
	{
		return (String)getAttribute(this.firstSubString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #firstSubString}.
	 * @author cope instrumentor
	 *
 */public final void setFirstSubString(final String firstSubString)
	{
		try
		{
			setAttribute(this.firstSubString,firstSubString);
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
	 * The persistent type information for firstSub.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			FirstSub.class,
			new com.exedio.cope.lib.Attribute[]{
				firstSubString.initialize(false,false),
			},
			null
		)
;}
