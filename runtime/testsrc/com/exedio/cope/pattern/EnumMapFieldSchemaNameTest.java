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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.FunctionField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnumMapFieldSchemaNameTest
{
	@Test void testIt()
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
		assertEquals(fieldName, field.getName(), "fieldName");
		assertEquals(schemaName, SchemaInfo.getColumnName(field), "schemaName");
	}

	private enum AnEnum
	{
		one,
		@CopeSchemaName("woo")
		two
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@CopeSchemaName("alfa")
		@WrapperIgnore static final EnumMapField<AnEnum, String> alpha = EnumMapField.create(AnEnum.class, new StringField());
		@WrapperIgnore static final EnumMapField<AnEnum, String> beta  = EnumMapField.create(AnEnum.class, new StringField());

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	@BeforeEach final void setUp()
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:EnumMapFieldSchemaNameTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		MODEL.connect(ConnectProperties.create(Sources.view(source , "EnumMapFieldSchemaNameTest")));
	}

	@AfterEach final void tearDown()
	{
		MODEL.disconnect();
	}
}
