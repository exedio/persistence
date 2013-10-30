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

package com.exedio.cope.mxsampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.CompositeField;
import java.util.Arrays;
import java.util.List;

final class MxSamplerMemoryPool extends Item
{
	private static final ItemField<MxSamplerGlobal> model = ItemField.create(MxSamplerGlobal.class).toFinal();
	static final ItemField<MxSamplerMemoryPoolName> name = ItemField.create(MxSamplerMemoryPoolName.class).toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndType = new UniqueConstraint(date, name); // date must be first, so purging can use the index
	@CopeSchemaName("thread") private static final IntegerField sampler = new IntegerField().toFinal().copyFrom(model);
	private static final IntegerField running = new IntegerField().toFinal().copyFrom(model).min(0);

	@SuppressWarnings("unchecked")
	static List<SetValue<?>> map(final MxSamplerGlobal m)
	{
		return Arrays.asList((SetValue<?>)
			model         .map(m),
			date          .map(MxSamplerGlobal.date.get(m)),
			sampler       .map(MxSamplerGlobal.sampler.get(m)),
			running       .map(MxSamplerGlobal.running.get(m)));
	}


	static final CompositeField<MxSamplerMemoryUsage> usage  = CompositeField.create(MxSamplerMemoryUsage.class).toFinal();
	static final CompositeField<MxSamplerMemoryUsage> collectionUsage  = CompositeField.create(MxSamplerMemoryUsage.class).toFinal().optional();


	private MxSamplerMemoryPool(final ActivationParameters ap) { super(ap); }

	private static final long serialVersionUID = 1l;

	static final Type<MxSamplerMemoryPool> TYPE = TypesBound.newType(MxSamplerMemoryPool.class);
}
