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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Column;
import java.util.List;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;

public class UpdateCounterNonFinalBlobTest extends TestWithEnvironment
{
	@Test void test()
	{
		final boolean vault = MyItem.data.getVaultBucket() != null;
		assertEquals(
				vault
				? List.of(synthetic("this"), synthetic("catch"), "someFinalField", "data", "anotherFinalField")
				: List.of(synthetic("this"), "someFinalField", "data", "anotherFinalField"),
				MODEL.getSchema().getTable(getTableName(MyItem.TYPE)).
						getColumns().stream().map(Column::getName).toList());

		final MyItem i = new MyItem(77, 88);

		if (vault)
			assertUpdateCount(0, i);
		else
			assertNoUpdateCount(i);

		i.setData("someData");
		if (vault)
			assertUpdateCount(1, i);
		else
			assertNoUpdateCount(i);
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final IntegerField someFinalField = new IntegerField().toFinal();

		@Wrapper(wrap="set", parameters=byte[].class, visibility=PACKAGE, internal=true)
		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final DataField data = new DataField().optional();

		@Wrapper(wrap=Wrapper.ALL_WRAPS, visibility=NONE)
		static final IntegerField anotherFinalField = new IntegerField().toFinal();

		void setData(@Nullable final String source)
		{
			setDataInternal(source!=null ? source.getBytes(US_ASCII) : null);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					final int someFinalField,
					final int anotherFinalField)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MyItem.someFinalField,someFinalField),
				com.exedio.cope.SetValue.map(MyItem.anotherFinalField,anotherFinalField),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setDataInternal(@javax.annotation.Nullable final byte[] data)
		{
			MyItem.data.set(this,data);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private String synthetic(final String name)
	{
		return synthetic(name, getTableName(MyItem.TYPE));
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	public UpdateCounterNonFinalBlobTest()
	{
		super(MODEL);
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private static void assertUpdateCount(final int expected, final MyItem item)
	{
		assertEquals(expected, item.getUpdateCountIfActive(MyItem.TYPE));
	}

	@SuppressWarnings("deprecation") // OK: using special accessors for tests
	private static void assertNoUpdateCount(final MyItem item)
	{
		assertFails(()->item.getUpdateCountIfActive(MyItem.TYPE), IllegalArgumentException.class, "unexpected request for unmodifiable MyItem");
	}
}
