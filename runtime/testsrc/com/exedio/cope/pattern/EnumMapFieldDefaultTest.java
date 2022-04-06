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

import static com.exedio.cope.pattern.EnumMapField.create;
import static com.exedio.cope.pattern.EnumMapFieldDefaultItem.TYPE;
import static com.exedio.cope.pattern.EnumMapFieldDefaultItem.text;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.SUBCLASS;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.pattern.EnumMapFieldItem.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class EnumMapFieldDefaultTest
{
	@SuppressWarnings("unused")
	private static final Model MODEL = new Model(TYPE);

	@Test void testModel()
	{
		assertEquals(null, text.getValueTemplate().getDefaultConstant());
		assertEquals("defaultDE", text.getField(DE).getDefaultConstant());
		assertEquals("defaultEN", text.getField(EN).getDefaultConstant());
		assertEquals("defaultPL", text.getField(PL).getDefaultConstant());
		assertEquals("defaultSUBCLASS", text.getField(SUBCLASS).getDefaultConstant());
		assertEquals(false, text.isFinal());
		assertEquals(true,  text.isMandatory());
		assertEquals(false, text.isInitial());
	}

	@Test void testTemplateWithoutDefault() throws Throwable
	{
		final EnumMapField<Language, Integer> f00 =
				create(Language.class, new IntegerField());
		final Executable f00a = () ->
		{
			assertEquals(null, d(f00.getValueTemplate()));
			assertEquals(null, d(f00.getField(DE)));
			assertEquals(null, d(f00.getField(EN)));
			assertEquals(null, d(f00.getField(PL)));
		};
		f00a.execute();

		final EnumMapField<Language, Integer> fDE =
				f00.defaultTo(DE, 66);
		final Executable fDEa = () ->
		{
			assertEquals(null, d(fDE.getValueTemplate()));
			assertEquals(66,   d(fDE.getField(DE)));
			assertEquals(null, d(fDE.getField(EN)));
			assertEquals(null, d(fDE.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();

		final EnumMapField<Language, Integer> fEN =
				fDE.defaultTo(EN, 77);
		final Executable fENa = () ->
		{
			assertEquals(null, d(fEN.getValueTemplate()));
			assertEquals(66,   d(fEN.getField(DE)));
			assertEquals(77,   d(fEN.getField(EN)));
			assertEquals(null, d(fEN.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();
		fENa.execute();
	}

	@Test void testTemplateWithDefaultConstant() throws Throwable
	{
		final EnumMapField<Language, Integer> f00 =
				create(Language.class, new IntegerField().defaultTo(50));
		final Executable f00a = () ->
		{
			assertEquals(50,   d(f00.getValueTemplate()));
			assertEquals(50,   d(f00.getField(DE)));
			assertEquals(50,   d(f00.getField(EN)));
			assertEquals(50,   d(f00.getField(PL)));
		};
		f00a.execute();

		final EnumMapField<Language, Integer> fDE =
				f00.defaultTo(DE, 66);
		final Executable fDEa = () ->
		{
			assertEquals(50,   d(fDE.getValueTemplate()));
			assertEquals(66,   d(fDE.getField(DE)));
			assertEquals(50,   d(fDE.getField(EN)));
			assertEquals(50,   d(fDE.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();

		final EnumMapField<Language, Integer> fEN =
				fDE.defaultTo(EN, 77);
		final Executable fENa = () ->
		{
			assertEquals(50,   d(fEN.getValueTemplate()));
			assertEquals(66,   d(fEN.getField(DE)));
			assertEquals(77,   d(fEN.getField(EN)));
			assertEquals(50,   d(fEN.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();
		fENa.execute();
	}

	@Test void testTemplateWithDefaultSpecial() throws Throwable
	{
		final EnumMapField<Language, Integer> f00 =
				create(Language.class, new IntegerField().defaultToNext(5));
		final Executable f00a = () ->
		{
			assertEquals("n5", d(f00.getValueTemplate()));
			assertEquals("n5", d(f00.getField(DE)));
			assertEquals("n5", d(f00.getField(EN)));
			assertEquals("n5", d(f00.getField(PL)));
		};
		f00a.execute();

		final EnumMapField<Language, Integer> fDE =
				f00.defaultTo(DE, 66);
		final Executable fDEa = () ->
		{
			assertEquals("n5", d(fDE.getValueTemplate()));
			assertEquals(66,   d(fDE.getField(DE)));
			assertEquals("n5", d(fDE.getField(EN)));
			assertEquals("n5", d(fDE.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();

		final EnumMapField<Language, Integer> fEN =
				fDE.defaultTo(EN, 77);
		final Executable fENa = () ->
		{
			assertEquals("n5", d(fEN.getValueTemplate()));
			assertEquals(66,   d(fEN.getField(DE)));
			assertEquals(77,   d(fEN.getField(EN)));
			assertEquals("n5", d(fEN.getField(PL)));
		};
		f00a.execute();
		fDEa.execute();
		fENa.execute();
	}

	private static Object d(final FunctionField<Integer> f)
	{
		final IntegerField fi = (IntegerField)f;

		if(!f.hasDefault())
			return null;

		if(fi.isDefaultNext())
			return "n" + fi.getDefaultNextStartX();

		return requireNonNull(f.getDefaultConstant());
	}
}
