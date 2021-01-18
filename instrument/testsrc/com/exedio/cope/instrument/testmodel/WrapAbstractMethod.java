package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.instrument.WrapInterim;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "AbstractClassNeverImplemented"})
@WrapInterim
abstract class WrapAbstractMethod
{
	@WrapInterim
	static final Consumer<WrapAbstractMethod> consumer = WrapAbstractMethod::abstractMethod;

	@WrapInterim
	abstract void abstractMethod();
}
