package org.sidoh.echo_proxy.speechlet;

import java.io.IOException;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
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

public class ProxySpeechlet implements Speechlet {
  private final String proxyUrl;
  private final HttpClient httpClient;

  public ProxySpeechlet(String proxyUrl) {
    this.proxyUrl = proxyUrl;
    this.httpClient = HttpClientBuilder.create().build();
  }

  @Override
  public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {

  }

  @Override
  public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
    final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
    output.setText("proxy hello");

    final SpeechletResponse response = new SpeechletResponse();
    response.setOutputSpeech(output);

    return response;
  }

  @Override
  public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
    final Gson gson = new Gson();
    final String requestJson = gson.toJson(intentRequest);

    try {
      final HttpPost request = new HttpPost(proxyUrl);
      request.setEntity(new StringEntity(requestJson));

      final HttpResponse proxyResponse = httpClient.execute(request);
      final int statusCode = proxyResponse.getStatusLine().getStatusCode();

      if (statusCode < 200 || statusCode > 299) {
        return buildResponse("Error handling request. Response code was " + statusCode);
      }

      final HttpEntity entity = proxyResponse.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");

      return buildResponse(responseString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {

  }

  private static SpeechletResponse buildResponse(String text) {
    final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
    output.setText(text);

    final SpeechletResponse response = new SpeechletResponse();
    response.setOutputSpeech(output);

    return response;
  }
}
