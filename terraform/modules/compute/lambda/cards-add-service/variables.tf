variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "current_aws_account_id" {
  description = "The current AWS account ID"
  type        = string
}

variable "function_name" {
  description = "The name of the add cards Lambda function"
  type        = string
}

variable "memory_size" {
  description = "The memory size (in MB) for the Lambda function"
  type        = number
}

variable "timeout" {
  description = "The timeout (in seconds) for the Lambda function"
  type        = number
}

variable "runtime" {
  description = "The runtime for the Lambda function (e.g., java17, java21)"
  type        = string
}

variable "handler" {
  description = "The fully qualified handler class for the Lambda function"
  type        = string
}

variable "s3_bucket" {
  description = "The name of the S3 bucket where the Lambda files are stored"
  type        = string
}

variable "s3_key" {
  description = "The key (path) of the Lambda deployment file in the S3 bucket"
  type        = string
}

variable "dynamodb_cards_table_arn" {
  description = "The ARN of the DynamoDB cards table the Lambda will interact with"
  type        = string
}

variable "sqs_card_application_queue_arn" {
  description = "The ARN of the card application SQS queue"
  type        = string
}

variable "sqs_card_application_dead_letter_queue_arn" {
  description = "The ARN of the card application SQS dead letter queue"
  type        = string
}

variable "environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
  default     = {}
}