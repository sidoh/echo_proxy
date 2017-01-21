# echo_proxy
A server that forwards requests from Amazon's Alexa API to a configurable location

You might also consider checking out [alexa_dispatcher](https://github.com/sidoh/alexa_dispatcher), which allows you to forward requests for different Alexa skills to different endpoints.

## About

`echo_proxy` is a simple servlet that receives requests from Amazon's Alexa API and forwards them to a configurable location. There are two reasons you might want this:

1. **The language you're writing your skill in doesn't yet have support.**
2. **Secure gateway for home networks.** If you're hosting your app on a home network, you'll need to make a home server accessible to the web. You probably don't want to expose any part of your app other than what's necessary for Amazon to call your service. `echo_proxy` provides that:
  1. All requests it forwards are from Amazon. This is ensured by verifying cryptographic signatures included with each request.
  2. `echo_proxy` is the only thing you'll need to expose to the outside world for your app to function. The guts of your app can remain internal to your network.

## Running

#### Check out this project:

```
$ git clone git@github.com:sidoh/echo_proxy.git ./echo_proxy
```

#### Edit the configuration:

Edit `./echo_proxy/config/config.yml` to suit your needs. In particular, make sure that you update the `endpoint` property to be the desired forward address.

#### Run it!

You should be able to start the server with the provided `run.sh` script:

```
$ ./echo_proxy/bin/run.sh
```

This is just a wrapper around a `mvn` command.

#### Run it! (As a daemon)

You can also use `./echo_proxy/bin/start` to run `echo_proxy` as a daemon. Output will be redirected to `./echo_proxy/log/echo_proxy.out`. You can use `./echo_proxy/bin/stop` to stop the daemon.

## Integrating

The server sends a `POST` request to the provided URL. The request information sent by Amazon is stuffed into a single object, and put in the body of the `POST` request. Here's a sample request:

```json
{
  "requestType": "IntentRequest",
  "request": {
    "intent": {
      "name": "NextBus",
      "slots": {
        "Route": {
          "name": "Route",
          "value": "thirty eight"
        }
      }
    },
    "requestId": "amzn1.echo-api.request.157f3045-9c73-41af-982d-2a25d1b7208c",
    "timestamp": "Aug 4, 2015 10:53:12 PM"
  },
  "session": {
    "isNew": true,
    "sessionId": "<session id>",
    "application": {
      "applicationId": "<app id>"
    },
    "attributes": {
    },
    "user": {
      "userId": "<user id>"
    }
  }
}
```

## SSL

This servlet doesn't handle SSL. Setting up SSL with jetty is really annoying, and I much prefer to do it with nginx. My nginx config looks like this:

```
server {
  listen 443 ssl;

  ssl_certificate /etc/nginx/ssl/echo-proxy.sidoh.org/nginx.crt;
  ssl_certificate_key /etc/nginx/ssl/echo-proxy.sidoh.org/nginx.key;

  location /{
    proxy_pass  http://127.0.0.1:8888;
  }
}
```
