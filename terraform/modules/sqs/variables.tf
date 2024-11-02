variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "card_application_queue_name" {
  type        = string
  description = "The name of the SQS queue for processing card applications"
}

variable "card_application_queue_visibility_timeout_seconds" {
  description = "The amount of time (in seconds) that a message is hidden from other consumers after it is retrieved by a consumer."
  type        = number
  default     = 30  # Default visibility timeout is 30 seconds
}

variable "card_application_queue_message_retention_seconds" {
  description = "The length of time (in seconds) that Amazon SQS retains a message (minimum 60 seconds, maximum 1209600 seconds or 14 days)."
  type        = number
  default     = 345600  # Default retention period is 4 days
}

variable "card_application_queue_delay_seconds" {
  description = "The amount of time (in seconds) that the delivery of all messages in the queue is delayed (minimum 0 seconds, maximum 900 seconds)."
  type        = number
  default     = 0
}

variable "card_application_queue_receive_wait_time_seconds" {
  description = "The maximum amount of time (in seconds) for which a ReceiveMessage call waits for a message to arrive in the queue before returning (minimum 0 seconds, maximum 20 seconds)."
  type        = number
  default     = 20
}


variable "card_application_queue_max_receive_count" {
  description = "The maximum number of times a message is received before it is moved to the Dead Letter Queue (DLQ)."
  type        = number
  default     = 3
}

variable "card_application_dlq_name" {
  type        = string
  description = "The name of the Dead Letter SQS queue for card application failures"
}

variable "card_application_dlq_message_retention_seconds" {
  description = "The length of time (in seconds) that Amazon SQS retains a message (minimum 60 seconds, maximum 1209600 seconds or 14 days)."
  type        = number
  default     = 1209600  # (14 days)
}

variable "tags" {
  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
  type        = map(string)
}