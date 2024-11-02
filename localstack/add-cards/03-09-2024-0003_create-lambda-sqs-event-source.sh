#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/sqs-card-application-queue-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-add-cards-settings.sh

CARD_APPLICATION_QUEUE_URL=$(awslocal sqs get-queue-url \
                            --queue-name "${CARD_APPLICATION_QUEUE_NAME}" \
                            --output text)

CARD_APPLICATION_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
                            --queue-url "${CARD_APPLICATION_QUEUE_URL}" \
                            --attribute-names QueueArn \
                            --query 'Attributes.QueueArn' \
                            --output text)

awslocal lambda create-event-source-mapping \
    --function-name "${ADD_CARDS_LAMBDA_NAME}" \
    --batch-size 5 \
    --maximum-retry-attempts 2 \
    --maximum-batching-window-in-seconds 1 \
    --event-source-arn "${CARD_APPLICATION_QUEUE_ARN}"