output "lambda_function_arn" {
  description = "The ARN of the Lambda function."
  value       = aws_lambda_function.process_cards_lambda.arn
}

output "lambda_function_name" {
  description = "The name of the Lambda function."
  value       = aws_lambda_function.process_cards_lambda.function_name
}