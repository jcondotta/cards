variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment the Lambda function is deployed to (e.g., dev, prod or staging)"
  type        = string
}

variable "current_aws_account_id" {
  description = "The current AWS account ID"
  type        = string
}

variable "lambda_function_name" {
  description = "The name of the add cards Lambda function"
  type        = string
}

variable "lambda_memory_size" {
  description = "The memory size (in MB) for the Lambda function"
  type        = number
}

variable "lambda_timeout" {
  description = "The timeout (in seconds) for the Lambda function"
  type        = number
}

variable "lambda_runtime" {
  description = "The runtime for the Lambda function (e.g., java17, java21)"
  type        = string
}

variable "lambda_handler" {
  description = "The fully qualified handler class for the Lambda function"
  type        = string
}

variable "lambda_file" {
  description = "The path to the file(jar, zip) for the Lambda function"
  type        = string
  default     = "../add-cards/target/add-cards-0.1.jar"
}

variable "lambda_environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "dynamodb_cards_table_arn" {
  description = "The ARN of the DynamoDB cards table the Lambda will interact with"
  type        = string
}

variable "dynamodb_cards_table_name" {
  description = "The name of the DynamoDB cards table the Lambda will interact with"
  type        = string
}

variable "dynamodb_cards_by_bank_account_id_gsi_name" {
  description = "The name of the DynamoDB global secondary index the Lambda will interact with"
  type        = string
}

variable "sqs_card_application_queue_arn" {
  description = "The ARN of the card application SQS queue"
  type        = string
}

variable "sqs_card_application_queue_name" {
  description = "The name of the card application SQS queue"
  type        = string
}

variable "sqs_card_application_dead_letter_queue_arn" {
  description = "The ARN of the card application SQS dead letter queue"
  type        = string
}

variable "sqs_card_application_dead_letter_queue_name" {
  description = "The name of the card application SQS dead letter queue"
  type        = string
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}