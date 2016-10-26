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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import org.junit.Test;

public class MediaManyContentTypeTest
{
	@Test public void test()
	{
		final Media[] medias = new Media[] {
			new Media(),
			new Media().contentType("ct/0"),
			new Media().contentType("ct/0", "ct/1"),
			new Media().contentType("ct/0", "ct/1", "ct/2"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7", "ct/8"),
			new Media().contentType("ct/0", "ct/1", "ct/2", "ct/3", "ct/4", "ct/5", "ct/6", "ct/7", "ct/8", "ct/9"),
		};

		assertEquals(null, medias[0].getContentTypesAllowed());

		for(int index = 1; index<medias.length; index++)
		{
			final ArrayList<String> expected = new ArrayList<>();
			for(int i = 0; i<index; i++)
				expected.add("ct/" + i);

			assertEquals(expected, medias[index].getContentTypesAllowed());
		}
	}
}
