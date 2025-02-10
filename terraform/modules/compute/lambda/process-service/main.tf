resource "aws_lambda_function" "process_cards_lambda" {
  function_name = var.function_name
  memory_size   = 1024
  timeout       = 10
  runtime       = "java21"
  handler       = "com.jcondotta.cards.process.web.events.CardApplicationEventHandler"

  s3_bucket     = var.cards_process_service_lambda_files_bucket_name
  s3_key        = "process-service-0.1.jar"

  architectures = ["arm64"]
  role          = aws_iam_role.cards_process_lambda_role_exec.arn

  environment {
    variables = var.environment_variables
  }

  vpc_config {
    subnet_ids = var.subnet_ids

    security_group_ids = [
      aws_security_group.process_cards_lambda_sg.id
    ]
  }
}