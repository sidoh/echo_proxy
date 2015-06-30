package org.sidoh.echo_proxy;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.sidoh.echo_proxy.speechlet.ProxySpeechlet;

public class ProxyServer {
  public static void main(String[] args) throws Exception {
    BasicConfigurator.configure();

    final String proxyUrl = args[0];
    final int port;

    if (args.length == 1) {
      port = 8888;
    } else {
      port = Integer.parseInt(args[1]);
    }

    // Configure server and its associated servlets
    Server server = new Server();

    ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setPort(port);
    server.setConnectors(new Connector[] { serverConnector });

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(createServlet(new ProxySpeechlet(proxyUrl))), "/proxy");
    server.start();
    server.join();
  }

  private static SpeechletServlet createServlet(final Speechlet speechlet) {
    SpeechletServlet servlet = new SpeechletServlet();
    servlet.setSpeechlet(speechlet);
    return servlet;
  }
}
