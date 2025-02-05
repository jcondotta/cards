variable "dynamodb_cards_table_param_name" {
  description = "The SSM parameter name for the DynamoDB cards table"
  type        = string
  default     = "/cards/prod/dynamodb/cards-table-name"
}

variable "dynamodb_cards_table_param_value" {
  description = "The value of the DynamoDB cards table name"
  type        = string
}

variable "dynamodb_cards_by_bank_account_id_gsi_param_name" {
  description = "The SSM parameter name for the DynamoDB cards by bank account id GSI"
  type        = string
  default     = "/cards/prod/dynamodb/cards-gsi-by-bank-account-id"
}

variable "dynamodb_cards_by_bank_account_id_gsi_param_value" {
  description = "The value of the DynamoDB cards by bank account id GSI name"
  type        = string
}

variable "sqs_card_application_queue_param_name" {
  description = "The SSM parameter name for the SQS cards queue"
  type        = string
  default     = "/cards/prod/sqs/card-application-queue"
}

variable "sqs_card_application_queue_param_value" {
  description = "The value of the SQS card application queue name"
  type        = string
}

variable "sqs_card_application_dlq_param_name" {
  description = "The SSM parameter name for the SQS card application DLQ"
  type        = string
  default     = "/cards/prod/sqs/card-application-dlq"
}

variable "sqs_card_application_dlq_param_value" {
  description = "The value of the SQS card application DLQ name"
  type        = string
}
