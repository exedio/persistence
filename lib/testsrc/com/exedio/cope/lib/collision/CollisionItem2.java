
package com.exedio.cope.lib.collision;

import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.ItemWithoutAttributes;

/**
 * @persistent
 */
public class CollisionItem2 extends Item
{
	public static final ItemAttribute collisionAttribute = new ItemAttribute(DEFAULT, ItemWithoutAttributes.class); 

/**

	 **
	 * Constructs a new CollisionItem2 with all the attributes initially needed.
	 * @author cope instrumentor
	 *
 */public CollisionItem2()
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private CollisionItem2(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * The persistent type information for collisionItem2.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			CollisionItem2.class,
			null
		)
;}
