resource "aws_lambda_event_source_mapping" "card_application_queue_to_process_cards_lambda" {
  event_source_arn                   = var.sqs_card_application_queue_arn
  function_name                      = aws_lambda_function.process_cards_lambda.function_name
  batch_size                         = 10
  maximum_batching_window_in_seconds = 5
  enabled                            = true
  function_response_types            = ["ReportBatchItemFailures"]
}