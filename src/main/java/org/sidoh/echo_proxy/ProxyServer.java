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

    // Configure server and its associated servlets
    Server server = new Server();

    ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setPort(8888);
    server.setConnectors(new Connector[] { serverConnector });

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(createServlet(new ProxySpeechlet())), "/proxy");
    server.start();
    server.join();

    org.eclipse.jetty.io.ByteBufferPool a;
  }

  private static SpeechletServlet createServlet(final Speechlet speechlet) {
    SpeechletServlet servlet = new SpeechletServlet();
    servlet.setSpeechlet(speechlet);
    return servlet;
  }
}
