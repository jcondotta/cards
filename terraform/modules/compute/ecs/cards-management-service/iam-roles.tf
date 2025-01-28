resource "aws_iam_role" "ecs_task_execution_role" {
  name = "ecs-task-execution-role"
  assume_role_policy = jsonencode(
    {
      Version = "2012-10-17"
      Statement = [
        {
          Action = "sts:AssumeRole",
          Effect = "Allow",
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
        }
      ]
    }
  )
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy" "ecs_task_execution_additional_policy" {
  name = "ecs-task-execution-additional-policy"
  role = aws_iam_role.ecs_task_execution_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = [
          "logs:CreateLogGroup"
        ],
        Resource = "arn:aws:logs:${var.aws_region}:${var.current_aws_account_id}:log-group:/ecs/*"
      },
      # {
      #   Effect   = "Allow",
      #   Action   = [
      #     "ssm:GetParameter",
      #     "ssm:GetParameters",
      #     "secretsmanager:GetSecretValue"
      #   ],
      #   Resource = "*"
      # }
    ]
  })
}

resource "aws_iam_role" "ecs_cards_management_task_role" {
  name = "ecs-cards-management-task-role"

  assume_role_policy = jsonencode(
    {
      Version = "2012-10-17",
      Statement = [
        {
          Effect = "Allow",
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          },
          Action = "sts:AssumeRole"
        }
      ]
    }
  )
}



# resource "aws_cloudwatch_log_group" "ecs_logs" {
#   name              = "/ecs/${aws_ecs_service.cards_ecs_services.name}"
#   retention_in_days = 1  # Retain logs for a single day
# }

resource "aws_iam_role_policy" "ecs_cards_management_task_policy" {
  name = "ecs-cards-management-task-policy"
  role = aws_iam_role.ecs_cards_management_task_role.id

  policy = jsonencode(
    {
      Version = "2012-10-17",
      Statement = [
        {
          Effect   = "Allow",
          Action   = [
            "dynamodb:PutItem",
            "dynamodb:GetItem",
          ],
          Resource = var.dynamodb_cards_table_arn
        },
        {
          "Action" : [
            "sqs:SendMessage",
            "sqs:GetQueueUrl",
          ],
          "Effect" : "Allow",
          "Resource" : var.sqs_card_application_queue_arn
        },
      ]
    }
  )
}