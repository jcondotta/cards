# Define the AWS Lambda function for add cards service
resource "aws_lambda_function" "add_cards_lambda" {
  function_name = var.function_name
  runtime       = var.runtime
  handler       = var.handler
  role          = aws_iam_role.add_cards_lambda_role_exec.arn
  s3_bucket     = "jcondotta-lambda-files"
  s3_key        = "add-cards-0.1.jar"
  memory_size   = var.memory_size
  timeout       = var.timeout
  architectures = ["arm64"]

  environment {
    variables = var.environment_variables
  }

  tags = var.tags
}