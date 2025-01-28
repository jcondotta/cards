resource "aws_ecs_task_definition" "ecs_card_management_service_task" {
  family                   = "ecs_card_management_service_task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_cards_management_task_role.arn
  cpu                      = "256" # 0.25 vCPU
  memory                   = "512" # 512 MB memory
  container_definitions = jsonencode(
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
          {
            name  = "AWS_DEFAULT_REGION",
            value = var.aws_region
          },
          {
            name  = "AWS_DYNAMODB_CARDS_TABLE_NAME",
            value = "cards-prod"
          },
          {
            name  = "AWS_SQS_CARD_APPLICATION_QUEUE_NAME",
            value = "card-application-prod"
          }
        ]
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group         = "/ecs/cards-management-service"
            awslogs-region        = var.aws_region
            awslogs-stream-prefix = "ecs"
          }
        }
        # Optional container health check example
        healthCheck = {
          command     = ["CMD-SHELL", "curl -f http://localhost:8072/health || exit 1"]
          interval    = 30
          timeout     = 5
          retries     = 3
          startPeriod = 30
        }
      }
    ]
  )
}