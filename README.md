# JRestless
[![Build Status](https://travis-ci.org/bbilger/jrestless.svg?branch=master)](https://travis-ci.org/bbilger/jrestless)
[![codecov](https://codecov.io/gh/bbilger/jrestless/branch/master/graph/badge.svg)](https://codecov.io/gh/bbilger/jrestless)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/bbilger/jrestless/blob/master/LICENSE)

JRestless allows you to create serverless applications using JAX-RS.

It provides a generic jersey container that handles requests.

On top of it there's an integration layer for each serverless service. At the moment only AWS Lambda is supported since neither Azure functions nor Google Cloud functions support Java.

In addition the whole API Gateway configuration gets generated at compile time from your JAX-RS resources.