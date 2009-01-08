/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.editor;

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.PartOf;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class DraftItem extends Item
{
	static final ItemField<Draft> parent = newItemField(Draft.class, CASCADE).toFinal();
	static final IntegerField position = new IntegerField().toFinal();
	static final PartOf<Draft> items = PartOf.newPartOf(parent);
	static final UniqueConstraint parentAndPosition = new UniqueConstraint(parent, position);
	
	static final StringField feature = new StringField().toFinal();
	static final StringField item = new StringField().toFinal();
	static final StringField oldValue = new StringField().toFinal().lengthMax(50000);
	static final StringField newValue = new StringField().toFinal().lengthMax(50000);
	
	
	DraftItem(
			final Draft parent,
			final int position,
			final StringField feature,
			final Item item,
			final String oldValue,
			final String newValue)
	{
		this(parent, position, feature.getID(), item, oldValue, newValue);
	}
	
	DraftItem(
			final Draft parent,
			final int position,
			final String feature,
			final Item item,
			final String oldValue,
			final String newValue)
	{
		this(new com.exedio.cope.SetValue[]{
			DraftItem.parent.map(parent),
			DraftItem.position.map(position),
			DraftItem.feature.map(feature),
			DraftItem.item.map(item.getCopeID()),
			DraftItem.oldValue.map(oldValue),
			DraftItem.newValue.map(newValue),
		});
	}
	
	private DraftItem(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused") private DraftItem(final ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;
	
	public static final Type<DraftItem> TYPE = newType(DraftItem.class);
}
