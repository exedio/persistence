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
	private static final Model MODEL = new Model(VeilI.TYPE, BareI.TYPE);

	public RenamedPatternSchemaTest()
	{
		super(MODEL);
	}

	@Test void testSchema()
	{
		assertEquals(true,  VeilI.veilF.sourceFeature.isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, VeilI.veilF.sourceFeature.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.getSourceType().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, VeilI.veilF.getSourceType().isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.getSourceTypePostfix().isAnnotationPresent(TestAnnotation.class));
		assertEquals(false, VeilI.veilF.getSourceTypePostfix().isAnnotationPresent(TestAnnotation2.class));

		assertEquals("sourceFeature-TestAnnotation", VeilI.veilF.sourceFeature.getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , VeilI.veilF.getSourceType().getAnnotation(TestAnnotation.class).value());
		assertEquals("sourceType-TestAnnotation"   , VeilI.veilF.getSourceTypePostfix().getAnnotation(TestAnnotation.class).value());
		assertEquals(null, VeilI.veilF.sourceFeature.getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.getSourceType().getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.getSourceTypePostfix().getAnnotation(TestAnnotation2.class));
	}

	@Test void testSchemaAnnotations()
	{
		assertEquals("CoatI", schemaName(VeilI.TYPE));
		assertEquals(null, schemaName(BareI.TYPE));
		assertEquals("coatF-sourceFeature", schemaName(VeilI.veilF.sourceFeature));
		assertEquals(null, schemaName(VeilI.bareF.sourceFeature));
		assertEquals("coatF-sourceFeature", schemaName(BareI.veilF.sourceFeature));
		assertEquals(null, schemaName(BareI.bareF.sourceFeature));
		assertEquals("CoatI-coatF", schemaName(VeilI.veilF.getSourceType()));
		assertEquals("CoatI-coatF-tail", schemaName(VeilI.veilF.getSourceTypePostfix()));
		assertEquals("CoatI-bareF", schemaName(VeilI.bareF.getSourceType()));
		assertEquals("CoatI-bareF-tail", schemaName(VeilI.bareF.getSourceTypePostfix()));
		assertEquals("BareI-coatF", schemaName(BareI.veilF.getSourceType()));
		assertEquals("BareI-coatF-tail", schemaName(BareI.veilF.getSourceTypePostfix()));
		assertEquals(null, schemaName(BareI.bareF.getSourceType()));
		assertEquals(null, schemaName(BareI.bareF.getSourceTypePostfix()));
		assertEquals(null, schemaName(VeilI.veilF.sourceTypeField));
		assertEquals(null, schemaName(VeilI.veilF.sourceTypePostfixField));
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
		assertEquals(filterTableName("CoatI"), getTableName(VeilI.TYPE));
		assertEquals(filterTableName("BareI"), getTableName(BareI.TYPE));
		assertPrimaryKeySequenceName("CoatI_this_Seq", VeilI.TYPE);
		assertPrimaryKeySequenceName("BareI_this_Seq", BareI.TYPE);
		assertEquals("coatF_sourceFeature", getColumnName(VeilI.veilF.sourceFeature));
		assertEquals("bareF_sourceFeature", getColumnName(VeilI.bareF.sourceFeature));
		assertEquals("coatF_sourceFeature", getColumnName(BareI.veilF.sourceFeature));
		assertEquals("bareF_sourceFeature", getColumnName(BareI.bareF.sourceFeature));
		assertEquals(filterTableName("CoatI_coatF"), getTableName(VeilI.veilF.getSourceType()));
		assertEquals(filterTableName("CoatI_coatF_tail"), getTableName(VeilI.veilF.getSourceTypePostfix()));
		assertEquals(filterTableName("CoatI_bareF"), getTableName(VeilI.bareF.getSourceType()));
		assertEquals(filterTableName("CoatI_bareF_tail"), getTableName(VeilI.bareF.getSourceTypePostfix()));
		assertEquals(filterTableName("BareI_coatF"), getTableName(BareI.veilF.getSourceType()));
		assertEquals(filterTableName("BareI_coatF_tail"), getTableName(BareI.veilF.getSourceTypePostfix()));
		assertEquals(filterTableName("BareI_bareF"), getTableName(BareI.bareF.getSourceType()));
		assertEquals(filterTableName("BareI_bareF_tail"), getTableName(BareI.bareF.getSourceTypePostfix()));
		assertPrimaryKeySequenceName("CoatI_coatF_this_Seq", VeilI.veilF.getSourceType());
		assertPrimaryKeySequenceName("CoatI_coatF_tail_this_Seq", "CoatI_coatF_tail_thi_Seq6", VeilI.veilF.getSourceTypePostfix());
		assertPrimaryKeySequenceName("CoatI_bareF_this_Seq", VeilI.bareF.getSourceType());
		assertPrimaryKeySequenceName("CoatI_bareF_tail_this_Seq", "CoatI_bareF_tail_thi_Seq6", VeilI.bareF.getSourceTypePostfix());
		assertPrimaryKeySequenceName("BareI_coatF_this_Seq", BareI.veilF.getSourceType());
		assertPrimaryKeySequenceName("BareI_coatF_tail_this_Seq", "BareI_coatF_tail_thi_Seq6", BareI.veilF.getSourceTypePostfix());
		assertPrimaryKeySequenceName("BareI_bareF_this_Seq", BareI.bareF.getSourceType());
		assertPrimaryKeySequenceName("BareI_bareF_tail_this_Seq", "BareI_bareF_tail_thi_Seq6", BareI.bareF.getSourceTypePostfix());
		assertEquals("field", getColumnName(VeilI.veilF.sourceTypeField));
		assertEquals("field", getColumnName(VeilI.veilF.sourceTypePostfixField));
	}

	@CopeSchemaName("CoatI")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class VeilI extends Item
	{
		@CopeSchemaName("coatF")
		static final RenamedSchemaPattern veilF = new RenamedSchemaPattern();

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<VeilI> TYPE = com.exedio.cope.TypesBound.newType(VeilI.class);

		@com.exedio.cope.instrument.Generated
		private VeilI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class BareI extends Item
	{
		@CopeSchemaName("coatF")
		static final RenamedSchemaPattern veilF = new RenamedSchemaPattern();

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BareI> TYPE = com.exedio.cope.TypesBound.newType(BareI.class);

		@com.exedio.cope.instrument.Generated
		private BareI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
