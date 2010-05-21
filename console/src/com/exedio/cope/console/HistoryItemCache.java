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
import java.util.Date;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;

final class HistoryItemCache extends Item
{
	private static final ItemField<HistoryModel> model = newItemField(HistoryModel.class).toFinal();
	private static final StringField type = new StringField().toFinal();
	
	private static final DateField date = new DateField().toFinal();
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndType = new UniqueConstraint(date, type); // date must be first, so purging can use the index
	private static final DateField initializeDate = new DateField().toFinal();
	private static final DateField connectDate = new DateField().toFinal();
	private static final IntegerField thread = new IntegerField().toFinal();
	private static final IntegerField running = new IntegerField().toFinal().min(0);
	
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
	
	
	private static final IntegerField limit = new IntegerField().toFinal().min(0);
	private static final IntegerField level = new IntegerField().toFinal().min(0);
	private static final LongField hits = new LongField().toFinal();
	private static final LongField misses = new LongField().toFinal();
	private static final LongField concurrentLoads = new LongField().toFinal();
	private static final IntegerField replacementRuns = new IntegerField().toFinal().min(0);
	private static final IntegerField replacements = new IntegerField().toFinal().min(0);
	private static final DateField lastReplacementRun = new DateField().toFinal().optional();
	private static final LongField ageAverageMillis = new LongField().toFinal();
	private static final LongField ageMinMillis = new LongField().toFinal();
	private static final LongField ageMaxMillis = new LongField().toFinal();
	private static final LongField invalidationsOrdered = new LongField().toFinal();
	private static final LongField invalidationsDone = new LongField().toFinal();
	
	static List<SetValue> map(final ItemCacheInfo info)
	{
		return Arrays.asList((SetValue)
			type  .map(info.getType().getID()),
			limit .map(info.getLimit()),
			level .map(info.getLevel()),
			hits  .map(info.getHits()),
			misses.map(info.getMisses()),
			
			concurrentLoads.map(info.getConcurrentLoads()),
			
			replacementRuns   .map(info.getReplacementRuns()),
			replacements      .map(info.getReplacements()),
			lastReplacementRun.map(info.getLastReplacementRun()),
			
			ageAverageMillis.map(info.getAgeAverageMillis()),
			ageMinMillis    .map(info.getAgeMinMillis()),
			ageMaxMillis    .map(info.getAgeMaxMillis()),
			
			invalidationsOrdered.map(info.getInvalidationsOrdered()),
			invalidationsDone   .map(info.getInvalidationsDone()));
	}
	
	
	@SuppressWarnings("unused")
	private HistoryItemCache(final ActivationParameters ap)
	{
		super(ap);
	}
	
	HistoryModel getModel()
	{
		return model.get(this);
	}
	
	String getType()
	{
		return type.get(this);
	}
	
	Date getDate()
	{
		return date.get(this);
	}
	
	Date getInitalizeDate()
	{
		return initializeDate.get(this);
	}
	
	Date getConnectDate()
	{
		return connectDate.get(this);
	}
	
	int getThread()
	{
		return thread.getMandatory(this);
	}
	
	int getRunning()
	{
		return running.getMandatory(this);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type<HistoryItemCache> TYPE = TypesBound.newType(HistoryItemCache.class);
}
