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

import static org.junit.Assert.assertEquals;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.TypesBound;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EnumMapFieldSchemaNameTest
{
	@Test public void testIt()
	{
		assertIt("alpha-one", "alfa_one", AnItem.alpha, AnEnum.one);
		assertIt("alpha-two", "alfa_woo", AnItem.alpha, AnEnum.two);
		assertIt("beta-one",  "beta_one", AnItem.beta,  AnEnum.one);
		assertIt("beta-two",  "beta_woo", AnItem.beta,  AnEnum.two);
	}

	private static void assertIt(
			final String fieldName, final String schemaName,
			final EnumMapField<AnEnum, String> pattern, final AnEnum facet)
	{
		final FunctionField<String> field = pattern.getField(facet);
		assertEquals("fieldName", fieldName, field.getName());
		assertEquals("schemaName", schemaName, SchemaInfo.getColumnName(field));
	}

	private static enum AnEnum
	{
		one,
		@CopeSchemaName("woo")
		two;
	}

	private static final class AnItem extends Item
	{
		@CopeSchemaName("alfa")
		static final EnumMapField<AnEnum, String> alpha = EnumMapField.create(AnEnum.class, new StringField());
		static final EnumMapField<AnEnum, String> beta  = EnumMapField.create(AnEnum.class, new StringField());
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TypesBound.newType(AnItem.class));

	@SuppressWarnings("static-method")
	@Before public final void setUp()
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:EnumMapFieldSchemaNameTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		MODEL.connect(new ConnectProperties(Sources.view(source , "EnumMapFieldSchemaNameTest"), null));
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		MODEL.disconnect();
	}
}