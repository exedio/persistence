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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaComposite.virgnTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaComposite.wrongTemp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.TYPE;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.virgnComp;
import static com.exedio.cope.pattern.CompositeFieldRenamedSchemaItem.wrongComp;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import org.junit.jupiter.api.Test;

public class CompositeFieldRenamedSchemaTest extends TestWithEnvironment
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

	@Test void testIt()
	{
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
}
