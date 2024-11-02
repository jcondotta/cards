#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-cards-table-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-management-cards-settings.sh
. /etc/localstack/init/ready.d/environment-variables/sqs-card-application-queue-settings.sh

CARD_TABLE_ARN=$(awslocal dynamodb describe-table \
                --table-name "${CARD_TABLE_NAME}" \
                --query 'Table.TableArn' \
                --output text)

CARD_APPLICATION_QUEUE_URL=$(awslocal sqs get-queue-url \
                            --queue-name "${CARD_APPLICATION_QUEUE_NAME}" \
                            --output text)

CARD_APPLICATION_QUEUE_ARN=$(awslocal sqs get-queue-attributes \
                            --queue-url "${CARD_APPLICATION_QUEUE_URL}" \
                            --attribute-names QueueArn \
                            --query 'Attributes.QueueArn' \
                            --output text)

awslocal iam create-role \
    --role-name "${MANAGEMENT_CARDS_LAMBDA_ROLE_NAME}" \
    --assume-role-policy-document '{
      "Version": "2012-10-17",
      "Statement": [
              {
                  "Effect": "Allow",
                  "Action": [
                      "dynamodb:PutItem",
                      "dynamodb:GetItem"
                  ],
                  "Resource": "'${CARD_TABLE_ARN}'"
              },
              {
                  "Effect": "Allow",
                  "Action": [
                    "sqs:SendMessage",
                    "sqs:GetQueueUrl"
                  ],
                  "Resource": "'${CARD_APPLICATION_QUEUE_ARN}'"
                }
          ]
      }'