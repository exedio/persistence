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

import java.io.Serial;
import org.junit.jupiter.api.Test;

public class SelfSourceFeaturePatternSourceTest
{
	@Test void test()
	{
		assertFails(
				MyPattern::new,
				IllegalArgumentException.class,
				"cannot become a source feature of itself: " +
				"com.exedio.cope.SelfSourceFeaturePatternSourceTest$MyPattern@xxx",
				s -> s.replaceAll("MyPattern@.*$", "MyPattern@xxx"));
	}

	private static final class MyPattern extends Pattern
	{
		MyPattern()
		{
			addSourceFeature(this, "source"); // This is nonsense !
		}

		@Serial
		private static final long serialVersionUID = 1l;
	}
}
