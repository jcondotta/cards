output "card_application_queue_arn" {
  description = "The ARN of the SQS card application queue"
  value       = aws_sqs_queue.card_application_queue.arn
}

output "card_application_queue_name" {
  description = "The name of the SQS card application queue"
  value       = aws_sqs_queue.card_application_queue.name
}

output "card_application_dead_letter_queue_arn" {
  description = "The ARN of the SQS card application dead Letter queue"
  value       = aws_sqs_queue.card_application_dead_letter_queue.arn
}

output "card_application_dead_letter_queue_name" {
  description = "The name of the SQS card application dead Letter queue"
  value       = aws_sqs_queue.card_application_dead_letter_queue.name
}
