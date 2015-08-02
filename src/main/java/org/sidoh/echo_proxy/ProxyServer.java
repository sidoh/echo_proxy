package org.sidoh.echo_proxy;

import java.io.FileInputStream;
import java.io.InputStream;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import com.google.common.base.Joiner;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.sidoh.echo_proxy.speechlet.ProxySpeechlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class ProxyServer {
  private static final Logger LOG = LoggerFactory.getLogger(ProxyServer.class);

  public static void main(String[] args) throws Exception {
    final InputStream configInputStream;
    if (args.length > 0) {
      configInputStream = new FileInputStream(args[0]);
    } else {
      configInputStream = ProxyServerConfig.class.getResourceAsStream("config.yml");
    }

    final Yaml yaml = new Yaml(new Constructor(ProxyServerConfig.class));
    final ProxyServerConfig config = (ProxyServerConfig)yaml.load(configInputStream);

    // Apply system settings used by SpeechletServlet
    if (config.verifyTimestamps) {
      System.setProperty("com.amazon.speech.speechlet.servlet.timestampTolerance", "150");
    }

    if (! config.verifySignatures) {
      System.setProperty("com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck", "true");
    }

    if (config.allowedApplications != null) {
      System.setProperty("com.amazon.speech.speechlet.servlet.supportedApplicationIds", Joiner.on(',').join(config.allowedApplications));
    }

    // Configure server and its associated servlets
    Server server = new Server();

    ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setPort(config.port);
    server.setConnectors(new Connector[]{serverConnector});

    ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    context.setContextPath("/");
    server.setHandler(context);

    context.addServlet(new ServletHolder(createServlet(new ProxySpeechlet(config))), "/proxy");

    LOG.info("Starting server");

    server.start();
    server.join();

    LOG.info("Shutting down");
  }

  private static SpeechletServlet createServlet(final Speechlet speechlet) {
    SpeechletServlet servlet = new SpeechletServlet();
    servlet.setSpeechlet(speechlet);
    return servlet;
  }
}
