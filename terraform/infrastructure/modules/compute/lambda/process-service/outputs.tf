output "process_cards_lambda_function_arn" {
  description = "The ARN of the Lambda function."
  value       = aws_lambda_function.process_cards_lambda.arn
}

output "process_cards_lambda_function_name" {
  description = "The name of the Lambda function."
  value       = aws_lambda_function.process_cards_lambda.function_name
}

output "process_cards_lambda_security_group_id" {
  description = "The Security Group ID for the process cards Lambda"
  value       = aws_security_group.process_cards_lambda_sg.id
}