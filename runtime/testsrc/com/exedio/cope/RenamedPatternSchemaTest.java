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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class RenamedPatternSchemaTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(PatternItem.TYPE, RawItem.TYPE);

	public RenamedPatternSchemaTest()
	{
		super(MODEL);
	}

	@Test void testSchema()
	{
		assertEquals(true,  PatternItem.pattern.sourceFeature.isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, PatternItem.pattern.sourceFeature.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  PatternItem.pattern.getSourceType().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, PatternItem.pattern.getSourceType().isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  PatternItem.pattern.getSourceTypePostfix().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, PatternItem.pattern.getSourceTypePostfix().isAnnotationPresent(TestAnnotation2.class));

		assertEquals("sourceFeature-TestAnnotation", PatternItem.pattern.sourceFeature.getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , PatternItem.pattern.getSourceType().getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , PatternItem.pattern.getSourceTypePostfix().getAnnotation(TestAnnotation.class).value());
		assertEquals(null, PatternItem.pattern.sourceFeature.getAnnotation(TestAnnotation2.class));
		assertEquals(null, PatternItem.pattern.getSourceType().getAnnotation(TestAnnotation2.class));
		assertEquals(null, PatternItem.pattern.getSourceTypePostfix().getAnnotation(TestAnnotation2.class));
	}

	@Test void testSchemaAnnotations()
	{
		assertEquals("ZackItem", schemaName(PatternItem.TYPE));
		assertEquals(null, schemaName(RawItem.TYPE));
		assertEquals("zack-sourceFeature", schemaName(PatternItem.pattern.sourceFeature));
		assertEquals(null, schemaName(PatternItem.raw.sourceFeature));
		assertEquals("zack-sourceFeature", schemaName(RawItem.pattern.sourceFeature));
		assertEquals(null, schemaName(RawItem.raw.sourceFeature));
		assertEquals("ZackItem-zack", schemaName(PatternItem.pattern.getSourceType()));
		assertEquals("ZackItem-zack-tail", schemaName(PatternItem.pattern.getSourceTypePostfix()));
		assertEquals("ZackItem-raw", schemaName(PatternItem.raw.getSourceType()));
		assertEquals("ZackItem-raw-tail", schemaName(PatternItem.raw.getSourceTypePostfix()));
		assertEquals("RawItem-zack", schemaName(RawItem.pattern.getSourceType()));
		assertEquals("RawItem-zack-tail", schemaName(RawItem.pattern.getSourceTypePostfix()));
		assertEquals(null, schemaName(RawItem.raw.getSourceType()));
		assertEquals(null, schemaName(RawItem.raw.getSourceTypePostfix()));
		assertEquals(null, schemaName(PatternItem.pattern.sourceTypeField));
		assertEquals(null, schemaName(PatternItem.pattern.sourceTypePostfixField));
	}

	@Test void testSchemaNames()
	{
		assertEquals(filterTableName("ZackItem"), getTableName(PatternItem.TYPE));
		assertEquals(filterTableName("RawItem"), getTableName(RawItem.TYPE));
		assertPrimaryKeySequenceName("ZackItem_this_Seq", PatternItem.TYPE);
		assertPrimaryKeySequenceName("RawItem_this_Seq", RawItem.TYPE);
		assertEquals("zack_sourceFeature", getColumnName(PatternItem.pattern.sourceFeature));
		assertEquals("raw_sourceFeature", getColumnName(PatternItem.raw.sourceFeature));
		assertEquals("zack_sourceFeature", getColumnName(RawItem.pattern.sourceFeature));
		assertEquals("raw_sourceFeature", getColumnName(RawItem.raw.sourceFeature));
		assertEquals(filterTableName("ZackItem_zack"), getTableName(PatternItem.pattern.getSourceType()));
		assertEquals(filterTableName("ZackItem_zack_tail"), getTableName(PatternItem.pattern.getSourceTypePostfix()));
		assertEquals(filterTableName("ZackItem_raw"), getTableName(PatternItem.raw.getSourceType()));
		assertEquals(filterTableName("ZackItem_raw_tail"), getTableName(PatternItem.raw.getSourceTypePostfix()));
		assertEquals(filterTableName("RawItem_zack"), getTableName(RawItem.pattern.getSourceType()));
		assertEquals(filterTableName("RawItem_zack_tail"), getTableName(RawItem.pattern.getSourceTypePostfix()));
		assertEquals(filterTableName("RawItem_raw"), getTableName(RawItem.raw.getSourceType()));
		assertEquals(filterTableName("RawItem_raw_tail"), getTableName(RawItem.raw.getSourceTypePostfix()));
		assertPrimaryKeySequenceName("ZackItem_zack_this_Seq", PatternItem.pattern.getSourceType());
		assertPrimaryKeySequenceName("ZackItem_zack_tai_thi_Seq", "ZackItem_zac_tai_thi_Seq6", PatternItem.pattern.getSourceTypePostfix());
		assertPrimaryKeySequenceName("ZackItem_raw_this_Seq", PatternItem.raw.getSourceType());
		assertPrimaryKeySequenceName("ZackItem_raw_tail_thi_Seq", "ZackItem_raw_tai_thi_Seq6", PatternItem.raw.getSourceTypePostfix());
		assertPrimaryKeySequenceName("RawItem_zack_this_Seq", RawItem.pattern.getSourceType());
		assertPrimaryKeySequenceName("RawItem_zack_tail_thi_Seq", "RawItem_zack_tai_thi_Seq6", RawItem.pattern.getSourceTypePostfix());
		assertPrimaryKeySequenceName("RawItem_raw_this_Seq", RawItem.raw.getSourceType());
		assertPrimaryKeySequenceName("RawItem_raw_tail_this_Seq", "RawItem_raw_tail_thi_Seq6", RawItem.raw.getSourceTypePostfix());
		assertEquals("field", getColumnName(PatternItem.pattern.sourceTypeField));
		assertEquals("field", getColumnName(PatternItem.pattern.sourceTypePostfixField));
	}

	private static String schemaName(final Type<?> type)
	{
		final CopeSchemaName ann = type.getAnnotation(CopeSchemaName.class);
		assertEquals(ann!=null, type.isAnnotationPresent(CopeSchemaName.class));
		return ann!=null ? ann.value() : null;
	}

	private static String schemaName(final Field<?> feature)
	{
		final CopeSchemaName ann = feature.getAnnotation(CopeSchemaName.class);
		assertEquals(ann!=null, feature.isAnnotationPresent(CopeSchemaName.class));
		return ann!=null ? ann.value() : null;
	}

	@CopeSchemaName("ZackItem")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class PatternItem extends Item
	{
		@CopeSchemaName("zack")
		static final RenamedSchemaPattern pattern = new RenamedSchemaPattern();

		static final RenamedSchemaPattern raw = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<PatternItem> TYPE = com.exedio.cope.TypesBound.newType(PatternItem.class);

		@com.exedio.cope.instrument.Generated
		private PatternItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class RawItem extends Item
	{
		@CopeSchemaName("zack")
		static final RenamedSchemaPattern pattern = new RenamedSchemaPattern();

		static final RenamedSchemaPattern raw = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<RawItem> TYPE = com.exedio.cope.TypesBound.newType(RawItem.class);

		@com.exedio.cope.instrument.Generated
		private RawItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
