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
	private static final Model MODEL = new Model(PatternItem.TYPE, BareI.TYPE);

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
		assertEquals(null, schemaName(BareI.TYPE));
		assertEquals("zack-sourceFeature", schemaName(PatternItem.pattern.sourceFeature));
		assertEquals(null, schemaName(PatternItem.bareF.sourceFeature));
		assertEquals("zack-sourceFeature", schemaName(BareI.pattern.sourceFeature));
		assertEquals(null, schemaName(BareI.bareF.sourceFeature));
		assertEquals("ZackItem-zack", schemaName(PatternItem.pattern.getSourceType()));
		assertEquals("ZackItem-zack-tail", schemaName(PatternItem.pattern.getSourceTypePostfix()));
		assertEquals("ZackItem-bareF", schemaName(PatternItem.bareF.getSourceType()));
		assertEquals("ZackItem-bareF-tail", schemaName(PatternItem.bareF.getSourceTypePostfix()));
		assertEquals("BareI-zack", schemaName(BareI.pattern.getSourceType()));
		assertEquals("BareI-zack-tail", schemaName(BareI.pattern.getSourceTypePostfix()));
		assertEquals(null, schemaName(BareI.bareF.getSourceType()));
		assertEquals(null, schemaName(BareI.bareF.getSourceTypePostfix()));
		assertEquals(null, schemaName(PatternItem.pattern.sourceTypeField));
		assertEquals(null, schemaName(PatternItem.pattern.sourceTypePostfixField));
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

	@Test void testSchemaNames()
	{
		assertEquals(filterTableName("ZackItem"), getTableName(PatternItem.TYPE));
		assertEquals(filterTableName("BareI"), getTableName(BareI.TYPE));
		assertPrimaryKeySequenceName("ZackItem_this_Seq", PatternItem.TYPE);
		assertPrimaryKeySequenceName("BareI_this_Seq", BareI.TYPE);
		assertEquals("zack_sourceFeature", getColumnName(PatternItem.pattern.sourceFeature));
		assertEquals("bareF_sourceFeature", getColumnName(PatternItem.bareF.sourceFeature));
		assertEquals("zack_sourceFeature", getColumnName(BareI.pattern.sourceFeature));
		assertEquals("bareF_sourceFeature", getColumnName(BareI.bareF.sourceFeature));
		assertEquals(filterTableName("ZackItem_zack"), getTableName(PatternItem.pattern.getSourceType()));
		assertEquals(filterTableName("ZackItem_zack_tail"), getTableName(PatternItem.pattern.getSourceTypePostfix()));
		assertEquals(filterTableName("ZackItem_bareF"), getTableName(PatternItem.bareF.getSourceType()));
		assertEquals(filterTableName("ZackItem_bareF_tail"), getTableName(PatternItem.bareF.getSourceTypePostfix()));
		assertEquals(filterTableName("BareI_zack"), getTableName(BareI.pattern.getSourceType()));
		assertEquals(filterTableName("BareI_zack_tail"), getTableName(BareI.pattern.getSourceTypePostfix()));
		assertEquals(filterTableName("BareI_bareF"), getTableName(BareI.bareF.getSourceType()));
		assertEquals(filterTableName("BareI_bareF_tail"), getTableName(BareI.bareF.getSourceTypePostfix()));
		assertPrimaryKeySequenceName("ZackItem_zack_this_Seq", PatternItem.pattern.getSourceType());
		assertPrimaryKeySequenceName("ZackItem_zack_tai_thi_Seq", "ZackItem_zac_tai_thi_Seq6", PatternItem.pattern.getSourceTypePostfix());
		assertPrimaryKeySequenceName("ZackItem_bareF_this_Seq", PatternItem.bareF.getSourceType());
		assertPrimaryKeySequenceName("ZackItem_barF_tai_thi_Seq", "ZackItem_barF_tai_th_Seq6", PatternItem.bareF.getSourceTypePostfix());
		assertPrimaryKeySequenceName("BareI_zack_this_Seq", BareI.pattern.getSourceType());
		assertPrimaryKeySequenceName("BareI_zack_tail_this_Seq", "BareI_zack_tail_this_Seq6", BareI.pattern.getSourceTypePostfix());
		assertPrimaryKeySequenceName("BareI_bareF_this_Seq", BareI.bareF.getSourceType());
		assertPrimaryKeySequenceName("BareI_bareF_tail_this_Seq", "BareI_bareF_tail_thi_Seq6", BareI.bareF.getSourceTypePostfix());
		assertEquals("field", getColumnName(PatternItem.pattern.sourceTypeField));
		assertEquals("field", getColumnName(PatternItem.pattern.sourceTypePostfixField));
	}

	@CopeSchemaName("ZackItem")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class PatternItem extends Item
	{
		@CopeSchemaName("zack")
		static final RenamedSchemaPattern pattern = new RenamedSchemaPattern();

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<PatternItem> TYPE = com.exedio.cope.TypesBound.newType(PatternItem.class);

		@com.exedio.cope.instrument.Generated
		private PatternItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class BareI extends Item
	{
		@CopeSchemaName("zack")
		static final RenamedSchemaPattern pattern = new RenamedSchemaPattern();

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BareI> TYPE = com.exedio.cope.TypesBound.newType(BareI.class);

		@com.exedio.cope.instrument.Generated
		private BareI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
