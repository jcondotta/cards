#!/bin/sh

CARD_APPLICATION_QUEUE_NAME='card-application'

awslocal sqs create-queue \
  --queue-name "${CARD_APPLICATION_QUEUE_NAME}"