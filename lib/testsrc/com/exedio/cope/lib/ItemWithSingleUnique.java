
package com.exedio.cope.lib;

/**
 * An item having a unique attribute.
 * @persistent
 */
public class ItemWithSingleUnique extends Item
{
	/**
	 * An attribute that is unique.
	 * @persistent
	 * @unique
	 */
	public static final StringAttribute uniqueString = new StringAttribute();

/**

	 **
	 * Constructs a new ItemWithSingleUnique with all the attributes initially needed.
	 * @author cope instrumentor
	 *
 */public ItemWithSingleUnique()
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private ItemWithSingleUnique(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #uniqueString}.
	 * @author cope instrumentor
	 *
 */public final String getUniqueString()
	{
		return (String)getAttribute(this.uniqueString);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #uniqueString}.
	 * @author cope instrumentor
	 *
 */public final void setUniqueString(final String uniqueString)
			throws
				com.exedio.cope.lib.UniqueViolationException
	{
		try
		{
			setAttribute(this.uniqueString,uniqueString);
		}
		catch(com.exedio.cope.lib.NotNullViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
		catch(com.exedio.cope.lib.ReadOnlyViolationException e)
		{
			throw new com.exedio.cope.lib.SystemException(e);
		}
	}/**

	 **
	 * Finds a itemWithSingleUnique by it's unique attributes
	 * @param searchedUniqueString shall be equal to attribute {@link #uniqueString}.
	 * @author cope instrumentor
	 *
 */public static final ItemWithSingleUnique findByUniqueString(final String searchedUniqueString)
	{
		return (ItemWithSingleUnique)searchUnique(TYPE,equal(uniqueString,searchedUniqueString));
	}/**

	 **
	 * The persistent type information for itemWithSingleUnique.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithSingleUnique.class,
			new com.exedio.cope.lib.Attribute[]{
				uniqueString.initialize(false,false),
			},
			new com.exedio.cope.lib.UniqueConstraint[]{
				new com.exedio.cope.lib.UniqueConstraint(uniqueString),
			}
		)
;}
