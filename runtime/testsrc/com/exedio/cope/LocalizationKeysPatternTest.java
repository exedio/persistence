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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.pattern.Block;
import com.exedio.cope.pattern.BlockField;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.pattern.CompositeField;
import com.exedio.cope.pattern.CompositeType;
import org.junit.jupiter.api.Test;

public class LocalizationKeysPatternTest
{
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

		assertIt("MyPatt.sourceFeature", "MyItem.itemPatt.sourceFeature",       "sourceFeature", MyItem.itemPatt    .sourceFeature);
		assertIt("MyPatt.sourceFeature", "MyItem.itemPattActual.sourceFeature", "sourceFeature", MyItem.itemPattPure.sourceFeature);

		assertIt("MyComp.compField",       "MyItem.itemComp.compField",             "compField",       MyItem.itemComp    .of(MyComp.compField));
		assertIt("MyComp.compFieldActual", "MyItem.itemComp.compFieldActual",       "compFieldActual", MyItem.itemComp    .of(MyComp.compFieldPure));
		assertIt("MyComp.compField",       "MyItem.itemCompActual.compField",       "compField",       MyItem.itemCompPure.of(MyComp.compField));
		assertIt("MyComp.compFieldActual", "MyItem.itemCompActual.compFieldActual", "compFieldActual", MyItem.itemCompPure.of(MyComp.compFieldPure));

		assertIt("MyBlok.blokField",       "MyItem.itemBlok.blokField",             "blokField",       MyItem.itemBlok    .of(MyBlok.blokField));
		assertIt("MyBlok.blokFieldActual", "MyItem.itemBlok.blokFieldActual",       "blokFieldActual", MyItem.itemBlok    .of(MyBlok.blokFieldPure));
		assertIt("MyBlok.blokField",       "MyItem.itemBlokActual.blokField",       "blokField",       MyItem.itemBlokPure.of(MyBlok.blokField));
		assertIt("MyBlok.blokFieldActual", "MyItem.itemBlokActual.blokFieldActual", "blokFieldActual", MyItem.itemBlokPure.of(MyBlok.blokFieldPure));

		assertIt("MyBlok.blokPattern",       "MyItem.itemBlok.blokPattern",             "blokPattern",       MyItem.itemBlok    .of(MyBlok.blokPattern));
		assertIt("MyBlok.blokPatternActual", "MyItem.itemBlok.blokPatternActual",       "blokPatternActual", MyItem.itemBlok    .of(MyBlok.blokPatternPure));
		assertIt("MyBlok.blokPattern",       "MyItem.itemBlokActual.blokPattern",       "blokPattern",       MyItem.itemBlokPure.of(MyBlok.blokPattern));
		assertIt("MyBlok.blokPatternActual", "MyItem.itemBlokActual.blokPatternActual", "blokPatternActual", MyItem.itemBlokPure.of(MyBlok.blokPatternPure));

		assertIt("MyPatt.sourceFeature", "MyItem.itemBlok.blokPattern.sourceFeature",             "sourceFeature", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceFeature);
		assertIt("MyPatt.sourceFeature", "MyItem.itemBlok.blokPatternActual.sourceFeature",       "sourceFeature", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceFeature);
		assertIt("MyPatt.sourceFeature", "MyItem.itemBlokActual.blokPattern.sourceFeature",       "sourceFeature", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceFeature);
		assertIt("MyPatt.sourceFeature", "MyItem.itemBlokActual.blokPatternActual.sourceFeature", "sourceFeature", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceFeature);

		assertIt("MyPatt.SourceItem", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceType());
		assertIt("MyPatt.SourceItem", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceType());
		assertIt("MyPatt.SourceItem", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceType());
		assertIt("MyPatt.SourceItem", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceType());
		assertIt("MyPatt.SourceItem", MyItem.itemPatt    .sourceType());
		assertIt("MyPatt.SourceItem", MyItem.itemPattPure.sourceType());

		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlok    .of(MyBlok.blokPattern    ).sourceTypeField);
		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlok    .of(MyBlok.blokPatternPure).sourceTypeField);
		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlokPure.of(MyBlok.blokPattern    ).sourceTypeField);
		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemBlokPure.of(MyBlok.blokPatternPure).sourceTypeField);
		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemPatt    .sourceTypeField);
		assertIt("MyPatt.SourceItem.sourceTypeField", "sourceTypeField", MyItem.itemPattPure.sourceTypeField);

		// caching
		assertSame(MyComp_TYPE.getLocalizationKeys(), MyComp_TYPE.getLocalizationKeys());
		assertSame(MyBlok.TYPE.getLocalizationKeys(), MyBlok.TYPE.getLocalizationKeys());
		assertSame(MyItem.TYPE.getLocalizationKeys(), MyItem.TYPE.getLocalizationKeys());
		assertSame(MyItem.itemPatt.getLocalizationKeys(), MyItem.itemPatt.getLocalizationKeys());
	}


	private static void assertIt(
			final String expected,
			final AbstractType<?> t)
	{
		assertEqualsUnmodifiable(asList(
				TEST1 + expected,
				TEST2 + expected),
				t.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected,
			final String expectedSimple,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				TEST1 + expected,
				TEST2 + expected,
				expectedSimple),
				f.getLocalizationKeys());
	}

	private static void assertIt(
			final String expected1,
			final String expected2,
			final String expectedSimple,
			final Feature f)
	{
		assertEqualsUnmodifiable(asList(
				TEST1 + expected1,
				TEST2 + expected1,
				TEST1 + expected2,
				TEST2 + expected2,
				expectedSimple),
				f.getLocalizationKeys());
	}

	private static final String TEST1 = LocalizationKeysPatternTest.class.getName() + '.';
	private static final String TEST2 = LocalizationKeysPatternTest.class.getSimpleName() + '.';


	static final class MyPatt extends Pattern implements Copyable
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
			sourceType = newSourceType(SourceItem.class, features, "SourceItemZack");
		}

		Type<SourceItem> sourceType()
		{
			assertNotNull(sourceType);
			return sourceType;
		}

		@WrapperIgnore
		static final class SourceItem extends Item
		{
			SourceItem(final ActivationParameters ap) { super(ap); }

			private static final long serialVersionUID = 1l;
		}

		@Override
		public MyPatt copy(final CopyMapper mapper)
		{
			return new MyPatt();
		}

		private static final long serialVersionUID = 1l;
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyComp extends Composite
	{
		@WrapperIgnore
		static final IntegerField compField = new IntegerField();
		@WrapperIgnore
		@CopeName("compFieldActual")
		static final IntegerField compFieldPure = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyComp(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;
	}

	private static final CompositeType<MyComp> MyComp_TYPE = MyItem.itemComp.getValueType();

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class MyBlok extends Block
	{
		@WrapperIgnore
		static final IntegerField blokField = new IntegerField();
		@WrapperIgnore
		@CopeName("blokFieldActual")
		static final IntegerField blokFieldPure = new IntegerField();

		static final MyPatt blokPattern = new MyPatt();
		@CopeName("blokPatternActual")
		static final MyPatt blokPatternPure = new MyPatt();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<MyBlok> TYPE = com.exedio.cope.pattern.BlockType.newType(MyBlok.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyBlok(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class MyItem extends Item
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

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
