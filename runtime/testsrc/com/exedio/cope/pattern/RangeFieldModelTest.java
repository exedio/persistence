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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.RangeFieldItem.TYPE;
import static com.exedio.cope.pattern.RangeFieldItem.text;
import static com.exedio.cope.pattern.RangeFieldItem.valid;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Feature;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.Join;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.StringLengthViolationException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class RangeFieldModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(RangeFieldModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				valid,
				valid.getFrom(),
				valid.getTo(),
				valid.getUnison(),
				text,
				text.getFrom(),
				text.getTo(),
				text.getUnison(),
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				valid,
				valid.getFrom(),
				valid.getTo(),
				valid.getUnison(),
				text,
				text.getFrom(),
				text.getTo(),
				text.getUnison(),
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, valid.getFrom().getType());
		assertEquals(TYPE, valid.getTo().getType());
		assertEquals(TYPE, valid.getType());
		assertEquals("valid-from", valid.getFrom().getName());
		assertEquals("valid-to",   valid.getTo().getName());
		assertEquals("valid",     valid.getName());
		assertEquals(valid, valid.getFrom().getPattern());

		assertEquals(false, valid.isInitial());
		assertEquals(false, valid.isFinal());
		assertEquals(false, valid.isMandatory());
		assertEquals(false, valid.getFrom().isFinal());
		assertEquals(false, valid.getTo().isFinal());
		assertContains(IntegerRangeViolationException.class, valid.getInitialExceptions());
		assertSerializedSame(valid, 388);

		assertEquals(true, text.isInitial());
		assertEquals(true, text.isFinal());
		assertEquals(true, text.isMandatory());
		assertEquals(true, text.getFrom().isFinal());
		assertEquals(true, text.getTo().isFinal());
		assertContains(FinalViolationException.class, MandatoryViolationException.class, StringLengthViolationException.class, text.getInitialExceptions());
		assertSerializedSame(text, 387);
	}

	@Test void testUnison()
	{
		assertEquals(
				"RangeFieldItem.valid-from<=RangeFieldItem.valid-to",
				valid.getUnison().getCondition().toString());
		assertEquals(
				"RangeFieldItem.text-from<=RangeFieldItem.text-to",
				text.getUnison().getCondition().toString());
	}

	@Test void testBorderTemplateUnique()
	{
		try
		{
			RangeField.create(new IntegerField().unique());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("unique borderTemplate is not supported", e.getMessage());
		}
	}

	@Test void testContainsNull()
	{
		try
		{
			valid.contains(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("right", e.getMessage());
		}
	}

	@Test void testInitialType()
	{
		assertEquals("com.exedio.cope.pattern.Range<java.lang.Integer>", valid.getInitialType().toString());
		assertEquals("com.exedio.cope.pattern.Range<java.lang.String>" , text .getInitialType().toString());
	}

	@Test void testContainsBind()
	{
		final Query<?> q = TYPE.newQuery();
		assertEquals(
				"select this from RangeFieldItem",
				q.toString());
		q.setCondition(valid.contains(55));
		assertEquals(
				"select this from RangeFieldItem " +
				"where ((valid-from is null or valid-from<='55') and (valid-to is null or valid-to>='55'))",
				q.toString());
		final Join j = q.join(TYPE);
		q.setCondition(valid.contains(66).bind(j));
		assertEquals(
				"select this from RangeFieldItem " +
				"join RangeFieldItem r1 " +
				"where ((r1.valid-from is null or r1.valid-from<='66') " +
				"and (r1.valid-to is null or r1.valid-to>='66'))",
				q.toString());
	}
}
