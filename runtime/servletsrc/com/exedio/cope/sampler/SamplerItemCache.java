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

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.sampler.Util.field;
import static com.exedio.cope.sampler.Util.maD;
import static com.exedio.cope.sampler.Util.maS;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemCacheInfo;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffItemCache")
final class SamplerItemCache extends Item
{
	private static final ItemField<SamplerModel > model = ItemField.create(SamplerModel .class).toFinal();
	private static final ItemField<SamplerTypeId> type  = ItemField.create(SamplerTypeId.class).toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndType = new UniqueConstraint(date, type); // date must be first, so purging can use the index

	static SetValue<?> mapIt(final SamplerModel m)
	{
		return map(model, m);
	}

	static SamplerItemCache forModelAndType(final SamplerModel model, final Type<?> type)
	{
		return TYPE.searchSingleton(Cope.and(
				SamplerItemCache.model.equal(model),
				SamplerItemCache.type.equal(SamplerTypeId.forId(type.getID()))
		));
	}


	/** this field is no longer used - will always contain 0 for new items */
	private static final IntegerField limit                = field(0);
	private static final IntegerField level                = field(0);
	private static final IntegerField hits                 = field(0);
	private static final IntegerField misses               = field(0);
	private static final IntegerField concurrentLoads      = field(0);
	/** this field is no longer used - will always contain 0 for new items */
	private static final IntegerField replacementRuns      = field(0);
	private static final IntegerField replacements         = field(0);
	/** this field is no longer used - will always contain null for new items */
	private static final DateField lastReplacementRun      = new DateField().toFinal().optional();
	/** this field is no longer used - will always contain 0 for new items */
	private static final LongField ageAverageMillis        = new LongField().toFinal();
	/** this field is no longer used - will always contain 0 for new items */
	private static final LongField ageMinimumMillis        = new LongField().toFinal();
	/** this field is no longer used - will always contain 0 for new items */
	private static final LongField ageMaximumMillis        = new LongField().toFinal();
	private static final IntegerField invalidationsOrdered = field(0);
	private static final IntegerField invalidationsDone    = field(0);
	@CopeSchemaName("invalidateLastSize")
	private static final IntegerField stampsSize           = field(0);
	@CopeSchemaName("invalidateLastHits")
	private static final IntegerField stampsHits           = field(0);
	@CopeSchemaName("invalidateLastPurged")
	private static final IntegerField stampsPurged         = field(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> mapIt(
			final ItemCacheInfo from,
			final ItemCacheInfo to)
	{
		final List<SetValue<?>> result = Arrays.asList(
			maS(type ,  from.getType(), to.getType()),
			map(limit,  0),
			map(level,  to.getLevel()),
			maD(hits,   from.getHits  (), to.getHits  ()),
			maD(misses, from.getMisses(), to.getMisses()),

			maD(concurrentLoads, from.getConcurrentLoads(), to.getConcurrentLoads()),
			map(replacementRuns, 0),
			maD(replacements,    from.getReplacementsL  (), to.getReplacementsL  ()),
			map(lastReplacementRun, null),

			map(ageAverageMillis, 0L),
			map(ageMinimumMillis, 0L),
			map(ageMaximumMillis, 0L),

			maD(invalidationsOrdered, from.getInvalidationsOrdered(), to.getInvalidationsOrdered()),
			maD(invalidationsDone,    from.getInvalidationsDone   (), to.getInvalidationsDone   ()),

			map(stampsSize,   to.getStampsSize()),
			maD(stampsHits,   from.getStampsHits  (), to.getStampsHits  ()),
			maD(stampsPurged, from.getStampsPurged(), to.getStampsPurged()));

		if(isDefault(result))
			return null;

		return result;
	}

	@SuppressWarnings("ConstantConditions") // too complex to analyze
	private static boolean isDefault(final List<SetValue<?>> result)
	{
		for(final SetValue<?> sv : result)
		{
			final Settable<?> s = sv.settable;
			if(s==type || s==limit || s==level || s==lastReplacementRun ||
				s==ageAverageMillis || s==ageMinimumMillis || s==ageMaximumMillis)
				continue;

			if(s instanceof IntegerField)
			{
				if((Integer)sv.value != 0)
					return false;
			}
			else
				throw new RuntimeException("" + sv);
		}
		return true;
	}

	int getInvalidationsOrdered()
	{
		return invalidationsOrdered.getMandatory(this);
	}

	SamplerModel getModel()
	{
		return model.get(this);
	}

	String getType()
	{
		return type.get(this).getID();
	}

	Date getDate()
	{
		return date.get(this);
	}

	private static final long serialVersionUID = 1l;
	static final Type<SamplerItemCache> TYPE = TypesBound.newType(SamplerItemCache.class);
	@SuppressWarnings("unused") private SamplerItemCache(final ActivationParameters ap){ super(ap); }
}
