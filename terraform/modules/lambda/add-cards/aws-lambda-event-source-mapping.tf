resource "aws_lambda_event_source_mapping" "card_application_queue_to_add_cards_lambda" {
  event_source_arn                   = var.sqs_card_application_queue_arn
  function_name                      = aws_lambda_function.add_cards_lambda.function_name
  batch_size                         = 5
  maximum_batching_window_in_seconds = 1
  enabled                            = true
}