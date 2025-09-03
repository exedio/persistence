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

import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Node.Color.WARNING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class SequenceTest extends SchemaReadyTest
{
	private static final String NAME = "SomeSequence";

	private Sequence sequence;

	@Override
	protected Schema getSchema()
	{
		final Schema result = newSchema();

		sequence = result.newSequence(NAME, Sequence.Type.bit63, 55);

		return result;
	}

	@Test void testCreateDrop()
	{
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(true, sequence.exists());
			assertEquals(null, sequence.getError());
			assertEquals(OK, sequence.getParticularColor());

			sequence.drop();
		}
		// SEQUENCE DROPPED
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(false, sequence.exists());
			assertEquals("missing", sequence.getError());
			assertEquals(ERROR, sequence.getParticularColor());

			sequence.create();
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(true, sequence.exists());
			assertEquals(null, sequence.getError());
			assertEquals(OK, sequence.getParticularColor());
		}
	}

	@Test void testRename()
	{
		final String NEW_NAME = NAME + "X";
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(true, sequence.exists());
			assertEquals(null, sequence.getError());
			assertEquals(OK, sequence.getParticularColor());

			sequence.renameTo(NEW_NAME);
		}
		// SEQUENCE RENAMED
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(false, sequence.exists());
			assertEquals("missing", sequence.getError());
			assertEquals(ERROR, sequence.getParticularColor());

			if(mysql)
			{
				// TODO should be recognized as sequence, not as table
				assertEquals(null, schema.getSequence(NEW_NAME));
				final Table newSequence = schema.getTable(NEW_NAME);
				assertNotNull(newSequence);
				assertEquals(false, newSequence.required());
				assertEquals(true, newSequence.exists());
				assertEquals("unused", newSequence.getError());
				assertEquals(WARNING, newSequence.getParticularColor());

				newSequence.renameTo(NAME);
			}
			else
			{
				final Sequence newSequence = schema.getSequence(NEW_NAME);
				assertNotSame(sequence, newSequence);
				assertNotNull(newSequence);
				assertEquals(false, newSequence.required());
				assertEquals(true, newSequence.exists());
				assertEquals("unused", newSequence.getError());
				assertEquals(WARNING, newSequence.getParticularColor());

				newSequence.renameTo(NAME);
			}
		}
		// OK
		{
			final Schema schema = getVerifiedSchema();

			assertSame(sequence, schema.getSequence(NAME));
			assertNotNull(sequence);
			assertEquals(true, sequence.required());
			assertEquals(true, sequence.exists());
			assertEquals(null, sequence.getError());
			assertEquals(OK, sequence.getParticularColor());
		}
	}
}
