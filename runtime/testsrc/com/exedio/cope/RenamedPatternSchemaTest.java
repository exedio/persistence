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
		assertEquals(true,  VeilI.veilF.srcF.isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.srcF.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.srcT    ().isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.srcT    ().isAnnotationPresent(TestAnnotation2.class));
		assertEquals(true,  VeilI.veilF.srcTtail().isAnnotationPresent(TestAnnotation .class));
		assertEquals(false, VeilI.veilF.srcTtail().isAnnotationPresent(TestAnnotation2.class));

		assertEquals("srcF-TestAnnotation", VeilI.veilF.srcF.getAnnotation(TestAnnotation.class).value());
		assertEquals("srcT-TestAnnotation", VeilI.veilF.srcT    ().getAnnotation(TestAnnotation.class).value());
		assertEquals("srcT-TestAnnotation", VeilI.veilF.srcTtail().getAnnotation(TestAnnotation.class).value());
		assertEquals(null, VeilI.veilF.srcF.getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.srcT    ().getAnnotation(TestAnnotation2.class));
		assertEquals(null, VeilI.veilF.srcTtail().getAnnotation(TestAnnotation2.class));
	}

	@Test void testSchemaAnnotations()
	{
		assertEquals("CoatI", schemaName(VeilI.TYPE));
		assertEquals(null,    schemaName(BareI.TYPE));
		assertEquals("coatF-srcF", schemaName(VeilI.veilF.srcF));
		assertEquals(     "-srcF", schemaName(VeilI.emptF.srcF)); // TODO
		assertEquals(null,         schemaName(VeilI.bareF.srcF));
		assertEquals("coatF-srcF", schemaName(BareI.veilF.srcF));
		assertEquals(     "-srcF", schemaName(BareI.emptF.srcF)); // TODO
		assertEquals(null,         schemaName(BareI.bareF.srcF));
		assertEquals("CoatI-coatF",      schemaName(VeilI.veilF.srcT    ()));
		assertEquals("CoatI-coatF-tail", schemaName(VeilI.veilF.srcTtail()));
		assertEquals("CoatI-",           schemaName(VeilI.emptF.srcT    ())); // TODO
		assertEquals("CoatI--tail",      schemaName(VeilI.emptF.srcTtail())); // TODO
		assertEquals("CoatI-bareF",      schemaName(VeilI.bareF.srcT    ()));
		assertEquals("CoatI-bareF-tail", schemaName(VeilI.bareF.srcTtail()));
		assertEquals("BareI-coatF",      schemaName(BareI.veilF.srcT    ()));
		assertEquals("BareI-coatF-tail", schemaName(BareI.veilF.srcTtail()));
		assertEquals("BareI-",           schemaName(BareI.emptF.srcT    ())); // TODO
		assertEquals("BareI--tail",      schemaName(BareI.emptF.srcTtail())); // TODO
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
		assertEquals(filterTableName("CoatI"), getTableName(VeilI.TYPE));
		assertEquals(filterTableName("BareI"), getTableName(BareI.TYPE));
		assertPrimaryKeySequenceName("CoatI_this_Seq", VeilI.TYPE);
		assertPrimaryKeySequenceName("BareI_this_Seq", BareI.TYPE);
		assertEquals("coatF_srcF", getColumnName(VeilI.veilF.srcF));
		assertEquals(     "-srcF", getColumnName(VeilI.emptF.srcF)); // TODO
		assertEquals("bareF_srcF", getColumnName(VeilI.bareF.srcF));
		assertEquals("coatF_srcF", getColumnName(BareI.veilF.srcF));
		assertEquals(     "-srcF", getColumnName(BareI.emptF.srcF)); // TODO
		assertEquals("bareF_srcF", getColumnName(BareI.bareF.srcF));
		assertEquals(filterTableName("CoatI_coatF"),      getTableName(VeilI.veilF.srcT    ()));
		assertEquals(filterTableName("CoatI_coatF_tail"), getTableName(VeilI.veilF.srcTtail()));
		assertEquals(filterTableName("CoatI_"),           getTableName(VeilI.emptF.srcT    ())); // TODO
		assertEquals(filterTableName("CoatI__tail"),      getTableName(VeilI.emptF.srcTtail())); // TODO
		assertEquals(filterTableName("CoatI_bareF"),      getTableName(VeilI.bareF.srcT    ()));
		assertEquals(filterTableName("CoatI_bareF_tail"), getTableName(VeilI.bareF.srcTtail()));
		assertEquals(filterTableName("BareI_coatF"),      getTableName(BareI.veilF.srcT    ()));
		assertEquals(filterTableName("BareI_coatF_tail"), getTableName(BareI.veilF.srcTtail()));
		assertEquals(filterTableName("BareI_"),           getTableName(BareI.emptF.srcT    ())); // TODO
		assertEquals(filterTableName("BareI__tail"),      getTableName(BareI.emptF.srcTtail())); // TODO
		assertEquals(filterTableName("BareI_bareF"),      getTableName(BareI.bareF.srcT    ()));
		assertEquals(filterTableName("BareI_bareF_tail"), getTableName(BareI.bareF.srcTtail()));
		assertPrimaryKeySequenceName("CoatI_coatF_this_Seq", VeilI.veilF.srcT());
		assertPrimaryKeySequenceName("CoatI_coatF_tail_this_Seq", "CoatI_coatF_tail_thi_Seq6", VeilI.veilF.srcTtail());
		assertPrimaryKeySequenceName("CoatI__this_Seq",      VeilI.emptF.srcT()); // TODO
		assertPrimaryKeySequenceName("CoatI__tail_this_Seq",      "CoatI__tail_this_Seq6", VeilI.emptF.srcTtail()); // TODO
		assertPrimaryKeySequenceName("CoatI_bareF_this_Seq", VeilI.bareF.srcT());
		assertPrimaryKeySequenceName("CoatI_bareF_tail_this_Seq", "CoatI_bareF_tail_thi_Seq6", VeilI.bareF.srcTtail());
		assertPrimaryKeySequenceName("BareI_coatF_this_Seq", BareI.veilF.srcT());
		assertPrimaryKeySequenceName("BareI_coatF_tail_this_Seq", "BareI_coatF_tail_thi_Seq6", BareI.veilF.srcTtail());
		assertPrimaryKeySequenceName("BareI__this_Seq",      BareI.emptF.srcT()); // TODO
		assertPrimaryKeySequenceName("BareI__tail_this_Seq",      "BareI__tail_this_Seq6", BareI.emptF.srcTtail()); // TODO
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
		static final RenamedSchemaPattern emptF = new RenamedSchemaPattern();

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

		@CopeSchemaName("")
		static final RenamedSchemaPattern emptF = new RenamedSchemaPattern();

		static final RenamedSchemaPattern bareF = new RenamedSchemaPattern();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BareI> TYPE = com.exedio.cope.TypesBound.newType(BareI.class);

		@com.exedio.cope.instrument.Generated
		private BareI(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
