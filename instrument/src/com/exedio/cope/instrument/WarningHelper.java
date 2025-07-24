package com.exedio.cope.instrument;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

final class WarningHelper
{
	private WarningHelper()
	{
	}

	static void printWarning(final Element sourceLocation, final Messager messager, final String key, final String message)
	{
		final SuppressWarnings suppressWarnings=sourceLocation.getAnnotation(SuppressWarnings.class);
		if (suppressWarnings!=null)
		{
			for (final String string: suppressWarnings.value())
			{
				if (key.equals(string))
					return;
			}
		}
		messager.printMessage(Diagnostic.Kind.WARNING, "[" + key + "] " + message, sourceLocation);
	}
}
