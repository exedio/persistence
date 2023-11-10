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

import static com.exedio.cope.tojunit.Assert.assertFails;

import org.junit.jupiter.api.Test;

public class CopyConstraintOfflineTest
{
	@Test void testTargetNull()
	{
		final StringField copy = new StringField();
		assertFails(
				() -> copy.copyFrom(null),
				NullPointerException.class,
				"target");
	}
	@Test void testTargetNullSupplier()
	{
		final StringField copy = new StringField();
		assertFails(
				() -> copy.copyFrom(null, null),
				NullPointerException.class,
				"target");
	}
	@Test void testCopyNull()
	{
		final ItemField<?> target = ItemField.create(CopySimpleSource.class);
		assertFails(
				() -> target.copyTo(null),
				NullPointerException.class,
				"copy");
	}
	@Test void testCopyNullSupplier()
	{
		final ItemField<?> target = ItemField.create(CopySimpleSource.class);
		assertFails(
				() -> target.copyTo(null, null),
				NullPointerException.class,
				"copy");
	}
	@Test void testTemplateNull()
	{
		final ItemField<?> target = ItemField.create(CopySimpleSource.class);
		final StringField copy = new StringField();
		assertFails(
				() -> copy.copyFrom(target, null),
				NullPointerException.class,
				"template");
		assertFails(
				() -> target.copyTo(copy, null),
				NullPointerException.class,
				"template");
	}
}
