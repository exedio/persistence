
package com.exedio.cope.lib;

/**
 * Signals, that an attempt to write a {@link StringAttribute string attribute} has been failed,
 * because value to be written violated the length constraint on that attribute.
 *
 * This exception will be thrown by {@link Item#setAttribute Item.setAttribute} methods
 * and item constructors
 * if that attribute {@link StringAttribute#isLengthConstrained is length constrained}.
 */
public final class LengthViolationException extends ConstraintViolationException
{
	private final Item item;
	private final Attribute stringAttribute;
	private final String value;
	private final boolean isTooShort;
	
	/**
	 * Creates a new NotNullViolationException with the neccessary information about the violation.
	 * @param item initializes, what is returned by {@link #getItem()}.
	 * @param stringAttribute initializes, what is returned by {@link #getStringAttribute()}.
	 * @param value initializes, what is returned by {@link #getValue()}.
	 */
	public LengthViolationException(final Item item, final Attribute stringAttribute, final String value, final boolean isTooShort)
	{
		super(null);
		this.item = item;
		this.stringAttribute = stringAttribute;
		this.value = value;
		this.isTooShort = isTooShort;
	}
	
	/**
	 * Returns the item that was attempted to be modified.
	 */
	public final Item getItem()
	{
		return item;
	}

	/**
	 * Returns the attribute, that was attempted to be written.
	 */
	public Attribute getStringAttribute()
	{
		return stringAttribute;
	}

	/**
	 * Returns the value, that was attempted to be written.
	 */	
	public String getValue()
	{
		return value;
	}
	
	public boolean isTooShort()
	{
		return isTooShort;
	}

}
