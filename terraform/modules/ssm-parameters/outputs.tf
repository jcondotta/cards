output "dynamodb_cards_table_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_table_ssm_param.value
  description = "The value of the DynamoDB cards table name stored in SSM"
}

output "dynamodb_cards_by_bank_account_id_gsi_param_value" {
  value       = aws_ssm_parameter.dynamodb_cards_by_bank_account_id_gsi_ssm_param.value
  description = "The value of the DynamoDB GSI for cards by bank account ID stored in SSM"
}

output "sqs_card_application_queue_param_value" {
  value       = aws_ssm_parameter.sqs_card_application_queue_ssm_param.value
  description = "The value of the SQS card application queue name stored in SSM"
}

output "sqs_card_application_dlq_param_value" {
  value       = aws_ssm_parameter.sqs_card_application_dlq_ssm_param.value
  description = "The value of the SQS card application DLQ name stored in SSM"
}
