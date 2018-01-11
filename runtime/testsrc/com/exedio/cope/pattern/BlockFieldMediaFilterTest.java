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
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BlockFieldMediaFilterTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldMediaFilterTest.class, "MODEL");
	}

	public BlockFieldMediaFilterTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				eins, eins.of(source), eins.of(source).getBody(), eins.of(source).getContentType(), eins.of(source).getLastModified(), eins.of(source).getUnison(), eins.of(filter),
				zwei, zwei.of(source), zwei.of(source).getBody(), zwei.of(source).getContentType(), zwei.of(source).getLastModified(), zwei.of(source).getUnison(), zwei.of(filter),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(source ).getType());
		assertEquals(AnItem.TYPE, eins.of(filter).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-source", eins.of(source).getName());
		assertEquals("eins-filter", eins.of(filter).getName());
		assertEquals("eins", eins.getName());
		assertEquals(ABlock.class.getName() + "#source", source.toString());
		assertEquals(ABlock.class.getName() + "#filter", filter.toString());
		assertEquals("AnItem.eins-source", eins.of(source).toString());
		assertEquals("AnItem.eins-filter", eins.of(filter).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(source).getPattern());
		assertEquals(eins, eins.of(filter).getPattern());
		assertEqualsUnmodifiable(list(
				eins.of(source), eins.of(filter)),
			eins.getSourceFeatures());

		assertEquals(eins.of(source), eins.of(filter).getSource());
		assertEquals(zwei.of(source), zwei.of(filter).getSource());
		assertEquals(source, filter.getSource());

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(source, eins.getTemplate(eins.of(source)));
		assertSame(source, zwei.getTemplate(zwei.of(source)));
		assertEqualsUnmodifiable(list(eins.of(source), eins.of(filter)), eins.getComponents());
		assertEqualsUnmodifiable(list(zwei.of(source), zwei.of(filter)), zwei.getComponents());
		assertEqualsUnmodifiable(list(source, filter), eins.getTemplates());

		// test persistence
		final AnItem i1 = new AnItem();
		final ABlock b1a = i1.eins();
		final ABlock b1b = i1.zwei();
		assertLocator(null, b1a.getSourceLocator());
		assertLocator(null, b1a.getFilterLocator());
		assertLocator(null, b1b.getSourceLocator());
		assertLocator(null, b1b.getFilterLocator());

		b1a.setSource(new byte[]{1,2,3}, MediaType.JPEG);
		assertLocator("AnItem/eins-source/"+i1+".jpg", b1a.getSourceLocator());
		assertLocator("AnItem/eins-filter/"+i1+".jpg", b1a.getFilterLocator());
		assertLocator(null, b1b.getSourceLocator());
		assertLocator(null, b1b.getFilterLocator());
	}

	static final class ABlock extends Block
	{
		@Wrapper(wrap="getURL", visibility=NONE)
		static final Media source = new Media().optional().contentTypes(MediaType.JPEG, MediaType.PNG);
		@Wrapper(wrap="getURL", visibility=NONE)
		static final MediaThumbnail filter = new MediaThumbnail(source, 10, 20);


	/**
	 * Returns a Locator the content of {@link #source} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getSourceLocator()
	{
		return field().of(ABlock.source).getLocator(item());
	}

	/**
	 * Returns the content type of the media {@link #source}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getContentType")
	@javax.annotation.Nullable
	java.lang.String getSourceContentType()
	{
		return field().of(ABlock.source).getContentType(item());
	}

	/**
	 * Returns whether media {@link #source} is null.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	boolean isSourceNull()
	{
		return field().of(ABlock.source).isNull(item());
	}

	/**
	 * Returns the last modification date of media {@link #source}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLastModified")
	@javax.annotation.Nullable
	java.util.Date getSourceLastModified()
	{
		return field().of(ABlock.source).getLastModified(item());
	}

	/**
	 * Returns the body length of the media {@link #source}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	long getSourceLength()
	{
		return field().of(ABlock.source).getLength(item());
	}

	/**
	 * Returns the body of the media {@link #source}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	@javax.annotation.Nullable
	byte[] getSourceBody()
	{
		return field().of(ABlock.source).getBody(item());
	}

	/**
	 * Writes the body of media {@link #source} into the given stream.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSourceBody(@javax.annotation.Nonnull final java.io.OutputStream body)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).getBody(item(),body);
	}

	/**
	 * Writes the body of media {@link #source} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSourceBody(@javax.annotation.Nonnull final java.nio.file.Path body)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).getBody(item(),body);
	}

	/**
	 * Writes the body of media {@link #source} into the given file.
	 * Does nothing, if the media is null.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getBody")
	void getSourceBody(@javax.annotation.Nonnull final java.io.File body)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).getBody(item(),body);
	}

	/**
	 * Sets the content of media {@link #source}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSource(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value source)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).set(item(),source);
	}

	/**
	 * Sets the content of media {@link #source}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSource(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
	{
		field().of(ABlock.source).set(item(),body,contentType);
	}

	/**
	 * Sets the content of media {@link #source}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSource(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).set(item(),body,contentType);
	}

	/**
	 * Sets the content of media {@link #source}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSource(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).set(item(),body,contentType);
	}

	/**
	 * Sets the content of media {@link #source}.
	 * @throws java.io.IOException if accessing {@code body} throws an IOException.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setSource(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
			throws
				java.io.IOException
	{
		field().of(ABlock.source).set(item(),body,contentType);
	}

	/**
	 * Returns a Locator the content of {@link #filter} is available under.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLocator")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.MediaPath.Locator getFilterLocator()
	{
		return field().of(ABlock.filter).getLocator(item());
	}

	/**
	 * Returns the body of {@link #filter}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	byte[] getFilter()
			throws
				java.io.IOException
	{
		return field().of(ABlock.filter).get(item());
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for aBlock.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #eins}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	@javax.annotation.Nonnull
	ABlock eins()
	{
		return AnItem.eins.get(this);
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="")
	@javax.annotation.Nonnull
	ABlock zwei()
	{
		return AnItem.zwei.get(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}


	// workaround eclipse warnings about unused imports when using static imports instead
	private static final Media source = ABlock.source;
	private static final MediaThumbnail filter = ABlock.filter;
	private static final BlockField<ABlock> eins = AnItem.eins;
	private static final BlockField<ABlock> zwei = AnItem.zwei;
}

