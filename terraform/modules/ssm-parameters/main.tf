resource "aws_ssm_parameter" "dynamodb_cards_table_ssm_param" {
  name        = "/cards/${var.environment}/dynamodb/cards-table-name"
  description = "The name of the DynamoDB cards table"
  type        = "String"

  value = "cards-${var.environment}"
}

resource "aws_ssm_parameter" "dynamodb_cards_by_bank_account_id_gsi_ssm_param" {
  name        = "/cards/${var.environment}/dynamodb/cards-gsi-by-bank-account-id"
  description = "The name of the GSI index for the DynamoDB cards table"
  type        = "String"

  value = "cards-by-bank-account-id-gsi-${var.environment}"
}

resource "aws_ssm_parameter" "sqs_card_application_queue_ssm_param" {
  name        = "/cards/${var.environment}/sqs/card-application-queue"
  description = "The name of the SQS card application queue"
  type        = "String"

  value = "card-application-${var.environment}"
}

resource "aws_ssm_parameter" "sqs_card_application_dlq_ssm_param" {
  name        = "/cards/${var.environment}/sqs/card-application-dlq"
  description = "The name of the Dead Letter Queue (DLQ) for the SQS card application queue"
  type        = "String"

  value = "card-application-dlq-${var.environment}"
}
