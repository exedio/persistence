
package com.exedio.copernica.admin;

import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.lib.Model;
import com.exedio.copernica.Util;

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
			model = Util.getModel(getServletConfig());
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

}
