
package com.exedio.cope.lib;

/**
 * An item not having any attribute.
 * @persistent
 */
public class ItemWithoutAttributes extends Item
{
/**

	 **
	 * Constructs a new ItemWithoutAttributes with all the attributes initially needed.
	 * @author cope instrumentor
	 *
 */public ItemWithoutAttributes()
	{
		super(new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @author cope instrumentor
	 *
 */private ItemWithoutAttributes(com.exedio.cope.lib.util.ReactivationConstructorDummy d,final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * The persistent type information for itemWithoutAttributes.
	 * @author cope instrumentor
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithoutAttributes.class,
			null,
			null
		)
;}
