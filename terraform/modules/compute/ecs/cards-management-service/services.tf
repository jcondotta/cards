resource "aws_ecs_service" "service_1" {
  name            = "service-1"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.service_1_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets          = var.public_subnet_ids
    security_groups  = [aws_security_group.ecs_task_security_group.id]
    assign_public_ip = true
  }
}