/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.Query;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Draft extends Item
{
	static final StringField user = new StringField().toFinal();
	static final StringField name = new StringField().toFinal().optional();
	static final DateField date = new DateField().toFinal().defaultToNow();
	static final StringField comment = new StringField().toFinal();
	
	String getDate()
	{
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(date.get(this));
	}
	
	String getAuthor()
	{
		final String name = Draft.name.get(this);
		return name!=null ? name : Draft.user.get(this);
	}
	
	String getComment()
	{
		return Draft.comment.get(this);
	}
	
	String getDropDownSummary()
	{
		return getAuthor() + " - " + getComment();
	}
	
	List<DraftItem> getItems()
	{
		return DraftItem.TYPE.search(DraftItem.parent.equal(this));
	}
	
	int getItemsCount()
	{
		return new Query<DraftItem>(DraftItem.TYPE.getThis(), DraftItem.parent.equal(this)).total();
	}
	
	private int nextPosition()
	{
		final Query<Integer> q = new Query<Integer>(DraftItem.position.max(), DraftItem.parent.equal(this));
		final Integer position = q.searchSingleton();
		return position!=null ? (position.intValue()+1) : 0;
	}
	
	public DraftItem addItem(final StringField feature, final Item item, final String value)
	{
		final DraftItem i = DraftItem.forParentFeatureAndItem(this, feature, item);
		if(i==null)
		{
			return new DraftItem(this, nextPosition(), feature, item, feature.get(item), value);
		}
		else
		{
			i.setNewValue(value);
			return i;
		}
	}
	
	public <K> DraftItem addItem(
			final MapField<K, String> feature,
			final K key,
			final Item item,
			final String value)
	{
		final Item ritem = feature.getRelationType().searchSingletonStrict(
				feature.getKey().equal(key).and(
				Cope.equalAndCast(feature.getParent(), item)));
		return addItem((StringField)feature.getValue(), ritem, value);
	}
	
	public Draft(
			final String user,
			final String name,
			final String comment)
	{
		this(new SetValue[]{
			Draft.user.map(user),
			Draft.name.map(name),
			Draft.comment.map(comment),
		});
	}
	
	private Draft(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused") private Draft(final ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;

	public static final Type<Draft> TYPE = newType(Draft.class);
}
