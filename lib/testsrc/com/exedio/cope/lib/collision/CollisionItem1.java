
package com.exedio.cope.lib.collision;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.ItemWithoutAttributes;

/**
 * Test for database name collisions
 * by using the same attributes names
 * in different persistent classes.
 * @persistent
 */
public class CollisionItem1 extends Item
{
	/**
	 * @persistent
	 */
	public static final ItemAttribute collisionAttribute = new ItemAttribute(UNIQUE, ItemWithoutAttributes.class); 

/**

	 **
	 * Constructs a new CollisionItem1 with all the attributes initially needed.
	 * @author cope instrumentor
	 *
 */public CollisionItem1()
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private CollisionItem1(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * Returns the value of the persistent attribute {@link #collisionAttribute}.
	 * @author cope instrumentor
	 *
 */public final ItemWithoutAttributes getCollisionAttribute()
	{
		return (ItemWithoutAttributes)getAttribute(this.collisionAttribute);
	}/**

	 **
	 * Sets a new value for the persistent attribute {@link #collisionAttribute}.
	 * @author cope instrumentor
	 *
 */public final void setCollisionAttribute(final ItemWithoutAttributes collisionAttribute)
			throws
				com.exedio.cope.lib.UniqueViolationException
	{
		try
		{
			setAttribute(this.collisionAttribute,collisionAttribute);
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
	 * Finds a collisionItem1 by it's unique attributes
	 * @param searchedCollisionAttribute shall be equal to attribute {@link #collisionAttribute}.
	 * @author cope instrumentor
	 *
 */public static final CollisionItem1 findByCollisionAttribute(final ItemWithoutAttributes searchedCollisionAttribute)
	{
		return (CollisionItem1)searchUnique(TYPE,equal(collisionAttribute,searchedCollisionAttribute));
	}/**

	 **
	 * The persistent type information for collisionItem1.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			CollisionItem1.class,
			new com.exedio.cope.lib.UniqueConstraint[]{
			}
		)
;}
