#!/bin/sh

. /etc/localstack/init/ready.d/environment-variables/api-gateway-management-cards-settings.sh
. /etc/localstack/init/ready.d/environment-variables/lambda-management-cards-settings.sh

#API_GATEWAY_ID=$(awslocal apigateway get-rest-apis \
#                --query "items[?name=='$API_GATEWAY_NAME'].[id]" \
#                --output text)
#
#ACTIVATION_RESOURCE_ID=$(awslocal apigateway get-resources \
#                  --rest-api-id $API_GATEWAY_ID \
#                  --query "items[?path=='/api/v1/cards/card-id/{card-id}/activation'].[id]" \
#                  --output text)
#
#MANAGEMENT_CARDS_LAMBDA_ARN=$(awslocal lambda get-function \
#    --function-name "${MANAGEMENT_CARDS_LAMBDA_NAME}" \
#    --query 'Configuration.FunctionArn' \
#    --output text)
#
#awslocal apigateway put-method \
#    --rest-api-id $API_GATEWAY_ID \
#    --resource-id $ACTIVATION_RESOURCE_ID \
#    --http-method PATCH \
#    --authorization-type NONE
#
#awslocal apigateway put-method-response \
#    --rest-api-id $API_GATEWAY_ID \
#    --resource-id $ACTIVATION_RESOURCE_ID \
#    --http-method PATCH \
#    --status-code 200 \
#    --response-models '{"application/json": "Empty"}'
#
#awslocal apigateway put-integration \
#    --rest-api-id "${API_GATEWAY_ID}" \
#    --resource-id "${ACTIVATION_RESOURCE_ID}" \
#    --http-method PATCH \
#    --integration-http-method PATCH \
#    --type AWS_PROXY \
#    --uri "arn:aws:apigateway:us-east-1:lambda:path/2015-03-31/functions/$MANAGEMENT_CARDS_LAMBDA_ARN/invocations"
#
#awslocal apigateway put-integration-response \
#    --rest-api-id "${API_GATEWAY_ID}" \
#    --resource-id "${ACTIVATION_RESOURCE_ID}" \
#    --http-method PATCH \
#    --status-code 200 --selection-pattern ''
##
#awslocal apigateway create-deployment \
#    --rest-api-id "${API_GATEWAY_ID}" \
#    --stage-name dev
##
#awslocal lambda add-permission \
#    --function-name "${MANAGEMENT_CARDS_LAMBDA_NAME}" \
#    --source-arn "arn:aws:execute-api:us-east-1:000000000000:${API_GATEWAY_ID}/*/PATCH/api/v1/cards/card-id/{card-id}/activation" \
#    --principal apigateway.amazonaws.com \
#    --statement-id apigateway-test-3 \
#    --action lambda:InvokeFunction