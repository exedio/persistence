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

import static com.exedio.cope.tojunit.TestSources.setupSchemaMinimal;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MediaUrlFingerPrintingTest
{
	MyItem item;

	@Test void testMinimum()
	{
		assertIt(MIN_VALUE + 2, ".-_________H");
		assertIt(MIN_VALUE + 1, ".__________H");
		assertIt(MIN_VALUE,     ".__________H"); // special case, same as MIN_VALUE + 1
	}

	@Test void testMaximum()
	{
		assertIt(MAX_VALUE - 2, "9_________H");
		assertIt(MAX_VALUE - 1, "-_________H");
		assertIt(MAX_VALUE,     "__________H");
	}

	@Test void testZero()
	{
		assertIt(-3, ".D");
		assertIt(-2, ".C");
		assertIt(-1, ".B");
		assertIt( 0,  "");
		assertIt( 1,  "B");
		assertIt( 2,  "C");
		assertIt( 3,  "D");
	}

	private void assertIt(final long lastModified, final String fingerPrint)
	{
		item.setFeatureLastModified(lastModified);
		assertEquals(new Date(lastModified), MyItem.feature.getLastModified(item));
		assertEquals("MyItem/feature/.f" + fingerPrint + "/" + item, item.getFeatureLocator().getPath());
	}


	@WrapperType(indent=2, comments=false)
	private static class MyItem extends Item
	{
		@UrlFingerPrinting
		static final MediaLongLastModifiedPath feature = new MediaLongLastModifiedPath();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getFeatureURL()
		{
			return MyItem.feature.getURL(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final com.exedio.cope.pattern.MediaPath.Locator getFeatureLocator()
		{
			return MyItem.feature.getLocator(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		final java.lang.String getFeatureContentType()
		{
			return MyItem.feature.getContentType(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setFeatureContentType(final java.lang.String feature)
		{
			MyItem.feature.setContentType(this,feature);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setFeatureLastModified(final long feature)
		{
			MyItem.feature.setLastModified(this,feature);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		protected MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@BeforeEach void setUp()
	{
		MODEL.connect(ConnectProperties.create(TestSources.minimal()));
		setupSchemaMinimal(MODEL);
		MODEL.startTransaction(getClass().getName());
		item = new MyItem();
		item.setFeatureContentType("what/ever");
	}

	@AfterEach void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.dropSchema();
		MODEL.disconnect();
	}

	static final Model MODEL = new Model(MyItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaUrlFingerPrintingTest.class, "MODEL");
	}
}
