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

import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.WrapperInitial;

public final class MediaCatchPhraseItem extends MediaCatchPhraseSuperItem implements MediaUrlCatchphraseProvider
{
	@WrapperInitial
	@CopeSchemaName("phrase")
	static final StringField catchphrase = new StringField().optional().lengthRange(0, 20);

	@Override
	public String getMediaUrlCatchphrase(final MediaPath path)
	{
		assertSame(feature, path);
		return getCatchphrase();
	}

	MediaCatchPhraseItem(final String catchphrase)
	{
		this(Media.toValue(new byte[]{10}, "foo/bar"), catchphrase);
	}


	/**
	 * Creates a new MediaCatchPhraseItem with all the fields initially needed.
	 * @param feature the initial value for field {@link #feature}.
	 * @param catchphrase the initial value for field {@link #catchphrase}.
	 * @throws com.exedio.cope.MandatoryViolationException if feature is null.
	 * @throws com.exedio.cope.StringLengthViolationException if catchphrase violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	MediaCatchPhraseItem(
				@javax.annotation.Nonnull final com.exedio.cope.pattern.Media.Value feature,
				@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.pattern.MediaCatchPhraseSuperItem.feature,feature),
			com.exedio.cope.SetValue.map(MediaCatchPhraseItem.catchphrase,catchphrase),
		});
	}

	/**
	 * Creates a new MediaCatchPhraseItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private MediaCatchPhraseItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #catchphrase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getCatchphrase()
	{
		return MediaCatchPhraseItem.catchphrase.get(this);
	}

	/**
	 * Sets a new value for {@link #catchphrase}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setCatchphrase(@javax.annotation.Nullable final java.lang.String catchphrase)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		MediaCatchPhraseItem.catchphrase.set(this,catchphrase);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for mediaCatchPhraseItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MediaCatchPhraseItem> TYPE = com.exedio.cope.TypesBound.newType(MediaCatchPhraseItem.class,MediaCatchPhraseItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private MediaCatchPhraseItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
