# Lambda permission to allow API Gateway to invoke the function
resource "aws_lambda_permission" "allow_apigateway_invoke_management_cards_lambda" {
  statement_id  = "AllowExecutionFromAPIGateway-${var.management_cards_lambda_function_name}"
  action        = "lambda:InvokeFunction"
  function_name = var.management_cards_lambda_function_name
  principal     = "apigateway.amazonaws.com"

  # Allow all API Gateway methods and stages to invoke the Lambda function
  source_arn = "${aws_api_gateway_rest_api.cards_api.execution_arn}/*"
}
