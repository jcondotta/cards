output "ecs_query_service_port" {
  description = "The container port used by the ECS query service"
  value       = var.container_port
}

output "ecs_task_security_group_id" {
  description = "The security group ID for the ECS task running the card query service."
  value       = aws_security_group.ecs_task_security_group.id
}