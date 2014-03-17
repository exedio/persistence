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

import static com.exedio.cope.sampler.AbsoluteModel.replaceByCopy;

import com.exedio.cope.CompareFunctionCondition;
import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.DateField;
import com.exedio.cope.Feature;
import com.exedio.cope.Function;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Join;
import com.exedio.cope.MinusView;
import com.exedio.cope.NumberField;
import com.exedio.cope.Query;
import com.exedio.cope.Selectable;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

final class AbsoluteDifferentiate
{
	static List<Query<List<Object>>> differentiate(final Date from, final Date until)
	{
		final ArrayList<Query<List<Object>>> result = new ArrayList<>(4);
		result.add(makeQuery(AbsoluteModel      .TYPE, from, until));
		result.add(makeQuery(AbsoluteItemCache  .TYPE, from, until));
		result.add(makeQuery(AbsoluteClusterNode.TYPE, from, until));
		result.add(makeQuery(AbsoluteMedia      .TYPE, from, until));
		return Collections.unmodifiableList(result);
	}

	private static Query<List<Object>> makeQuery(final Type<?> type, final Date from, final Date until)
	{
		final Query<List<Object>> query =
			Query.newQuery(new Selectable<?>[]{type.getThis(), type.getThis()}, type, null);
		final Join join = query.join(type);

		final DateField dateField = replaceByCopy(AbsoluteModel.date, type);
		final ArrayList<FunctionField<?>> byDateUnique = new ArrayList<>();
		final IntegerField sampler = replaceByCopy(AbsoluteModel.sampler, type);
		final IntegerField running = replaceByCopy(AbsoluteModel.running, type);
		{
			final ArrayList<Function<?>> selects = new ArrayList<>();
			{
				final List<UniqueConstraint> constraints = type.getUniqueConstraints();
				switch(constraints.size())
				{
					case 0:
						break;
					case 1:
						final UniqueConstraint constraint = constraints.get(0);
						for(final FunctionField<?> field : constraint.getFields())
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
				if(feature instanceof NumberField<?> &&
					!feature.isAnnotationPresent(AbsoluteNoDifferentiate.class) &&
					feature!=sampler && feature!=running &&
					!byDateUnique.contains(feature))
				{
					selects.add(minus(join, (NumberField<?>)feature));
				}
			}
			if(selects.isEmpty())
				throw new IllegalArgumentException(type.toString());
			query.setSelects(selects.toArray(new Function<?>[selects.size()]));
		}
		{
			final ArrayList<Condition> conditions = new ArrayList<>();

			if(from!=null)
				conditions.add(dateField.greaterOrEqual(from));
			if(until!=null)
				conditions.add(dateField.lessOrEqual(until));

			for(final FunctionField<?> field : byDateUnique)
				conditions.add(equal(join, field));

			conditions.add(equal(join, replaceByCopy(AbsoluteModel.connectDate, type)));
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
	private static <N extends Number> MinusView<N> minus(final Join j, final NumberField<N> field)
	{
		return field.bind(j).minus(field);
	}

	/**
	 * helper method for generics
	 */
	private static <N> CompareFunctionCondition<N> equal(final Join j, final FunctionField<N> field)
	{
		return field.bind(j).equal(field);
	}

	private AbsoluteDifferentiate()
	{
		// prevent instantiation
	}
}
