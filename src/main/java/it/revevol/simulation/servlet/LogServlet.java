package it.revevol.simulation.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.log.*;

import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Servlet responsible for query the GAE log DB using the Logs API and display it on the webpage.
 * 
 * It is also possible to access the logs in the Logs Viewer (Google Developers Console) by 
 * clicking Monitoring > Logs in the left navigation panel
 * 
 * @author Angelo
 *
 */
public class LogServlet extends HttpServlet {
       
	private static final long serialVersionUID = -7556257558279896893L;

	  @Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp)
	         throws ServletException,IOException {

	    resp.setContentType("text/html");
	    PrintWriter writer = resp.getWriter();

	    // Retrieves the offset from the Next link upon user click.
	    String offset = req.getParameter("offset");

	    // The App logs for each request log
	    LogQuery query = LogQuery.Builder.withDefaults();
	    query.includeAppLogs(true);

	    // Set the offset value retrieved from the Next link click.
	    if (offset != null) {
	      query.offset(offset);
	    }

	    // Display a few properties of each request log.
	    for (RequestLogs record : LogServiceFactory.getLogService().fetch(query)) {
	      writer.println("<br /><strong>Request Log</strong><br />");
	      
	      Calendar cal = Calendar.getInstance();
	      cal.setTimeInMillis(record.getStartTimeUsec() / 1000);

	      writer.println(String.format("Date: %s", cal.getTime().toString()));
	      writer.println("IP: " + record.getIp()+"<br />");
	      writer.println("Method: " + record.getMethod()+"<br />");
	      writer.println("Resource " + record.getResource()+"<br />");

	      // Display all the app logs for each request log.
	      for (AppLogLine appLog : record.getAppLogLines()) {
	        writer.println("<br /><strong>Application Log</strong><br />");
	        
	        Calendar appCal = Calendar.getInstance();
	        appCal.setTimeInMillis(appLog.getTimeUsec() / 1000);
	        
	        writer.println(String.format("Date: %s", appCal.getTime().toString()));
	        writer.println("<br />Level: "+appLog.getLogLevel()+"<br />");
	        writer.println("Message: "+ appLog.getLogMessage()+"<br /> <br />");
	      }
	      
	    }
	  }
}
