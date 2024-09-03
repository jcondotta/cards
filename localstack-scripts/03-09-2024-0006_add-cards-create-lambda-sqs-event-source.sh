#!/bin/sh

CARD_APPLICATION_QUEUE_NAME='card-application'

CARD_APPLICATION_QUEUE_URL=$(awslocal sqs get-queue-url \
                            --queue-name "${CARD_APPLICATION_QUEUE_NAME}" \
                            --output text)

CARD_APPLICATION_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
                            --queue-url "${CARD_APPLICATION_QUEUE_URL}" \
                            --attribute-names QueueArn \
                            --query 'Attributes.QueueArn' \
                            --output text)

ADD_CARDS_LAMBDA_NAME='add-cards-java17'

awslocal lambda create-event-source-mapping \
    --function-name add-cards-java17 \
    --batch-size 5 \
    --maximum-retry-attempts 2 \
    --maximum-batching-window-in-seconds 1 \
    --event-source-arn ${CARD_APPLICATION_QUEUE_ARN}