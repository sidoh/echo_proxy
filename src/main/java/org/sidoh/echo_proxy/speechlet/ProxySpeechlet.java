package org.sidoh.echo_proxy.speechlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySpeechlet implements Speechlet {
  private static final Logger LOG = LoggerFactory.getLogger(ProxySpeechlet.class);

  private final String proxyUrl;
  private final HttpClient httpClient;
  private final Gson gson;

  public static class ProxyRequest {
    private final SpeechletRequest request;
    private final Session session;

    public ProxyRequest(SpeechletRequest request, Session session) {
      this.request = request;
      this.session = session;
    }
  }

  public static class ProxyResponse {
    private String speech;
    private boolean shouldEndSession;

    public String getSpeech() {
      return speech;
    }

    public void setSpeech(String speech) {
      this.speech = speech;
    }

    public boolean isShouldEndSession() {
      return shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
      this.shouldEndSession = shouldEndSession;
    }

    public ProxyResponse withShouldEndSession(boolean shouldEndSession) {
      this.shouldEndSession = shouldEndSession;
      return this;
    }

    public ProxyResponse withSpeech(String speech) {
      this.speech = speech;
      return this;
    }
  }

  public ProxySpeechlet(String proxyUrl) {
    this.proxyUrl = proxyUrl;
    this.httpClient = HttpClientBuilder.create().build();
    this.gson = new Gson();
  }

  @Override
  public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
    handleRequest(sessionStartedRequest, session);
  }

  @Override
  public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
    return handleRequest(launchRequest, session);
  }

  @Override
  public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
    return handleRequest(intentRequest, session);
  }

  @Override
  public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
    handleRequest(sessionEndedRequest, session);
  }

  private SpeechletResponse handleRequest(SpeechletRequest request, Session session) {
    try {
      final ProxyResponse proxyResponse = postRequest(request, session);
      final SpeechletResponse response = buildResponse(proxyResponse.getSpeech());
      response.setShouldEndSession(proxyResponse.isShouldEndSession());

      return response;
    } catch (IOException e) {
      LOG.error("Error processing request", e);
      return buildResponse("Error communicating with remote proxy server");
    }
  }

  private ProxyResponse postRequest(SpeechletRequest request, Session session) throws IOException {
    final String requestJson = gson.toJson(new ProxyRequest(request, session));

    final HttpPost proxyRequest = new HttpPost(proxyUrl);
    proxyRequest.setEntity(new StringEntity(requestJson));

    final HttpResponse proxyResponse = httpClient.execute(proxyRequest);
    final int statusCode = proxyResponse.getStatusLine().getStatusCode();

    if (statusCode < 200 || statusCode > 299) {
      return new ProxyResponse()
          .withSpeech("Error handling request. Response code was " + statusCode);
    }

    final HttpEntity entity = proxyResponse.getEntity();
    final String json = EntityUtils.toString(entity, "UTF-8");

    return gson.fromJson(json, ProxyResponse.class);
  }

  private static SpeechletResponse buildResponse(String text) {
    final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
    output.setText(text);

    final SpeechletResponse response = new SpeechletResponse();
    response.setOutputSpeech(output);

    return response;
  }
}
