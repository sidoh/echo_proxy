# echo_proxy
A server that forwards requests from Amazon's Alexa API to a configurable location

## Running

You should be able to start the server with the provided `run.sh` script:

```bash
./run.sh 'http://url-to-be-called.com/path'
```

This is just a wrapper around some `mvn` calls. Since Amazon's library isn't available in a Maven repository, this installs it to the local repository before executing anything.

## Integrating

The server sends a `POST` request to the provided URL with the raw JSON of Amazon's request in the request body. Right now, it only does much of anything for the `onIntent` call. 
