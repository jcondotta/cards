variable "vpc_id" {
  description = "The ID of the VPC where the target group is created."
  type        = string
}

variable "lb_target_group_port" {
  description = "The port for the Load Balancer Target Group"
  type        = number
}

variable "listener_http_arn" {
  description = "The ARN of the Application Load Balancer (ALB) HTTP listener."
  type        = string
}