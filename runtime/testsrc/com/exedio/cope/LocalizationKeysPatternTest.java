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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import com.exedio.cope.pattern.CompositeType;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LocalizationKeysPatternTest
{
	/**
	 * @see com.exedio.cope.pattern.SetFieldModelTest#testLocalizationKeys()
	 * @see com.exedio.cope.pattern.DispatcherModelTest#testLocalizationKeys()
	 * @see com.exedio.cope.pattern.BlockFieldStandardModelTest#testLocalizationKeys()
	 */
	@Test public void testVerbose()
	{
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyComp.compField",
				"LocalizationKeysPatternTest.MyComp.compField",
				"compField"),
				MyComp.compField.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyItem.itemPatt",
				"LocalizationKeysPatternTest.MyItem.itemPatt",
				"itemPatt"),
				MyItem.itemPatt.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyItem.itemPatt.sourceFeature",
				"LocalizationKeysPatternTest.MyItem.itemPatt.sourceFeature",
				"com.exedio.cope.LocalizationKeysPatternTest.MyPatt.sourceFeature",
				"LocalizationKeysPatternTest.MyPatt.sourceFeature",
				"sourceFeature"),
				MyItem.itemPatt.sourceFeature.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyItem.itemComp.compField",
				"LocalizationKeysPatternTest.MyItem.itemComp.compField",
				"com.exedio.cope.LocalizationKeysPatternTest.MyComp.compField",
				"LocalizationKeysPatternTest.MyComp.compField",
				"compField"),
				MyItem.itemComp.of(MyComp.compField).getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyItem.itemPatt.SourceItemZack",
				"LocalizationKeysPatternTest.MyItem.itemPatt.SourceItemZack",
				"itemPatt.SourceItemZack",
				"com.exedio.cope.LocalizationKeysPatternTest.MyPatt.SourceItem",
				"LocalizationKeysPatternTest.MyPatt.SourceItem"),
				MyItem.itemPatt.sourceType().getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.LocalizationKeysPatternTest.MyItem.itemPatt.SourceItemZack.sourceTypeField",
				"LocalizationKeysPatternTest.MyItem.itemPatt.SourceItemZack.sourceTypeField",
				"itemPatt.SourceItemZack.sourceTypeField",
				"com.exedio.cope.LocalizationKeysPatternTest.MyPatt.SourceItem.sourceTypeField",
				"LocalizationKeysPatternTest.MyPatt.SourceItem.sourceTypeField",
				"sourceTypeField"),
				MyItem.itemPatt.sourceTypeField.getLocalizationKeys());
	}

	@Test void testIt()
	{
		assertIt("MyComp", MyComp_TYPE);
		assertIt("MyComp.compField",       "compField",       MyComp.compField);
		assertIt("MyComp.compFieldActual", "compFieldActual", MyComp.compFieldPure);
		assertIt("MyBlok", MyBlok.TYPE);
		assertIt("MyBlok.blokField",       "blokField",       MyBlok.blokField);
		assertIt("MyBlok.blokFieldActual", "blokFieldActual", MyBlok.blokFieldPure);
		assertIt("MyBlok.blokPattern",       "blokPattern",        MyBlok.blokPattern);
		assertIt("MyBlok.blokPatternActual", "blokPatternActual",  MyBlok.blokPatternPure);
		assertIt("MyItem", MyItem.TYPE);
		assertIt("MyItem.itemPatt",       "itemPatt",       MyItem.itemPatt);
		assertIt("MyItem.itemPattActual", "itemPattActual", MyItem.itemPattPure);
		assertIt("MyItem.itemComp",       "itemComp",       MyItem.itemComp);
		assertIt("MyItem.itemCompActual", "itemCompActual", MyItem.itemCompPure);
		assertIt("MyItem.itemBlok",       "itemBlok",       MyItem.itemBlok);
		assertIt("MyItem.itemBlokActual", "itemBlokActual", MyItem.itemBlokPure);

		assertIt("MyItem.itemPatt.sourceFeature",       "MyPatt.sourceFeature", "sourceFeature", MyItem.itemPatt    .sourceFeature);
		assertIt("MyItem.itemPattActual.sourceFeature", "MyPatt.sourceFeature", "sourceFeature", MyItem.itemPattPure.sourceFeature);

		assertIt("MyItem.itemComp.compField",             "MyComp.compField",       "compField",       MyItem.itemComp    .of(MyComp.compField));
		assertIt("MyItem.itemComp.compFieldActual",       "MyComp.compFieldActual", "compFieldActual", MyItem.itemComp    .of(MyComp.compFieldPure));
		assertIt("MyItem.itemCompActual.compField",       "MyComp.compField",       "compField",       MyItem.itemCompPure.of(MyComp.compField));
		assertIt("MyItem.itemCompActual.compFieldActual", "MyComp.compFieldActual", "compFieldActual", MyItem.itemCompPure.of(MyComp.compFieldPure));

		assertIt("MyItem.itemBlok.blokField",             "MyBlok.blokField",       "blokField",       MyItem.itemBlok    .of(MyBlok.blokField));
		assertIt("MyItem.itemBlok.blokFieldActual",       "MyBlok.blokFieldActual", "blokFieldActual", MyItem.itemBlok    .of(MyBlok.blokFieldPure));
		assertIt("MyItem.itemBlokActual.blokField",       "MyBlok.blokField",       "blokField",       MyItem.itemBlokPure.of(MyBlok.blokField));
		assertIt("MyItem.itemBlokActual.blokFieldActual", "MyBlok.blokFieldActual", "blokFieldActual", MyItem.itemBlokPure.of(MyBlok.blokFieldPure));

		assertIt("MyItem.itemBlok.blokPattern",             "MyBlok.blokPattern",       "blokPattern",       MyItem.itemBlok    .of(MyBlok.blokPattern));
		assertIt("MyItem.itemBlok.blokPatternActual",       "MyBlok.blokPatternActual", "blokPatternActual", MyItem.itemBlok    .of(MyBlok.blokPatternPure));
		assertIt("MyItem.itemBlokActual.blokPattern",       "MyBlok.blokPattern",       "blokPattern",       MyItem.itemBlokPure.of(MyBlok.blokPattern));
		assertIt("MyItem.itemBlokActual.blokPatternActual", "MyBlok.blokPatternActual", "blokPatternActual", MyItem.itemBlokPure.of(MyBlok.blokPatternPure));

		assertIt("MyItem.itemBlok.blokPattern.sourceFeature",             "MyPatt.sourceFeature", "sourceFeature", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceFeature);
		assertIt("MyItem.itemBlok.blokPatternActual.sourceFeature",       "MyPatt.sourceFeature", "sourceFeature", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceFeature);
		assertIt("MyItem.itemBlokActual.blokPattern.sourceFeature",       "MyPatt.sourceFeature", "sourceFeature", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceFeature);
		assertIt("MyItem.itemBlokActual.blokPatternActual.sourceFeature", "MyPatt.sourceFeature", "sourceFeature", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceFeature);

		assertIt("MyItem.itemBlok" +  ".blokPattern" +  ".SourceItemZack", "MyBlok.blokPattern" +  ".SourceItemZack", "blokPattern" +  ".SourceItemZack", "MyPatt.SourceItem", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceType());
		assertIt("MyItem.itemBlok" +  ".blokPatternActual.SourceItemZack", "MyBlok.blokPatternActual.SourceItemZack", "blokPatternActual.SourceItemZack", "MyPatt.SourceItem", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceType());
		assertIt("MyItem.itemBlokActual.blokPattern" +  ".SourceItemZack", "MyBlok.blokPattern" +  ".SourceItemZack", "blokPattern" +  ".SourceItemZack", "MyPatt.SourceItem", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceType());
		assertIt("MyItem.itemBlokActual.blokPatternActual.SourceItemZack", "MyBlok.blokPatternActual.SourceItemZack", "blokPatternActual.SourceItemZack", "MyPatt.SourceItem", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceType());
		assertIt("MyItem.itemPatt" +  ".SourceItemZack", "itemPatt" +  ".SourceItemZack", "MyPatt.SourceItem", MyItem.itemPatt    .sourceType());
		assertIt("MyItem.itemPattActual.SourceItemZack", "itemPattActual.SourceItemZack", "MyPatt.SourceItem", MyItem.itemPattPure.sourceType());

		assertIt("MyItem.itemBlok" +  ".blokPattern" +  ".SourceItemZack.sourceTypeField", "MyBlok.blokPattern" +  ".SourceItemZack.sourceTypeField", "blokPattern" +  ".SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceTypeField);
		assertIt("MyItem.itemBlok" +  ".blokPatternActual.SourceItemZack.sourceTypeField", "MyBlok.blokPatternActual.SourceItemZack.sourceTypeField", "blokPatternActual.SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceTypeField);
		assertIt("MyItem.itemBlokActual.blokPattern" +  ".SourceItemZack.sourceTypeField", "MyBlok.blokPattern" +  ".SourceItemZack.sourceTypeField", "blokPattern" +  ".SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceTypeField);
		assertIt("MyItem.itemBlokActual.blokPatternActual.SourceItemZack.sourceTypeField", "MyBlok.blokPatternActual.SourceItemZack.sourceTypeField", "blokPatternActual.SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceTypeField);
		assertIt("MyItem.itemPatt" +  ".SourceItemZack.sourceTypeField", "itemPatt" +  ".SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemPatt    .sourceTypeField);
		assertIt("MyItem.itemPattActual.SourceItemZack.sourceTypeField", "itemPattActual.SourceItemZack.sourceTypeField", "MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemPattPure.sourceTypeField);

		// caching
		assertSame(MyComp_TYPE.getLocalizationKeys(), MyComp_TYPE.getLocalizationKeys());
		assertSame(MyBlok.TYPE.getLocalizationKeys(), MyBlok.TYPE.getLocalizationKeys());
		assertSame(MyItem.TYPE.getLocalizationKeys(), MyItem.TYPE.getLocalizationKeys());
		assertSame(MyItem.itemPatt.getLocalizationKeys(), MyItem.itemPatt.getLocalizationKeys());
	}


	private static void assertIt(
			final String expectedPrefixed,
			final AbstractType<?> t)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expectedPrefixed,
				PREFIX2 + expectedPrefixed),
				t.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2,
			final String expected3Prefixed,
			final AbstractType<?> t)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				expected2,
				PREFIX1 + expected3Prefixed,
				PREFIX2 + expected3Prefixed),
				t.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2Prefixed,
			final String expected3,
			final String expected4Prefixed,
			final AbstractType<?> t)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				PREFIX1 + expected2Prefixed,
				PREFIX2 + expected2Prefixed,
				expected3,
				PREFIX1 + expected4Prefixed,
				PREFIX2 + expected4Prefixed),
				t.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				expected2),
				f.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2Prefixed,
			final String expected3,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				PREFIX1 + expected2Prefixed,
				PREFIX2 + expected2Prefixed,
				expected3),
				f.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2,
			final String expected3Prefixed,
			final String expected4,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				expected2,
				PREFIX1 + expected3Prefixed,
				PREFIX2 + expected3Prefixed,
				expected4),
				f.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1Prefixed,
			final String expected2Prefixed,
			final String expected3,
			final String expected4Prefixed,
			final String expected5,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				PREFIX1 + expected1Prefixed,
				PREFIX2 + expected1Prefixed,
				PREFIX1 + expected2Prefixed,
				PREFIX2 + expected2Prefixed,
				expected3,
				PREFIX1 + expected4Prefixed,
				PREFIX2 + expected4Prefixed,
				expected5),
				f.getLocalizationKeys());
	}

	private static final String PREFIX1 = LocalizationKeysPatternTest.class.getName() + '.';
	private static final String PREFIX2 = LocalizationKeysPatternTest.class.getSimpleName() + '.';


	private static final class MyPatt extends Pattern implements Copyable
	{
		final IntegerField sourceFeature = new IntegerField();
		private Type<SourceItem> sourceType;
		final IntegerField sourceTypeField = new IntegerField();

		MyPatt()
		{
			addSourceFeature(sourceFeature, "sourceFeature");
		}

		@Override
		protected void onMount()
		{
			super.onMount();
			final Features features = new Features();
			features.put("sourceTypeField", sourceTypeField);
			sourceType = newSourceType(SourceItem.class, SourceItem::new, features, "SourceItemZack");
		}

		Type<SourceItem> sourceType()
		{
			assertNotNull(sourceType);
			return sourceType;
		}

		@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=3, comments=false)
		private static final class SourceItem extends Item
		{
			@com.exedio.cope.instrument.Generated
			private static final long serialVersionUID = 1l;

			@com.exedio.cope.instrument.Generated
			private SourceItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
		}

		@Override
		public MyPatt copy(final CopyMapper mapper)
		{
			return new MyPatt();
		}

		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyComp extends Composite
	{
		@WrapperIgnore
		static final IntegerField compField = new IntegerField();
		@WrapperIgnore
		@CopeName("compFieldActual")
		static final IntegerField compFieldPure = new IntegerField();

		@com.exedio.cope.instrument.Generated
		private MyComp(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}

	private static final CompositeType<MyComp> MyComp_TYPE = MyItem.itemComp.getValueType();

	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyBlok extends Block
	{
		@WrapperIgnore
		static final IntegerField blokField = new IntegerField();
		@WrapperIgnore
		@CopeName("blokFieldActual")
		static final IntegerField blokFieldPure = new IntegerField();

		static final MyPatt blokPattern = new MyPatt();
		@CopeName("blokPatternActual")
		static final MyPatt blokPatternPure = new MyPatt();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.pattern.BlockType<MyBlok> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlok.class,MyBlok::new);

		@com.exedio.cope.instrument.Generated
		private MyBlok(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MyItem extends Item
	{
		static final MyPatt itemPatt = new MyPatt();
		@CopeName("itemPattActual")
		static final MyPatt itemPattPure = new MyPatt();

		@WrapperIgnore
		static final CompositeField<MyComp> itemComp = CompositeField.create(MyComp.class);
		@WrapperIgnore
		@CopeName("itemCompActual")
		static final CompositeField<MyComp> itemCompPure = CompositeField.create(MyComp.class);

		@WrapperIgnore
		static final BlockField<MyBlok> itemBlok = BlockField.create(MyBlok.TYPE);
		@WrapperIgnore
		@CopeName("itemBlokActual")
		static final BlockField<MyBlok> itemBlokPure = BlockField.create(MyBlok.TYPE);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
