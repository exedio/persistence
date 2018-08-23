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

package com.exedio.dsmf;

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.Test;

public class AlreadyVerifiedTest extends SchemaReadyTest
{
	private static final String MESSAGE = "already verified";

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();
		result.newTable("AlrVerifiedTab").newColumn("someColumn", intType);
		result.newSequence("AlrVerifiedSeq", Sequence.Type.bit63, 55);
		return result;
	}

	@SuppressWarnings("Convert2MethodRef")
	@Test void test()
	{
		final Schema schema = getVerifiedSchema();

		assertFails(
				() -> schema.verify(),
				IllegalStateException.class, MESSAGE);

		assertFails(
				() -> schema.create(),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.create(null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.drop(),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.drop(null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.tearDown(),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.tearDown(null),
				IllegalStateException.class, MESSAGE);

		assertFails(
				() -> schema.createConstraints(null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.createConstraints(null, null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.dropConstraints(null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.dropConstraints(null, null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.tearDownConstraints(null),
				IllegalStateException.class, MESSAGE);
		assertFails(
				() -> schema.tearDownConstraints(null, null),
				IllegalStateException.class, MESSAGE);

		assertFails(
				() -> schema.checkUnsupportedConstraints(),
				IllegalStateException.class, MESSAGE);
	}
}
