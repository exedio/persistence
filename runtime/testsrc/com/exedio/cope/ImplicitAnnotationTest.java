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

import static com.exedio.cope.ImplicitAnnotationTest.MyItem.TYPE;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.misc.Computed;
import com.exedio.dsmf.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.Test;

public class ImplicitAnnotationTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public ImplicitAnnotationTest()
	{
		super(MODEL);
	}

	@Test public void testIt()
	{
		final UniqueConstraint uniqCon = uniqFeature.getImplicitUniqueConstraint();
		final Sequence nextSeq = nextFeature.getDefaultNext();
		final CopyConstraint copyFrom = TYPE.getCopyConstraints().get(0);
		final CopyConstraint copyTo = TYPE.getCopyConstraints().get(1);
		final UniqueConstraint uniqComputedCon = uniqComputed.getImplicitUniqueConstraint();
		final Sequence nextComputedSeq = nextComputed.getDefaultNext();
		final CopyConstraint copyComputedFrom = TYPE.getCopyConstraints().get(2);
		final CopyConstraint copyComputedTo = TYPE.getCopyConstraints().get(3);
		assertEquals(asList(copyFrom, copyTo, copyComputedFrom, copyComputedTo), TYPE.getCopyConstraints());

		assertIt("uniq", "uniq", "uniqSchema", "uniqSchema", false, "uniqArbi", uniqFeature);
		assertIt("next", "next", "nextSchema", "nextSchema", false, "nextArbi", nextFeature);
		assertIt("copy", "copy", "copySchema", "copySchema", false, "copyArbi", copyFeature);
		assertIt("targ", "targ", "targSchema", "targSchema", false, "targArbi", targFeature);
		assertIt("temp", "temp", "tempSchema", "tempSchema", false, "tempArbi", tempFeature);

		assertIt("uniqComputed", null, "uniqComputed", null, true, null, uniqComputed);
		assertIt("nextComputed", null, "nextComputed", null, true, null, nextComputed);
		assertIt("copyComputed", null, "copyComputed", null, true, null, copyComputed);
		assertIt("targComputed", null, "targComputed", null, true, null, targComputed);
		assertIt("tempComputed", null, "tempComputed", null, true, null, tempComputed);

		assertIt("uniqImplicitUnique", null,   "MyItem_uniqSchema_Unq", null,             false, null,       uniqCon);
		assertIt("next-Seq",           "next", "MyItem_nextSchema_Seq", "nextSchema-Seq", false, "nextArbi", nextSeq);
		assertIt("copyCopyFromtarg", null, null, null, false, null, copyFrom);
		assertIt("tempCopyFromtarg", null, null, null, false, null, copyTo  );

		assertIt("uniqComputedImplicitUnique", null, "MyItem_uniqComputed_Unq", null, false, null, uniqComputedCon);
		assertIt("nextComputed-Seq",           null, "MyItem_nextComputed_Seq", null, true,  null, nextComputedSeq);
		assertIt("copyComputedCopyFromtargComputed", null, null, null, false, null, copyComputedFrom);
		assertIt("tempComputedCopyFromtargComputed", null, null, null, false, null, copyComputedTo  );
	}

	private static void assertIt(
			final String name, final String nameAnno,
			final String schema, final String schemaAnno,
			final boolean computed,
			final String arbitrary,
			final Field<?> f)
	{
		assertFeature(name, nameAnno, schema, SchemaInfo.getColumnName(f), schemaAnno, computed, arbitrary, f);
	}

	private static void assertIt(
			final String name, final String nameAnno,
			final String schema, final String schemaAnno,
			final boolean computed,
			final String arbitrary,
			final UniqueConstraint f)
	{
		final com.exedio.dsmf.Table table = MODEL.getSchema().getTable(SchemaInfo.getTableName(TYPE));
		assertNotNull(SchemaInfo.getTableName(TYPE), table);
		final Constraint constraint = table.getConstraint(schema);
		assertNotNull(schema, constraint);
		assertFeature(name, nameAnno, schema, constraint.getName(), schemaAnno, computed, arbitrary, f);
	}

	private static void assertIt(
			final String name, final String nameAnno,
			final String schema, final String schemaAnno,
			final boolean computed,
			final String arbitrary,
			final CopyConstraint f)
	{
		assertFeature(name, nameAnno, schema, null, schemaAnno, computed, arbitrary, f);
	}

	private void assertIt(
			final String name, final String nameAnno,
			final String schema, final String schemaAnno,
			final boolean computed,
			final String arbitrary,
			final Sequence f)
	{
		assertFeature(name, nameAnno, filterTableName(schema), SchemaInfo.getSequenceName(f), schemaAnno, computed, arbitrary, f);
	}

	private static void assertFeature(
			final String name, final String nameAnno,
			final String schema, final String actualSchema, final String schemaAnno,
			final boolean computed,
			final String arbitrary,
			final Feature f)
	{
		assertEquals("name", name, f.getName());

		final CopeName actualNameAnno = f.getAnnotation(CopeName.class);
		assertEquals("nameAnno", nameAnno, actualNameAnno!=null ? actualNameAnno.value() : null);
		assertEquals("nameAnno", nameAnno!=null, f.isAnnotationPresent(CopeName.class));

		assertEquals("schema", schema, actualSchema);

		final CopeSchemaName actualSchemaAnno = f.getAnnotation(CopeSchemaName.class);
		assertEquals("schemaAnno", schemaAnno, actualSchemaAnno!=null ? actualSchemaAnno.value() : null);
		assertEquals("schemaAnno", schemaAnno!=null, f.isAnnotationPresent(CopeSchemaName.class));

		assertEquals("computed", computed, f.isAnnotationPresent(Computed.class));
		assertEquals("computed", computed, f.getAnnotation(Computed.class)!=null);

		final Arbitrary actualArbitrary = f.getAnnotation(Arbitrary.class);
		assertEquals("anno", arbitrary, actualArbitrary!=null ? actualArbitrary.value() : null);
		assertEquals("anno", arbitrary!=null, f.isAnnotationPresent(Arbitrary.class));
	}


	@com.exedio.cope.instrument.WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		@CopeName("uniq")
		@CopeSchemaName("uniqSchema")
		@Arbitrary("uniqArbi")
		@WrapperIgnore
		static final StringField uniqFeature = new StringField().unique();

		@CopeName("next")
		@CopeSchemaName("nextSchema")
		@Arbitrary("nextArbi")
		@WrapperIgnore
		static final IntegerField nextFeature = new IntegerField().defaultToNext(456);

		@CopeName("copy")
		@CopeSchemaName("copySchema")
		@Arbitrary("copyArbi")
		@WrapperIgnore
		static final StringField copyFeature = new StringField().toFinal();

		@CopeName("targ")
		@CopeSchemaName("targSchema")
		@Arbitrary("targArbi")
		@WrapperIgnore
		static final ItemField<MyItem> targFeature = ItemField.create(MyItem.class).toFinal().copyTo(copyFeature);

		@CopeName("temp")
		@CopeSchemaName("tempSchema")
		@Arbitrary("tempArbi")
		@WrapperIgnore
		static final StringField tempFeature = new StringField().toFinal().copyFrom(targFeature);


		@Computed
		@WrapperIgnore
		static final StringField uniqComputed = new StringField().unique();

		@Computed
		@WrapperIgnore
		static final IntegerField nextComputed = new IntegerField().defaultToNext(789);

		@Computed
		@WrapperIgnore
		static final StringField copyComputed = new StringField().toFinal();

		@Computed
		@WrapperIgnore
		static final ItemField<MyItem> targComputed = ItemField.create(MyItem.class).toFinal().copyTo(copyComputed);

		@Computed
		@WrapperIgnore
		static final StringField tempComputed = new StringField().toFinal().copyFrom(targComputed);


		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Arbitrary
	{
		String value();
	}

	// workaround eclipse warnings about unused imports when using static imports instead
	static final StringField uniqFeature = MyItem.uniqFeature;
	static final IntegerField nextFeature = MyItem.nextFeature;
	static final StringField copyFeature = MyItem.copyFeature;
	static final ItemField<MyItem> targFeature = MyItem.targFeature;
	static final StringField tempFeature = MyItem.tempFeature;
	static final StringField uniqComputed = MyItem.uniqComputed;
	static final IntegerField nextComputed = MyItem.nextComputed;
	static final StringField copyComputed = MyItem.copyComputed;
	static final ItemField<MyItem> targComputed = MyItem.targComputed;
	static final StringField tempComputed = MyItem.tempComputed;
}
