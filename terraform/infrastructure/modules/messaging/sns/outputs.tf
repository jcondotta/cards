output "card_application_dlq_notifications_arn" {
  description = "ARN of the SNS topic for card application DLQ notifications"
  value       = aws_sns_topic.card_application_dlq_notifications.arn
}