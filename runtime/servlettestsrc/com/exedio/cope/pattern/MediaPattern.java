/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;

import com.exedio.cope.Cope;
import com.exedio.cope.Features;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Pattern;
import com.exedio.cope.Type;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Computed;
import java.io.Serial;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class MediaPattern extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final Media sourceFeature;

	private ItemField<?> parent = null;
	private final Media sourceTypeValue;
	private Type<SourceItem> sourceType = null;

	public MediaPattern()
	{
		this.sourceFeature = addSourceFeature(new Media().optional(), "sourceFeature");
		this.sourceTypeValue = new Media();
	}

	public void setSourceFeature(final Item item, final byte[] body, final String contentType, final int hour)
		throws ParseException
	{
		sourceFeature.set(item, body, contentType);
		sourceFeature.getLastModified().set(item, hour(hour));
	}

	public void addSourceItem(final Item item, final byte[] body, final String contentType, final int hour)
		throws ParseException
	{
		final SourceItem result =
			sourceType.newItem(
				Cope.mapAndCast(parent, item),
				map(sourceTypeValue, Media.toValue(body, contentType)));
		sourceTypeValue.getLastModified().set(result, hour(hour));
	}

	private static Date hour(final int hour) throws ParseException
	{
		return df().parse("2010-09-11 " + new DecimalFormat("00").format(hour) + ":23:55.555");
	}

	private static SimpleDateFormat df()
	{
		final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		result.setTimeZone(getTimeZone("Europe/Berlin"));
		result.setLenient(false);
		return result;
	}

	@Override
	protected void onMount()
	{
		super.onMount();
		final Type<?> type = getType();

		parent = type.newItemField(CASCADE).toFinal();
		final Features features = new Features();
		features.put("parent", parent);
		features.put("value", sourceTypeValue);
		this.sourceType = newSourceType(SourceItem.class, SourceItem::new, features);
	}

	@Computed
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class SourceItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private SourceItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
