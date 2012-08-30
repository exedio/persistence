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

package com.exedio.cope.pattern;

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaComposite.wrongTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.virgnComp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.wrongComp;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.misc.Computed;

public final class CompositeFieldRenamedSchemaTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(CompositeFieldRenamedSchemaTest.class, "MODEL");
	}

	public CompositeFieldRenamedSchemaTest()
	{
		super(MODEL);
	}

	public static void testIt()
	{
		assertTrue(comp(virgnComp.of(virgnTemp)));
		assertFalse(comp(virgnComp));
		assertFalse(comp(virgnTemp));

		assertEquals(null,        ann(virgnTemp));
		assertEquals("namedTemp", ann(wrongTemp));
		assertEquals(null,        ann(virgnComp));
		assertEquals("namedComp", ann(wrongComp));

		assertEquals(null,                  ann(virgnComp.of(virgnTemp)));
		assertEquals("virgnComp-namedTemp", ann(virgnComp.of(wrongTemp)));
		assertEquals("namedComp-virgnTemp", ann(wrongComp.of(virgnTemp)));
		assertEquals("namedComp-namedTemp", ann(wrongComp.of(wrongTemp)));

		assertEquals("virgnComp_virgnTemp", getColumnName(virgnComp.of(virgnTemp)));
		assertEquals("virgnComp_namedTemp", getColumnName(virgnComp.of(wrongTemp)));
		assertEquals("namedComp_virgnTemp", getColumnName(wrongComp.of(virgnTemp)));
		assertEquals("namedComp_namedTemp", getColumnName(wrongComp.of(wrongTemp)));
	}

	private static String ann(final Feature f)
	{
		final CopeSchemaName a = f.getAnnotation(CopeSchemaName.class);
		return a!=null ? a.value() : null;
	}

	private static boolean comp(final Feature f)
	{
		final boolean result = f.isAnnotationPresent(Computed.class);
		assertEquals(result, f.getAnnotation(Computed.class)!=null);
		return result;
	}
}
