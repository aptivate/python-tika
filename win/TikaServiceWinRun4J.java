/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.boris.winrun4j.Service;
import org.boris.winrun4j.ServiceException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class TikaServiceWinRun4J implements Service
{
	private static final Log logger = LogFactory.getLog(TikaServiceWinRun4J.class);
	public static final int DEFAULT_PORT = 9998;
	private Server server;

	private static Options getOptions() {
		Options options = new Options();
		options.addOption("p", "port", true, "listen port (default = "+DEFAULT_PORT+ ')');
		options.addOption("h", "help", false, "this help message");

		return options;
	}

	public int serviceMain(String[] args) {
		Properties properties = new Properties();
		try {
			properties.load(ClassLoader.getSystemResourceAsStream("tikaserver-version.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		logger.info("Starting Tika server "+properties.getProperty("tikaserver.version"));

		try {
			Options options = getOptions();

			CommandLineParser cliParser = new GnuParser();
			CommandLine line = cliParser.parse(options, args);

			int port = DEFAULT_PORT;

			if (line.hasOption("port")) {
				port = Integer.valueOf(line.getOptionValue("port"));
			}
			if (line.hasOption("help")) {
				HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp("tikaserver", options);
				System.exit(-1);
			}

			server = new Server(port);
			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);

			context.addServlet(new ServletHolder(new ServletContainer(new PackagesResourceConfig("org.apache.tika.server"))), "/*");

			server.start();
			logger.info("Started");
			server.join();
			
			return 0;
		} catch (Exception ex) {
			logger.fatal("Can't start", ex);
			return 1;
		}
	}
	
    public int serviceRequest(int control) throws ServiceException {
        switch (control) {
        case SERVICE_CONTROL_STOP:
        case SERVICE_CONTROL_SHUTDOWN:
        	try
        	{
        		server.stop();
        	}
        	catch (Exception e)
        	{
        		throw new ServiceException(e);
        	}
            break;
        default:
            break;
        }
        return 0;
    }

    public static void main(String [] args)
    {
    	System.err.println("This class should only be run as a Windows " +
    			"service with WinRun4J");
	System.exit(new TikaServiceWinRun4J().serviceMain(args));
    }
}
