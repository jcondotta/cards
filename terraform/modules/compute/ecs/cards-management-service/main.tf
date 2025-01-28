resource "aws_ecs_cluster" "this" {
  name = "cards-management-service-cluster"

  setting {
    name  = "containerInsights"
    value = "disabled"
  }
}