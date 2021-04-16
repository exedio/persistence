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

import static com.exedio.cope.sampler.Stuff.sampler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.CopeExternal;
import com.exedio.cope.Type;
import com.exedio.cope.util.JobContexts;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class SamplerTest
{
	@Test void testToString()
	{
		assertEquals("Sampler#com.exedio.cope.sampler.Stuff#MODEL", sampler.toString());
	}

	@Test void testModelToString()
	{
		assertEquals("Sampler(com.exedio.cope.sampler.Stuff#MODEL)", sampler.getModel().toString());
	}

	@Test void testConstructorNull()
	{
		try
		{
			new Sampler(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("sampledModel", e.getMessage());
		}
	}

	@Test void testPurgeNegativeDays()
	{
		try
		{
			sampler.purge(0, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("0", e.getMessage());
		}
	}

	@Test void testPurgeNullContext()
	{
		try
		{
			sampler.purge(1, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	@Test void testPurgeNullLimit()
	{
		try
		{
			sampler.purge((Date)null, JobContexts.EMPTY);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("limit", e.getMessage());
		}
	}

	@Test void testPurgeExternal()
	{
		for(final Type<?> type : sampler.getModel().getTypes())
			assertEquals(
					type.isAnnotationPresent(Purgeable.class),
					type.isAnnotationPresent(CopeExternal.class),
					type.getID());
	}
}
