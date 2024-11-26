output "card_application_queue_url" {
  description = "The URL of the card application SQS queue"
  value       = aws_sqs_queue.card_application_queue.url
}

output "card_application_queue_arn" {
  description = "The ARN of the card application SQS queue"
  value       = aws_sqs_queue.card_application_queue.arn
}

output "card_application_queue_name" {
  description = "The name of the card application SQS queue"
  value       = aws_sqs_queue.card_application_queue.name
}

output "card_application_dlq_url" {
  description = "The URL of the card application dead Letter SQS queue"
  value       = aws_sqs_queue.card_application_dead_letter_queue.url
}

output "card_application_dead_letter_queue_arn" {
  description = "The ARN of the card application dead Letter SQS queue"
  value       = aws_sqs_queue.card_application_dead_letter_queue.arn
}

output "card_application_dead_letter_queue_name" {
  description = "The name of the card application dead Letter SQS queue"
  value       = aws_sqs_queue.card_application_dead_letter_queue.name
}
