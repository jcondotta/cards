resource "aws_sqs_queue" "card_application_dead_letter_queue" {
  name = var.card_application_dlq_name

  message_retention_seconds = var.card_application_dlq_message_retention_seconds

  tags = var.tags
}