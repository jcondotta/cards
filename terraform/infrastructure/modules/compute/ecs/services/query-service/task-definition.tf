resource "aws_ecs_task_definition" "ecs_card_query_service_task" {
  family                   = "ecs-card-query-service-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_card_query_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_card_query_task_role.arn
  cpu                      = "512"  # 0.5 vCPU
  memory                   = "1024" # 1.0 MB memory
  container_definitions = jsonencode(
    [
      {
        name      = var.container_name
        image     = var.container_image
        essential = true
        portMappings = [
          {
            containerPort = var.container_port
            protocol      = var.container_protocol
          }
        ]
        environment = [
          for key, value in var.environment_variables : {
            name  = key
            value = value
          }
        ]
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group         = aws_cloudwatch_log_group.ecs_query_service_logs.name
            awslogs-region        = var.aws_region
            awslogs-stream-prefix = "ecs"
          }
        }
        healthCheck = {
          command     = ["CMD-SHELL", "curl -f http://localhost:${var.container_port}/health || exit 1"]
          interval    = 30
          timeout     = 8
          retries     = 3
          startPeriod = 60
        }
      }
    ]
  )
}