# Create a CloudWatch alarm if the DLQ has > 0 messages
resource "aws_cloudwatch_metric_alarm" "card_application_dlq_high_messages_alarm" {
  alarm_name = "${aws_sqs_queue.card_application_dead_letter_queue.name}-high-messages"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Triggers an alert if the ${aws_sqs_queue.card_application_dead_letter_queue.name} contains one or more visible messages"
  alarm_actions       = [
    var.sns_topic_card_application_dlq_notification_arn
  ]

  dimensions = {
    QueueName = aws_sqs_queue.card_application_dead_letter_queue.name
  }
}