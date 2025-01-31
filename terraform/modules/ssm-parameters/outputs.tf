# Output for DynamoDB Table Name SSM Parameter
output "dynamodb_cards_table_ssm_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_table_ssm_param.value
  description = "The name of the SSM parameter for the DynamoDB cards table"
}

output "dynamodb_cards_by_bank_account_id_gsi_ssm_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_by_bank_account_id_gsi_ssm_param.value
  description = "The value of the DynamoDB cards table name stored in SSM"
  sensitive   = true # Mark as sensitive if using SecureString
}

# Output for SQS Queue Name SSM Parameter
output "sqs_card_application_queue_queue_ssm_param_value" {
  value       = aws_ssm_parameter.sqs_card_application_queue_queue_ssm_param.value
  description = "The name of the SSM parameter for the SQS cards queue"
}

output "sqs_card_application_dlq_ssm_param_value" {
  value       = aws_ssm_parameter.sqs_card_application_dlq_ssm_param.value
  description = "The value of the SQS cards queue name stored in SSM"
}