package com.exedio.cope.instrument;

import static java.util.Objects.requireNonNull;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

final class WarningHelper
{
	private WarningHelper()
	{
	}

	static void printWarning(final VariableElement variableElement, final Messager messager, final String key, final String message)
	{
		requireNonNull(variableElement);
		for (Element sourceLocation = variableElement; sourceLocation!=null; sourceLocation = sourceLocation.getEnclosingElement())
		{
			final SuppressWarnings suppressWarnings = sourceLocation.getAnnotation(SuppressWarnings.class);
			if(suppressWarnings != null)
			{
				for(final String string : suppressWarnings.value())
				{
					if(key.equals(string))
						return;
				}
			}
		}
		messager.printMessage(Diagnostic.Kind.WARNING, "[" + key + "] " + message, variableElement);
	}
}
