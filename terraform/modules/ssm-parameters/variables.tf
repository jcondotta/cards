variable "dynamodb_cards_table_ssm_param_name" {
  description = "The SSM parameter name for the DynamoDB cards table"
  type        = string
  default     = "/dynamodb/cards-table-name"
}

variable "dynamodb_cards_table_ssm_param_value" {
  description = "The value of the DynamoDB cards table name"
  type        = string
}

variable "dynamodb_cards_gsi_ssm_param_name" {
  description = "The SSM parameter name for the DynamoDB cards GSI"
  type        = string
  default     = "/dynamodb/cards-gsi-name"
}

variable "dynamodb_cards_gsi_ssm_param_value" {
  description = "The value of the DynamoDB cards GSI name"
  type        = string
}

variable "sqs_cards_queue_ssm_param_name" {
  description = "The SSM parameter name for the SQS cards queue"
  type        = string
  default     = "/sqs/cards-queue-name"
}

variable "sqs_cards_queue_ssm_param_value" {
  description = "The value of the SQS cards queue name"
  type        = string
}

variable "sqs_cards_dlq_ssm_param_name" {
  description = "The SSM parameter name for the SQS cards DLQ"
  type        = string
  default     = "/sqs/cards-dlq-name"
}

variable "sqs_cards_dlq_ssm_param_value" {
  description = "The value of the SQS cards DLQ name"
  type        = string
}
