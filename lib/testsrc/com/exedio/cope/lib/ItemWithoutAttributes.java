
package com.exedio.cope.lib;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.MediaAttribute;
import com.exedio.cope.lib.StringAttribute;

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
	 * The persistent type information for itemWithoutAttributes.
	 * @generated
	 *
 */public static final com.exedio.cope.lib.Type TYPE = 
		new com.exedio.cope.lib.Type(
			ItemWithoutAttributes.class,
			new com.exedio.cope.lib.Attribute[]{
			},
			new com.exedio.cope.lib.UniqueConstraint[]{
			},
			new Runnable()
			{
				public void run()
				{
				}
			}
		)
;}
