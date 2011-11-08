/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.List;

import com.exedio.cope.CompareFunctionCondition;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Join;
import com.exedio.cope.NumberField;
import com.exedio.cope.PlusView;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;

final class Differentiate
{
	static Query<List<Object>> makeQuery(final Type<?> type)
	{
		final Query<List<Object>> query =
			Query.newQuery(new Selectable[]{type.getThis(), type.getThis()}, type, null);
		final Join join = query.join(type);

		final ArrayList<FunctionField> byDateUnique = new ArrayList<FunctionField>();
		final IntegerField sampler = replaceByCopy(SamplerModel.sampler, type);
		final IntegerField running = replaceByCopy(SamplerModel.running, type);
		{
			final ArrayList<Function> selects = new ArrayList<Function>();
			final DateField dateField = replaceByCopy(SamplerModel.date, type);
			{
				final List<UniqueConstraint> constraints = type.getUniqueConstraints();
				switch(constraints.size())
				{
					case 0:
						break;
					case 1:
						final UniqueConstraint constraint = constraints.get(0);
						for(final FunctionField field : constraint.getFields())
							if(field!=dateField)
							{
								selects.add(field);
								byDateUnique.add(field);
							}
						break;
					default:
						throw new RuntimeException(constraints.toString());
				}
			}
			selects.add(dateField);
			selects.add(dateField.bind(join));

			for(final Feature feature : type.getDeclaredFeatures())
			{
				if(feature instanceof NumberField &&
					!feature.isAnnotationPresent(NoDifferentiate.class) &&
					feature!=sampler && feature!=running &&
					!byDateUnique.contains(feature))
				{
					selects.add(minus(join, (NumberField<?>)feature));
				}
			}
			if(selects.isEmpty())
				throw new IllegalArgumentException(type.toString());
			query.setSelects(selects.toArray(new Function[selects.size()]));
		}
		{
			final ArrayList<Condition> conditions = new ArrayList<Condition>();
			for(final FunctionField<?> field : byDateUnique)
				conditions.add(equal(join, field));

			conditions.add(equal(join, replaceByCopy(SamplerModel.connectDate, type)));
			conditions.add(equal(join, sampler));
			conditions.add(running.bind(join).equal(running.plus(1)));

			query.setCondition(Cope.and(conditions));
		}
		query.setOrderByThis(true);
		return query;
	}

	/**
	 * helper method for generics
	 */
	private static <N extends Number> PlusView<N> minus(final Join j, final NumberField<N> field)
	{
		return field.bind(j).plus(field); // TODO has to be minus
	}

	/**
	 * helper method for generics
	 */
	private static <N> CompareFunctionCondition equal(final Join j, final FunctionField<N> field)
	{
		return field.bind(j).equal(field);
	}

	private static <F extends FunctionField> F replaceByCopy(final F field, final Type<?> type)
	{
		if(field.getType()==type)
			return field;

		for(final CopyConstraint cc : type.getCopyConstraints())
		{
			if(cc.getTemplate()==field)
			{
				final FunctionField<?> result = cc.getCopy();
				@SuppressWarnings("unchecked")
				final F resultCasted = (F)result;
				return resultCasted;
			}
		}
		throw new RuntimeException(field.getID());
	}

	private Differentiate()
	{
		// prevent instantiation
	}
}
