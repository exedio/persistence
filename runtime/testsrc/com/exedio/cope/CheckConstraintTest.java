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

package com.exedio.cope;

import static com.exedio.cope.CheckConstraintItem.TYPE;
import static com.exedio.cope.CheckConstraintSuperItem.eins;
import static com.exedio.cope.CheckConstraintSuperItem.zwei;
import static com.exedio.cope.CheckConstraintSuperItem.drei;
import static com.exedio.cope.CheckConstraintSuperItem.einsGreaterOrEqualZwei;
import static com.exedio.cope.CheckConstraintItem.alpha;
import static com.exedio.cope.CheckConstraintItem.alphaLessBeta;
import static com.exedio.cope.CheckConstraintItem.beta;
import static com.exedio.cope.CheckConstraintItem.delta;
import static com.exedio.cope.CheckConstraintItem.gamma;

public class CheckConstraintTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CheckConstraintItem.TYPE, CheckConstraintSuperItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(CheckConstraintTest.class, "MODEL");
	}
	
	public CheckConstraintTest()
	{
		super(MODEL);
	}

	public void testItemWithSingleUnique()
	{
		// test model
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				alpha, beta, gamma, delta,
				alphaLessBeta),
			TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei,
				alpha, beta, gamma, delta,
				alphaLessBeta),
			TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				alphaLessBeta),
			TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei,
				alphaLessBeta),
			TYPE.getCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getCheckConstraints());
		
		assertSerializedSame(alphaLessBeta, 393);
	}
}
