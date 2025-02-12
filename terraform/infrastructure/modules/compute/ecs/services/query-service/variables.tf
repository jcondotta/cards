variable "aws_region" {
  description = "The AWS region where resources will be deployed."
  type        = string
}

variable "environment" {
  description = "The environment to deploy to (e.g., dev, staging, prod)"
  type        = string
}

variable "ecs_cluster_id" {
  description = "The ID of the ECS cluster"
  type        = string
}

variable "vpc_id" {
  description = "The ID of the cards VPC"
  type        = string
}

variable "service_subnet_ids" {
  type        = list(string)
  description = "List of subnet IDs where the ECS service will run"
}

variable "container_port" {
  description = "The port on which the container listens."
  type        = number
}

variable "container_protocol" {
  description = "The protocol used for container communication (TCP or UDP)."
  type        = string
  default     = "tcp"
}

variable "container_name" {
  description = "The name of the container in the ECS task definition."
  type        = string
}

variable "container_image" {
  description = "The container image URI for the ECS task."
  type        = string
}

variable "lb_target_group_arn" {
  description = "ARN of the ALB target group for ECS service"
  type        = string
}

variable "lb_security_group_id" {
  description = "ID of the ALB security group for ECS service"
  type        = string
}

variable "dynamodb_cards_table_arn" {
  description = "The ARN of the DynamoDB cards table the Lambda will interact with"
  type        = string
}

variable "dynamodb_cards_by_bank_account_id_gsi_arn" {
  description = "The ARN of the Global Secondary Index for querying cards by bankAccountId"
  type        = string
}

variable "environment_variables" {
  type    = map(string)
  default = {}
}