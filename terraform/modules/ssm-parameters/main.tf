resource "aws_ssm_parameter" "dynamodb_cards_table_ssm_param" {
  name        = var.dynamodb_cards_table_param_name
  description = "The name of the DynamoDB cards table"
  type        = "String"
  value       = var.dynamodb_cards_table_param_value
}

resource "aws_ssm_parameter" "dynamodb_cards_by_bank_account_id_gsi_ssm_param" {
  name        = var.dynamodb_cards_by_bank_account_id_gsi_param_name
  description = "The name of the GSI index for the DynamoDB cards table"
  type        = "String"
  value       = var.dynamodb_cards_by_bank_account_id_gsi_param_value
}

resource "aws_ssm_parameter" "sqs_card_application_queue_ssm_param" {
  name        = var.sqs_card_application_queue_param_name
  description = "The name of the SQS card application queue"
  type        = "String"
  value       = var.sqs_card_application_queue_param_value
}

resource "aws_ssm_parameter" "sqs_card_application_dlq_ssm_param" {
  name        = var.sqs_card_application_dlq_param_name
  description = "The name of the Dead Letter Queue (DLQ) for the SQS card application queue"
  type        = "String"
  value       = var.sqs_card_application_dlq_param_value
}
