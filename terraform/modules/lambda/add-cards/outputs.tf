output "lambda_function_arn" {
  description = "The ARN of the Lambda function."
  value       = aws_lambda_function.add_cards_lambda.arn
}

output "lambda_function_name" {
  description = "The name of the Lambda function."
  value       = aws_lambda_function.add_cards_lambda.function_name
}