variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "aws_profile" {
  description = "The AWS profile to use for deployment."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, localstack, staging, prod)"
  type        = string

  validation {
    condition     = contains(["dev", "localstack", "staging", "prod"], var.environment)
    error_message = "Environment must be one of 'dev', 'localstack', 'staging', or 'prod'."
  }
}

variable "add_cards_lambda_environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "management_cards_lambda_memory_size" {
  description = "The memory size (in MB) for the Lambda function"
  type        = number
  default     = 1024

  validation {
    condition     = var.management_cards_lambda_memory_size >= 128 && var.management_cards_lambda_memory_size <= 10240
    error_message = "management_cards_lambda_memory_size must be between 128 MB and 10,240 MB."
  }
}

variable "management_cards_lambda_timeout" {
  description = "The timeout (in seconds) for the add cards Lambda function"
  type        = number
  default     = 15

  validation {
    condition     = var.management_cards_lambda_timeout > 0 && var.management_cards_lambda_timeout <= 900
    error_message = "management_cards_lambda_timeout must be a positive number and less than or equal to 900 seconds (15 minutes)."
  }
}

variable "management_cards_lambda_handler" {
  description = "The fully qualified handler class for the Lambda function"
  type        = string
  default     = "io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction"
}

variable "management_cards_lambda_runtime" {
  description = "The runtime for the add cards Lambda function (e.g., java17, java21)"
  type        = string
  default     = "java17"
}

variable "management_cards_lambda_environment_variables" {
  description = "A key-value map of environment variables for the Lambda function, used to configure dynamic runtime settings."
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
  default = {
    "project" = "cards"
  }
}