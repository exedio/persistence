package com.exedio.cope;

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.vault.VaultProperties;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TypeUpdateCounterTest extends TestWithEnvironment
{
	static final Model MODEL = Model.builder().
			name(TypeUpdateCounterTest.class).
			add(
					UnmodifiableGrandparent.TYPE,
					ModifiableParent.TYPE,
					ModifiableChild.TYPE,
					UnmodifiableChild.TYPE,
					BlobChild.TYPE,
					VaultChild.TYPE,
					SeparateUnmodifiable.TYPE,
					SeparateBlob.TYPE
			).build();

	TypeUpdateCounterTest()
	{
		super(MODEL);
	}

	@Test
	void hasUpdateCounter()
	{
		assertNull(UnmodifiableGrandparent.TYPE.table.updateCounter);
		assertNotNull(ModifiableParent.TYPE.table.updateCounter);
		assertNotNull(ModifiableChild.TYPE.table.updateCounter);
		assertNull(UnmodifiableChild.TYPE.table.updateCounter);
		assertNullIff(!isVaultAllFields(), BlobChild.TYPE.table.updateCounter);
		assertNullIff(isVaultEnabled(), VaultChild.TYPE.table.updateCounter);
		assertNull(SeparateUnmodifiable.TYPE.table.updateCounter);
		assertNullIff(!isVaultAllFields(), SeparateBlob.TYPE.table.updateCounter);
	}

	private static void assertNullIff(final boolean condition, final Object actual)
	{
		if(condition)
			assertNull(actual);
		else
			assertNotNull(actual);
	}

	private boolean isVaultEnabled()
	{
		return model.getConnectProperties().vault == null;
	}

	private boolean isVaultAllFields()
	{
		return Optional.ofNullable(model.getConnectProperties().vault).
				map(VaultProperties::isAppliedToAllFields).
				orElse(false);
	}

	@WrapperType(indent=2, comments=false)
	private static class UnmodifiableGrandparent extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private UnmodifiableGrandparent()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableGrandparent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<UnmodifiableGrandparent> TYPE = com.exedio.cope.TypesBound.newType(UnmodifiableGrandparent.class,UnmodifiableGrandparent::new);

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableGrandparent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class ModifiableParent extends UnmodifiableGrandparent
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField modA = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ModifiableParent(
					final int modA)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(ModifiableParent.modA,modA),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ModifiableParent(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModifiableParent> TYPE = com.exedio.cope.TypesBound.newType(ModifiableParent.class,ModifiableParent::new);

		@com.exedio.cope.instrument.Generated
		protected ModifiableParent(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class ModifiableChild extends ModifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField modB = new IntegerField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ModifiableChild(
					final int modA,
					final int modB)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.TypeUpdateCounterTest.ModifiableParent.modA,modA),
				com.exedio.cope.SetValue.map(ModifiableChild.modB,modB),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ModifiableChild(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ModifiableChild> TYPE = com.exedio.cope.TypesBound.newType(ModifiableChild.class,ModifiableChild::new);

		@com.exedio.cope.instrument.Generated
		protected ModifiableChild(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class UnmodifiableChild extends ModifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField unmod = new IntegerField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private UnmodifiableChild(
					final int modA,
					final int unmod)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.TypeUpdateCounterTest.ModifiableParent.modA,modA),
				com.exedio.cope.SetValue.map(UnmodifiableChild.unmod,unmod),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableChild(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<UnmodifiableChild> TYPE = com.exedio.cope.TypesBound.newType(UnmodifiableChild.class,UnmodifiableChild::new);

		@com.exedio.cope.instrument.Generated
		protected UnmodifiableChild(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class BlobChild extends ModifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final DataField data = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private BlobChild(
					final int modA,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.TypeUpdateCounterTest.ModifiableParent.modA,modA),
				com.exedio.cope.SetValue.map(BlobChild.data,data),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected BlobChild(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BlobChild> TYPE = com.exedio.cope.TypesBound.newType(BlobChild.class,BlobChild::new);

		@com.exedio.cope.instrument.Generated
		protected BlobChild(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class VaultChild extends ModifiableParent
	{
		@Wrapper(wrap="*", visibility=NONE)
		@Vault
		private static final DataField data = new DataField();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private VaultChild(
					final int modA,
					@javax.annotation.Nonnull final com.exedio.cope.DataField.Value data)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.TypeUpdateCounterTest.ModifiableParent.modA,modA),
				com.exedio.cope.SetValue.map(VaultChild.data,data),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected VaultChild(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<VaultChild> TYPE = com.exedio.cope.TypesBound.newType(VaultChild.class,VaultChild::new);

		@com.exedio.cope.instrument.Generated
		protected VaultChild(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


		@WrapperType(indent=2, comments=false)
	private static class SeparateUnmodifiable extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField unmod = new IntegerField().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private SeparateUnmodifiable(
					final int unmod)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(SeparateUnmodifiable.unmod,unmod),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected SeparateUnmodifiable(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SeparateUnmodifiable> TYPE = com.exedio.cope.TypesBound.newType(SeparateUnmodifiable.class,SeparateUnmodifiable::new);

		@com.exedio.cope.instrument.Generated
		protected SeparateUnmodifiable(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class SeparateBlob extends Item
	{
		@SuppressWarnings("unused")
		@Wrapper(wrap="*", visibility=NONE)
		private static final DataField data = new DataField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private SeparateBlob()
		{
			this(com.exedio.cope.SetValue.EMPTY_ARRAY);
		}

		@com.exedio.cope.instrument.Generated
		protected SeparateBlob(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SeparateBlob> TYPE = com.exedio.cope.TypesBound.newType(SeparateBlob.class,SeparateBlob::new);

		@com.exedio.cope.instrument.Generated
		protected SeparateBlob(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
