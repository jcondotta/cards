resource "aws_sqs_queue" "card_application_queue" {
  name = var.card_application_queue_name

  visibility_timeout_seconds = 90
  message_retention_seconds  = 345600 #4 days
  delay_seconds              = 0
  receive_wait_time_seconds  = 20

  redrive_policy = jsonencode(
    {
      deadLetterTargetArn = aws_sqs_queue.card_application_dead_letter_queue.arn
      maxReceiveCount     = 3
    }
  )
}

resource "aws_sqs_queue" "card_application_dead_letter_queue" {
  name = var.card_application_dlq_name

  message_retention_seconds = 1209600 #14 days
}