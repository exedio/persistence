/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;

@SuppressWarnings("unused") // OK: deprecated item
@Purgeable()
@CopeSchemaName("SamplerItemCache")
final class AbsoluteItemCache extends Item
{
	private static final ItemField<AbsoluteModel> model = ItemField.create(AbsoluteModel.class).toFinal();
	private static final ItemField<SamplerTypeId> type = ItemField.create(SamplerTypeId.class).toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndType = new UniqueConstraint(date, type); // date must be first, so purging can use the index
	private static final DateField initializeDate = new DateField().toFinal().copyFrom(model);
	private static final DateField connectDate = new DateField().toFinal().copyFrom(model);
	@CopeSchemaName("thread") private static final IntegerField sampler = new IntegerField().toFinal().copyFrom(model);
	private static final IntegerField running = new IntegerField().toFinal().copyFrom(model).min(0);


	@AbsoluteNoDifferentiate
	private static final IntegerField limit = new IntegerField().toFinal().min(0);
	@AbsoluteNoDifferentiate
	private static final IntegerField level = new IntegerField().toFinal().min(0);
	private static final LongField hits = new LongField().toFinal().min(0);
	private static final LongField misses = new LongField().toFinal().min(0);
	private static final LongField concurrentLoads = new LongField().toFinal().min(0);
	private static final IntegerField replacementRuns = new IntegerField().toFinal().min(0);
	private static final IntegerField replacements = new IntegerField().toFinal().min(0);
	private static final DateField lastReplacementRun = new DateField().toFinal().optional();
	@AbsoluteNoDifferentiate private static final LongField ageAverageMillis = new LongField().toFinal();
	@AbsoluteNoDifferentiate private static final LongField ageMinimumMillis = new LongField().toFinal();
	@AbsoluteNoDifferentiate private static final LongField ageMaximumMillis = new LongField().toFinal();
	private static final LongField invalidationsOrdered = new LongField().toFinal().min(0);
	private static final LongField invalidationsDone = new LongField().toFinal().min(0);
	@AbsoluteNoDifferentiate
	private static final IntegerField invalidateLastSize = new IntegerField().toFinal().min(0);
	private static final LongField invalidateLastHits = new LongField().toFinal().min(0);
	private static final LongField invalidateLastPurged = new LongField().toFinal().min(0);


	@SuppressWarnings("unused")
	private AbsoluteItemCache(final ActivationParameters ap)
	{
		super(ap);
	}

	private static final long serialVersionUID = 1l;

	static final Type<AbsoluteItemCache> TYPE = TypesBound.newType(AbsoluteItemCache.class);
}
