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

package com.exedio.cope.sampler;

import static com.exedio.cope.sampler.Util.field;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffItemCache")
final class SamplerItemCache extends Item
{
	private static final ItemField<SamplerModel > model = ItemField.create(SamplerModel .class).toFinal();
	private static final ItemField<SamplerTypeId> type  = ItemField.create(SamplerTypeId.class).toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndType = UniqueConstraint.create(date, type); // date must be first, so purging can use the index

	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField level                = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField hits                 = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField misses               = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField concurrentLoads      = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField replacements         = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField invalidationsOrdered = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField invalidationsDone    = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField stampsSize           = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField stampsHits           = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField stampsPurged         = field(0);

	private static final long serialVersionUID = 1l;
	static final Type<SamplerItemCache> TYPE = TypesBound.newType(SamplerItemCache.class, SamplerItemCache::new);
	@SuppressWarnings("unused") private SamplerItemCache(final ActivationParameters ap){ super(ap); }
}
