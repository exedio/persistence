
package com.exedio.copernica.admin;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.copernica.TransientCopernicaProvider;

public class AdminServlet extends HttpServlet
{

	Model model = null;
	
	public final void init()
	{
		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			model = getModel();
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	private void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		final PrintStream out = new PrintStream(response.getOutputStream());
		response.setContentType("text/html");
		Admin_Jspm.write(out, request, model);
		out.close();
	}


	public void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	public void doPost(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		doRequest(request, response);
	}

	private final Model getModel()
	{
		try
		{
			final ServletConfig config = getServletConfig();
			final String modelName = config.getInitParameter("model");
			if(modelName==null)
				throw new NullPointerException("init-param 'model' missing");

			final int pos = modelName.indexOf('#');
			if(pos<=0)
				throw new RuntimeException("init-param 'model' does not contain '#', but was "+modelName);
			final String modelClassName = modelName.substring(0, pos);
			final String modelAttributeName = modelName.substring(pos+1);

			final Class modelClass = Class.forName(modelClassName);
			final Field modelField = modelClass.getField(modelAttributeName);
			final Model model = (Model)modelField.get(null);
			TransientCopernicaProvider.initialize(model, config);
			return model;
		}
		catch(ClassNotFoundException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(NoSuchFieldException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
	}
	
}
