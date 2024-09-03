#!/bin/sh

ADD_CARDS_LAMBDA_ROLE_NAME='add-cards-lambda-exec'
ADD_CARDS_LAMBDA_ROLE_ARN=$(awslocal iam get-role \
                           --role-name "${ADD_CARDS_LAMBDA_ROLE_NAME}" \
                           --query 'Role.Arn' \
                           --output text)

ADD_CARDS_LAMBDA_NAME='add-cards-java17'
LOCALSTACK_URL='http://localstack:4566'

awslocal lambda create-function \
    --function-name "${ADD_CARDS_LAMBDA_NAME}" \
    --runtime java17 \
    --zip-file fileb:///tmp/add-cards-0.1.jar \
    --handler com.blitzar.cards.application.web.events.CardApplicationEventHandler \
    --role "${ADD_CARDS_LAMBDA_ROLE_ARN}" \
    --architectures arm64 \
    --memory-size 1024 \
    --timeout 2700 \
    --environment Variables="{AWS_DYNAMODB_ENDPOINT='${LOCALSTACK_URL}',AWS_SQS_ENDPOINT='${LOCALSTACK_URL}'}"