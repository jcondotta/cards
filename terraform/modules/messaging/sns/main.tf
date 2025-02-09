resource "aws_sns_topic" "card_application_dlq_notifications" {
  name = "${var.sqs_card_application_dlq_name}-notifications"
}