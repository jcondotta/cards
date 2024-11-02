#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/lambda-management-cards-settings.sh

MANAGEMENT_CARDS_LAMBDA_ROLE_ARN=$(awslocal iam get-role \
                           --role-name "${MANAGEMENT_CARDS_LAMBDA_ROLE_NAME}" \
                           --query 'Role.Arn' \
                           --output text)

LOCALSTACK_URL='http://localstack:4566'

awslocal lambda create-function \
    --function-name "${MANAGEMENT_CARDS_LAMBDA_NAME}" \
    --runtime java17 \
    --zip-file fileb:///tmp/management-cards-0.1.jar \
    --handler io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction \
    --role "${MANAGEMENT_CARDS_LAMBDA_ROLE_ARN}" \
    --architectures arm64 \
    --memory-size 1024 \
    --timeout 2700 \
    --environment Variables="{AWS_DYNAMODB_ENDPOINT='${LOCALSTACK_URL}',AWS_SQS_ENDPOINT='${LOCALSTACK_URL}'}"