resource "aws_sns_topic" "card_application_dlq_alerts" {
  name = "${var.sqs_card_application_dlq_name}-alerts"
}

# Create a CloudWatch alarm if the DLQ has > 0 messages
resource "aws_cloudwatch_metric_alarm" "card_application_dlq_sns_topic_alarm" {
  alarm_name          = "${var.sqs_card_application_dlq_name}-high-messages"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "ApproximateNumberOfMessagesVisible"
  namespace           = "AWS/SQS"
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  alarm_description   = "Triggers an alert if the ${var.sqs_card_application_dlq_name} contains one or more visible messages"
  alarm_actions       = [
    aws_sns_topic.card_application_dlq_alerts.arn
  ]

  dimensions = {
    QueueName = var.sqs_card_application_dlq_name
  }
}