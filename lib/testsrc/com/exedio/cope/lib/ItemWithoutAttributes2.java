
package com.exedio.cope.lib;

/**
 * Another item not having any attribute.
 * @persistent
 */
public class ItemWithoutAttributes2 extends Item
{
/**

	 **
	 * Constructs a new ItemWithoutAttributes2 with all the attributes initially needed.
	 * @generated
	 *
 */public ItemWithoutAttributes2()
	{
		super(TYPE, new com.exedio.cope.lib.AttributeValue[]{
		});
	}/**

	 **
	 * Reactivation constructor. Used for internal purposes only.
	 * @see Item#Item(Type, int)
	 * @generated
	 *
 */private ItemWithoutAttributes2(com.exedio.cope.lib.util.ReactivationConstructorDummy d, final int pk)
	{
		super(TYPE, pk);
	}/**

	 **
	 * The persistent type information for itemWithoutAttributes2.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithoutAttributes2.class,
			new com.exedio.cope.lib.Attribute[]{
			},
			new com.exedio.cope.lib.UniqueConstraint[]{
			}
		)
;}
