#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/dynamodb-cards-table-settings.sh

awslocal dynamodb batch-write-item \
    --request-items \
        '{"'${CARD_TABLE_NAME}'":
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
                  "cardStatus": {"S": "ACTIVE"},
                  "dailyPaymentLimit": {"N": "1000"},
                  "dailyWithdrawalLimit": {"N": "500"},
                  "expirationDate": {"S": "2028-01-11T11:11:33.448644918"},
                  "createdAt": {"S": "2023-01-11T11:11:33.448631954"}
                }
              }
            }
          ]
        }'