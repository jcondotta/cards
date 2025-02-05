variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "load_balancer_name" {
  type        = string
  description = "The name of the Application Load Balancer"
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "subnet_ids" {
  description = "The IDs of the subnets where the load balancer will be deployed."
  type        = list(string)
}