resource "aws_ecs_task_definition" "service_1_task" {
  family                   = "service-1-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  cpu                      = "512" # Adjust as needed
  memory                   = "1024" # Adjust as needed
  container_definitions    = jsonencode(
    [
      {
        name      = "cards-management-service"
        image     = "public.ecr.aws/e0g3c1r4/jcondotta/cards-management-service:latest"
        essential = true
        portMappings = [
          {
            containerPort = 8072
            protocol      = "tcp"
          }
        ]
        environment = [
          { name = "AWS_DYNAMODB_CARDS_TABLE_NAME", value = "cards-prod" },
          { name = "AWS_DYNAMODB_CARDS_BY_BANK_ACCOUNT_ID_GSI_NAME", value = "cards-by-bank-account-id-gsi-prod" }
        ]
      }
    ]
  )
}