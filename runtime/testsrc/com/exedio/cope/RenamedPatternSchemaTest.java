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
import static org.junit.jupiter.api.Assertions.assertNull;

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
		assertEquals(true,  VeilI.veilF.bareSF.isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.bareSF.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.srcT    ().isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.srcT    ().isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.srcTtail().isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.srcTtail().isAnnotationPresent(TestAnnotation2.class));

		assertEquals("bareSF-TestAnnotation", VeilI.veilF.bareSF.getAnnotation(TestAnnotation.class).value());
		assertEquals("srcT-TestAnnotation", VeilI.veilF.srcT    ().getAnnotation(TestAnnotation.class).value());
		assertEquals("srcT-TestAnnotation", VeilI.veilF.srcTtail().getAnnotation(TestAnnotation.class).value());
		assertEquals(null, VeilI.veilF.bareSF.getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.srcT    ().getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.srcTtail().getAnnotation(TestAnnotation2.class));
	}

	@Test void testSchemaAnnotations()
	{
		assertNull(VeilI.emptF.emptSF);
		assertNull(BareI.emptF.emptSF);

		assertEquals("CoatI", schemaName(VeilI.TYPE));
		assertEquals(null,    schemaName(BareI.TYPE));
		assertEquals("coatF-coatSF", schemaName(VeilI.veilF.veilSF));
		assertEquals("coatF",        schemaName(VeilI.veilF.emptSF));
		assertEquals("coatF-bareSF", schemaName(VeilI.veilF.bareSF));
		assertEquals(      "coatSF", schemaName(VeilI.emptF.veilSF));
		assertEquals(      "bareSF", schemaName(VeilI.emptF.bareSF));
		assertEquals("bareF-coatSF", schemaName(VeilI.bareF.veilSF));
		assertEquals("bareF",        schemaName(VeilI.bareF.emptSF));
		assertEquals(null,           schemaName(VeilI.bareF.bareSF));
		assertEquals("coatF-coatSF", schemaName(BareI.veilF.veilSF));
		assertEquals("coatF",        schemaName(BareI.veilF.emptSF));
		assertEquals("coatF-bareSF", schemaName(BareI.veilF.bareSF));
		assertEquals(      "coatSF", schemaName(BareI.emptF.veilSF));
		assertEquals(      "bareSF", schemaName(BareI.emptF.bareSF));
		assertEquals("bareF-coatSF", schemaName(BareI.bareF.veilSF));
		assertEquals("bareF",        schemaName(BareI.bareF.emptSF));
		assertEquals(null,           schemaName(BareI.bareF.bareSF));
		assertEquals("CoatI-coatF",      schemaName(VeilI.veilF.srcT    ()));
		assertEquals("CoatI-coatF-tail", schemaName(VeilI.veilF.srcTtail()));
		assertEquals("CoatI-default",    schemaName(VeilI.emptF.srcT    ()));
		assertEquals("CoatI-tail",       schemaName(VeilI.emptF.srcTtail()));
		assertEquals("CoatI-bareF",      schemaName(VeilI.bareF.srcT    ()));
		assertEquals("CoatI-bareF-tail", schemaName(VeilI.bareF.srcTtail()));
		assertEquals("BareI-coatF",      schemaName(BareI.veilF.srcT    ()));
		assertEquals("BareI-coatF-tail", schemaName(BareI.veilF.srcTtail()));
		assertEquals("BareI-default",    schemaName(BareI.emptF.srcT    ()));
		assertEquals("BareI-tail",       schemaName(BareI.emptF.srcTtail()));
		assertEquals(null, schemaName(BareI.bareF.srcT    ()));
		assertEquals(null, schemaName(BareI.bareF.srcTtail()));
		assertEquals(null, schemaName(VeilI.veilF.srcTField));
		assertEquals(null, schemaName(VeilI.veilF.srcTtailField));
		assertEquals(null, schemaName(VeilI.emptF.srcTField));
		assertEquals(null, schemaName(VeilI.emptF.srcTtailField));
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
		assertNull(VeilI.emptF.emptSF);
		assertNull(BareI.emptF.emptSF);

		assertEquals("CoatI", getTableName(VeilI.TYPE));
		assertEquals("BareI", getTableName(BareI.TYPE));
		assertPrimaryKeySequenceName("CoatI_this_Seq", VeilI.TYPE);
		assertPrimaryKeySequenceName("BareI_this_Seq", BareI.TYPE);
		assertEquals("coatF_coatSF", getColumnName(VeilI.veilF.veilSF));
		assertEquals("coatF",        getColumnName(VeilI.veilF.emptSF));
		assertEquals("coatF_bareSF", getColumnName(VeilI.veilF.bareSF));
		assertEquals(      "coatSF", getColumnName(VeilI.emptF.veilSF));
		assertEquals(      "bareSF", getColumnName(VeilI.emptF.bareSF));
		assertEquals("bareF_coatSF", getColumnName(VeilI.bareF.veilSF));
		assertEquals("bareF",        getColumnName(VeilI.bareF.emptSF));
		assertEquals("bareF_bareSF", getColumnName(VeilI.bareF.bareSF));
		assertEquals("coatF_coatSF", getColumnName(BareI.veilF.veilSF));
		assertEquals("coatF",        getColumnName(BareI.veilF.emptSF));
		assertEquals("coatF_bareSF", getColumnName(BareI.veilF.bareSF));
		assertEquals(      "coatSF", getColumnName(BareI.emptF.veilSF));
		assertEquals(      "bareSF", getColumnName(BareI.emptF.bareSF));
		assertEquals("bareF_coatSF", getColumnName(BareI.bareF.veilSF));
		assertEquals("bareF",        getColumnName(BareI.bareF.emptSF));
		assertEquals("bareF_bareSF", getColumnName(BareI.bareF.bareSF));
		assertEquals("CoatI_coatF",      getTableName(VeilI.veilF.srcT    ()));
		assertEquals("CoatI_coatF_tail", getTableName(VeilI.veilF.srcTtail()));
		assertEquals("CoatI_default",    getTableName(VeilI.emptF.srcT    ()));
		assertEquals("CoatI_tail",       getTableName(VeilI.emptF.srcTtail()));
		assertEquals("CoatI_bareF",      getTableName(VeilI.bareF.srcT    ()));
		assertEquals("CoatI_bareF_tail", getTableName(VeilI.bareF.srcTtail()));
		assertEquals("BareI_coatF",      getTableName(BareI.veilF.srcT    ()));
		assertEquals("BareI_coatF_tail", getTableName(BareI.veilF.srcTtail()));
		assertEquals("BareI_default",    getTableName(BareI.emptF.srcT    ()));
		assertEquals("BareI_tail",       getTableName(BareI.emptF.srcTtail()));
		assertEquals("BareI_bareF",      getTableName(BareI.bareF.srcT    ()));
		assertEquals("BareI_bareF_tail", getTableName(BareI.bareF.srcTtail()));
		assertPrimaryKeySequenceName("CoatI_coatF_this_Seq", VeilI.veilF.srcT());
		assertPrimaryKeySequenceName("CoatI_coatF_tail_this_Seq", "CoatI_coatF_tail_thi_Seq6", VeilI.veilF.srcTtail());
		assertPrimaryKeySequenceName("CoatI_default_this_Seq",VeilI.emptF.srcT());
		assertPrimaryKeySequenceName("CoatI_tail_this_Seq",       "CoatI_tail_this_Seq6", VeilI.emptF.srcTtail());
		assertPrimaryKeySequenceName("CoatI_bareF_this_Seq", VeilI.bareF.srcT());
		assertPrimaryKeySequenceName("CoatI_bareF_tail_this_Seq", "CoatI_bareF_tail_thi_Seq6", VeilI.bareF.srcTtail());
		assertPrimaryKeySequenceName("BareI_coatF_this_Seq", BareI.veilF.srcT());
		assertPrimaryKeySequenceName("BareI_coatF_tail_this_Seq", "BareI_coatF_tail_thi_Seq6", BareI.veilF.srcTtail());
		assertPrimaryKeySequenceName("BareI_default_this_Seq",BareI.emptF.srcT());
		assertPrimaryKeySequenceName("BareI_tail_this_Seq",       "BareI_tail_this_Seq6", BareI.emptF.srcTtail());
		assertPrimaryKeySequenceName("BareI_bareF_this_Seq", BareI.bareF.srcT());
		assertPrimaryKeySequenceName("BareI_bareF_tail_this_Seq", "BareI_bareF_tail_thi_Seq6", BareI.bareF.srcTtail());
		assertEquals("field", getColumnName(VeilI.veilF.srcTField));
		assertEquals("field", getColumnName(VeilI.veilF.srcTtailField));
		assertEquals("field", getColumnName(VeilI.emptF.srcTField));
		assertEquals("field", getColumnName(VeilI.emptF.srcTtailField));
	}

	@CopeSchemaName("CoatI")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class VeilI extends Item
	{
		@CopeSchemaName("coatF")
		static final RenamedSchemaPattern veilF = new RenamedSchemaPattern();

		@CopeSchemaName("")
		static final RenamedSchemaPattern emptF = new RenamedSchemaPattern(false);

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<VeilI> TYPE = com.exedio.cope.TypesBound.newType(VeilI.class,VeilI::new);

		@com.exedio.cope.instrument.Generated
		private VeilI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class BareI extends Item
	{
		@CopeSchemaName("coatF")
		static final RenamedSchemaPattern veilF = new RenamedSchemaPattern();

		@CopeSchemaName("")
		static final RenamedSchemaPattern emptF = new RenamedSchemaPattern(false);

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BareI> TYPE = com.exedio.cope.TypesBound.newType(BareI.class,BareI::new);

		@com.exedio.cope.instrument.Generated
		private BareI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
