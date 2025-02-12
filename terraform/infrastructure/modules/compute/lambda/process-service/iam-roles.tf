resource "aws_iam_role" "cards_process_lambda_role_exec" {
  name = "${var.function_name}-exec-role"

  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [{
      "Action" : "sts:AssumeRole",
      "Principal" : {
        "Service" : "lambda.amazonaws.com"
      },
      "Effect" : "Allow"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "cards_process_lambda_vpc_access_policy_attach" {
  role       = aws_iam_role.cards_process_lambda_role_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

resource "aws_iam_role_policy_attachment" "cards_process_lambda_policy_attach" {
  role       = aws_iam_role.cards_process_lambda_role_exec.name
  policy_arn = aws_iam_policy.cards_process_lambda_policy.arn
}

resource "aws_iam_policy" "cards_process_lambda_policy" {
  name        = "${var.function_name}-policy"
  description = "Policy for Lambda to interact with CloudWatch Logs, SQS, DynamoDB"
  policy      = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : "logs:CreateLogGroup",
        "Resource" : "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/lambda/*"
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        "Resource" : "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/aws/lambda/${var.function_name}:*"
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        "Resource" : var.sqs_card_application_queue_arn
      },
      {
        "Effect" : "Allow",
        "Action" : "dynamodb:PutItem",
        "Resource" : var.dynamodb_cards_table_arn
      }
    ]
  })
}
