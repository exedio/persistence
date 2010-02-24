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

package com.exedio.cope.pattern;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;

public final class MediaPattern extends Pattern
{
	private static final long serialVersionUID = 1l;
	
	private final Media sourceFeature;
	
	private ItemField<? extends Item> parent = null;
	private final Media sourceTypeValue;
	private Type<SourceItem> sourceType = null;

	public MediaPattern()
	{
		this.sourceFeature = new Media().optional();
		this.sourceTypeValue = new Media();
		addSource(sourceFeature, "sourceFeature");
	}

	public void setSourceFeature(final Item item, final byte[] body, final String contentType)
	{
		this.sourceFeature.set(item, body, contentType);
	}

	public void addSourceItem(final Item item, final byte[] body, final String contentType)
	{
		sourceType.newItem(
				Cope.mapAndCast(this.parent, item),
				this.sourceTypeValue.map(Media.toValue(body, contentType)));
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();
		
		parent = type.newItemField( ItemField.DeletePolicy.CASCADE ).toFinal();
		final Features features = new Features();
		features.put("parent", parent);
		features.put("value", sourceTypeValue);
		this.sourceType = newSourceType(SourceItem.class, features);
	}

	@Computed
	final static class SourceItem extends Item
	{
		private static final long serialVersionUID = 1l;

		SourceItem(final ActivationParameters ap)
		{
			super(ap);
		}
	}
}
