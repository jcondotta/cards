variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "current_aws_account_id" {
  description = "The current AWS account ID"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "subnet_ids" {
  description = "List of subnet IDs where the lambda function will be placed"
  type        = list(string)
}

variable "function_name" {
  description = "The name of the add cards Lambda function"
  type        = string
}

variable "cards_process_service_lambda_files_bucket_name" {
  description = "The name of the S3 bucket that stores deployment artifacts for the Cards Process Service Lambda."
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

variable "environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}
