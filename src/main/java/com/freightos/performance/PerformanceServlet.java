package com.freightos.performance;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PerformanceServlet", value = "/perftest")
public class PerformanceServlet extends HttpServlet {
	private static final Logger logger = Logger.getLogger(PerformanceServlet.class.getName());
	private static final int DEFAULT_LOOPS_PER_HTTP_CALL = 10;
	private static final int DEFAULT_ENTITY_SIZE_IN_BYTES = 100_0000;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			resp.setContentType("text/plain");
			String loopsS = req.getParameter("loops");
			int loops = loopsS == null ? DEFAULT_LOOPS_PER_HTTP_CALL : Integer.parseInt(loopsS);
			String sizeS = req.getParameter("size");

			int size = sizeS == null ? DEFAULT_ENTITY_SIZE_IN_BYTES : Integer.parseInt(sizeS);
			String implementation = req.getParameter("impl");
			logger.info("Will run loops " + loops + " and " + size + " bytes-per-entity; using: " + implementation);

			String httpReply = Tester.runTest(implementation, loops, size);
			resp.setStatus(200);
			resp.getWriter().write(httpReply);
		} catch (Throwable th) {
			resp.setStatus(500);
			StringWriter sw = new StringWriter();
			th.printStackTrace(new PrintWriter(sw));
			String errorStr = sw.toString();
			logger.warning(errorStr);
			resp.getWriter().write("Error " + errorStr);

		}
	}

}
