output "security_group_id" {
  description = "The ID of the load balancer security group"
  value       = aws_security_group.load_balancer_security_group.id
}