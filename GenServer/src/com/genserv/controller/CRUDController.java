package com.genserv.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.genserv.dao.CrudDao;
import com.genserv.model.User;
import com.genserv.utils.DBUtility;

/*
<servlet>
    <servlet-name>CRUDController</servlet-name>
    <servlet-class>com.genserv.controller.CRUDController</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>CRUDController</servlet-name>
    <url-pattern>/CRUDController</url-pattern>
</servlet-mapping>
*/

public class CRUDController extends HttpServlet
{
 private static final long serialVersionUID = 1L;

/**
	 * Valid command IDs
	 */
	static final int CMD_UNKNOWN = -1;
	static final int CMD_NONE = 0;
	static final int CMD_HELLO = 1;
	static final int CMD_PARAM = 2;
	static final int CMD_LIST = 3;
	static final int CMD_CREATE = 4;
	static final int CMD_UPDATE = 5;
	static final int CMD_DELETE = 6;

	/**
     * List of valid commands
     */
	String strCommands[] =
	{
		"hello",
		"param",
		"list",
		"create",
		"update",
		"delete"
	};

 	private CrudDao dao;
	protected boolean m_bInitDone = false;

    public CRUDController()
    {
    }

	public void initServlet()
	{
		m_bInitDone = true;

		String strPropsFolder = getServletConfig().getInitParameter("props_folder");
		String strPropsFile = "gen.props";

		if (strPropsFolder != null && strPropsFolder.length() > 0)
			strPropsFile = strPropsFolder + "/" + "gen.props";

		AppManager.init(strPropsFile);

        dao = new CrudDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		if (!m_bInitDone)
			initServlet();

		boolean bRet = processRequest(request, response);

		if (!bRet)
			super.doGet(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{
		if (!m_bInitDone)
			initServlet();

		boolean bRet = processRequest(request, response);

		if (!bRet)
			super.doPost(request, response);
	}

	public void processCmd(String strCmd, HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		int nCmdID = GetCommandID(strCmd);

		switch (nCmdID)
		{
			case CMD_HELLO:
				doHello(req, res);
				break;
			case CMD_PARAM:
				doParamListing(req, res);
				break;
			case CMD_LIST:
				doDBList(req, res);
				break;
			case CMD_CREATE:
				doDBCreate(req, res);
				break;
			case CMD_UPDATE:
				doDBUpdate(req, res);
				break;
			case CMD_DELETE:
				doDBDelete(req, res);
				break;
			case CMD_UNKNOWN:
			default:
				sendErrResponse("Invalid Command or Operation.", res);
				break;
		}
	}

 	protected boolean processRequest(HttpServletRequest request, HttpServletResponse response)
 		throws ServletException, IOException
 	{
		String strCmd = request.getParameter("action");

		if (strCmd != null && strCmd.length() > 0)
		{
			processCmd(strCmd, request, response);
			return true;
		}

		return false;
	}

	protected void doHello(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// Set response content type
		response.setContentType("text/html");

		String message = "Hello World !! I am CRUD Controller.";

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>");
	}

	protected void doParamListing(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		printParameters(request, response);
	}

	protected void doDBList(HttpServletRequest request, HttpServletResponse response)
 	throws ServletException, IOException
	{
		List<User> lstUser=new ArrayList<User>();
		Gson gson = new Gson();
		response.setContentType("application/json");

		try
		{
			//Fetch Data from User Table
			lstUser = dao.getAllUsers();

			//Convert Java Object to Json
			JsonElement element = gson.toJsonTree(lstUser, new TypeToken<List<User>>() {}.getType());
			JsonArray jsonArray = element.getAsJsonArray();
			String listData=jsonArray.toString();

			//Return Json in the format required by jTable plugin
			listData="{\"Result\":\"OK\",\"Records\":"+listData+"}";
			response.getWriter().print(listData);
		}
		catch(Exception ex)
		{
			String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
			response.getWriter().print(error);
			ex.printStackTrace();
		}
	}

	protected void doDBCreate(HttpServletRequest request, HttpServletResponse response)
 	throws ServletException, IOException
	{
		Gson gson = new Gson();
		response.setContentType("application/json");

		try
		{
			String strParam;
			int userid;
			String firstname, lastname, email;

			User user = new User();

			strParam = request.getParameter("userid");
			if(strParam != null)
			{
				userid = Integer.parseInt(strParam);
				user.setUserid(userid);
			}

			firstname = request.getParameter("firstName");
			if(firstname != null)
				user.setFirstName(firstname);

			lastname = request.getParameter("lastName");
			if(lastname != null)
				user.setLastName(lastname);

			email = request.getParameter("email");
			if(email != null)
				user.setEmail(email);

			dao.addUser(user);

			//Convert Java Object to Json
			String json=gson.toJson(user);

			//Return Json in the format required by jTable plugin
			String listData="{\"Result\":\"OK\",\"Record\":"+json+"}";
			response.getWriter().print(listData);
		}
		catch(Exception ex)
		{
			String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
			response.getWriter().print(error);
			ex.printStackTrace();
		}
	}

	protected void doDBUpdate(HttpServletRequest request, HttpServletResponse response)
 	throws ServletException, IOException
	{
		Gson gson = new Gson();
		response.setContentType("application/json");

		try
		{
			String strParam;
			int userid;
			String firstname, lastname, email;

			User user = new User();

			strParam = request.getParameter("userid");
			if(strParam != null)
			{
				userid = Integer.parseInt(strParam);
				user.setUserid(userid);
			}

			firstname = request.getParameter("firstName");
			if(firstname != null)
				user.setFirstName(firstname);

			lastname = request.getParameter("lastName");
			if(lastname != null)
				user.setLastName(lastname);

			email = request.getParameter("email");
			if(email != null)
				user.setEmail(email);

			dao.updateUser(user);

			String listData="{\"Result\":\"OK\"}";
			response.getWriter().print(listData);
		}
		catch(Exception ex)
		{
			String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
			response.getWriter().print(error);
			ex.printStackTrace();
		}
	}

	protected void doDBDelete(HttpServletRequest request, HttpServletResponse response)
 	throws ServletException, IOException
	{
		Gson gson = new Gson();
		response.setContentType("application/json");

		try
		{
			String strUserId = request.getParameter("userid");

			if(strUserId != null)
			{
				String userid = (String)request.getParameter("userid");

				dao.deleteUser(Integer.parseInt(userid));

				String listData = "{\"Result\":\"OK\"}";
				response.getWriter().print(listData);
			}
		}
		catch(Exception ex)
		{
			String error="{\"Result\":\"ERROR\",\"Message\":"+ex.getMessage()+"}";
			response.getWriter().print(error);
			ex.printStackTrace();
		}
	}

	/**
     * Returns the command ID given the string
     */
	int GetCommandID(String strCmd)
	{
		if (strCmd == null)
			return CMD_NONE;

		for (int i = 0; i < strCommands.length; i++)
		{
			if (strCommands[i].equals(strCmd))
				return i+1;
		}

		return CMD_UNKNOWN;
	}

	/**
	 * Returns the given error message as response
	 */
	void sendMsgResponse(String strMsg, HttpServletResponse res)
	throws IOException
	{
		sendMsgResponse(strMsg, res, false);
	}

	/**
	 * Returns the given error message as response
	 */
	void sendErrResponse(String strMsg, HttpServletResponse res)
	throws IOException
	{
		sendMsgResponse(strMsg, res, true);
	}

	/**
	 * Returns the given error message as response
	 */
	void sendMsgResponse(String strMsg, HttpServletResponse res, boolean bError)
	throws IOException
	{
		PrintWriter out = res.getWriter();

		out.println("<HTML>");

		out.print("<HEAD><TITLE>");
		if (bError)
			out.print("Error");
		else
			out.print("Message");
		out.println("</HEAD></TITLE>");

		out.println("<BODY>");
		out.println("<BIG>");

		if (bError)
			out.print("<br><br><br><font color=\"red\"> Error :");
		out.println("<p align=\"center\" valign=\"center\" >");
		out.println(strMsg);
		out.println("</p>");
		if (bError)
			out.println("</font>");

		out.println("</BIG><br>");

		out.println("</BODY></HTML>");
		out.flush();
	}

	/**
     * Sends the request parameter info <Name = Value> as response
     */
	void printParameters(HttpServletRequest req, HttpServletResponse res)
	throws IOException
	{
		Enumeration enumParams = req.getParameterNames();

		PrintWriter out = res.getWriter();

		out.println("<HTML>");

		out.println("<HEAD><TITLE>PWS</TITLE></HEAD>");
		out.println("<BODY>");
		out.println("<BIG>List of Parameters Received</BIG><br>");

		while (enumParams.hasMoreElements())
		{
			String name = (String)enumParams.nextElement();
			String value = req.getParameter(name);
			out.println(name + " = " + value + "<br>");
		}

		out.println("</BODY></HTML>");
		out.flush();
	}
}


