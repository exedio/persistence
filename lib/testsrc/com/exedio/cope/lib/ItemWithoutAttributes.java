
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
	 * @generated
	 *
 */public ItemWithoutAttributes()
	{
		super(TYPE, new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(com.exedio.cope.lib.util.ReactivationConstructorDummy,int)
	 * @generated
	 *
 */private ItemWithoutAttributes(com.exedio.cope.lib.util.ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}/**

	 **
	 * The persistent type information for itemWithoutAttributes.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithoutAttributes.class,
			null,
			null
		)
;}
