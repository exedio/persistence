
package com.exedio.cope.lib.contacts;

import com.exedio.cope.lib.StringAttribute;

/**
 * @persistent
 */
public class SMSContact extends Contact
{
	/**
	 * @persistent
	 */
	public static final StringAttribute phoneNumber = new StringAttribute(); 

/**

	 **
	 * Constructs a new SMSContact with all the attributes initially needed.
	 * @generated
	 *
 */public SMSContact()
	{
		super(TYPE, new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @generated
	 *
 */private SMSContact(com.exedio.cope.lib.util.ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #phoneNumber}.
	 * @generated
	 *
 */public final String getPhoneNumber()
	{
		return (String)getAttribute(this.phoneNumber);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #phoneNumber}.
	 * @generated
	 *
 */public final void setPhoneNumber(final String phoneNumber)
	{
		try
		{
			setAttribute(this.phoneNumber,phoneNumber);
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
	 * The persistent type information for sMSContact.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			SMSContact.class,
			new com.exedio.cope.lib.Attribute[]{
				phoneNumber.initialize("phoneNumber",false,false),
			},
			null
		)
;}
