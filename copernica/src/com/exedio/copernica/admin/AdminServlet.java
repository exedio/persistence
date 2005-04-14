
package com.exedio.copernica.admin;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.Model;
import com.exedio.cope.lib.NestingRuntimeException;
import com.exedio.copernica.TransientCopernicaProvider;
import com.exedio.cops.CopsServlet;

/**
 * The servlet providing the COPE Database Administration application.
 * 
 * In order to use it, you have to deploy the servlet in your <code>web.xml</code>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <code>web.xml</code> would contain a snippet like this:  
 *  
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;admin&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.copernica.admin.AdminServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.lib.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;admin&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/admin.jsp&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 */
public final class AdminServlet extends CopsServlet
{
	final static String ENCODING = "ISO-8859-1";

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

	protected void doRequest(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html; charset="+ENCODING);

		final PrintStream out = new PrintStream(response.getOutputStream(), false, ENCODING);
		Admin_Jspm.write(out, request, model);
		out.close();
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
