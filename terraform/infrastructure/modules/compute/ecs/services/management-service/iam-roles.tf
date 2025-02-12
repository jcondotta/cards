resource "aws_iam_role" "ecs_card_management_task_execution_role" {
  name = "ecs-manage-card-task-exec-role-${var.environment}"
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
  role       = aws_iam_role.ecs_card_management_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_card_management_task_role" {
  name = "ecs-manage-card-task-role-${var.environment}"

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

resource "aws_iam_role_policy" "ecs_cards_management_task_policy" {
  name = "ecs-manage-card-task-policy-${var.environment}"
  role = aws_iam_role.ecs_card_management_task_role.id

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