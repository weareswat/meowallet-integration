# MeoWallet Integration Service

[![Build Status](https://semaphoreci.com/api/v1/projects/287e777d-687f-4770-a040-a66a7104110f/812449/shields_badge.svg)](https://semaphoreci.com/rupeal/meowallet-integration)

### Running

You need to install [lein](http://leiningen.org/)

* `lein server` - starts the HTTP server

#### Tests

* `lein test` - runs the test suite
* `script/autotest` or `lein autotest` -listen for file changes and is always running tests
* To Run the integration test you should provide a valid Meo Wallet API KEY with access to mb references api.
  `MEO_WALLET_API_KEY=<YOUR_MEO_WALLET_API_KEY> lein test`


## Environments

### Staging

The staging environment is located [here](http://meo-wallet-integration-staging.herokuapp.com/) and you can read and try the api documentation [here](http://meo-wallet-integration-staging.herokuapp.com/api-docs/).

SANDBOX_MEO_WALLET_API_KEY= "ef5a07f7c74f2c2caaa20890943fb4bdc94e7c6d"
