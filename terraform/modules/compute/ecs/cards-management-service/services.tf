resource "aws_ecs_service" "ecs_cards_services" {
  name            = "ecs-cards-services"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.ecs_card_management_service_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1  # Run a single instance of the same service

  network_configuration {
    subnets          = var.service_subnet_ids
    security_groups  = [
      aws_security_group.ecs_task_security_group.id
    ]
    assign_public_ip = true
  }
}
