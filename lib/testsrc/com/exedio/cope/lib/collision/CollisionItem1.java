
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
	public static final ItemAttribute collisionAttribute = new ItemAttribute(DEFAULT, ItemWithoutAttributes.class); 

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
	 * The persistent type information for collisionItem1.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			CollisionItem1.class,
			null
		)
;}
