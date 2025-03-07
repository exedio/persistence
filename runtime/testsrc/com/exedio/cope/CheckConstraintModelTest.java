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

package com.exedio.cope;

import static com.exedio.cope.CheckConstraintItem.TYPE;
import static com.exedio.cope.CheckConstraintItem.alpha;
import static com.exedio.cope.CheckConstraintItem.alphaToBeta;
import static com.exedio.cope.CheckConstraintItem.beta;
import static com.exedio.cope.CheckConstraintItem.delta;
import static com.exedio.cope.CheckConstraintItem.gamma;
import static com.exedio.cope.CheckConstraintSuperItem.drei;
import static com.exedio.cope.CheckConstraintSuperItem.eins;
import static com.exedio.cope.CheckConstraintSuperItem.einsToZwei;
import static com.exedio.cope.CheckConstraintSuperItem.zwei;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

public class CheckConstraintModelTest
{
	static final Model MODEL = new Model(TYPE, CheckConstraintSuperItem.TYPE);

	static
	{
		MODEL.enableSerialization(CheckConstraintModelTest.class, "MODEL");
	}

	@Test void testMeta()
	{
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				alpha, beta, gamma, delta,
				alphaToBeta),
			TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				eins, zwei, drei,
				einsToZwei,
				alpha, beta, gamma, delta,
				alphaToBeta),
			TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				alphaToBeta),
			TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsToZwei,
				alphaToBeta),
			TYPE.getCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsToZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsToZwei),
			CheckConstraintSuperItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				einsToZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsToZwei),
			CheckConstraintSuperItem.TYPE.getCheckConstraints());

		assertEquals(alpha.less(beta), alphaToBeta.getCondition());
		assertEquals(eins.greaterOrEqual(zwei), einsToZwei.getCondition());
		assertEqualsUnmodifiable(Set.of(alpha, beta), alphaToBeta.getFieldsCoveredByCondition());
		assertEqualsUnmodifiable(Set.of(eins, zwei), einsToZwei.getFieldsCoveredByCondition());

		assertFails(
				() -> new CheckConstraint(null),
				NullPointerException.class,
				"condition");
		assertFails(
				() -> new CheckConstraint(Condition.ofTrue()),
				IllegalArgumentException.class,
				"literal condition makes no sense, " +
				"but was Condition.TRUE");
		assertFails(
				() -> new CheckConstraint(Condition.ofFalse()),
				IllegalArgumentException.class,
				"literal condition makes no sense, " +
				"but was Condition.FALSE");
		final Condition unsupportedCondition = new MatchCondition(new StringField(), "literal");
		assertFails(
				() -> new CheckConstraint(unsupportedCondition),
				IllegalArgumentException.class,
				"not yet implemented: " + unsupportedCondition);
		final ExtremumAggregate<Integer> unsupportedFunction = new IntegerField().max();
		final Condition unsupportedFunctionCondition = unsupportedFunction.is(5);
		assertFails(
				() -> new CheckConstraint(unsupportedFunctionCondition),
				IllegalArgumentException.class,
				"check constraint condition contains unsupported function: " + unsupportedFunction);

		assertSerializedSame(alphaToBeta, 381);
	}
}
