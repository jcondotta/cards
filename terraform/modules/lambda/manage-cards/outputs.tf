output "lambda_function_arn" {
  description = "The ARN of the Lambda function."
  value       = aws_lambda_function.management_cards_lambda.arn
}

output "lambda_function_name" {
  description = "The name of the Lambda function."
  value       = aws_lambda_function.management_cards_lambda.function_name
}

output "lambda_invoke_uri" {
  description = "The ARN for invoking the Lambda function via API Gateway"
  value       = "arn:aws:apigateway:${var.aws_region}:lambda:path/2015-03-31/functions/${aws_lambda_function.management_cards_lambda.arn}/invocations"
}