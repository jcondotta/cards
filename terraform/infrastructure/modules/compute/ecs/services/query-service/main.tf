locals {
  ecs_query_service_name = "ecs-card-query-service"
}

resource "aws_ecs_service" "ecs_card_query_service" {
  name            = local.ecs_query_service_name
  cluster         = var.ecs_cluster_id
  task_definition = aws_ecs_task_definition.ecs_card_query_service_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets          = var.service_subnet_ids
    security_groups  = [
      aws_security_group.ecs_task_security_group.id
    ]
    assign_public_ip = false
  }

  # deployment_minimum_healthy_percent = 50
  # deployment_maximum_percent         = 200

  load_balancer {
    target_group_arn = var.lb_target_group_arn
    container_name   = var.container_name
    container_port   = var.container_port
  }

  tags = {
    Name = local.ecs_query_service_name
  }
}
