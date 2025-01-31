resource "aws_ssm_parameter" "dynamodb_cards_table_ssm_param" {
  name        = var.dynamodb_cards_table_ssm_param_name
  description = "The name of the DynamoDB table for the cards module"
  type        = "SecureString"
  value       = var.dynamodb_cards_table_ssm_param_value

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_ssm_parameter" "dynamodb_cards_by_bank_account_id_gsi_ssm_param" {
  name        = var.dynamodb_cards_by_bank_account_id_gsi_ssm_param_name
  description = "The name of the GSI index for the DynamoDB cards table"
  type        = "String"
  value       = var.dynamodb_cards_by_bank_account_id_gsi_ssm_param_value

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_ssm_parameter" "sqs_card_application_queue_queue_ssm_param" {
  name        = var.sqs_card_application_queue_queue_ssm_param_name
  description = "The name of the SQS queue for the cards module"
  type        = "String"
  value       = var.sqs_card_application_queue_queue_ssm_param_value

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_ssm_parameter" "sqs_card_application_dlq_ssm_param" {
  name        = var.sqs_card_application_dlq_ssm_param_name
  description = "The name of the Dead Letter Queue (DLQ) for the SQS cards queue"
  type        = "String"
  value       = var.sqs_card_application_dlq_ssm_param_value

  lifecycle {
    create_before_destroy = true
  }
}
