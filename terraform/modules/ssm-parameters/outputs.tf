# Output for DynamoDB Table Name SSM Parameter
output "dynamodb_cards_table_ssm_param" {
  value       = aws_ssm_parameter.dynamodb_cards_table_name.name
  description = "The name of the SSM parameter for the DynamoDB cards table"
}

output "dynamodb_cards_table_ssm_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_table_name.value
  description = "The value of the DynamoDB cards table name stored in SSM"
  sensitive   = true # Mark as sensitive if using SecureString
}

# Output for DynamoDB GSI Name SSM Parameter
output "dynamodb_cards_gsi_ssm_param" {
  value       = aws_ssm_parameter.dynamodb_cards_gsi_name.name
  description = "The name of the SSM parameter for the DynamoDB cards GSI"
}

output "dynamodb_cards_gsi_ssm_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_gsi_name.value
  description = "The value of the DynamoDB cards GSI name stored in SSM"
}

# Output for SQS Queue Name SSM Parameter
output "sqs_cards_queue_ssm_param" {
  value       = aws_ssm_parameter.sqs_cards_queue_name.name
  description = "The name of the SSM parameter for the SQS cards queue"
}

output "sqs_cards_queue_ssm_param_value" {
  value       = aws_ssm_parameter.sqs_cards_queue_name.value
  description = "The value of the SQS cards queue name stored in SSM"
}

# Output for SQS DLQ Name SSM Parameter
output "sqs_cards_dlq_ssm_param" {
  value       = aws_ssm_parameter.sqs_cards_dlq_name.name
  description = "The name of the SSM parameter for the SQS cards DLQ"
}

output "sqs_cards_dlq_ssm_param_value" {
  value       = aws_ssm_parameter.sqs_cards_dlq_name.value
  description = "The value of the SQS cards DLQ name stored in SSM"
}
