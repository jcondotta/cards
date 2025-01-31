variable "dynamodb_cards_table_ssm_param_name" {
  description = "The SSM parameter name for the DynamoDB cards table"
  type        = string
  default     = "/dynamodb/cards-table-name"
}

variable "dynamodb_cards_table_ssm_param_value" {
  description = "The value of the DynamoDB cards table name"
  type        = string
}

variable "dynamodb_cards_by_bank_account_id_gsi_ssm_param_name" {
  description = "The SSM parameter name for the DynamoDB cards by bank account id GSI"
  type        = string
  default     = "/dynamodb/cards-gsi-name"
}

variable "dynamodb_cards_by_bank_account_id_gsi_ssm_param_value" {
  description = "The value of the DynamoDB cards by bank account id GSI name"
  type        = string
}

variable "sqs_card_application_queue_queue_ssm_param_name" {
  description = "The SSM parameter name for the SQS cards queue"
  type        = string
  default     = "/sqs/card-application-queue-name"
}

variable "sqs_card_application_queue_queue_ssm_param_value" {
  description = "The value of the SQS card application queue name"
  type        = string
}

variable "sqs_card_application_dlq_ssm_param_name" {
  description = "The SSM parameter name for the SQS card application DLQ"
  type        = string
  default     = "/sqs/card-application-dlq-name"
}

variable "sqs_card_application_dlq_ssm_param_value" {
  description = "The value of the SQS card application DLQ name"
  type        = string
}
