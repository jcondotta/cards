#!/bin/sh

awslocal sqs create-queue --queue-name card-application

awslocal dynamodb create-table \
   --table-name cards \
   --attribute-definitions \
           AttributeName=cardId,AttributeType=S \
           AttributeName=bankAccountId,AttributeType=S \
   --key-schema AttributeName=cardId,KeyType=HASH \
   --billing-mode=PROVISIONED \
   --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
   --global-secondary-indexes \
           IndexName=bankAccountId-gsi,KeySchema=["{AttributeName=bankAccountId,KeyType=HASH}"],Projection="{ProjectionType=ALL}",ProvisionedThroughput="{ReadCapacityUnits=3,WriteCapacityUnits=3}"

awslocal dynamodb batch-write-item \
    --request-items \
        '{"cards":
          [
            {"PutRequest":
              {"Item":
                {
                  "cardId": {"S": "1"},
                  "bankAccountId": {"S": "998372"},
                  "cardholderName": {"S": "Jefferson Condotta"},
                  "cardNumber": {"S": "414192c2-cbd1-499b-8fd4-ab08b662ec57"},
                  "cardStatus": {"S": "LOCKED"},
                  "dailyPaymentLimit": {"N": "2500"},
                  "dailyWithdrawalLimit": {"N": "1000"},
                  "expirationDate": {"S": "2029-08-20T09:27:33.448644918"},
                  "createdAt": {"S": "2024-08-20T09:27:33.448631954"}
                }
              }
            },
            {"PutRequest":
              {"Item":
                {
                  "cardId": {"S": "2"},
                  "bankAccountId": {"S": "138271"},
                  "cardholderName": {"S": "Jefferson William"},
                  "cardNumber": {"S": "50a4ddb3-4f29-47c8-906b-c7bbc2e9d845"},
                  "cardStatus": {"S": "LOCKED"},
                  "dailyPaymentLimit": {"N": "1000"},
                  "dailyWithdrawalLimit": {"N": "500"},
                  "expirationDate": {"S": "2028-01-11T11:11:33.448644918"},
                  "createdAt": {"S": "2023-01-11T11:11:33.448631954"}
                }
              }
            }
          ]
        }'

awslocal iam create-role --role-name add-cards-lambda-exec --assume-role-policy-document '{
    "Version": "2012-10-17",
    "Statement": [
            {
                "Effect": "Allow",
                "Action": "dynamodb:PutItem",
                "Resource": "arn:aws:dynamodb:us-east-1:000000000000:table/cards"
            }
        ]
}'

awslocal lambda create-function \
    --function-name add-cards-java17 \
    --runtime java17 \
    --zip-file fileb:///tmp/add-cards-0.1.jar \
    --handler com.blitzar.cards.application.web.events.CardApplicationEventHandler \
    --role arn:aws:iam::000000000000:role/add-cards-lambda-exec \
    --architectures arm64 \
    --memory-size 1024 \
    --timeout 2700 \
    --environment Variables="{AWS_DYNAMODB_ENDPOINT=http://localstack:4566,AWS_SQS_ENDPOINT=http://localstack:4566}"

awslocal lambda create-event-source-mapping \
    --function-name add-cards-java17 \
    --batch-size 5 \
    --maximum-retry-attempts 2 \
    --maximum-batching-window-in-seconds 1 \
    --event-source-arn arn:aws:sqs:us-east-1:000000000000:card-application