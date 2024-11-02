#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-management-cards-settings.sh

awslocal apigateway create-rest-api \
  --name "${API_GATEWAY_NAME}" \
  --endpoint-configuration types=REGIONAL \
  --description "Management cards' API Gateway"