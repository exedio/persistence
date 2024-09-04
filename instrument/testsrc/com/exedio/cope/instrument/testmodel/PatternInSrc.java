package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.instrument.BooleanGetter;
import com.exedio.cope.instrument.Nullability;
import com.exedio.cope.instrument.NullabilityGetter;
import com.exedio.cope.instrument.Parameter;
import com.exedio.cope.instrument.StringGetter;
import com.exedio.cope.instrument.ThrownGetter;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import com.exedio.cope.instrument.WrapInterim;
import java.io.Serial;
import java.util.Set;

@WrapFeature
class PatternInSrc extends Pattern
{
	@Serial
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	@Wrap(order=10,
			nameGetter=CallMeIshmael.class,
			thrownGetter=RuntimeThrown.class,
			nullability=AlwaysNonnull.class,
			hide=DontHide.class)
	@WrapInterim(methodBody=false)
	public PatternInSrcItem get(final Item i, @Parameter(value="a", nullability=AlwaysNonnull.class) final Integer a, @Parameter("b") final int b)
	{
		return PatternInSrcItem.methodThatIsNotInInterimCode();
	}

	@WrapInterim
	static final class CallMeIshmael implements StringGetter<PatternInSrc>
	{
		@WrapInterim
		@Override
		public String get(final PatternInSrc feature)
		{
			return "ishmael";
		}
	}

	@WrapInterim
	static final class RuntimeThrown implements ThrownGetter<PatternInSrc>
	{
		@WrapInterim
		@Override
		public Set<Class<? extends Throwable>> get(final PatternInSrc feature)
		{
			return Set.of(RuntimeException.class);
		}
	}

	@WrapInterim
	static final class AlwaysNonnull implements NullabilityGetter<PatternInSrc>
	{
		@WrapInterim
		@Override
		public Nullability getNullability(final PatternInSrc feature)
		{
			return Nullability.NONNULL;
		}
	}

	@WrapInterim
	static final class DontHide implements BooleanGetter<PatternInSrc>
	{
		@WrapInterim
		@Override
		public boolean get(final PatternInSrc feature)
		{
			return false;
		}
	}
}
