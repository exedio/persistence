/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.editor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.exedio.cope.Cope;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.pattern.History;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cope.util.ConnectToken;
import com.exedio.cope.util.ServletUtil;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;
import com.exedio.cops.XMLEncoder;

public abstract class Editor implements Filter
{
	static final String UTF8 = CopsServlet.UTF8;
	
	private final Model model;
	
	/**
	 * Subclasses must define a public no-args constructor
	 * providing the model.
	 */
	protected Editor(final Model model)
	{
		if(model==null)
			throw new NullPointerException("model was null in " + getClass().getName());
		
		this.model = model;
	}
	
	private FilterConfig config = null;
	private boolean persistentPreviews = false;
	private ConnectToken connectToken = null;
	private final Object connectTokenLock = new Object();
	
	public final void init(final FilterConfig config)
	{
		this.config = config;
		for(final Type<?> type : model.getTypes())
			if(type==DraftItem.TYPE) // DraftItem implies Draft because of the parent field
			{
				persistentPreviews = true;
				break;
			}
	}
	
	private final void startTransaction(final String name)
	{
		synchronized(connectTokenLock)
		{
			if(connectToken==null)
			{
				connectToken = ServletUtil.connect(model, config, getClass().getName());
				model.reviseIfSupported();
			}
		}
		model.startTransaction(getClass().getName() + '#' + name);
	}
	
	public final void destroy()
	{
		synchronized(connectTokenLock)
		{
			if(connectToken!=null)
			{
				connectToken.returnIt();
				connectToken = null;
			}
		}
	}
	
	protected abstract Session login(String user, String password);
	
	@SuppressWarnings("unused")
	protected String getBorderButtonURL(HttpServletRequest request, HttpServletResponse response, boolean bordersEnabled)
	{
		return null;
	}
	
	@SuppressWarnings("unused")
	protected String getCloseButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	@SuppressWarnings("unused")
	protected String getPreviousPositionButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	public final void doFilter(
			final ServletRequest servletRequest,
			final ServletResponse servletResponse,
			final FilterChain chain)
	throws IOException, ServletException
	{
		if(!(servletRequest instanceof HttpServletRequest))
		{
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		final HttpServletRequest request = (HttpServletRequest)servletRequest;
		
		if(LOGIN_URL_PATH_INFO.equals(request.getPathInfo()))
		{
			servletRequest.setCharacterEncoding(UTF8);
			final HttpServletResponse response = (HttpServletResponse)servletResponse;
			final HttpSession httpSession = request.getSession(true);
			final Object anchor = httpSession.getAttribute(ANCHOR);
			
			if(anchor==null)
				doLogin(request, httpSession, response);
			else
			{
				if(request.getParameter(PREVIEW_OVERVIEW)!=null)
					doPreviewOverview(request, response, (Anchor)anchor);
				else
					doBar(request, httpSession, response, (Anchor)anchor);
			}
			
			return;
		}

		final HttpSession httpSession = request.getSession(false);
		if(httpSession!=null)
		{
			final Object anchor = httpSession.getAttribute(ANCHOR);
			if(anchor!=null)
			{
				try
				{
					tls.set(new TL(this, request, (HttpServletResponse)servletResponse, (Anchor)anchor));
					chain.doFilter(request, servletResponse);
				}
				finally
				{
					tls.remove();
				}
			}
			else
				chain.doFilter(request, servletResponse);
		}
		else
		{
			chain.doFilter(request, servletResponse);
		}
	}
	
	private static final void redirectHome(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + '/'));
	}
	
	static final String AVOID_COLLISION = "contentEditorBar823658617";
	static final String REFERER = "referer";
	private static final String BORDERS_ON  = "bordersOn";
	private static final String BORDERS_OFF = "bordersOff";
	static final String CLOSE = "close";
	static final String BAR_FEATURE = "feature";
	static final String BAR_ITEM    = "item";
	static final String BAR_TEXT    = "text";
	static final String BAR_FILE    = "file";
	static final String BAR_ITEM_FROM = "itemPrevious";
	static final String PREVIEW = "preview";
	
	private static final String CLOSE_IMAGE       = CLOSE       + ".x";
	private static final String BORDERS_ON_IMAGE  = BORDERS_ON  + ".x";
	private static final String BORDERS_OFF_IMAGE = BORDERS_OFF + ".x";
	
	@SuppressWarnings("deprecation")
	private static final boolean isMultipartContent(final HttpServletRequest request)
	{
		return ServletFileUpload.isMultipartContent(request);
	}
	
	static final String PREVIEW_OVERVIEW = "po";
	static final String PREVIEW_PUBLISH = "preview.publish";
	static final String PREVIEW_DISCARD = "preview.discard";
	static final String PREVIEW_PERSIST = "preview.persist";
	static final String PREVIEW_PERSIST_COMMENT = "preview.persistComment";
	static final String PREVIEW_IDS = "id";
	static final String PERSISTENT_PREVIEW_ID = "persistent_preview.id";
	static final String PERSISTENT_PREVIEW_LOAD = "persistent_preview.load";
	
	private final void doPreviewOverview(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Anchor anchor)
	throws IOException
	{
		if(Cop.isPost(request))
		{
			final String[] idA = request.getParameterValues(PREVIEW_IDS);
			final HashSet<String> ids = idA!=null ? new HashSet<String>(Arrays.asList(idA)) : null;
			if(request.getParameter(PREVIEW_PUBLISH)!=null)
			{
				final Map<Preview, String> previews = anchor.getPreviewsModifiable();
				try
				{
					startTransaction("publishPreviews");
					for(final Iterator<Map.Entry<Preview, String>> i = previews.entrySet().iterator(); i.hasNext(); )
					{
						final Map.Entry<Preview, String> e = i.next();
						final Preview p = e.getKey();
						if(ids!=null && ids.contains(p.getID()))
						{
							p.publish(model, e.getValue());
							i.remove();
						}
					}
					// TODO maintain history
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(PREVIEW_PERSIST)!=null)
			{
				final Map<Preview, String> previews = anchor.getPreviewsModifiable();
				try
				{
					startTransaction("persistProposals");
					final Draft parent = new Draft(anchor.user, anchor.sessionName, request.getParameter(PREVIEW_PERSIST_COMMENT));
					int position = 0;
					for(final Iterator<Map.Entry<Preview, String>> i = previews.entrySet().iterator(); i.hasNext(); )
					{
						final Map.Entry<Preview, String> e = i.next();
						final Preview p = e.getKey();
						if(ids!=null && ids.contains(p.getID()))
						{
							new DraftItem(parent, position++, p.feature, p.item, p.getOldValue(model), e.getValue());
							i.remove();
						}
					}
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else if(request.getParameter(PREVIEW_DISCARD)!=null)
			{
				final Map<Preview, String> previews = anchor.getPreviewsModifiable();
				for(final Iterator<Map.Entry<Preview, String>> i = previews.entrySet().iterator(); i.hasNext(); )
				{
					final Map.Entry<Preview, String> e = i.next();
					final Preview p = e.getKey();
					if(ids!=null && ids.contains(p.getID()))
						i.remove();
				}
			}
			else if(request.getParameter(PERSISTENT_PREVIEW_LOAD)!=null)
			{
				try
				{
					startTransaction("loadPreview");
					final Draft p =
						(Draft)model.getItem(request.getParameter(PERSISTENT_PREVIEW_ID));
					for(final DraftItem f : p.getFeatures())
						anchor.setPreview(
								DraftItem.newValue.get(f),
								(StringField)model.getFeature(DraftItem.feature.get(f)),
								model.getItem(DraftItem.item.get(f)));
				}
				catch(NoSuchIDException e)
				{
					throw new RuntimeException(e);
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
		}
		
		final StringBuilder out = new StringBuilder();
		try
		{
			startTransaction("proposal");
			final List<Draft> persistent =
				persistentPreviews
				? Draft.TYPE.search(null, Draft.date, false)
				: null;
			Preview_Jspm.writeOverview(
					out, model,
					response.encodeURL(LOGIN_URL + '?' + PREVIEW_OVERVIEW + "=t"),
					anchor.getPreviews(),
					persistentPreviews, persistent);
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
		
		response.setContentType("text/html; charset="+UTF8);
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.addHeader("Cache-Control", "max-age=0");
		response.addHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
		writeBody(out, response);
	}
	
	private final void doBar(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response,
			final Anchor anchor)
	throws IOException
	{
		if(!Cop.isPost(request))
		{
			redirectHome(request, response);
			return;
		}
		
		final String referer;
		
		if(isMultipartContent(request))
		{
			final HashMap<String, String> fields = new HashMap<String, String>();
			final HashMap<String, FileItem> files = new HashMap<String, FileItem>();
			final FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding(UTF8);
			try
			{
				for(Iterator<?> i = upload.parseRequest(request).iterator(); i.hasNext(); )
				{
					final FileItem item = (FileItem)i.next();
					if(item.isFormField())
						fields.put(item.getFieldName(), item.getString(UTF8));
					else
						files.put(item.getFieldName(), item);
				}
			}
			catch(FileUploadException e)
			{
				throw new RuntimeException(e);
			}
			
			final String featureID = fields.get(BAR_FEATURE);
			if(featureID==null)
				throw new NullPointerException();
			
			final Media feature = (Media)model.getFeature(featureID);
			if(feature==null)
				throw new NullPointerException(featureID);
			
			final String itemID = fields.get(BAR_ITEM);
			if(itemID==null)
				throw new NullPointerException();
			
			final FileItem file = files.get(BAR_FILE);
		
			try
			{
				startTransaction("publishFile(" + featureID + ',' + itemID + ')');
				
				final Item item = model.getItem(itemID);

				for(final History history : History.getHistories(item.getCopeType()))
				{
					final History.Event event = history.createEvent(item, anchor.getHistoryAuthor(), false);
					event.createFeature(
							feature, feature.getName(),
							feature.isNull(item) ? null : ("file type=" + feature.getContentType(item) + " size=" + feature.getLength(item)),
							"file name=" + file.getName() + " type=" + file.getContentType() + " size=" + file.getSize());
				}
				
				// TODO use more efficient setter with File or byte[]
				feature.set(item, file.getInputStream(), file.getContentType());
				
				model.commit();
			}
			catch(NoSuchIDException e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			
			referer = fields.get(REFERER);
		}
		else // isMultipartContent
		{
			if(request.getParameter(BORDERS_ON)!=null || request.getParameter(BORDERS_ON_IMAGE)!=null)
			{
				anchor.borders = true;
			}
			else if(request.getParameter(BORDERS_OFF)!=null || request.getParameter(BORDERS_OFF_IMAGE)!=null)
			{
				anchor.borders = false;
			}
			else if(request.getParameter(CLOSE)!=null || request.getParameter(CLOSE_IMAGE)!=null)
			{
				httpSession.removeAttribute(ANCHOR);
			}
			else
			{
				final String featureID = request.getParameter(BAR_FEATURE);
				if(featureID==null)
					throw new NullPointerException();
				
				final Feature featureO = model.getFeature(featureID);
				if(featureO==null)
					throw new NullPointerException(featureID);
				
				final String itemID = request.getParameter(BAR_ITEM);
				if(itemID==null)
					throw new NullPointerException();
				
				if(featureO instanceof StringField)
				{
					final StringField feature = (StringField)featureO;
					final String value = request.getParameter(BAR_TEXT);
				
					try
					{
						startTransaction("barText(" + featureID + ',' + itemID + ')');
						
						final Item item = model.getItem(itemID);
	
						if(request.getParameter(PREVIEW)!=null)
						{
							anchor.setPreview(value, feature, item);
						}
						else
						{
							String v = value;
							if("".equals(v))
								v = null;
							for(final History history : History.getHistories(item.getCopeType()))
							{
								final History.Event event = history.createEvent(item, anchor.getHistoryAuthor(), false);
								event.createFeature(feature, feature.getName(), feature.get(item), v);
							}
							feature.set(item, v);
							anchor.notifyPublished(feature, item);
						}
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
				else
				{
					final IntegerField feature = (IntegerField)featureO;
					final String itemIDFrom = request.getParameter(BAR_ITEM_FROM);
					if(itemIDFrom==null)
						throw new NullPointerException();
					
					try
					{
						startTransaction("swapPosition(" + featureID + ',' + itemIDFrom + ',' + itemID + ')');
						
						final Item itemFrom = model.getItem(itemIDFrom);
						final Item itemTo   = model.getItem(itemID);
	
						final Integer positionFrom = feature.get(itemFrom);
						final Integer positionTo   = feature.get(itemTo);
						feature.set(itemFrom, feature.getMinimum());
						feature.set(itemTo,   positionFrom);
						feature.set(itemFrom, positionTo);
						
						for(final History history : History.getHistories(itemFrom.getCopeType()))
						{
							final History.Event event = history.createEvent(itemFrom, anchor.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionFrom, positionTo);
						}
						for(final History history : History.getHistories(itemTo.getCopeType()))
						{
							final History.Event event = history.createEvent(itemTo, anchor.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionTo, positionFrom);
						}
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
			}
			
			referer = request.getParameter(REFERER);
		}
		
		if(referer!=null)
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + referer));
	}
	
	static final String LOGIN_URL = "contentEditorLogin.html";
	private static final String LOGIN_URL_PATH_INFO = '/' + LOGIN_URL;
	static final String LOGIN_SUBMIT   = "login.submit";
	static final String LOGIN_USER     = "login.user";
	static final String LOGIN_PASSWORD = "login.password";
	
	private final void doLogin(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response)
	throws IOException
	{
		assert httpSession!=null;
		response.setContentType("text/html; charset="+UTF8);
		if(Cop.isPost(request) && request.getParameter(LOGIN_SUBMIT)!=null)
		{
			final String user = request.getParameter(LOGIN_USER);
			final String password = request.getParameter(LOGIN_PASSWORD);
			try
			{
				startTransaction("login");
				final Session session = login(user, password);
				if(session!=null)
				{
					httpSession.setAttribute(ANCHOR, new Anchor(user, session, session.getName()));
					redirectHome(request, response);
				}
				else
				{
					final StringBuilder out = new StringBuilder();
					Login_Jspm.write(out, response, Editor.class.getPackage(), user);
					writeBody(out, response);
				}
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
		}
		else
		{
			final StringBuilder out = new StringBuilder();
			Login_Jspm.write(out, response, Editor.class.getPackage(), null);
			writeBody(out, response);
		}
	}
	
	private static final String ANCHOR = Session.class.getName();
	
	private static final class TL
	{
		final Editor filter;
		final HttpServletRequest request;
		final HttpServletResponse response;
		final Anchor anchor;
		private HashMap<IntegerField, Item> positionItems = null;
		
		TL(
				final Editor filter,
				final HttpServletRequest request,
				final HttpServletResponse response,
				final Anchor anchor)
		{
			this.filter = filter;
			this.request = request;
			this.response = response;
			this.anchor = anchor;
			
			assert filter!=null;
			assert request!=null;
			assert response!=null;
			assert anchor!=null;
		}
		
		Item registerPositionItem(final IntegerField feature, final Item item)
		{
			final Integer next = feature.get(item);
			if(next==null)
				return null;
			
			if(positionItems==null)
				positionItems = new HashMap<IntegerField, Item>();
			
			final Item result = positionItems.put(feature, item);
			if(result==null)
				return null;
			
			final Integer previous = feature.get(result);
			return (previous!=null && previous.intValue()<next.intValue()) ? result : null;
		}
	}
	
	private static final ThreadLocal<TL> tls = new ThreadLocal<TL>();
	
	public static final boolean isLoggedIn()
	{
		return tls.get()!=null;
	}
	
	public static final Session getSession()
	{
		final TL tl = tls.get();
		return tl!=null ? tl.anchor.session : null;
	}
	
	private static final <K> Item getItem(final MapField<K, String> feature, final K key, final Item item)
	{
		return
				feature.getRelationType().searchSingletonStrict(
						feature.getKey().equal(key).and(
						Cope.equalAndCast(feature.getParent(), item)));
	}
	
	public static final <K> String edit(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		checkEdit(feature, item);
		
		return edit(
				tl,
				content,
				(StringField)feature.getValue(),
				getItem(feature, key, item));
	}
	
	public static final String edit(final String content, final StringField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		return edit(tl, content, feature, item);
	}
	
	static final String EDIT_METHOD_LINE = AVOID_COLLISION + "line";
	static final String EDIT_METHOD_FILE = AVOID_COLLISION + "file";
	static final String EDIT_METHOD_AREA = AVOID_COLLISION + "area";
	
	private static final String edit(final TL tl, final String content, final StringField feature, final Item item)
	{
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		if(!tl.anchor.borders)
		{
			final String preview = tl.anchor.getPreview(feature, item);
			return (preview!=null) ? preview : content;
		}
		
		final boolean block = feature.getMaximumLength()>StringField.DEFAULT_LENGTH;
		final String savedContent = feature.get(item);
		final String pageContent;
		final String editorContent;
		final boolean previewAllowed;
		if(content!=null ? content.equals(savedContent) : (savedContent==null))
		{
			previewAllowed = true;
			final String preview = tl.anchor.getPreview(feature, item);
			if(preview!=null)
				pageContent = editorContent = preview;
			else
				pageContent = editorContent = savedContent; // equals content anyway
		}
		else
		{
			previewAllowed = false;
			pageContent = content;
			editorContent = savedContent;
		}
		
		final String tag = block ? "div" : "span";
		final String editorContentEncoded = XMLEncoder.encode(editorContent);
		final StringBuilder bf = new StringBuilder();
		bf.append('<').
			append(tag).
			append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					"return " + (block ? EDIT_METHOD_AREA : EDIT_METHOD_LINE) + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(block ? editorContentEncoded.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") : editorContentEncoded).
					append("'," + previewAllowed + ");\"").
			append('>').
			append(pageContent).
			append("</").
			append(tag).
			append('>');
		
		return bf.toString();
	}
	
	public static final String edit(final Media feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final StringBuilder bf = new StringBuilder();
		bf.append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					"return " + EDIT_METHOD_FILE + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(XMLEncoder.encode(feature.getURL(item))).
					append("');\"");
		
		return bf.toString();
	}
	
	public static final String edit(final MediaFilter feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		checkEdit(feature, item);
		
		return edit(feature.getSource(), item);
	}
	
	public static final String edit(final IntegerField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.anchor.borders)
			return "";
		
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final Item previousItem = tl.registerPositionItem(feature, item);
		if(previousItem==null)
			return "";
		
		final HttpServletRequest request = tl.request;
		final String buttonURL = tl.filter.getPreviousPositionButtonURL(request, tl.response);
		return
			"<form action=\"" + action(request, tl.response) + "\" method=\"POST\" class=\"contentEditorPosition\">" +
				"<input type=\"hidden\" name=\"" + REFERER        + "\" value=\"" + referer(request)         + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_FEATURE   + "\" value=\"" + feature.getID()          + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_ITEM_FROM + "\" value=\"" + previousItem.getCopeID() + "\">" +
				"<input type=\"hidden\" name=\"" + BAR_ITEM      + "\" value=\"" + item.getCopeID()         + "\">" +
				(
					buttonURL!=null
					? ("<input type=\"image\" src=\"" + buttonURL + "\" alt=\"Swap with previous item\">")
					: ("<input type=\"submit\" value=\"Up\">")
				) +
			"</form>";
	}
	
	private static final void checkEdit(final Feature feature, final Item item)
	{
		if(feature==null)
			throw new NullPointerException("feature must not be null");
		if(item==null)
			throw new NullPointerException("item must not be null");
		if(!feature.getType().isAssignableFrom(item.getCopeType()))
			throw new IllegalArgumentException("item " + item.getCopeID() + " does not belong to type of feature " + feature.getID());
	}
	
	public static final void writeBar(final PrintStream out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final StringBuilder bf = new StringBuilder();
		writeBar(bf);
		out.print(bf);
	}
	
	public static final void writeBar(final StringBuilder out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final HttpServletRequest request = tl.request;
		Bar_Jspm.write(out,
				action(request, tl.response),
				referer(request),
				tl.anchor.borders,
				tl.anchor.borders ? BORDERS_OFF : BORDERS_ON,
				tl.filter.getBorderButtonURL(request, tl.response, tl.anchor.borders),
				tl.filter.getCloseButtonURL(request, tl.response),
				tl.anchor.getPreviewNumber(),
				tl.anchor.sessionName);
	}
	
	private static final String action(final HttpServletRequest request, final HttpServletResponse response)
	{
		return response.encodeURL(request.getContextPath() + request.getServletPath() + LOGIN_URL_PATH_INFO);
	}
	
	private static final String referer(final HttpServletRequest request)
	{
		final String queryString = request.getQueryString();
		return queryString!=null ? (request.getPathInfo() + '?' + request.getQueryString()) : request.getPathInfo();
	}
	
	private static final void writeBody(
			final StringBuilder out,
			final HttpServletResponse response)
		throws IOException
	{
		ServletOutputStream outStream = null;
		try
		{
			outStream = response.getOutputStream();
			final byte[] outBytes = out.toString().getBytes(UTF8);
			outStream.write(outBytes);
		}
		finally
		{
			if(outStream!=null)
				outStream.close();
		}
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #isLoggedIn()} instead
	 */
	@Deprecated
	public static final boolean isActive()
	{
		return isLoggedIn();
	}
	
	/**
	 * @deprecated use {@link #edit(String, MapField, Item, Object)} instead.
	 */
	@Deprecated
	public static final <K> String editBlock(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		return edit(content, feature, item, key);
	}
	
	/**
	 * @deprecated Use {@link #getSession()} instead
	 */
	@Deprecated
	public static final Session getLogin()
	{
		return getSession();
	}
}
