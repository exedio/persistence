
package com.exedio.cope.lib.contacts;

import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;

/**
 * @persistent
 */
public abstract class Contact extends Item
{
	/**
	 * @persistent
	 * @not-null
	 */
	private static final IntegerAttribute latency = new IntegerAttribute(); 
	
	/**
	 * @persistent
	 * @not-null
	 */
	private static final IntegerAttribute annoyance = new IntegerAttribute(); 
	

/**

	 **
	 * Constructs a new Contact with all the attributes initially needed.
	 * @param initialLatency the initial value for attribute {@link #latency}.
	 * @param initialAnnoyance the initial value for attribute {@link #annoyance}.
	 * @generated
	 *
 */private Contact(
				final int initialLatency,
				final int initialAnnoyance)
	{
		super(TYPE, new com.exedio.cope.lib.AttributeValue[]{
			new com.exedio.cope.lib.AttributeValue(latency,new Integer(initialLatency)),
			new com.exedio.cope.lib.AttributeValue(annoyance,new Integer(initialAnnoyance)),
		});
	}/**

	 **
	 * Creates an item and sets the given attributes initially.
	 * @generated
	 *
 */protected Contact(final com.exedio.cope.lib.Type type,final com.exedio.cope.lib.AttributeValue[] initialAttributes)
	{
		super(type,initialAttributes);
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @generated
	 *
 */protected Contact(com.exedio.cope.lib.util.ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #latency}.
	 * @generated
	 *
 */private final int getLatency()
	{
		return ((Integer)getAttribute(this.latency)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #latency}.
	 * @generated
	 *
 */private final void setLatency(final int latency)
	{
		try
		{
			setAttribute(this.latency,new Integer(latency));
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
	 * Returns the value of the persistent attribute {@link #annoyance}.
	 * @generated
	 *
 */private final int getAnnoyance()
	{
		return ((Integer)getAttribute(this.annoyance)).intValue();
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #annoyance}.
	 * @generated
	 *
 */private final void setAnnoyance(final int annoyance)
	{
		try
		{
			setAttribute(this.annoyance,new Integer(annoyance));
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
	 * The persistent type information for contact.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			Contact.class,
			new com.exedio.cope.lib.Attribute[]{
				latency.initialize("latency",false,true),
				annoyance.initialize("annoyance",false,true),
			},
			null
		)
;}
