#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/sqs-card-application-queue-settings.sh

CARD_APPLICATION_QUEUE_URL=$(awslocal sqs create-queue \
                            --queue-name "${CARD_APPLICATION_QUEUE_NAME}" \
                            --query 'QueueUrl' \
                            --output text)

#{"Policy" : "{\"Id\": \"Policy1564523767951\",\"Version\": \"2012-10-17\",\"Statement\": [{\"Sid\": \"Stmt1564523766749\",\"Action\": \"sqs:*\",\"Effect\": \"Allow\",\"Resource\": \"arn:aws:sqs:us-east-1:000000000000:card-application\"},\"Principal\": \"*\"}]}"}

#echo $CARD_APPLICATION_QUEUE_URL

#awslocal sqs add-permission \
#  --queue-url $CARD_APPLICATION_QUEUE_URL \
#  --label SendMessagesFromMyQueue \
#  --aws-account-ids 000000000000 \
#  --actions SendMessage
#
#awslocal sqs add-permission \
#  --queue-url $CARD_APPLICATION_QUEUE_URL \
#  --label SendMessagesFromMyQueue2 \
#  --aws-account-ids 000000000000 \
#  --actions GetQueueUrl

#awslocal iam attach-role-policy --role-name management-cards-lambda-exec --policy-arn arn:aws:iam::aws:policy/service-role/AmazonSQSFullAccess

#
#aws sqs set-queue-attributes \
#  --queue-url http://sqs.us-east-1.localhost.localstack.cloud:4566/000000000000/card-application \
#  --attributes \
#  '{"Policy": "Statement": [{
#                "Id": "Policy1725490877430",
#                "Version": "2012-10-17",
#                "Sid": "Stmt1725490874924",
#                "Action": [
#                    "sqs:*"
#                ],
#                "Effect": "Allow",
#                "Resource": "arn:aws:sqs:us-east-1:000000000000:card-application",
#                "Principal": {
#                    "AWS": [
#                        "arn:aws:iam::000000000000:root"
#                    ]
#                }
#              }]
#            }'