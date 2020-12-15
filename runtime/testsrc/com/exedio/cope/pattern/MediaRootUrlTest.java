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
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MediaRootUrlTest
{
	@Test void testIt()
	{
		final ConnectProperties properties = ConnectProperties.create(cascade(
				single("media.rooturl", "zack/"),
				TestSources.minimal()
		));
		assertEquals("zack/", properties.getMediaRootUrl());

		MODEL.connect(properties);
		assertSame(properties, MODEL.getConnectProperties());

		setupSchemaMinimal(MODEL);
		MODEL.startTransaction(getClass().getName());

		final AnItem i1 = new AnItem(Media.toValue(new byte[]{1,2,3}, "image/jpeg"));
		final AnItem i2 = new AnItem(Media.toValue(new byte[]{1,2,3}, "image/png"));
		final AnItem iN = new AnItem((Media.Value)null);

		final MediaPath.Locator l1 = i1.getFileLocator();
		final MediaPath.Locator l2 = i2.getFileLocator();
		final MediaPath.Locator lN = iN.getFileLocator();
		assertEquals("AnItem/file/AnItem-0.jpg", l1.getPath());
		assertEquals("AnItem/file/AnItem-1.png", l2.getPath());
		assertNull(lN);

		assertEquals("zack/AnItem/file/AnItem-0.jpg", l1.getURLByConnect());
		assertEquals("zack/AnItem/file/AnItem-1.png", l2.getURLByConnect());

		final StringBuilder bf = new StringBuilder();
		l1.appendURLByConnect(bf);
		assertEquals("zack/AnItem/file/AnItem-0.jpg", bf.toString());
		bf.setLength(0);
		l2.appendURLByConnect(bf);
		assertEquals("zack/AnItem/file/AnItem-1.png", bf.toString());

		assertEquals("zack/AnItem/file/AnItem-0.jpg", i1.getFileURL());
		assertEquals("zack/AnItem/file/AnItem-1.png", i2.getFileURL());
		assertEquals(null, iN.getFileURL());
	}

	@AfterEach final void tearDown()
	{
		MODEL.rollbackIfNotCommitted();
		MODEL.dropSchema();
		MODEL.disconnect();
	}

	private static class AnItem extends Item
	{
		@WrapperInitial
		static final Media file = new Media().optional();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param file the initial value for field {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private AnItem(
				@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value file)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.file.map(file),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns a URL the content of {@link #file} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getURL")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getFileURL()
	{
		return AnItem.file.getURL(this);
	}

	/**
	 * Returns a Locator the content of {@link #file} is available under.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLocator")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final com.exedio.cope.pattern.MediaPath.Locator getFileLocator()
	{
		return AnItem.file.getLocator(this);
	}

	/**
	 * Returns the content type of the media {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getContentType")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getFileContentType()
	{
		return AnItem.file.getContentType(this);
	}

	/**
	 * Returns whether media {@link #file} is null.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="isNull")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean isFileNull()
	{
		return AnItem.file.isNull(this);
	}

	/**
	 * Returns the last modification date of media {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLastModified")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.util.Date getFileLastModified()
	{
		return AnItem.file.getLastModified(this);
	}

	/**
	 * Returns the body length of the media {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getLength")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final long getFileLength()
	{
		return AnItem.file.getLength(this);
	}

	/**
	 * Returns the body of the media {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final byte[] getFileBody()
	{
		return AnItem.file.getBody(this);
	}

	/**
	 * Writes the body of media {@link #file} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getFileBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		AnItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getFileBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		AnItem.file.getBody(this,body);
	}

	/**
	 * Writes the body of media {@link #file} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getBody")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void getFileBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		AnItem.file.getBody(this,body);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setFile(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value file)
			throws
				java.io.IOException
	{
		AnItem.file.set(this,file);
	}

	/**
	 * Sets the content of media {@link #file}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setFile(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		AnItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setFile(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setFile(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.file.set(this,body,contentType);
	}

	/**
	 * Sets the content of media {@link #file}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setFile(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		AnItem.file.set(this,body,contentType);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaRootUrlTest.class, "MODEL");
	}
}
