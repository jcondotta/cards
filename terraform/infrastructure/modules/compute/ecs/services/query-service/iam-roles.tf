resource "aws_iam_role" "ecs_card_query_task_execution_role" {
  name = "ecs-query-card-task-exec-role-${var.environment}"
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
  role       = aws_iam_role.ecs_card_query_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_card_query_task_role" {
  name = "ecs-query-card-task-role-${var.environment}"

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

resource "aws_iam_role_policy" "ecs_cards_query_task_policy" {
  name = "ecs-query-card-task-policy-${var.environment}"
  role = aws_iam_role.ecs_card_query_task_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = [
          "dynamodb:Query",
          "dynamodb:GetItem"
        ]
        Resource = [
          var.dynamodb_cards_table_arn,
          var.dynamodb_cards_by_bank_account_id_gsi_arn
        ]
      }
    ]
  })
}