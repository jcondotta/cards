#!/bin/sh

CARD_TABLE_NAME='cards'
BANK_ACCOUNT_ID_GSI='bankAccountId-gsi'

awslocal dynamodb create-table \
   --table-name "${CARD_TABLE_NAME}" \
   --attribute-definitions \
          AttributeName=cardId,AttributeType=S \
          AttributeName=bankAccountId,AttributeType=S \
   --key-schema AttributeName=cardId,KeyType=HASH \
   --billing-mode=PROVISIONED \
   --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
   --global-secondary-indexes \
          IndexName="${BANK_ACCOUNT_ID_GSI}",KeySchema=["{AttributeName=bankAccountId,KeyType=HASH}"],Projection="{ProjectionType=ALL}",ProvisionedThroughput="{ReadCapacityUnits=3,WriteCapacityUnits=3}"
