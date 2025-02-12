resource "aws_cloudwatch_log_group" "ecs_management_service_logs" {
  name              = "/ecs/cards/management-service"
  retention_in_days = 1

  tags = {
    Name = "ecs-card-management-service-logs"
  }
}