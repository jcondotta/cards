#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-management-cards-settings.sh

API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
                --query "items[?name=='$API_GATEWAY_NAME'].[id]" \
                --output text)

API_GATEWAY_ROOT_RESOURCE_ID=$(awslocal apigateway get-resources \
                              --rest-api-id $API_GATEWAY_ID \
                              --query "items[?path=='/'].[id]" \
                              --output text)

API_RESOURCE_ID=$(awslocal apigateway create-resource \
                  --rest-api-id $API_GATEWAY_ID \
                  --parent-id $API_GATEWAY_ROOT_RESOURCE_ID \
                  --path-part $PART_PATH_API \
                  --query 'id' \
                  --output text)

V1_RESOURCE_ID=$(awslocal apigateway create-resource \
                --rest-api-id $API_GATEWAY_ID \
                --parent-id $API_RESOURCE_ID \
                --path-part $PART_PATH_V1 \
                --query 'id' \
                --output text)

CARDS_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $V1_RESOURCE_ID \
                    --path-part $PART_PATH_CARDS \
                    --query 'id' \
                    --output text)

CARD_ID_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $CARDS_RESOURCE_ID \
                    --path-part $PART_PATH_CARD_ID \
                    --query 'id' \
                    --output text)

CARD_ID_PLACE_HOLDER_RESOURCE_ID=$(awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $CARD_ID_RESOURCE_ID \
                    --path-part $PART_PATH_CARD_ID_PLACE_HOLDER \
                    --query 'id' \
                    --output text)

awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $CARD_ID_PLACE_HOLDER_RESOURCE_ID \
                    --path-part $PART_PATH_ACTIVATION

awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $CARD_ID_PLACE_HOLDER_RESOURCE_ID \
                    --path-part $PART_PATH_CANCELLATION

awslocal apigateway create-resource \
                    --rest-api-id $API_GATEWAY_ID \
                    --parent-id $CARD_ID_PLACE_HOLDER_RESOURCE_ID \
                    --path-part $PART_PATH_LOCK