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

import static com.exedio.cope.RenamedPatternSchemaItem.TYPE;
import static com.exedio.cope.RenamedPatternSchemaItem.pattern;
import static com.exedio.cope.RenamedPatternSchemaItem.raw;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;

public class RenamedPatternSchemaTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE);
	
	public RenamedPatternSchemaTest()
	{
		super(MODEL);
	}

	public void testSchema()
	{
		if(postgresql) return;
		
		assertEquals(true,  pattern.sourceFeature.isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, pattern.sourceFeature.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  pattern.getSourceType().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, pattern.getSourceType().isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  pattern.getSourceTypePostfix().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, pattern.getSourceTypePostfix().isAnnotationPresent(TestAnnotation2.class));
		
		assertEquals("sourceFeature-TestAnnotation", pattern.sourceFeature.getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , pattern.getSourceType().getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , pattern.getSourceTypePostfix().getAnnotation(TestAnnotation.class).value());
		assertEquals(null, pattern.sourceFeature.getAnnotation(TestAnnotation2.class));
		assertEquals(null, pattern.getSourceType().getAnnotation(TestAnnotation2.class));
		assertEquals(null, pattern.getSourceTypePostfix().getAnnotation(TestAnnotation2.class));
		
		assertEquals(filterTableName("ZackItem"), getTableName(TYPE));
		assertEquals("zack_sourceFeature", getColumnName(pattern.sourceFeature));
		assertEquals("raw_sourceFeature", getColumnName(raw.sourceFeature));
		assertEquals(filterTableName("ZackItem_zack"), getTableName(pattern.getSourceType()));
		assertEquals(filterTableName("ZackItem_zack_tail"), getTableName(pattern.getSourceTypePostfix()));
		assertEquals(filterTableName("ZackItem_raw"), getTableName(raw.getSourceType()));
		assertEquals(filterTableName("ZackItem_raw_tail"), getTableName(raw.getSourceTypePostfix()));
		assertEquals("field", getColumnName(pattern.sourceTypeField));
		assertEquals("field", getColumnName(pattern.sourceTypePostfixField));
	}
}
