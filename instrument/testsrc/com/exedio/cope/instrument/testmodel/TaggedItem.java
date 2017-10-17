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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.SimpleSettable;

/**
 * @cope.type private
 * @cope.constructor private
 * @cope.generic.constructor public
 * @cope.activation.constructor package
 * @cope.indent 2
 */
public class TaggedItem extends Item implements OneOverrideFeatureable
{
	/**
	 * @cope.initial
	 */
	static final SimpleSettable initialFeature = new SimpleSettable();

	/**
	 * @cope.ignore
	 */
	static final SimpleSettable ignoredFeature = new SimpleSettable(true);

	/**
	 * @cope.one public
	 */
	private static final SimpleSettable publicFeature = new SimpleSettable();

	/**
	 * @cope.one package
	 */
	private static final SimpleSettable packageFeature = new SimpleSettable();

	/**
	 * @cope.one protected
	 */
	private static final SimpleSettable protectedFeature = new SimpleSettable();

	/**
	 * @cope.one private
	 */
	static final SimpleSettable privateFeature = new SimpleSettable();

	/**
	 * @cope.one none
	 */
	@SuppressWarnings("unused")
	private static final SimpleSettable noneFeature = new SimpleSettable();

	/**
	 * @cope.one non-final
	 */
	private static final SimpleSettable nonFinalFeature = new SimpleSettable();

	/**
	 * @cope.one internal
	 */
	private static final SimpleSettable internalFeature = new SimpleSettable();

	/**
	 * @cope.get boolean-as-is
	 */
	private static final BooleanField booleanAsIsFeature = new BooleanField().optional();

	/**
	 * @cope.get boolean-as-is public
	 */
	private static final BooleanField booleanAsIsPublicFeature = new BooleanField().optional();

	/**
	 * @cope.one override public
	 */
	private static final SimpleSettable overrideFeature = new SimpleSettable();

	// marker for end of hand-written code
		/**
		 * Creates a new TaggedItem with all the fields initially needed.
		 * @param initialFeature the initial value for field {@link #initialFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		private TaggedItem(
					@javax.annotation.Nullable final java.lang.String initialFeature)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				TaggedItem.initialFeature.map(initialFeature),
			});
		}

		/**
		 * Creates a new TaggedItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		public TaggedItem(final com.exedio.cope.SetValue<?>... setValues)
		{
			super(setValues);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		final java.lang.String oneInitialFeature()
		{
			return TaggedItem.initialFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		public final java.lang.String onePublicFeature()
		{
			return TaggedItem.publicFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		final java.lang.String onePackageFeature()
		{
			return TaggedItem.packageFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		protected final java.lang.String oneProtectedFeature()
		{
			return TaggedItem.protectedFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private final java.lang.String onePrivateFeature()
		{
			return TaggedItem.privateFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private java.lang.String oneNonFinalFeature()
		{
			return TaggedItem.nonFinalFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private final java.lang.String oneInternalFeatureInternal()
		{
			return TaggedItem.internalFeature.one(this);
		}

		/**
		 * Returns the value of {@link #booleanAsIsFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		private final java.lang.Boolean isBooleanAsIsFeature()
		{
			return TaggedItem.booleanAsIsFeature.get(this);
		}

		/**
		 * Sets a new value for {@link #booleanAsIsFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		private final void setBooleanAsIsFeature(@javax.annotation.Nullable final java.lang.Boolean booleanAsIsFeature)
		{
			TaggedItem.booleanAsIsFeature.set(this,booleanAsIsFeature);
		}

		/**
		 * Returns the value of {@link #booleanAsIsPublicFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		public final java.lang.Boolean isBooleanAsIsPublicFeature()
		{
			return TaggedItem.booleanAsIsPublicFeature.get(this);
		}

		/**
		 * Sets a new value for {@link #booleanAsIsPublicFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		private final void setBooleanAsIsPublicFeature(@javax.annotation.Nullable final java.lang.Boolean booleanAsIsPublicFeature)
		{
			TaggedItem.booleanAsIsPublicFeature.set(this,booleanAsIsPublicFeature);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		@java.lang.Override
		public final java.lang.String oneOverrideFeature()
		{
			return TaggedItem.overrideFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for taggedItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<TaggedItem> TYPE = com.exedio.cope.TypesBound.newType(TaggedItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		TaggedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
