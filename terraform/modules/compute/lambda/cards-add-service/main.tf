resource "aws_lambda_function" "add_cards_lambda" {
  function_name = var.function_name
  runtime       = var.runtime
  handler       = var.handler
  role          = aws_iam_role.add_cards_lambda_role_exec.arn
  s3_bucket     = var.s3_bucket
  s3_key        = var.s3_key
  memory_size   = var.memory_size
  timeout       = var.timeout
  architectures = ["arm64"]

  environment {
    variables = var.environment_variables
  }

  tags = var.tags
}