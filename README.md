# echo_proxy
A server that forwards requests from Amazon's Alexa API to a configurable location

## About

I created this project because I expect some platforms won't have stable SDKs for a while. I wanted start playing with the API without dealing with too much setup, so I based this on [Amazon's example](https://developer.amazon.com/appsandservices/solutions/alexa/alexa-skills-kit).

## Running

You should be able to start the server with the provided `run.sh` script:

```bash
./run.sh 'http://url-to-be-called.com/path'
```

This is just a wrapper around some `mvn` calls. Since Amazon's library isn't available in a Maven repository, this installs it to the local repository before executing anything.

## Integrating

The server sends a `POST` request to the provided URL with the raw JSON of Amazon's request in the request body. Right now, it only does much of anything for the `onIntent` call. 

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
