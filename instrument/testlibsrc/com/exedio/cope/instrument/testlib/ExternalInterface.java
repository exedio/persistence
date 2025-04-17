package com.exedio.cope.instrument.testlib;

import com.exedio.cope.instrument.WrapImplementsInterim;

@WrapImplementsInterim(addMethods=true)
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional") // OK: functional interface not needed
public interface ExternalInterface
{
	@SuppressWarnings("unused")
	int methodInInterface();
}
