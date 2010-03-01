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

package com.exedio.cope.console;

import java.util.Arrays;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.MediaInfo;

final class HistoryMedia extends Item
{
	static final ItemField<HistoryModel> model = newItemField(HistoryModel.class).toFinal();
	static final StringField media = new StringField().toFinal();
	
	static final DateField date = new DateField().toFinal();
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndMedia = new UniqueConstraint(date, media); // date must be first, so purging can use the index
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	static final IntegerField thread = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);
	
	@SuppressWarnings("unused") private static final CopyConstraint dateCC = new CopyConstraint(model, date);
	@SuppressWarnings("unused") private static final CopyConstraint initializeDateCC = new CopyConstraint(model, initializeDate);
	@SuppressWarnings("unused") private static final CopyConstraint connectDateCC = new CopyConstraint(model, connectDate);
	@SuppressWarnings("unused") private static final CopyConstraint threadCC = new CopyConstraint(model, thread);
	@SuppressWarnings("unused") private static final CopyConstraint runningCC = new CopyConstraint(model, running);
	
	static List<SetValue> map(final HistoryModel m)
	{
		return Arrays.asList((SetValue)
			model         .map(m),
			date          .map(HistoryModel.date.get(m)),
			initializeDate.map(HistoryModel.initializeDate.get(m)),
			connectDate   .map(HistoryModel.connectDate.get(m)),
			thread        .map(HistoryModel.thread.get(m)),
			running       .map(HistoryModel.running.get(m)));
	}
	
	
	static final IntegerField exception = new IntegerField().toFinal().min(0);
	static final IntegerField notAnItem = new IntegerField().toFinal().min(0);
	static final IntegerField noSuchItem = new IntegerField().toFinal().min(0);
	static final IntegerField isNull = new IntegerField().toFinal().min(0);
	static final IntegerField notComputable = new IntegerField().toFinal().min(0);
	static final IntegerField notModified = new IntegerField().toFinal().min(0);
	static final IntegerField delivered = new IntegerField().toFinal().min(0);
	
	static List<SetValue> map(final MediaInfo info)
	{
		return Arrays.asList((SetValue)
			media        .map(info.getPath().getID()),
			exception    .map(info.getException()),
			notAnItem    .map(info.getNotAnItem()),
			noSuchItem   .map(info.getNoSuchItem()),
			isNull       .map(info.getIsNull()),
			notComputable.map(info.getNotComputable()),
			notModified  .map(info.getNotModified()),
			delivered    .map(info.getDelivered()));
	}
	
	HistoryMedia(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused")
	private HistoryMedia(final ActivationParameters ap)
	{
		super(ap);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type<HistoryMedia> TYPE = TypesBound.newType(HistoryMedia.class);
}
