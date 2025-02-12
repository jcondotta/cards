output "security_group_id" {
  description = "The ID of the security group associated with the load balancer."
  value       = aws_security_group.load_balancer_sg.id
}

output "listener_http_arn" {
  description = "The ARN of the Application Load Balancer (ALB) HTTP listener."
  value       = aws_lb_listener.load_balancer_listener_http.arn
}