# Define the AWS Lambda function for add cards service
resource "aws_lambda_function" "add_cards_lambda" {
  function_name = var.lambda_function_name
  runtime       = var.lambda_runtime
  handler       = var.lambda_handler
  role          = aws_iam_role.add_cards_lambda_role_exec.arn
  filename      = var.lambda_file
  memory_size   = var.lambda_memory_size
  timeout       = var.lambda_timeout
  architectures = ["arm64"]

  environment {
    variables = merge({
      AWS_DYNAMODB_CARDS_TABLE_NAME = var.dynamodb_cards_table_name,
      AWS_SQS_CARD_APPLICATION_QUEUE_NAME = var.sqs_card_application_queue_name,
      AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME = var.dynamodb_cards_by_bank_account_id_gsi_name},
      var.lambda_environment_variables
    )
  }

  tags = var.tags
}