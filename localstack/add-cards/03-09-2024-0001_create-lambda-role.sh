#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-cards-table-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-add-cards-settings.sh

CARD_TABLE_ARN=$(awslocal dynamodb describe-table \
                --table-name "${CARD_TABLE_NAME}" \
                --query 'Table.TableArn' \
                --output text)

awslocal iam create-role \
    --role-name ""${ADD_CARDS_LAMBDA_ROLE_NAME} \
    --assume-role-policy-document '{
      "Version": "2012-10-17",
      "Statement": [
              {
                  "Effect": "Allow",
                  "Action": "dynamodb:PutItem",
                  "Resource": "'${CARD_TABLE_ARN}'"
              }
          ]
      }'