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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.NullableAsOptional;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE, genericConstructor=NONE)
final class PasswordRecoveryInstrumentItem extends Item
{
	static final PasswordRecovery standard = new PasswordRecovery();

	@Wrapper(wrap="*", nullableAsOptional=NullableAsOptional.YES)
	static final PasswordRecovery nullableAsOptional = new PasswordRecovery();

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="issue")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.PasswordRecovery.Token issueStandard(@javax.annotation.Nonnull final com.exedio.cope.pattern.PasswordRecovery.Config config)
	{
		return PasswordRecoveryInstrumentItem.standard.issue(this,config);
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a valid token, if existing, otherwise null
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getValidToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	com.exedio.cope.pattern.PasswordRecovery.Token getValidStandardToken(final long secret)
	{
		return PasswordRecoveryInstrumentItem.standard.getValidToken(this,secret);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeStandard(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		PasswordRecoveryInstrumentItem.standard.purge(ctx);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="issue")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.PasswordRecovery.Token issueNullableAsOptional(@javax.annotation.Nonnull final com.exedio.cope.pattern.PasswordRecovery.Config config)
	{
		return PasswordRecoveryInstrumentItem.nullableAsOptional.issue(this,config);
	}

	/**
	 * @param secret a secret for password recovery
	 * @return a valid token, if existing, otherwise an empty {@link java.util.Optional}
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getValidToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Optional<com.exedio.cope.pattern.PasswordRecovery.Token> getValidNullableAsOptionalToken(final long secret)
	{
		return java.util.Optional.ofNullable(PasswordRecoveryInstrumentItem.nullableAsOptional.getValidToken(this,secret));
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="purge")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void purgeNullableAsOptional(@javax.annotation.Nonnull final com.exedio.cope.util.JobContext ctx)
	{
		PasswordRecoveryInstrumentItem.nullableAsOptional.purge(ctx);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for passwordRecoveryInstrumentItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<PasswordRecoveryInstrumentItem> TYPE = com.exedio.cope.TypesBound.newType(PasswordRecoveryInstrumentItem.class,PasswordRecoveryInstrumentItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private PasswordRecoveryInstrumentItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
