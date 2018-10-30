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

import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.CheckViolationException;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BlockFieldMediaSameContentTypeTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldMediaSameContentTypeTest.class, "MODEL");
	}

	public BlockFieldMediaSameContentTypeTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				eins,
				eins.of(uno), eins.of(uno).getBody(), eins.of(uno).getContentType(), eins.of(uno).getLastModified(), eins.of(uno).getUnison(),
				eins.of(duo), eins.of(duo).getBody(), eins.of(duo).getContentType(), eins.of(duo).getLastModified(), eins.of(duo).getUnison(),
				eins.of(chk),
				zwei,
				zwei.of(uno), zwei.of(uno).getBody(), zwei.of(uno).getContentType(), zwei.of(uno).getLastModified(), zwei.of(uno).getUnison(),
				zwei.of(duo), zwei.of(duo).getBody(), zwei.of(duo).getContentType(), zwei.of(duo).getLastModified(), zwei.of(duo).getUnison(),
				zwei.of(chk),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(uno).getType());
		assertEquals(AnItem.TYPE, eins.of(duo).getType());
		assertEquals(AnItem.TYPE, eins.of(chk).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-uno", eins.of(uno).getName());
		assertEquals("eins-duo", eins.of(duo).getName());
		assertEquals("eins-chk", eins.of(chk).getName());
		assertEquals("eins", eins.getName());
		assertEquals(ABlock.class.getName() + "#uno", uno.toString());
		assertEquals(ABlock.class.getName() + "#duo", duo.toString());
		assertEquals(ABlock.class.getName() + "#chk", chk.toString());
		assertEquals("AnItem.eins-uno", eins.of(uno).toString());
		assertEquals("AnItem.eins-duo", eins.of(duo).toString());
		assertEquals("AnItem.eins-chk", eins.of(chk).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(uno).getPattern());
		assertEquals(eins, eins.of(duo).getPattern());
		assertEquals(eins, eins.of(chk).getPattern());
		assertEqualsUnmodifiable(list(
				eins.of(uno), eins.of(duo), eins.of(chk)),
			eins.getSourceFeatures());

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(uno, eins.getTemplate(eins.of(uno)));
		assertSame(uno, zwei.getTemplate(zwei.of(uno)));
		assertEqualsUnmodifiable(list(eins.of(uno), eins.of(duo), eins.of(chk)), eins.getComponents());
		assertEqualsUnmodifiable(list(zwei.of(uno), zwei.of(duo), zwei.of(chk)), zwei.getComponents());
		assertEqualsUnmodifiable(list(uno, duo, chk), eins.getTemplates());

		assertEquals(
				"AnItem.eins-uno-contentType=AnItem.eins-duo-contentType",
				eins.of(chk).getCondition().toString());
		assertEquals(
				"AnItem.zwei-uno-contentType=AnItem.zwei-duo-contentType",
				zwei.of(chk).getCondition().toString());

		// test persistence
		final AnItem i1 = new AnItem();
		final ABlock b1a = i1.eins();
		final ABlock b1b = i1.zwei();
		assertLocator(null, b1a.getUnoLocator());
		assertLocator(null, b1a.getDuoLocator());
		assertLocator(null, b1b.getUnoLocator());
		assertLocator(null, b1b.getDuoLocator());

		b1a.setUno(new byte[]{1, 2, 3}, MediaType.JPEG);
		assertLocator("AnItem/eins-uno/"+i1+".jpg", b1a.getUnoLocator());
		assertLocator(null, b1a.getDuoLocator());
		assertLocator(null, b1b.getUnoLocator());
		assertLocator(null, b1b.getDuoLocator());

		try
		{
			b1a.setDuo(new byte[]{1, 2, 4}, MediaType.PNG);
			fail();
		}
		catch(final CheckViolationException e)
		{
			assertEquals(i1, e.getItem());
			assertEquals(eins.of(chk), e.getFeature());
		}
		assertLocator("AnItem/eins-uno/"+i1+".jpg", b1a.getUnoLocator());
		assertLocator(null, b1a.getDuoLocator());
		assertLocator(null, b1b.getUnoLocator());
		assertLocator(null, b1b.getDuoLocator());

		b1a.setDuo(new byte[]{1, 2, 8}, MediaType.JPEG);
		assertLocator("AnItem/eins-uno/"+i1+".jpg", b1a.getUnoLocator());
		assertLocator("AnItem/eins-duo/"+i1+".jpg", b1a.getDuoLocator());
		assertLocator(null, b1b.getUnoLocator());
		assertLocator(null, b1b.getDuoLocator());
	}

	@WrapperType(indent=2, comments=false)
	static final class ABlock extends Block
	{
		static final Media uno = new Media().optional().contentType(MediaType.JPEG, MediaType.PNG);
		static final Media duo = new Media().optional().contentType(MediaType.JPEG, MediaType.PNG);

		@SuppressWarnings("OverlyStrongTypeCast")
		static final CheckConstraint chk = new CheckConstraint(
				((IntegerField)uno.getContentType()).equal((IntegerField)duo.getContentType()));


		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getUnoURL()
		{
			return field().of(ABlock.uno).getURL(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		com.exedio.cope.pattern.MediaPath.Locator getUnoLocator()
		{
			return field().of(ABlock.uno).getLocator(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getUnoContentType()
		{
			return field().of(ABlock.uno).getContentType(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		boolean isUnoNull()
		{
			return field().of(ABlock.uno).isNull(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.util.Date getUnoLastModified()
		{
			return field().of(ABlock.uno).getLastModified(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		long getUnoLength()
		{
			return field().of(ABlock.uno).getLength(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		byte[] getUnoBody()
		{
			return field().of(ABlock.uno).getBody(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void getUnoBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).getBody(item(),body);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void getUnoBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).getBody(item(),body);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setUno(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value uno)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).set(item(),uno);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setUno(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			field().of(ABlock.uno).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setUno(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setUno(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setUno(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.uno).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getDuoURL()
		{
			return field().of(ABlock.duo).getURL(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		com.exedio.cope.pattern.MediaPath.Locator getDuoLocator()
		{
			return field().of(ABlock.duo).getLocator(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getDuoContentType()
		{
			return field().of(ABlock.duo).getContentType(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		boolean isDuoNull()
		{
			return field().of(ABlock.duo).isNull(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.util.Date getDuoLastModified()
		{
			return field().of(ABlock.duo).getLastModified(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		long getDuoLength()
		{
			return field().of(ABlock.duo).getLength(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		byte[] getDuoBody()
		{
			return field().of(ABlock.duo).getBody(item());
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void getDuoBody(@javax.annotation.Nonnull final java.io.OutputStream body)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).getBody(item(),body);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void getDuoBody(@javax.annotation.Nonnull final java.io.File body)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).getBody(item(),body);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDuo(@javax.annotation.Nullable final com.exedio.cope.pattern.Media.Value duo)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).set(item(),duo);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDuo(@javax.annotation.Nullable final byte[] body,@javax.annotation.Nullable final java.lang.String contentType)
		{
			field().of(ABlock.duo).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDuo(@javax.annotation.Nullable final java.io.InputStream body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDuo(@javax.annotation.Nullable final java.nio.file.Path body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setDuo(@javax.annotation.Nullable final java.io.File body,@javax.annotation.Nullable final java.lang.String contentType)
				throws
					java.io.IOException
		{
			field().of(ABlock.duo).set(item(),body,contentType);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);


		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		ABlock eins()
		{
			return AnItem.eins.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nonnull
		ABlock zwei()
		{
			return AnItem.zwei.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	// workaround eclipse warnings about unused imports when using static imports instead
	private static final Media uno = ABlock.uno;
	private static final Media duo = ABlock.duo;
	private static final CheckConstraint chk = ABlock.chk;
	private static final BlockField<ABlock> eins = AnItem.eins;
	private static final BlockField<ABlock> zwei = AnItem.zwei;
}

