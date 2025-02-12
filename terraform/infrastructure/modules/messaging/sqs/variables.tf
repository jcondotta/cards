variable "card_application_queue_name" {
  type        = string
  description = "The name of the SQS queue for processing card applications"
}

variable "card_application_dlq_name" {
  type        = string
  description = "The name of the Dead Letter SQS queue for card application failures"
}

variable "sns_topic_card_application_dlq_notification_arn" {
  description = "ARN of the SNS topic for SQS card application DLQ CloudWatch alarms"
  type        = string
}