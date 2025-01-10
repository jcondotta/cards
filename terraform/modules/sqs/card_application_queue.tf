resource "aws_sqs_queue" "card_application_queue" {
  name = var.card_application_queue_name

  visibility_timeout_seconds = var.card_application_queue_visibility_timeout_seconds
  message_retention_seconds  = var.card_application_queue_message_retention_seconds
  delay_seconds              = var.card_application_queue_delay_seconds
  receive_wait_time_seconds  = var.card_application_queue_receive_wait_time_seconds

  redrive_policy = jsonencode(
    {
      deadLetterTargetArn = aws_sqs_queue.card_application_dead_letter_queue.arn
      maxReceiveCount     = var.card_application_queue_max_receive_count
    }
  )

  tags = var.tags

  depends_on = [
    aws_sqs_queue.card_application_dead_letter_queue
  ]
}