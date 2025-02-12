resource "aws_cloudwatch_log_group" "ecs_query_service_logs" {
  name              = "/ecs/cards/query-service"
  retention_in_days = 1

  tags = {
    Name = "ecs-card-query-service-logs"
  }
}