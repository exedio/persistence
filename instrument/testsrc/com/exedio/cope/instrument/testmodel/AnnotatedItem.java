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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.instrument.Visibility.PACKAGE;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.instrument.testfeature.SimpleSettable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@WrapperType(
	type=PRIVATE,
	constructor=PRIVATE,
	genericConstructor=PUBLIC,
	activationConstructor=PACKAGE,
	indent=2
)
@AnnotationNotInInterim
@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
public class AnnotatedItem extends Item implements OneOverrideFeatureable
{
	@WrapperInitial
	@AnnotationNotInInterim
	static final SimpleSettable initialFeature = new SimpleSettable();

	@WrapperIgnore
	@SuppressWarnings("unused") // OK: test @WrapperIgnore
	static final SimpleSettable ignoredFeature = new SimpleSettable(true);

	@Wrapper(wrap="one", visibility=PUBLIC)
	private static final SimpleSettable publicFeature = new SimpleSettable();

	@Wrapper(wrap="one", visibility=PACKAGE)
	private static final SimpleSettable packageFeature = new SimpleSettable();

	@Wrapper(wrap="one", visibility=PROTECTED)
	private static final SimpleSettable protectedFeature = new SimpleSettable();

	@Wrapper(wrap="one", visibility=PRIVATE)
	static final SimpleSettable privateFeature = new SimpleSettable();

	@Wrapper(wrap="one", visibility=NONE)
	@SuppressWarnings("unused")
	private static final SimpleSettable noneFeature = new SimpleSettable();

	@Wrapper(wrap="one", asFinal=false)
	private static final SimpleSettable nonFinalFeature = new SimpleSettable();

	@Wrapper(wrap="one", internal=true)
	private static final SimpleSettable internalFeature = new SimpleSettable();

	@Wrapper(wrap="get", booleanAsIs=true)
	private static final BooleanField booleanAsIsFeature = new BooleanField().optional();

	@Wrapper(wrap="get", booleanAsIs=true, visibility=PUBLIC)
	private static final BooleanField booleanAsIsPublicFeature = new BooleanField().optional();

	@Wrapper(wrap="one", override=true, visibility=PUBLIC)
	private static final SimpleSettable overrideFeature = new SimpleSettable();

	@AnnotationNotInInterim
	@SuppressWarnings("unused") // OK: just for testing instrumentor
	static final void annotatedMethod()
	{
		// empty
	}

	// marker for end of hand-written code
		/**
		 * Creates a new AnnotatedItem with all the fields initially needed.
		 * @param initialFeature the initial value for field {@link #initialFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
		private AnnotatedItem(
					@javax.annotation.Nullable final java.lang.String initialFeature)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnnotatedItem.initialFeature.map(initialFeature),
			});
		}

		/**
		 * Creates a new AnnotatedItem and sets the given fields initially.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
		public AnnotatedItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		final java.lang.String oneInitialFeature()
		{
			return AnnotatedItem.initialFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		public final java.lang.String onePublicFeature()
		{
			return AnnotatedItem.publicFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		final java.lang.String onePackageFeature()
		{
			return AnnotatedItem.packageFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		protected final java.lang.String oneProtectedFeature()
		{
			return AnnotatedItem.protectedFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private java.lang.String onePrivateFeature()
		{
			return AnnotatedItem.privateFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private java.lang.String oneNonFinalFeature()
		{
			return AnnotatedItem.nonFinalFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		private java.lang.String oneInternalFeatureInternal()
		{
			return AnnotatedItem.internalFeature.one(this);
		}

		/**
		 * Returns the value of {@link #booleanAsIsFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		private java.lang.Boolean isBooleanAsIsFeature()
		{
			return AnnotatedItem.booleanAsIsFeature.get(this);
		}

		/**
		 * Sets a new value for {@link #booleanAsIsFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		private void setBooleanAsIsFeature(@javax.annotation.Nullable final java.lang.Boolean booleanAsIsFeature)
		{
			AnnotatedItem.booleanAsIsFeature.set(this,booleanAsIsFeature);
		}

		/**
		 * Returns the value of {@link #booleanAsIsPublicFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
		@javax.annotation.Nullable
		public final java.lang.Boolean isBooleanAsIsPublicFeature()
		{
			return AnnotatedItem.booleanAsIsPublicFeature.get(this);
		}

		/**
		 * Sets a new value for {@link #booleanAsIsPublicFeature}.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
		private void setBooleanAsIsPublicFeature(@javax.annotation.Nullable final java.lang.Boolean booleanAsIsPublicFeature)
		{
			AnnotatedItem.booleanAsIsPublicFeature.set(this,booleanAsIsPublicFeature);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="one")
		@java.lang.Override
		public final java.lang.String oneOverrideFeature()
		{
			return AnnotatedItem.overrideFeature.one(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		/**
		 * The persistent type information for annotatedItem.
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
		private static final com.exedio.cope.Type<AnnotatedItem> TYPE = com.exedio.cope.TypesBound.newType(AnnotatedItem.class);

		/**
		 * Activation constructor. Used for internal purposes only.
		 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
		 */
		@javax.annotation.Generated("com.exedio.cope.instrument")
		AnnotatedItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
